package by.ititon.orm.query;

import by.ititon.orm.MetaCache;
import by.ititon.orm.annotation.*;
import by.ititon.orm.metadata.EntityMetaData;
import by.ititon.orm.metadata.FieldMetaData;

import java.util.*;

public class QueryBuilder {


    private static final Map<Class<?>, String> selectQueriesCache = new HashMap<>();


    public static String buildManyToManyQuery(Class<?> mainTable, Class<?> innerTable) {

        if (selectQueriesCache.containsKey(mainTable)) {
            return selectQueriesCache.get(mainTable);
        }

        List<String> columns = new ArrayList<>();

        EntityMetaData inner = null;
        String mappedBy = null;
        String joinTableName = null;
        String joinColumn = null;
        String inverseJoinColumn = null;


        EntityMetaData main = MetaCache.getEntityMeta(mainTable);
        List<FieldMetaData> mainFieldMetaData = main.getFieldMetaData();


        for (FieldMetaData field : mainFieldMetaData) {

            if (field.getAnnotations().containsKey(ManyToMany.class)) {
                ManyToMany manyToMany = (ManyToMany) field.getAnnotations().get(ManyToMany.class);
                mappedBy = manyToMany.mappedBy();

            }

            if (!Objects.equals(field.getColumnName(), null)) {

                columns.add(field.getColumnName());
            }

        }

        inner = MetaCache.getEntityMeta(innerTable);
        List<FieldMetaData> innerFieldMetaData = inner.getFieldMetaData();


        for (FieldMetaData field : innerFieldMetaData) {

            if (field.getField().getName().equals(mappedBy)) {
                JoinTable joinTable = (JoinTable) field.getAnnotations().get(JoinTable.class);
                joinTableName = joinTable.name();
                joinColumn = joinTableName + "." + joinTable.joinColumns()[0].name();
                inverseJoinColumn = joinTableName + "." + joinTable.inverseJoinColumns()[0].name();
            }

            if (!Objects.equals(field.getColumnName(), null)) {

                columns.add(field.getColumnName());
            }
        }


        String allColumns = String.join(", ", columns);

        StringBuilder stringBuilder = new StringBuilder("SELECT ").append(allColumns)
                .append(" FROM ").append(main.getTableName())
                .append(" LEFT JOIN ").append(joinTableName)
                .append(" ON ").append(inverseJoinColumn).append(" = ").append(main.getIdColumnName())
                .append(" LEFT JOIN ").append(inner.getTableName())
                .append(" ON ").append(inner.getIdColumnName()).append(" = ").append(joinColumn);


        selectQueriesCache.put(mainTable, stringBuilder.toString());

        return stringBuilder.toString();

    }



    public static String buildOneToOneQuery(Class<?> mainTable, Class<?> innerTable) {

//        if (selectQueriesCache.containsKey(mainTable)) {
//            return selectQueriesCache.get(mainTable);
//        }

        List<String> columns = new ArrayList<>();

        EntityMetaData inner = null;
        String mappedBy = null;



        EntityMetaData main = MetaCache.getEntityMeta(mainTable);
        List<FieldMetaData> mainFieldMetaData = main.getFieldMetaData();


        for (FieldMetaData field : mainFieldMetaData) {

            if (field.getAnnotations().containsKey(OneToOne.class)) {
                OneToOne oneToOne = (OneToOne) field.getAnnotations().get(OneToOne.class);
                mappedBy = oneToOne.mappedBy();

            }

            if (!Objects.equals(field.getColumnName(), null)) {

                columns.add(field.getColumnName());
            }

        }

        String joinColumnName = null;

        inner = MetaCache.getEntityMeta(innerTable);
        List<FieldMetaData> innerFieldMetaData = inner.getFieldMetaData();


        for (FieldMetaData field : innerFieldMetaData) {

            if (field.getField().getName().equals(mappedBy)) {
                JoinColumn joinColumn = (JoinColumn) field.getAnnotations().get(JoinColumn.class);
                joinColumnName = inner.getTableName() + "." + joinColumn.name();
            }

            if (!Objects.equals(field.getColumnName(), null)) {

                columns.add(field.getColumnName());
            }
        }


        String allColumns = String.join(", ", columns);

        StringBuilder stringBuilder = new StringBuilder("SELECT ").append(allColumns)
                .append(" FROM ").append(main.getTableName())
                .append(" LEFT JOIN ").append(inner.getTableName())
                .append(" ON ").append(joinColumnName).append(" = ").append(main.getIdColumnName());



//        selectQueriesCache.put(mainTable, stringBuilder.toString());

        return stringBuilder.toString();

    }



    public static String buildManyToManyInsertQuery(Object main, EntityMetaData mainMeta, Object inner, EntityMetaData innerMeta) {


        ///
        return null;
    }





//    public static String buildSimpleQuery(Class<?> mainTable) {
//
//        if (selectQueriesCache.containsKey(mainTable)) {
//            return selectQueriesCache.get(mainTable);
//        }
//
//
//        List<String> columns = new ArrayList<>();
//
//
//        EntityMetaData main = MetaCache.getEntityMeta(mainTable);
//        List<FieldMetaData> mainFieldMetaData = main.getFieldMetaData();
//
//        for (FieldMetaData field : mainFieldMetaData) {
//
//            if (!Objects.equals(field.getColumnName(), null)) {
//
//                columns.add(field.getColumnName());
//            }
//
//        }
//
//
//        String allColumns = String.join(", ", columns);
//
//        StringBuilder stringBuilder = new StringBuilder("SELECT ").append(allColumns)
//                .append(" FROM ").append(main.getTableName());
//
//        selectQueriesCache.put(mainTable, stringBuilder.toString());
//
//        return stringBuilder.toString();
//
//    }





}
