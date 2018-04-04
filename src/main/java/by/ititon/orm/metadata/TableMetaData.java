package by.ititon.orm.metadata;

import by.ititon.orm.action.TableType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TableMetaData {

    private TableType tableType;

    private Class<?> mappedTableClass;

    private String tableName;

    private String idColumnName;

    private String mappedByValue;

    private Field mappedByField;

    private List<String> columnNames = new ArrayList<>();

    private List<String> columnValues = new ArrayList<>();

    private List<TableMetaData> innerTables;

    private List<JoinTableMetaData> joinTables;


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


    public List<TableMetaData> getInnerTables() {
        return innerTables;
    }

    public void setInnerTables(List<TableMetaData> innerTables) {
        this.innerTables = innerTables;
    }

    public List<JoinTableMetaData> getJoinTables() {
        return joinTables;
    }

    public void setJoinTables(List<JoinTableMetaData> joinTables) {
        this.joinTables = joinTables;
    }



    public String getMappedByValue() {
        return mappedByValue;
    }

    public void setMappedByValue(String mappedByValue) {
        this.mappedByValue = mappedByValue;
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

    public String getIdColumnName() {
        return idColumnName;
    }

    public void setIdColumnName(String idColumnName) {
        this.idColumnName = idColumnName;
    }

    public TableType getTableType() {
        return tableType;
    }

    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }
}
