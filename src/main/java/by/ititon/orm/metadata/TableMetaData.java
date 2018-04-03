package by.ititon.orm.metadata;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TableMetaData {

    private Class<?> mappedTableClass;

    private String tableName;

    private String mappedByFieldName;

    private Field mappedByField;

    private List<String> columnNames = new ArrayList<>();

    private List<String> columnValues = new ArrayList<>();

    private TableMetaData tableMetaData;


    public TableMetaData() {
    }


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<String> getColumnValues() {
        return columnValues;
    }

    public void setColumnValues(List<String> columnValues) {
        this.columnValues = columnValues;
    }

    public TableMetaData getTableMetaData() {
        return tableMetaData;
    }

    public void setTableMetaData(TableMetaData tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    public String getMappedByFieldName() {
        return mappedByFieldName;
    }

    public void setMappedByFieldName(String mappedByFieldName) {
        this.mappedByFieldName = mappedByFieldName;
    }

    public Field getMappedByField() {
        return mappedByField;
    }

    public void setMappedByField(Field mappedByField) {
        this.mappedByField = mappedByField;
    }

    public Class<?> getMappedTableClass() {
        return mappedTableClass;
    }

    public void setMappedTableClass(Class<?> mappedTableClass) {
        this.mappedTableClass = mappedTableClass;
    }
}
