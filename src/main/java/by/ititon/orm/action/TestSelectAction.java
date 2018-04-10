package by.ititon.orm.action;

import by.ititon.orm.MetaCache;
import by.ititon.orm.annotation.*;
import by.ititon.orm.exception.MappingException;
import by.ititon.orm.exception.MetaInfoNotFoundException;
import by.ititon.orm.metadata.EntityMetaData;
import by.ititon.orm.metadata.FieldMetaData;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestSelectAction {

    public void genericFindAllStatementQueryCreator(final Class<?> clazz) {

        EntityMetaData mainEntityMeta = MetaCache.getEntityMeta(clazz);
        if (mainEntityMeta == null) {
            throw new MetaInfoNotFoundException("ENTITY " + clazz + " NOT FOUND");
        }

        List<FieldMetaData> fieldMetaData = mainEntityMeta.getFieldMetaData();
        for (FieldMetaData field : fieldMetaData) {
            Map<Class<? extends Annotation>, Annotation> fieldAnnotations = field.getAnnotations();

            if (fieldAnnotations.containsKey(ManyToMany.class)) {
                System.out.println("MANY TO MANY : ");
                manyToManyHandler(mainEntityMeta, field);
                System.out.println();
            }

            if (fieldAnnotations.containsKey(OneToMany.class)) {
                System.out.println("One TO MANY : ");
                oneToManyHandler(mainEntityMeta, field);
                System.out.println();
            }

            if (fieldAnnotations.containsKey(ManyToOne.class)) {
                System.out.println("MANY TO ONE : ");
                manyToOneHandler(mainEntityMeta, field);
                System.out.println();
            }

            if (fieldAnnotations.containsKey(OneToOne.class)) {
//                System.out.println("ONE TO ONE : ");
//                oneToOneHandler(mainEntityMeta, field);
//                System.out.println();
            }


        }
    }


    private void manyToManyHandler(EntityMetaData mainMeta, FieldMetaData fieldMetaData) {

        ManyToMany manyToMany = (ManyToMany) fieldMetaData.getAnnotations().get(ManyToMany.class);
        String mappedByValue = manyToMany.mappedBy();

        String mainTableName = mainMeta.getTableName();
        List<FieldMetaData> mainFieldsMeta = mainMeta.getFieldMetaData();
        List<String> columns = getColumns(mainFieldsMeta);
        StringBuilder query = buildSimpleQuery(mainTableName, columns);


        if (manyToMany.fetch().equals(FetchType.EAGER)) {
            query = manyToManyQueryBuilder(mainMeta, columns, fieldMetaData, mappedByValue);
        }

        System.out.println(query.append(";"));
    }


    private StringBuilder manyToManyQueryBuilder(EntityMetaData mainMeta,
                                                 List<String> mainMetaColumns,
                                                 FieldMetaData mainFieldMeta,
                                                 String mappedByValue) {

        JoinTable joinTable;
        boolean reverse = false;
        String mainTableName = mainMeta.getTableName();

        Class<?> innerTableClass = mainFieldMeta.getFieldGenericType();
        EntityMetaData innerMeta = MetaCache.getEntityMeta(innerTableClass);
        List<FieldMetaData> innerFieldsMeta = innerMeta.getFieldMetaData();


        mainMetaColumns.addAll(getColumns(innerFieldsMeta));

        StringBuilder query = buildSimpleQuery(mainTableName, mainMetaColumns);

        if (mappedByValue.length() > 0) {
            joinTable = findJoinTableByMappedByValue(mappedByValue, innerFieldsMeta);
        } else {
            joinTable = findJoinTableByFieldMeta(mainFieldMeta, innerFieldsMeta);
            reverse = true;
        }


        return appendJoinManyToManyQuery(query, joinTable, mainMeta, innerMeta, reverse);

    }


    private JoinTable findJoinTableByMappedByValue(String mappedByValue, List<FieldMetaData> innerFieldMeta) {
        for (FieldMetaData innerField : innerFieldMeta) {
            if (innerField.getFieldName().equals(mappedByValue)) {
                return (JoinTable) innerField.getAnnotations().get(JoinTable.class);
            }
        }

        throw new MappingException("MAPPED FIELD " + mappedByValue + " NOT FOUND");
    }



    private JoinTable findJoinTableByFieldMeta(FieldMetaData mainFieldMeta, List<FieldMetaData> innerFieldsMeta) {

        for (FieldMetaData innerField : innerFieldsMeta) {
            Map<Class<? extends Annotation>, Annotation> innerAnnotations = innerField.getAnnotations();
            if (innerAnnotations.containsKey(ManyToMany.class)) {
                ManyToMany manyToManyInner = (ManyToMany) innerAnnotations.get(ManyToMany.class);
                String mainFieldName = mainFieldMeta.getFieldName();
                if (Objects.equals(manyToManyInner.mappedBy(), mainFieldName)) {
                    return (JoinTable) mainFieldMeta.getAnnotations().get(JoinTable.class);
                }
            }
        }

        throw new MappingException("MAPPED FIELD " + mainFieldMeta.getFieldName() + " NOT FOUND");
    }


    private StringBuilder appendJoinManyToManyQuery(StringBuilder existQuery,
                                                    JoinTable joinTable,
                                                    EntityMetaData mainMeta,
                                                    EntityMetaData innerMeta,
                                                    boolean reverse) {

        String joinTableName = joinTable.name();
        String joinColumn = joinTableName + "." + joinTable.joinColumns()[0].name();
        String inverseJoinColumn = joinTableName + "." + joinTable.inverseJoinColumns()[0].name();

        if (reverse) {
            String temp = joinColumn;
            joinColumn = inverseJoinColumn;
            inverseJoinColumn = temp;
        }

        return existQuery.append("left join ").append(joinTableName).append("\n")
                .append("on ").append(inverseJoinColumn).append(" = ").append(mainMeta.getIdColumnName()).append("\n")
                .append("left join ").append(innerMeta.getTableName()).append("\n")
                .append("on ").append(innerMeta.getIdColumnName()).append(" = ").append(joinColumn);

    }


    private void oneToManyHandler(EntityMetaData mainMeta, FieldMetaData fieldMetaData) {
        OneToMany oneToMany = (OneToMany) fieldMetaData.getAnnotations().get(OneToMany.class);
        String mainTableName = mainMeta.getTableName();
        List<String> columns = getColumns(mainMeta.getFieldMetaData());
        StringBuilder query = buildSimpleQuery(mainTableName, columns);
        if (oneToMany.fetch().equals(FetchType.EAGER)) {
            String mappedBy = oneToMany.mappedBy();
            query = oneToManyQueryBuilder(mainMeta, columns, fieldMetaData, mappedBy);
        }
        System.out.println(query.append(";"));

    }

    private StringBuilder oneToManyQueryBuilder(EntityMetaData mainMeta,
                                                List<String> mainColumns,
                                                FieldMetaData mainFieldMeta,
                                                String mappedByValue) {

        String mainTableName = mainMeta.getTableName();
        String mainIdColumn = mainMeta.getIdColumnName();
        Class<?> innerTableClass = mainFieldMeta.getFieldGenericType();

        EntityMetaData innerMeta = MetaCache.getEntityMeta(innerTableClass);
        String innerTableName = innerMeta.getTableName();
        List<FieldMetaData> innerFieldMetaData = innerMeta.getFieldMetaData();
        List<String> innerColumns = getColumns(innerFieldMetaData);

        String joinColumnName = getJoinColumnValueByMappedBy(mappedByValue, innerFieldMetaData);
        mainColumns.addAll(innerColumns);

        StringBuilder query = buildSimpleQuery(mainTableName, mainColumns);

        return appendJoinOneToManyQuery(query, innerTableName, joinColumnName, mainIdColumn);

    }


    private StringBuilder appendJoinOneToManyQuery(StringBuilder existQuery,
                                                   String innerTableName,
                                                   String joinColumn,
                                                   String mainIdColumn) {
        return existQuery.append("left join ").append(innerTableName)
                .append(" on ").append(innerTableName).append(".")
                .append(joinColumn).append(" = ").append(mainIdColumn);
    }


    private String getJoinColumnValueByMappedBy(String mappedBy, List<FieldMetaData> innerFieldMeta) {
        for (FieldMetaData field : innerFieldMeta) {
            if (field.getFieldName().equals(mappedBy)) {
                JoinColumn joinColumn = (JoinColumn) field.getAnnotations().get(JoinColumn.class);
                return joinColumn.name();
            }
        }

        throw new MappingException("MAPPED FIELD " + mappedBy + " NOT FOUND");

    }


    private void manyToOneHandler(EntityMetaData mainMeta, FieldMetaData mainField) {

        ManyToOne manyToOne = (ManyToOne) mainField.getAnnotations().get(ManyToOne.class);
        String mainTableName = mainMeta.getTableName();
        List<String> columns = getColumns(mainMeta.getFieldMetaData());

        if (manyToOne.fetch().equals(FetchType.LAZY)) {
            System.out.println(buildSimpleQuery(mainTableName, columns).toString());
            return;
        }

        System.out.println(manyToOneQueryBuilder(mainMeta, mainField, columns));

    }

    private StringBuilder manyToOneQueryBuilder(EntityMetaData mainMeta, FieldMetaData mainField, List<String> columns) {

        String mainTableName = mainMeta.getTableName();
        JoinColumn joinColumn = (JoinColumn) mainField.getAnnotations().get(JoinColumn.class);

        String joinColumnName = mainTableName + "." + joinColumn.name();
        Class<?> innerTableClass = mainField.getFieldType();
        String mainFieldName = mainField.getFieldName();

        EntityMetaData innerMeta = MetaCache.getEntityMeta(innerTableClass);
        String innerTableName = innerMeta.getTableName();
        String innerColumnId = innerMeta.getIdColumnName();
        List<FieldMetaData> innerFields = innerMeta.getFieldMetaData();

        if (isMappingCorrect(mainFieldName, innerFields)) {
            List<String> innerColumns = getColumns(innerFields);
            columns.addAll(innerColumns);
            StringBuilder query = buildSimpleQuery(mainTableName, columns);

            return appendJoinJManyToOneQuery(query, joinColumnName, innerTableName, innerColumnId);
        }


        throw new MappingException("MAPPED FIELD " + mainFieldName + " NOT FOUND");


    }


    private StringBuilder appendJoinJManyToOneQuery(StringBuilder existQuery, String joinColumnName, String innerTableName, String innerColumnId) {

        return existQuery.append("left join ").append(innerTableName)
                .append(" on ")
                .append(innerColumnId)
                .append(" = ")
                .append(joinColumnName);

    }


    private boolean isMappingCorrect(String mainFieldName, List<FieldMetaData> innerFields) {
        for (FieldMetaData field : innerFields) {
            if (field.getAnnotations().containsKey(OneToMany.class)) {
                OneToMany oneToMany = (OneToMany) field.getAnnotations().get(OneToMany.class);
                if (oneToMany.mappedBy().equals(mainFieldName)) {
                    return true;
                }
            }
        }

        return false;

    }


//    private void oneToOneHandler(EntityMetaData mainMeta, FieldMetaData fieldMetaData) {
//
//        OneToOne oneToOne = (OneToOne) fieldMetaData.getAnnotations().get(OneToOne.class);
//
//        if (oneToOne.fetch().equals(FetchType.LAZY)) {
////            System.out.println(QueryBuilder.buildSimpleQuery(main));
//            return;
//        }
//
//        String mappedByValue = oneToOne.mappedBy();
//
//        Class<?> innerTableClass = fieldMetaData.getFieldType();
//
//
//        if (mappedByValue.length() > 0) {
//
//            System.out.println(QueryBuilder.buildOneToOneQuery(main, innerTableClass));
//
//        } else {
//
//            System.out.println(QueryBuilder.buildOneToOneQuery(innerTableClass, main));
//
//
//        }
//
//
//    }


    private StringBuilder buildSimpleQuery(String mainTableName, List<String> columns) {
        String allColumns = String.join(", ", columns);
        return new StringBuilder().append("select").append("\n").append(allColumns).append("\n").append("from").append("\n")
                .append(mainTableName).append("\n");

    }

    private List<String> getColumns(List<FieldMetaData> fieldMetaData) {
        return fieldMetaData
                .stream()
                .filter(field -> !Objects.equals(field.getColumnName(), null))
                .map(FieldMetaData::getColumnName)
                .collect(Collectors.toList());

    }
}
