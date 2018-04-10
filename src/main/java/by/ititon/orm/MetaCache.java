package by.ititon.orm;

import by.ititon.orm.metadata.EntityMetaData;
import by.ititon.orm.metadata.FieldMetaData;

import java.util.List;
import java.util.Map;

public class MetaCache {


    private final static Map<Class<?>, EntityMetaData> entityMetaDataCache;

    static {
        entityMetaDataCache = MetaBuilder.buildEntityMeta();

        System.out.println();

        System.out.println();

        System.out.println();

        System.out.println();
    }



    public static EntityMetaData getEntityMeta(Class<?> clazz) {
        return entityMetaDataCache.get(clazz);
    }

    public static List<FieldMetaData> getColumnMetaByEntity(Class<?> clazz) {
        return getEntityMeta(clazz).getFieldMetaData();
    }

    public static FieldMetaData getColumnMetaByFieldName(Class<?> clazz, String fieldName) {

        return getColumnMetaByEntity(clazz)
                .stream()
                .filter(meta -> meta.getFieldName().equals(fieldName))
                .findFirst()
                .orElse(null);
    }




}
