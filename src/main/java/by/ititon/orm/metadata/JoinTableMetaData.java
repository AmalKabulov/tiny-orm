package by.ititon.orm.metadata;

public class JoinTableMetaData {

    private String tableName;


    private String joinColumn;


    private String inverseJoinColumn;


    public JoinTableMetaData() {
    }


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getJoinColumn() {
        return joinColumn;
    }

    public void setJoinColumn(String joinColumn) {
        this.joinColumn = joinColumn;
    }

    public String getInverseJoinColumn() {
        return inverseJoinColumn;
    }

    public void setInverseJoinColumn(String inverseJoinColumn) {
        this.inverseJoinColumn = inverseJoinColumn;
    }
}
