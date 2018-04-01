package by.ititon.orm.action;

import by.ititon.orm.annotation.Column;
import by.ititon.orm.annotation.Id;
import by.ititon.orm.annotation.Table;
import by.ititon.orm.testEntity.TestEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenericEntityAction {

    public static void main(String[] args) {

        TestEntity testEntity = new TestEntity(1L, "AMAL", "KABULOV");
        GenericEntityAction genericEntityAction = new GenericEntityAction();
//        genericEntityAction.genericInsertUpdateQueryStatementCreator(testEntity);

        genericEntityAction.genericFindAllStatementQueryCreator(testEntity);
        genericEntityAction.genericFindByIdStatementQueryCreator(1, testEntity.getClass());
    }


    public void genericFindByIdStatementQueryCreator(final Object id, Class<?> clazz) {
        List<String> columns = new ArrayList<>();

        Field[] declaredFields = clazz.getDeclaredFields();
        String tableName = clazz.getAnnotation(Table.class).name();

        String idColumnName = null;

        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Id.class)) {
                idColumnName = field.getAnnotation(Column.class).name();
            }
            if (field.isAnnotationPresent(Column.class)) {
                columns.add(field.getAnnotation(Column.class).name());
            }
        }

        System.out.println(buildFindByIdQuery(tableName, columns, idColumnName, id));


    }


    public void genericFindAllStatementQueryCreator(final Object object) {
        List<String> columns = new ArrayList<>();

        Class<?> objClass = object.getClass();

        Field[] declaredFields = objClass.getDeclaredFields();

        String tableName = objClass.getAnnotation(Table.class).name();

        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Column.class)) {
                columns.add(field.getAnnotation(Column.class).name());
            }

        }



        System.out.println(buildFindAllQuery(tableName, columns));
    }


//    public void genericInsertUpdateQueryStatementCreator(final Object object) {
//
//        Map<String, String> columnNameValues = new LinkedHashMap<>();
//
//        Class<?> objClass = object.getClass();
//
//        Field[] declaredFields = objClass.getDeclaredFields();
//
//        String tableName = objClass.getAnnotation(Table.class).name();
//
//
//        for (Field field : declaredFields) {
//
//
//            if (field.isAnnotationPresent(Column.class)) {
//
//                Method method = ReflectionUtil.getMethod(objClass, "get" + capitalizeFirstLetter(field.getName()));
//
//                String columnName = field.getAnnotation(Column.class).name();
//                String columnValue = wrap(String.valueOf(ReflectionUtil.invokeMethod(object, method)));
//
//                columnNameValues.put(columnName, columnValue);
//            }
//
//        }
//
//
//        System.out.println(buildInsertQuery(tableName, columnNameValues));
//
//        System.out.println(buildUpdateQuery(tableName, columnNameValues));
//    }


    private String buildFindByIdQuery(String tableName, List<String> columns, String idColumn, Object idVal) {
        String columnNames = String.join(", ", columns);
        return new StringBuilder("SELECT ")
                .append(columnNames)
                .append(" FROM ")
                .append(tableName)
                .append(" WHERE ")
                .append(idColumn)
                .append(" = ")
                .append(idVal).toString();
    }


    private String buildFindAllQuery(String tableName, List<String> columns) {
        String columnNames = String.join(", ", columns);

        return new StringBuilder("SELECT ").append(columnNames).append(" FROM ").append(tableName).append(";").toString();
    }


    private String buildUpdateQuery(String tableName, Map<String, String> columnNameValues) {
        List<String> result = columnNameValues.entrySet().stream()
                .map(entry ->
                        new StringBuilder(entry.getKey())
                                .append(" = ")
                                .append(entry.getValue())
                                .toString())
                .collect(Collectors.toList());

        String columnsValues = String.join(", ", result);


        return new StringBuilder("UPDATE ")
                .append(tableName)
                .append(" SET ").append(columnsValues)
                .append(";")
                .toString();
    }

    private String buildInsertQuery(String tableName, Map<String, String> columnNameValues) {

        String columnNames = String.join(", ", columnNameValues.keySet());
        String columnValues = String.join(", ", columnNameValues.values());

        return new StringBuilder("INSERT INTO ")
                .append(tableName)
                .append(" (").append(columnNames)
                .append(") VALUES (")
                .append(columnValues)
                .append(");").toString();
    }


    private String wrap(String value) {
        return "\'" + value + "\'";
    }

    private String capitalizeFirstLetter(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

}
