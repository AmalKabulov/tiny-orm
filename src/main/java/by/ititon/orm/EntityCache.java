package by.ititon.orm;

import by.ititon.orm.annotation.Id;
import by.ititon.orm.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EntityCache {

    private Map<Class<?>, Map<Object, Object>> entitiesCache = new HashMap<>();


    public void addToCache(Object object, Object objectId) {
        put(object, objectId);
    }


    public Object findInCache(Object object, Object objectId) {
        return get(object, objectId);

    }


    public void addToCache(Object obj) {

        Object id = getId(obj);

        if (id != null) {
            put(obj, id);
        }
    }

    public Object findInCache(Object object) {

        Object id = getId(object);

        if (id != null) {
            return get(object, id);
        }

        return null;

    }


    private Object getId(Object object) {

        Class<?> objClass = object.getClass();

        Field[] declaredFields = objClass.getDeclaredFields();

        for (Field field : declaredFields) {

            if (field.isAnnotationPresent(Id.class)) {
                return ReflectionUtil.invokeGetter(object, field.getName());
            }
        }
        return null;

    }

    private void put(Object object, Object objectId) {

        Class<?> objClass = object.getClass();

        if (entitiesCache.containsKey(objClass)) {
            entitiesCache.get(objClass).put(objectId, object);

        } else {
            HashMap<Object, Object> classesCache = new HashMap<>();
            classesCache.put(objectId, object);

            entitiesCache.put(objClass, classesCache);
        }
    }


    private Object get(Object object, Object objectId) {

        Class<?> objClass = object.getClass();

        if (entitiesCache.containsKey(objClass)) {
            return entitiesCache.get(objClass).get(objectId);
        }

        return null;
    }

}
