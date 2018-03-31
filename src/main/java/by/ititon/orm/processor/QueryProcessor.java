package by.ititon.orm.processor;

import by.ititon.orm.annotation.FetchType;
import by.ititon.orm.annotation.JoinTable;
import by.ititon.orm.annotation.ManyToMany;
import by.ititon.orm.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class QueryProcessor {



    public void findAll(Class<?> clazz) {
        if (clazz != null) {

        }
    }


    public void handleClass(Class<?> clazz) {
        Field[] classFields = clazz.getDeclaredFields();

        for (Field field : classFields) {
                handleField(field);

        }
    }





    public void handleField(Field field) {

        if (field.isAnnotationPresent(ManyToMany.class)) {
            ManyToMany manyToManyAnnotation = field.getAnnotation(ManyToMany.class);

            if (manyToManyAnnotation.fetch().equals(FetchType.EAGER)) {
                String fieldGenericType = ReflectionUtil.getFieldGenericType(field)[0].getTypeName();
                Class<?> fieldGenericClass = ReflectionUtil.newClass(fieldGenericType);


            }
        }
    }


    public void fetchTypeEagerHandler(Field field) {

        if (field.isAnnotationPresent(JoinTable.class)) {
            JoinTable joinTableAnnotation = field.getAnnotation(JoinTable.class);

            String joinTable = joinTableAnnotation.name();
            String joinColumn = joinTableAnnotation.joinColumns()[0].name();
            String inverseJoinColumn = joinTableAnnotation.inverseJoinColumns()[0].name();

        } else {

            String mappedField = field.getAnnotation(ManyToMany.class).mappedBy();
        }

    }



}
