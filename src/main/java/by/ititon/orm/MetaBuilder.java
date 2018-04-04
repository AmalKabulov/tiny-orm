package by.ititon.orm;

import by.ititon.orm.annotation.Column;
import by.ititon.orm.annotation.Entity;
import by.ititon.orm.annotation.Id;
import by.ititon.orm.annotation.Table;
import by.ititon.orm.metadata.FieldMetaData;
import by.ititon.orm.metadata.EntityMetaData;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MetaBuilder {



    public static Map<Class<?>, EntityMetaData> buildEntityMeta() {

        Map<Class<?>, EntityMetaData> entityMeta = new HashMap<>();

        List<Class<?>> entities = AnnotationProcessor.getClassesByAnnotation(Entity.class, "by.ititon.orm.testEntity");

        for (Class<?> clazz : entities) {

            Table tableAnn = clazz.getAnnotation(Table.class);
            EntityMetaData entityMetaData = new EntityMetaData();
            String tableName = tableAnn.name();
            entityMetaData.setTableName(tableName);

            for (Field field : clazz.getDeclaredFields()) {
                Annotation[] declaredAnnotations = field.getDeclaredAnnotations();

                if (declaredAnnotations.length == 0) {
                    break;
                }


                FieldMetaData fieldMetaData = new FieldMetaData();
                fieldMetaData.setField(field);


                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    fieldMetaData.setColumnName(tableName + "." + column.name());
                }

                if (field.isAnnotationPresent(Id.class)) {
                    entityMetaData.setIdColumnName(fieldMetaData.getColumnName());
                }


                for (Annotation annotation : declaredAnnotations) {
                    fieldMetaData.getAnnotations().put(annotation.annotationType(), annotation);
                }


                entityMetaData.getFieldMetaData().add(fieldMetaData);
            }

            entityMeta.put(clazz, entityMetaData);
        }


        entityMeta.forEach((k, v) -> System.out.println("KEY " + k + " VALUE " + v));

        return entityMeta;


    }






}
