package by.ititon.orm.action;

import by.ititon.orm.MetaCache;
import by.ititon.orm.annotation.*;
import by.ititon.orm.exception.MetaInfoNotFoundException;
import by.ititon.orm.metadata.EntityMetaData;
import by.ititon.orm.metadata.FieldMetaData;
import by.ititon.orm.util.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TestInsertAction {






    public void genericInsertQuery(final Object object) {
        Class<?> objClass = object.getClass();

        EntityMetaData main = MetaCache.getEntityMeta(objClass);

        if (Objects.equals(main, null)) {
            throw new MetaInfoNotFoundException("ENTITY WITH NAME " + object + " NOT FOUND");
        }


        List<FieldMetaData> mainEntityFieldMeta = main.getFieldMetaData();


        for (FieldMetaData field : mainEntityFieldMeta) {
            Map<Class<? extends Annotation>, Annotation> fieldAnnotations = field.getAnnotations();

            if (fieldAnnotations.containsKey(ManyToMany.class)) {


            }

            if (fieldAnnotations.containsKey(OneToMany.class)) {


            }

            if (fieldAnnotations.containsKey(ManyToOne.class)) {

            }

            if (fieldAnnotations.containsKey(OneToOne.class)) {

            }

        }
    }


    private void manyToManyHandler(Object object, EntityMetaData main, FieldMetaData fieldMetaData) {

        ManyToMany manyToMany = (ManyToMany) fieldMetaData.getAnnotations().get(ManyToMany.class);

        FetchType fetch = manyToMany.fetch();

        if (fetch.equals(FetchType.LAZY)) {
            return;
        }


        String mappedByValue = manyToMany.mappedBy();



        Class<?> innerTableClass =fieldMetaData.getFieldGenericType();

        Object innerObject = ReflectionUtil.newInstance(innerTableClass.getName());

        if (mappedByValue.length() > 0) {


        } else {


        }

    }
}
