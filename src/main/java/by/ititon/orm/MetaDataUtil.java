package by.ititon.orm;

import by.ititon.orm.annotation.JoinTable;
import by.ititon.orm.annotation.ManyToMany;
import by.ititon.orm.exception.MappingException;
import by.ititon.orm.metadata.FieldMetaData;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MetaDataUtil {


    public static List<String> getColumns(List<FieldMetaData> entityFieldsMeta) {
        return entityFieldsMeta
                .stream()
                .filter(field -> !Objects.equals(field.getColumnName(), null))
                .map(FieldMetaData::getColumnName)
                .collect(Collectors.toList());
    }


    public static JoinTable getJoinTableByMappedBy(String mappedByValue, List<FieldMetaData> fieldMetaData) {
        for (FieldMetaData innerField : fieldMetaData) {
            if (innerField.getFieldName().equals(mappedByValue)) {
                return (JoinTable) innerField.getAnnotations().get(JoinTable.class);
            }
        }

        throw new MappingException("MAPPED FIELD " + mappedByValue + " NOT FOUND");
    }


    public static JoinTable findJoinTableByFieldMeta(FieldMetaData mainFieldMeta, List<FieldMetaData> innerFieldsMeta) {

        for (FieldMetaData innerField : innerFieldsMeta) {
            Map<Class<? extends Annotation>, Annotation> innerAnnotations = innerField.getAnnotations();
            if (innerAnnotations.containsKey(ManyToMany.class)) {
                ManyToMany manyToManyInner = (ManyToMany) innerAnnotations.get(ManyToMany.class);
                String mainFieldName = mainFieldMeta.getFieldName();
                if (Objects.equals(manyToManyInner.mappedBy(), mainFieldName)) {
                    return (JoinTable) mainFieldMeta.getAnnotations().get(JoinTable.class);
                }
            }
        }

        throw new MappingException("MAPPED FIELD " + mainFieldMeta.getFieldName() + " NOT FOUND");
    }



}
