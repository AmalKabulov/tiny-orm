package by.ititon.orm.action;

import by.ititon.orm.annotation.*;
import by.ititon.orm.metadata.JoinTableMetaData;
import by.ititon.orm.metadata.TableMetaData;
import by.ititon.orm.testEntity.TestEntity;
import by.ititon.orm.testEntity.TestEntity2;
import by.ititon.orm.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntitySelectAction {

    private TableMetaData tableMetaData = new TableMetaData();


    public static void main(String[] args) {
        EntitySelectAction entitySelectAction = new EntitySelectAction();
        entitySelectAction.genericFindAllStatementQueryCreator(TestEntity2.class);
        System.out.println();
        entitySelectAction.genericFindAllStatementQueryCreator(TestEntity.class);
    }


    public void genericFindAllStatementQueryCreator(final Class<?> clazz) {

        TableMetaData mainTable = new TableMetaData();
        TableMetaData innerTable = new TableMetaData();
        JoinTableMetaData joinTable = new JoinTableMetaData();

        Field[] declaredFields = clazz.getDeclaredFields();

        String tableName = clazz.getAnnotation(Table.class).name();
        mainTable.setTableName(tableName);

        for (Field field : declaredFields) {
            //TODO

            fillTheTableByFieldValue(mainTable, field);

            if (field.isAnnotationPresent(ManyToMany.class)) {

                manyToManyHandler(innerTable, joinTable, field, mainTable.getIdColumnName());
            }

            if (field.isAnnotationPresent(OneToMany.class)) {

            }
        }


        System.out.println(buildFindAllQuery(mainTable, innerTable, joinTable));

    }





    private void manyToManyHandler(TableMetaData innerTable, JoinTableMetaData joinTable, Field field, String id) {

//        String result = null;

        ManyToMany annotation = field.getAnnotation(ManyToMany.class);
        if (annotation.fetch().equals(FetchType.LAZY)) {
            return;
        }


        String typeClassName = ReflectionUtil.getFieldGenericType(field)[0].getTypeName();


        Class<?> innerTableClass = ReflectionUtil.newClass(typeClassName);
        innerTable.setMappedTableClass(innerTableClass);
        String mappedByValue = annotation.mappedBy();

        if (mappedByValue.length() > 0) {

            innerTable.setMappedByValue(mappedByValue);

            createJoinQueryByITMappedByValue(innerTable, joinTable);

        } else {

            innerTable.setMappedByField(field);

            createJoinQueryByITFieldName(innerTable, joinTable);
        }


    }

    private void createJoinQueryByITFieldName(TableMetaData innerTable, JoinTableMetaData joinTable) {

        Class<?> mappedTableClass = innerTable.getMappedTableClass();
        Field[] declaredFields = mappedTableClass.getDeclaredFields();
        String tableName = mappedTableClass.getAnnotation(Table.class).name();

        innerTable.setTableName(tableName);

        for (Field field : declaredFields) {

            fillTheTableByFieldValue(innerTable, field);

            if (field.isAnnotationPresent(ManyToMany.class)) {

                String mappedFieldName = field.getAnnotation(ManyToMany.class).mappedBy();

                Field mappedByField = innerTable.getMappedByField();

                if (mappedFieldName.equals(mappedByField.getName())) {

                    fillJoinTableByFieldValue(joinTable, mappedByField);

                }
            }
        }

    }


    private void createJoinQueryByITMappedByValue(TableMetaData innerTable, JoinTableMetaData joinTable) {

        Class<?> mappedTableClass = innerTable.getMappedTableClass();

        Field[] declaredFields = mappedTableClass.getDeclaredFields();

        String tableName = mappedTableClass.getAnnotation(Table.class).name();

        innerTable.setTableName(tableName);


        for (Field field : declaredFields) {

            fillTheTableByFieldValue(innerTable, field);


            if (field.getName().equals(innerTable.getMappedByValue())) {

                //TODO may be nullpointer

                fillJoinTableByFieldValue(joinTable, field);

            }
        }

    }


    private void fillTheTableByFieldValue(TableMetaData table, Field field) {
        String tableName = table.getTableName();

        if (field.isAnnotationPresent(Column.class)) {
            String columnName = field.getAnnotation(Column.class).name();

            if (field.isAnnotationPresent(Id.class)) {
                table.setIdColumnName(tableName + "." + columnName);
            }

            table.getColumnNames().add(tableName + "." + columnName);
        }
    }

    private void fillJoinTableByFieldValue(JoinTableMetaData joinTable, Field field) {
        JoinTable table = field.getAnnotation(JoinTable.class);
        String tableName = table.name();

        joinTable.setTableName(tableName);
        joinTable.setJoinColumn(tableName + "." + table.joinColumns()[0].name());
        joinTable.setInverseJoinColumn(tableName + "." + table.inverseJoinColumns()[0].name());

    }


    private String buildFindAllQuery(TableMetaData mainTable, TableMetaData innerTable, JoinTableMetaData joinTable) {

        List<String> columns = collectColumns(mainTable, innerTable);


        String columnNames = String.join(", ", columns);
        StringBuilder query = new StringBuilder("SELECT ").append(columnNames)
                .append(" FROM ").append(mainTable.getTableName());
        if (!Objects.equals(joinTable.getTableName(), null)) {

            String a = null;
            String b = null;


            if (!Objects.equals(innerTable.getMappedByValue(), null)) {
                a = joinTable.getInverseJoinColumn();
                b = joinTable.getJoinColumn();

            } else {

                a = joinTable.getJoinColumn();
                b = joinTable.getInverseJoinColumn();

            }

            query.append(" LEFT JOIN ").append(joinTable.getTableName())
                    .append(" ON ").append(a).append(" = ").append(mainTable.getIdColumnName())
                    .append(" LEFT JOIN ").append(innerTable.getTableName())
                    .append(" ON ").append(innerTable.getIdColumnName()).append(" = ").append(b);
        }
        return query.append(";").toString();
    }

    private List<String> collectColumns(TableMetaData mainTable, TableMetaData innerTable) {

        List<String> columns = new ArrayList<>();
        columns.addAll(mainTable.getColumnNames());

        if (!Objects.equals(innerTable.getTableName(), null)) {
            columns.addAll(innerTable.getColumnNames());
        }


        return columns;

    }


}
