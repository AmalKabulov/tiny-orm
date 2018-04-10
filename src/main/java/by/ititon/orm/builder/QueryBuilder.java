package by.ititon.orm.builder;

import java.util.List;

public abstract class QueryBuilder {

    protected String buildSimpleQuery(String tableName, List<String> columns) {
        String allColumns = String.join(", ", columns);
        return new StringBuilder().append("select").append("\n").append(allColumns).append("\n").append("from").append("\n")
                .append(tableName).append("\n").toString();
    }
}
