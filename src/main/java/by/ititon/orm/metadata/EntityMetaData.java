package by.ititon.orm.metadata;

import java.util.ArrayList;
import java.util.List;

public class EntityMetaData {

    String tableName;

    String idColumnName;

    List<FieldMetaData> fieldMetaData = new ArrayList<>();


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<FieldMetaData> getFieldMetaData() {
        return fieldMetaData;
    }

    public void setFieldMetaData(List<FieldMetaData> fieldMetaData) {
        this.fieldMetaData = fieldMetaData;
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public void setIdColumnName(String idColumnName) {
        this.idColumnName = idColumnName;
    }

    @Override
    public String toString() {
        return "EntityMetaData{" +
                "tableName='" + tableName + '\'' +
                ", idColumnName='" + idColumnName + '\'' +
                ", fieldMetaData=" + fieldMetaData +
                '}';
    }
}
