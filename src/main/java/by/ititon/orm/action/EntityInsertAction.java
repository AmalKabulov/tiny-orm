package by.ititon.orm.action;

import by.ititon.orm.annotation.*;
import by.ititon.orm.metadata.TableMetaData;
import by.ititon.orm.testEntity.TestEntity;
import by.ititon.orm.testEntity.TestEntity2;
import by.ititon.orm.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class EntityInsertAction {

    public static void main(String[] args) {
        EntityInsertAction insertAction = new EntityInsertAction();
        TestEntity testEntity = new TestEntity();
        testEntity.getTestEntities2().add(new TestEntity2(1L, "asdasda"));
        testEntity.getTestEntities2().add(new TestEntity2(2L, "sadasdas"));
        testEntity.getTestEntities2().add(new TestEntity2(3L, "asasdasdasdada"));
        testEntity.getTestEntities2().add(new TestEntity2(4L, "a12312dsada"));
        testEntity.getTestEntities2().add(new TestEntity2(5L, "alknasjknfsajkf"));
        insertAction.genericInsertQueryStatementCreator(testEntity);
    }


    public void genericInsertQueryStatementCreator(final Object object) {

        String cascadeInsertQuery = null;

        TableMetaData mainTable = new TableMetaData();
        TableMetaData joinTable = new TableMetaData();
        TableMetaData innerTable = new TableMetaData();

        Class<?> objClass = object.getClass();

        Field[] declaredFields = objClass.getDeclaredFields();

        String tableName = objClass.getAnnotation(Table.class).name();
        mainTable.setTableName(tableName);


        for (Field field : declaredFields) {

            if (field.isAnnotationPresent(Column.class)) {
                fillMainTable(object, field, mainTable);

            } else if (field.isAnnotationPresent(ManyToMany.class)) {
                complexAssocHandler(object, field, joinTable, innerTable);
            }
        }


        long id = 100L;

        List<String> result = joinTable.getColumnValues()
                .stream()
                .map(s -> new StringBuilder()
                        .append("('")
                        .append(id)
                        .append("', '")
                        .append(s)
                        .append("')").toString())
                .collect(Collectors.toList());

        joinTable.setColumnValues(result);
        System.out.println(generateComplexInsertQuery(joinTable));
        System.out.println(generateComplexInsertQuery(innerTable));
        System.out.println(generateSoloInsertQuery(mainTable));
    }


    private void fillMainTable(Object object, Field fieldOfObject, TableMetaData mainTable) {

        String columnName = fieldOfObject.getAnnotation(Column.class).name();
        String columnValue = wrap(String.valueOf(ReflectionUtil.invokeGetter(object, fieldOfObject.getName())));

        mainTable.getColumnNames().add(columnName);
        mainTable.getColumnValues().add(columnValue);
    }


    private void complexAssocHandler(Object object, Field objField, TableMetaData joinTable, TableMetaData innerTable) {

        Object result = ReflectionUtil.invokeGetter(object, objField.getName());

        if (result != null) {

            Collection assocMappedVal = (Collection) result;

            if (!assocMappedVal.isEmpty()) {
//
//                if (objField.isAnnotationPresent(Cascade.class)) {
//
//                    String tableName1 = fieldClass.getAnnotation(Table.class).name();
//                    innerTable.setTableName(tableName1);
//
//                    assocMappedVal.forEach(o -> smthblabla(o, innerTable));
//
//                } else {
//
//                    ManyToMany annotation = objField.getAnnotation(ManyToMany.class);
//                    String mappedByField = annotation.mappedBy();
//
//                    if (mappedByField.length() > 0) {
//
//                        joinTable.setMappedByFieldName(mappedByField);
//
//                        assocMappedVal.forEach(o -> findJoinTableByMappedByValue(o, joinTable));
//
//                    } else {
//
//                        joinTable.setMappedByField(objField);
//
//                        assocMappedVal.forEach(o -> findJoinTableByFieldName(o, joinTable));
//
//                    }

//                }

            }

        }

    }

    public void cascadeHadler(Collection assocMappedVal, Field field, TableMetaData joinTable, TableMetaData innerTable) {

        String typeClassName = ReflectionUtil.getFieldGenericType(field)[0].getTypeName();
        Class<?> fieldClass = ReflectionUtil.newClass(typeClassName);

        if (field.isAnnotationPresent(Cascade.class)) {

            CascadeType[] cascadeTypes = field.getAnnotation(Cascade.class).value();
           if (isContains(cascadeTypes, CascadeType.SAVE)) {

               String tableName1 = fieldClass.getAnnotation(Table.class).name();
               innerTable.setTableName(tableName1);

               assocMappedVal.forEach(o -> smthblabla(o, innerTable));
           }

        }

    }


    private void manyToManyHandler(Collection assocMappedVal, Field field, TableMetaData joinTable) {

        ManyToMany annotation = field.getAnnotation(ManyToMany.class);
        String mappedByField = annotation.mappedBy();

        if (mappedByField.length() > 0) {

            joinTable.setMappedByFieldName(mappedByField);

            assocMappedVal.forEach(o -> findJoinTableByMappedByValue(o, joinTable));

        } else {

            joinTable.setMappedByField(field);

            assocMappedVal.forEach(o -> findJoinTableByFieldName(o, joinTable));

        }


    }


    private void findJoinTableByFieldName(Object object, TableMetaData tableMetaData) {

        Class<?> objClass = object.getClass();

        Field[] declaredFields = objClass.getDeclaredFields();

        for (Field field : declaredFields) {

            if (field.isAnnotationPresent(Id.class)) {

                // TODO id - long change collection
                String id = String.valueOf(ReflectionUtil.invokeGetter(object, field.getName()));
                tableMetaData.getColumnValues().add(id);
            }

            if (field.isAnnotationPresent(ManyToMany.class)) {

                String mappedFieldName = field.getAnnotation(ManyToMany.class).mappedBy();
                String fieldName = tableMetaData.getMappedByField().getName();

                if (mappedFieldName.equals(fieldName)) {

                    fillColumnNamesJT(field, tableMetaData.getColumnNames());

                }
            }

        }
    }


    private void findJoinTableByMappedByValue(Object object, TableMetaData tableMetaData) {


        Class<?> objClass = object.getClass();

        Field[] declaredFields = objClass.getDeclaredFields();


        for (Field field : declaredFields) {

            if (field.isAnnotationPresent(Id.class)) {


                // TODO id - long change collection
                String id = String.valueOf(ReflectionUtil.invokeGetter(object, field.getName()));

                tableMetaData.getColumnValues().add(id);
            }
            if (field.getName().equals(tableMetaData.getMappedByFieldName())) {

                String tableName = field.getAnnotation(JoinTable.class).name();
                tableMetaData.setTableName(tableName);

                fillColumnNamesJT(field, tableMetaData.getColumnNames());

            }

        }
    }

    private void smthblabla(Object object, TableMetaData tableMetaData) {
        StringBuilder columnValues = new StringBuilder("(");

        Class<?> objClass = object.getClass();

        Field[] declaredFields = objClass.getDeclaredFields();

        List<String> columnNames = tableMetaData.getColumnNames();

        for (Field field : declaredFields) {

            if (field.isAnnotationPresent(Column.class)) {

                String columnName = field.getAnnotation(Column.class).name();

                String columnValue = wrap(String.valueOf(ReflectionUtil.invokeGetter(object, field.getName())));
                columnValues.append(columnValue).append(",");

                if (!columnNames.contains(columnName)) {
                    columnNames.add(columnName);
                }

            }
        }


        columnValues.setLength(columnValues.length() - 1);
        columnValues.append(")");
        tableMetaData.getColumnValues().add(columnValues.toString());

    }

    private String generateSoloInsertQuery(TableMetaData tableMetaData) {
        String columns = String.join(", ", tableMetaData.getColumnNames());
        String columnValues = String.join(", ", tableMetaData.getColumnValues());

        return new StringBuilder("INSERT INTO ")
                .append(tableMetaData.getTableName())
                .append(" (").append(columns)
                .append(") VALUES (")
                .append(columnValues).append(")")
                .append(";").toString();

    }


    private String generateComplexInsertQuery(TableMetaData tableMetaData) {
        String columns = String.join(", ", tableMetaData.getColumnNames());
        String columnValues = String.join(", ", tableMetaData.getColumnValues());

        return new StringBuilder("INSERT INTO ")
                .append(tableMetaData.getTableName())
                .append(" (").append(columns)
                .append(") VALUES ")
                .append(columnValues)
                .append(";").toString();

    }


    private void fillColumnNamesJT(Field field, List<String> columns) {
        StringBuilder columnNames = null;
        JoinTable jtAnnotation = field.getAnnotation(JoinTable.class);


//        joinTableName = jtAnnotation.name();
        String joinColumn = jtAnnotation.joinColumns()[0].name();
        String inverseJoinColumn = jtAnnotation.inverseJoinColumns()[0].name();

        if (!columns.contains(inverseJoinColumn)) {
            columns.add(inverseJoinColumn);

        }
        if (!columns.contains(joinColumn)) {

            columns.add(joinColumn);

        }


    }


    private String wrap(String value) {
        return "\'" + value + "\'";
    }

    private boolean isContains(Object[] mas, Object checkVal) {
        for (Object o : mas) {

            if (o.equals(checkVal)) {
                return true;
            }
        }

        return false;

    }
}
