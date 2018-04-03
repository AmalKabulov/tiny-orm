package by.ititon.orm.action;

import by.ititon.orm.annotation.*;

import by.ititon.orm.metadata.TableMetaData;
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

        TableMetaData mainTable = new TableMetaData();
        TableMetaData innerTable = new TableMetaData();

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
                mainTable.getColumnNames().add(tableName + "." + columnName);

            } else if (field.isAnnotationPresent(ManyToMany.class)) {

                joining = manyToManyHandler(field, innerTable, id);
            }
        }


        System.out.println(buildFindAllQuery(mainTable, innerTable, joining));

    }


    public String manyToManyHandler(Field field, TableMetaData innerTable, String id) {

        String result = null;

        ManyToMany annotation = field.getAnnotation(ManyToMany.class);
        if (annotation.fetch().equals(FetchType.LAZY)) {
            return null;
        }


        ParameterizedType genericReturnType = (ParameterizedType) field.getGenericType();
        String typeClassName = genericReturnType.getActualTypeArguments()[0].getTypeName();


        Class<?> innerTableClass = ReflectionUtil.newClass(typeClassName);
        innerTable.setMappedTableClass(innerTableClass);
        String mappedByFieldName = annotation.mappedBy();

//        joinTable.setMappedTableClass(innerTableClass);

        if (mappedByFieldName.length() > 0) {

            innerTable.setMappedByFieldName(mappedByFieldName);

            result = findJoinTableByMappedByValue(innerTable,  id);

        } else {

            innerTable.setMappedByField(field);

            result = findJoinTableByFieldName(innerTable,  id);
        }

        return result;
    }

    public String findJoinTableByFieldName (TableMetaData innerTable, String mainclassid) {
        Class<?> mappedTableClass = innerTable.getMappedTableClass();
        Field[] declaredFields = mappedTableClass.getDeclaredFields();
        String tableName = mappedTableClass.getAnnotation(Table.class).name();
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

                innerTable.getColumnNames().add(tableName + "." + columnName);
            }
            if (field.isAnnotationPresent(ManyToMany.class)) {

                String mappedFieldName = field.getAnnotation(ManyToMany.class).mappedBy();

                Field mappedByField = innerTable.getMappedByField();

                if (mappedFieldName.equals(mappedByField.getName())){

                    JoinTable annotation = mappedByField.getAnnotation(JoinTable.class);

                    joinTableName = annotation.name();
                    joinColumn = joinTableName + "." + annotation.joinColumns()[0].name();
                    inverseJoinColumn = joinTableName + "." + annotation.inverseJoinColumns()[0].name();


//                    joinTable.setTableName(joinTableName);
//                    joinTable.getColumnNames().add(joinColumn);
//                    joinTable.getColumnNames().add(inverseJoinColumn);

//                    joinTableName = annotation.name();

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




    public String findJoinTableByMappedByValue (TableMetaData innerTable, String mainclassid) {

        Class<?> mappedTableClass = innerTable.getMappedTableClass();

        Field[] declaredFields = mappedTableClass.getDeclaredFields();

        String tableName = mappedTableClass.getAnnotation(Table.class).name();
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

                innerTable.getColumnNames().add(tableName + "." + columnName);
            }
            if (field.getName().equals(innerTable.getMappedByFieldName())) {

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


    private String buildFindAllQuery(TableMetaData mainTable, TableMetaData innerTable,  String joining) {
        List<String> columns = new ArrayList<>();

        columns.addAll(mainTable.getColumnNames());
        columns.addAll(innerTable.getColumnNames());
        String columnNames = String.join(", ", columns);
        StringBuilder query = new StringBuilder("SELECT ").append(columnNames).append(" FROM ").append(mainTable.getTableName()).append("\n");
        if (joining != null) {
            query.append(joining).append(";");
        }
        return query.toString();
    }


}
