package by.ititon.orm.action;

import by.ititon.orm.MetaCache;
import by.ititon.orm.annotation.*;
import by.ititon.orm.exception.MappingException;
import by.ititon.orm.metadata.EntityMetaData;
import by.ititon.orm.metadata.FieldMetaData;
import by.ititon.orm.query.QueryBuilder;
import by.ititon.orm.util.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestSelectAction {

    public void genericFindAllStatementQueryCreator(final Class<?> clazz) {

        EntityMetaData mainEntityMeta = MetaCache.getEntityMeta(clazz);

        List<FieldMetaData> fieldMetaData = mainEntityMeta.getFieldMetaData();

        for (FieldMetaData field : fieldMetaData) {
            Map<Class<? extends Annotation>, Annotation> fieldAnnotations = field.getAnnotations();

            if (fieldAnnotations.containsKey(ManyToMany.class)) {

                System.out.println("MANY TO MANY : ");

                manyToManyHandler(clazz, field);
                System.out.println();
            }

            if (fieldAnnotations.containsKey(OneToMany.class)) {

                System.out.println("One TO MANY : ");
                oneToManyHandler(clazz, field);
                System.out.println();
            }

            if (fieldAnnotations.containsKey(ManyToOne.class)) {
                System.out.println("MANY TO ONE : ");
                manyToOneHandler(clazz, field);
                System.out.println();
            }

            if (fieldAnnotations.containsKey(OneToOne.class)) {

            }


        }
    }


    private void manyToManyHandler(Class<?> main, FieldMetaData fieldMetaData) {

        ManyToMany manyToMany = (ManyToMany) fieldMetaData.getAnnotations().get(ManyToMany.class);

        if (manyToMany.fetch().equals(FetchType.LAZY)) {
//            System.out.println(QueryBuilder.buildSimpleQuery(main));
            return;
        }

        String mappedByValue = manyToMany.mappedBy();

        Field field = fieldMetaData.getField();

        String fieldGenericType = ReflectionUtil.getFieldGenericType(field)[0].getTypeName();

        Class<?> innerTableClass = ReflectionUtil.newClass(fieldGenericType);

//        EntityMetaData innerEntityMeta = MetaCache.getEntityMeta(innerTableClass);

        if (mappedByValue.length() > 0) {

            System.out.println(QueryBuilder.buildManyToManyQuery(main, innerTableClass));

        } else {

            System.out.println(QueryBuilder.buildManyToManyQuery(innerTableClass, main));


        }

    }



    private void oneToManyHandler(Class<?> main, FieldMetaData fieldMetaData) {

        OneToMany oneToMany = (OneToMany) fieldMetaData.getAnnotations().get(OneToMany.class);

        if (oneToMany.fetch().equals(FetchType.LAZY)) {
            return;
        }


        EntityMetaData mainMeta = MetaCache.getEntityMeta(main);

        List<String> columns =  mainMeta.getFieldMetaData()
                .stream()
                .filter(field -> field.getColumnName() != null)
                .map(FieldMetaData::getColumnName)
                .collect(Collectors.toList());



        String mappedBy = oneToMany.mappedBy();

        Class<?> innerTableClass =  ReflectionUtil.classFromFieldGenericType(fieldMetaData.getField());

        EntityMetaData innerMeta = MetaCache.getEntityMeta(innerTableClass);

        List<FieldMetaData> innerFieldMetaData = innerMeta.getFieldMetaData();


        String joinColumnName = null;

        for (FieldMetaData field : innerFieldMetaData) {

            if (field.getField().getName().equals(mappedBy)) {

                JoinColumn joinColumn = (JoinColumn) field.getAnnotations().get(JoinColumn.class);

                joinColumnName = innerMeta.getTableName() + "." +joinColumn.name();

            }

            if (field.getColumnName() != null) {
                columns.add(field.getColumnName());
            }

        }


        String cols = String.join(", ", columns);

        StringBuilder stringBuilder = new StringBuilder("SELECT ");
        stringBuilder.append(cols).append(" FROM ").append(mainMeta.getTableName())
                .append(" LEFT JOIN ").append(innerMeta.getTableName())
                .append(" ON ").append(joinColumnName).append(" = ").append(mainMeta.getIdColumnName());


        System.out.println(stringBuilder.toString());

    }



    private void manyToOneHandler(Class<?> main, FieldMetaData fieldMetaData) {

        ManyToOne manyToOne = (ManyToOne) fieldMetaData.getAnnotations().get(ManyToOne.class);

        JoinColumn joinColumn = (JoinColumn) fieldMetaData.getAnnotations().get(JoinColumn.class);



        if (manyToOne.fetch().equals(FetchType.LAZY)) {
            return;
        }

        EntityMetaData mainMeta = MetaCache.getEntityMeta(main);


        String joinColumnName = mainMeta.getTableName() + "." + joinColumn.name();


        List<String> columns =  mainMeta.getFieldMetaData()
                .stream()
                .filter(field -> field.getColumnName() != null)
                .map(FieldMetaData::getColumnName)
                .collect(Collectors.toList());


        Class<?> innerTableClass =  fieldMetaData.getField().getType();

        EntityMetaData innerMeta = MetaCache.getEntityMeta(innerTableClass);

        List<FieldMetaData> innerFieldMetaData = innerMeta.getFieldMetaData();

        boolean mapped = false;

        for (FieldMetaData field : innerFieldMetaData) {

            if (field.getAnnotations().containsKey(OneToMany.class)) {
                OneToMany oneToMany = (OneToMany) field.getAnnotations().get(OneToMany.class);

                if (oneToMany.mappedBy().equals(fieldMetaData.getField().getName())) {
                    mapped = true;
                }
            }

            if (field.getColumnName() != null) {
                columns.add(field.getColumnName());
            }
        }


        if (!mapped) {
            throw new MappingException("MAPPED FIELD WITH NOT FOUND");
        }


        String cols = String.join(", ", columns);

        StringBuilder stringBuilder = new StringBuilder("SELECT ");
        stringBuilder.append(cols).append(" FROM ").append(mainMeta.getTableName())
                .append(" LEFT JOIN ").append(innerMeta.getTableName())
                .append(" ON ").append(joinColumnName).append(" = ").append(mainMeta.getIdColumnName());


        System.out.println(stringBuilder.toString());

    }
}
