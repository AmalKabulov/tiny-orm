package by.ititon.orm.action;

import by.ititon.orm.annotation.*;

import by.ititon.orm.testEntity.TestEntity;
import by.ititon.orm.testEntity.TestEntity2;
import by.ititon.orm.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class EntitySelectAction {

    public static void main(String[] args) {
        EntitySelectAction entitySelectAction = new EntitySelectAction();
        entitySelectAction.genericFindAllStatementQueryCreator(TestEntity2.class);
        entitySelectAction.genericFindAllStatementQueryCreator(TestEntity.class);
    }


    public void genericFindAllStatementQueryCreator(final Class<?> clazz) {

        List<String> columns = new ArrayList<>();

        Field[] declaredFields = clazz.getDeclaredFields();

        String tableName = clazz.getAnnotation(Table.class).name();
        String id = null;
        String joining = null;

        for (Field field : declaredFields) {

            if (field.isAnnotationPresent(Column.class)) {
                String columnName = field.getAnnotation(Column.class).name();

                if (field.isAnnotationPresent(Id.class)) {
                    id = tableName + "." + columnName;
                }
                columns.add(tableName + "." + columnName);

            } else if (field.isAnnotationPresent(ManyToMany.class)) {

                joining = manyToManyHandler(field, columns, id);
            }
        }


        System.out.println(buildFindAllQuery(tableName, columns, joining));

    }


    public String manyToManyHandler(Field field, List<String> columns, String id) {

        String result = null;

        ManyToMany annotation = field.getAnnotation(ManyToMany.class);
        if (annotation.fetch().equals(FetchType.LAZY)) {
            return null;
        }


        ParameterizedType genericReturnType = (ParameterizedType) field.getGenericType();
        String typeClassName = genericReturnType.getActualTypeArguments()[0].getTypeName();

        Class<?> depClass = ReflectionUtil.newClass(typeClassName);

        String mappedByField = annotation.mappedBy();

        if (mappedByField.length() > 0) {

            result = doMpWork(depClass, mappedByField, columns, id);

        } else {
            result = doNoMappedBy(depClass, field, columns, id);
        }

        return result;
    }

    public String doNoMappedBy(Class<?> clazz, Field depField, List<String> columns, String mainclassid) {

        Field[] declaredFields = clazz.getDeclaredFields();
        String tableName = clazz.getAnnotation(Table.class).name();
        String id = null;
        String joinTableName = null;
        String joinColumn = null;
        String inverseJoinColumn = null;

        for (Field field : declaredFields) {

            if (field.isAnnotationPresent(Column.class)) {
                String columnName = field.getAnnotation(Column.class).name();

                if (field.isAnnotationPresent(Id.class)) {
                    id = columnName;
                }

                columns.add(tableName + "." + columnName);
            }
            if (field.isAnnotationPresent(ManyToMany.class)) {

                String mappedFieldName = field.getAnnotation(ManyToMany.class).mappedBy();

                if (mappedFieldName.equals(depField.getName())){

                    JoinTable annotation = depField.getAnnotation(JoinTable.class);

                    joinTableName = annotation.name();
                    joinColumn = annotation.joinColumns()[0].name();
                    inverseJoinColumn = annotation.inverseJoinColumns()[0].name();
                }
            }
        }

        return " LEFT JOIN " + joinTableName
                + " ON "
                + joinTableName + "." + joinColumn
                + " = "
                + mainclassid
                + " LEFT JOIN " + tableName +
                " ON "
                + tableName + "." + id
                + " = "
                + joinTableName + "." + inverseJoinColumn;
    }




    public String doMpWork(Class<?> clazz, String mappedBy, List<String> columns, String mainclassid) {

        Field[] declaredFields = clazz.getDeclaredFields();

        String tableName = clazz.getAnnotation(Table.class).name();
        String id = null;
        String joinTableName = null;
        String joinColumn = null;
        String inverseJoinColumn = null;

        for (Field field : declaredFields) {

            if (field.isAnnotationPresent(Column.class)) {
                String columnName = field.getAnnotation(Column.class).name();

                if (field.isAnnotationPresent(Id.class)) {
                    id = columnName;
                }

                columns.add(tableName + "." + columnName);
            }
            if (field.getName().equals(mappedBy)) {

                //TODO may be nullpointer

                JoinTable annotation = field.getAnnotation(JoinTable.class);
                joinTableName = annotation.name();
                joinColumn = annotation.joinColumns()[0].name();
                inverseJoinColumn = annotation.inverseJoinColumns()[0].name();
            }
        }


        return " LEFT JOIN " + joinTableName
                + " ON "
                + joinTableName + "." + inverseJoinColumn
                + " = "
                + mainclassid
                + " LEFT JOIN " + tableName +
                " ON "
                + tableName + "." + id
                + " = "
                + joinTableName + "." + joinColumn;

    }


    private String buildFindAllQuery(String tableName, List<String> columns, String joining) {
        String columnNames = String.join(", ", columns);
        StringBuilder query = new StringBuilder("SELECT ").append(columnNames).append(" FROM ").append(tableName).append("\n");
        if (joining != null) {
            query.append(joining).append(";");
        }
        return query.toString();
    }


}
