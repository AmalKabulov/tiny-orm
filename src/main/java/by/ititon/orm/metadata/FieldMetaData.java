package by.ititon.orm.metadata;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;


public class FieldMetaData {

    private String columnName;


    private String fieldName;

    private Class<?> fieldType;

    private Class<?> fieldGenericType;


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


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public void setFieldType(Class<?> fieldType) {
        this.fieldType = fieldType;
    }

    public Class<?> getFieldGenericType() {
        return fieldGenericType;
    }

    public void setFieldGenericType(Class<?> fieldGenericType) {
        this.fieldGenericType = fieldGenericType;
    }

    @Override
    public String toString() {
        return "FieldMetaData{" +
                "columnName='" + columnName + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", fieldType=" + fieldType +
                ", fieldGenericType=" + fieldGenericType +
                ", annotations=" + annotations +
                '}';
    }
}
