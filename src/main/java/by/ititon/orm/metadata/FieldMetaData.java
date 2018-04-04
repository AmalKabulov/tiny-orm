package by.ititon.orm.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FieldMetaData {

    private String columnName;

    private Field field;

//    private String fieldName;
//
//    private Class<?> fieldType;


    private Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<>();


    public FieldMetaData() {

    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }


    public Map<Class<? extends Annotation>, Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map<Class<? extends Annotation>, Annotation> annotations) {
        this.annotations = annotations;
    }


    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "FieldMetaData{" +
                "columnName='" + columnName + '\'' +
                ", field=" + field +
                ", annotations=" + annotations +
                '}';
    }
}
