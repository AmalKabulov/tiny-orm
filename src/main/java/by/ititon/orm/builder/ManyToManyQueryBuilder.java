package by.ititon.orm.builder;

import by.ititon.orm.MetaCache;
import by.ititon.orm.MetaDataUtil;
import by.ititon.orm.annotation.FetchType;
import by.ititon.orm.annotation.JoinTable;
import by.ititon.orm.annotation.ManyToMany;
import by.ititon.orm.exception.MappingException;
import by.ititon.orm.metadata.EntityMetaData;
import by.ititon.orm.metadata.FieldMetaData;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Objects;



public class ManyToManyQueryBuilder extends QueryBuilder{


    public String manyToManyHandler(EntityMetaData entityMeta, FieldMetaData fieldMeta) {

        ManyToMany manyToMany = (ManyToMany) fieldMeta.getAnnotations().get(ManyToMany.class);
        String mappedByValue = manyToMany.mappedBy();

        String mainTableName = entityMeta.getTableName();
        List<FieldMetaData> mainFieldsMeta = entityMeta.getFieldMetaData();
        List<String> columns = MetaDataUtil.getColumns(mainFieldsMeta);


        if (manyToMany.fetch().equals(FetchType.LAZY)) {
            return buildSimpleQuery(mainTableName, columns);

        }

        return manyToManyQueryBuilder(entityMeta, columns, fieldMeta, mappedByValue);
    }




    private String manyToManyQueryBuilder(EntityMetaData mainMeta,
                                                 List<String> columns,
                                                 FieldMetaData fieldMeta,
                                                 String mappedByValue) {

        JoinTable joinTable;
        boolean reverse = false;
        String mainTableName = mainMeta.getTableName();

        Class<?> innerTableClass = fieldMeta.getFieldGenericType();
        EntityMetaData innerMeta = MetaCache.getEntityMeta(innerTableClass);
        List<FieldMetaData> innerFieldsMeta = innerMeta.getFieldMetaData();


        columns.addAll(MetaDataUtil.getColumns(innerFieldsMeta));

        String query = buildSimpleQuery(mainTableName, columns);

        if (mappedByValue.length() > 0) {
            joinTable = MetaDataUtil.getJoinTableByMappedBy(mappedByValue, innerFieldsMeta);
        } else {
            joinTable = MetaDataUtil.findJoinTableByFieldMeta(fieldMeta, innerFieldsMeta);
            reverse = true;
        }


        return appendJoinManyToManyQuery(query, joinTable, mainMeta, innerMeta, reverse);

    }




    private String appendJoinManyToManyQuery(String existQuery,
                                                    JoinTable joinTable,
                                                    EntityMetaData mainMeta,
                                                    EntityMetaData innerMeta,
                                                    boolean reverse) {

        String joinTableName = joinTable.name();
        String joinColumn = joinTableName + "." + joinTable.joinColumns()[0].name();
        String inverseJoinColumn = joinTableName + "." + joinTable.inverseJoinColumns()[0].name();

        if (reverse) {
            String temp = joinColumn;
            joinColumn = inverseJoinColumn;
            inverseJoinColumn = temp;
        }

        return new StringBuilder(existQuery).append("left join ").append(joinTableName).append("\n")
                .append("on ").append(inverseJoinColumn).append(" = ").append(mainMeta.getIdColumnName()).append("\n")
                .append("left join ").append(innerMeta.getTableName()).append("\n")
                .append("on ").append(innerMeta.getIdColumnName()).append(" = ").append(joinColumn).toString();

    }
}
