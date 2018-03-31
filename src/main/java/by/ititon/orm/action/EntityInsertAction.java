package by.ititon.orm.action;

import by.ititon.orm.annotation.Column;
import by.ititon.orm.annotation.Table;
import by.ititon.orm.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class EntityInsertAction {


    public void genericInsertUpdateQueryStatementCreator(final Object object) {

        Map<String, String> columnNameValues = new LinkedHashMap<>();

        Class<?> objClass = object.getClass();

        Field[] declaredFields = objClass.getDeclaredFields();

        String tableName = objClass.getAnnotation(Table.class).name();


        for (Field field : declaredFields) {


            if (field.isAnnotationPresent(Column.class)) {

                Method method = ReflectionUtil.getMethod(objClass, "get" + capitalizeFirstLetter(field.getName()));

                String columnName = field.getAnnotation(Column.class).name();
                String columnValue = wrap(String.valueOf(ReflectionUtil.invokeMethod(object, method)));

                columnNameValues.put(columnName, columnValue);
            }
        }

    }



    private String wrap(String value) {
        return "\'" + value + "\'";
    }

    private String capitalizeFirstLetter(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
