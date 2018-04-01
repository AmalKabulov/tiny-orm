package by.ititon.orm.action;

import by.ititon.orm.annotation.*;
import by.ititon.orm.testEntity.TestEntity;
import by.ititon.orm.testEntity.TestEntity2;
import by.ititon.orm.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

public class EntityInsertAction {

    public static void main(String[] args) {
        EntityInsertAction insertAction = new EntityInsertAction();
        TestEntity testEntity = new TestEntity();
        testEntity.getTestEntities2().add(new TestEntity2(1L,"asdasda"));
        testEntity.getTestEntities2().add(new TestEntity2(2L,"sadasdas"));
        testEntity.getTestEntities2().add(new TestEntity2(3L,"asasdasdasdada"));
        testEntity.getTestEntities2().add(new TestEntity2(4L,"a12312dsada"));
        testEntity.getTestEntities2().add(new TestEntity2(5L,"alknasjknfsajkf"));
        insertAction.genericInsertQueryStatementCreator(testEntity);
    }


    public void genericInsertQueryStatementCreator(final Object object) {

        String cascadeInsertQuery = null;

        ArrayList<String> columnNames = new ArrayList<>();
        ArrayList<String> columnValues = new ArrayList<>();
        Class<?> objClass = object.getClass();

        Field[] declaredFields = objClass.getDeclaredFields();

        String tableName = objClass.getAnnotation(Table.class).name();


        for (Field field : declaredFields) {

            if (field.isAnnotationPresent(Column.class)) {

                Method method = ReflectionUtil.getMethod(objClass, "get" + capitalizeFirstLetter(field.getName()));

                String columnName = field.getAnnotation(Column.class).name();
                String columnValue = wrap(String.valueOf(ReflectionUtil.invokeMethod(object, method)));

                columnNames.add(columnName);
                columnValues.add(columnValue);

            } else if (field.isAnnotationPresent(ManyToMany.class)) {


                String typeClassName = ReflectionUtil.getFieldGenericType(field)[0].getTypeName();


                if (field.isAnnotationPresent(Cascade.class)) {
                    Method method = ReflectionUtil.getMethod(objClass, "get" + capitalizeFirstLetter(field.getName()));
                    Object result = ReflectionUtil.invokeMethod(object, method);

                    if (result != null && result instanceof Collection) {
                        Collection result1 = (Collection) result;
                        if (!result1.isEmpty()) {
                            LinkedHashSet<String> columns1 = new LinkedHashSet<>();
                            ArrayList<String> columnValues1 = new ArrayList<>();

                            Object obj = ReflectionUtil.newInstance(typeClassName);
                            String tableName1 = obj.getClass().getAnnotation(Table.class).name();

                            result1.forEach(o -> smthblabla(o, columns1, columnValues1));

                            cascadeInsertQuery = generateInsertQuery(tableName1, columns1, columnValues1);
                        }
                    }
                }

            }
        }


        System.out.println(cascadeInsertQuery);
        System.out.println(generateInsertQuery(tableName, columnNames, columnValues));
    }

    private void smthblabla(Object object, Set<String> columns, List<String> values) {
        StringBuilder columnValues = new StringBuilder("(");

        Class<?> objClass = object.getClass();

        Field[] declaredFields = objClass.getDeclaredFields();


        for (Field field : declaredFields) {

            if (field.isAnnotationPresent(Column.class)) {

                Method method = ReflectionUtil.getMethod(objClass, "get" + capitalizeFirstLetter(field.getName()));

                String columnName = field.getAnnotation(Column.class).name();
                String columnValue = wrap(String.valueOf(ReflectionUtil.invokeMethod(object, method)));
                columnValues.append(columnValue).append(",");
                columns.add(columnName);
            }
        }


        columnValues.setLength(columnValues.length() - 1);
        columnValues.append(")");
        values.add(columnValues.toString());

    }

    private String generateInsertQuery(String tableName, Collection<String> columnNames, Collection<String> values) {
        String columns = String.join(", ", columnNames);
        String columnValues = String.join(", ", values);

        return new StringBuilder("INSERT INTO ")
                .append(tableName)
                .append(" (").append(columns)
                .append(") VALUES ")
                .append(columnValues)
                .append(";").toString();

    }


    public void manyToManyHandler(Field field) {
        ManyToMany annotation = field.getAnnotation(ManyToMany.class);

        String result = null;

        ParameterizedType genericReturnType = (ParameterizedType) field.getGenericType();
        String typeClassName = genericReturnType.getActualTypeArguments()[0].getTypeName();

        Class<?> depClass = ReflectionUtil.newClass(typeClassName);

        String mappedByField = annotation.mappedBy();

        if (mappedByField.length() > 0) {


        } else {

        }


    }

    public String doMpWork(Class<?> clazz, String mappedBy, List<String> columns, String mainclassid) {

        Field[] declaredFields = clazz.getDeclaredFields();

        String tableName = clazz.getAnnotation(Table.class).name();
        String id = null;
        String joinTableName = null;
        String joinColumn = null;
        String inverseJoinColumn = null;

        for (Field field : declaredFields) {

//            if (field.isAnnotationPresent(Column.class)) {
//                String columnName = field.getAnnotation(Column.class).name();
//
//                if (field.isAnnotationPresent(Id.class)) {
//                    id = columnName;
//                }
//
//                columns.add(tableName + "." + columnName);
//            }
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


    private String wrap(String value) {
        return "\'" + value + "\'";
    }

    private String capitalizeFirstLetter(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
