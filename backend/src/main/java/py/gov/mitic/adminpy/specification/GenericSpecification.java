package py.gov.mitic.adminpy.specification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

public class GenericSpecification<T> {

    public Specification<T> getSpecifications(List<SearchCriteria> filter, boolean isAnd) {
        Specification<T> specification = Specification.where(setSpecifications(filter.remove(0)));
        for (SearchCriteria input : filter) {
            specification = isAnd 
        		? specification.and(setSpecifications(input))
        		: specification.or(setSpecifications(input));
        }
        return specification;
    }

	public  Predicate equal(CriteriaBuilder cb, Expression<?> field, String value) {
        return ObjectUtils.isEmpty(value)
                ? cb.conjunction()
                : cb.equal(field, castType(field.getJavaType(), value.trim()));
    }

    public  Predicate notEqual(CriteriaBuilder cb, Expression<?> field, String value) {
        return ObjectUtils.isEmpty(value)
                ? cb.conjunction()
                : cb.notEqual(field, castType(field.getJavaType(), value.trim()));
    }

    public  Predicate like(CriteriaBuilder cb, Expression<String> field, String value) {
        return ObjectUtils.isEmpty(value)
                ? cb.conjunction()
                : cb.like(cb.lower(field), "%"+value.toLowerCase().trim()+"%");
    }

    public Predicate in(CriteriaBuilder cb, Expression<Object> field, List<Object> values){
        return ObjectUtils.isEmpty(values)
                ? cb.conjunction()
                : cb.in(field).value(castType(field.getJavaType(), values));
    }

    public Predicate between(CriteriaBuilder cb, Expression<String> field, String firstValue, String endValue){
         return ObjectUtils.isEmpty(firstValue) || ObjectUtils.isEmpty(endValue)
                 ? cb.conjunction()
                 : cb.between(field, firstValue, endValue);
    }

    public Sort getSortField(boolean sortAsc, String sortField) {
        if (sortAsc) {
            return Sort.by(sortField).ascending();
        } else {
            return Sort.by(sortField).descending();
        }
    }
    
    public Specification<T> setSpecifications(SearchCriteria filter) {
        return (root, query, cb) -> {
            switch (filter.getOperator()) {
                case EQUALS:
                    return equal(cb, root.get(filter.getField()), filter.getValue().trim());

                case NOT_EQ:
                    return notEqual(cb, root.get(filter.getField()), filter.getValue().trim());

                case LIKE:
                    return like(cb, root.get(filter.getField()), filter.getValue().trim());

                case IN:
                    return in(cb, root.get(filter.getField()), Collections.singletonList(filter.getValues()));

                case IS_NULL:
                    return cb.isNull(root.get(filter.getField()));

                case IS_NOT_NULL:
                    return cb.isNotNull(root.get(filter.getField()));

                case GREATER_THAN:
                    return ObjectUtils.isEmpty(filter.getValue())
                        ? cb.conjunction()
                        : cb.gt(root.get(filter.getField()), (Number) castType(root.get(filter.getField()).getJavaType(), filter.getValue()));

                case LESS_THAN:
                    return ObjectUtils.isEmpty(filter.getValue())
                        ? cb.conjunction()
                        : cb.lt(root.get(filter.getField()), (Number) castType(root.get(filter.getField()).getJavaType(), filter.getValue()));

                case BETWEEN:
                    return between(cb, root.get(filter.getField()), filter.getValue().trim(), filter.getEndValue().trim());

                default:
                    throw new RuntimeException("Operación no soportada");
            }
        };
    }

	private static Object castType(Class<?> fieldType, String value) {
        if(fieldType.isAssignableFrom(Double.class)) {
            return Double.valueOf(value);

        } else if(fieldType.isAssignableFrom(Integer.class)) {
            return Integer.valueOf(value);

        } else if(fieldType.isAssignableFrom(Long.class)) {
            return Long.valueOf(value);

        } else if(fieldType.isAssignableFrom(Boolean.class)) {
            return Boolean.valueOf(value);

        } else if(fieldType.isAssignableFrom(Date.class)) {
            try {
                return new SimpleDateFormat("dd/MM/yyyy").parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Object castType(Class<?> fieldType, List<?> value) {
        List<Object> lists = new ArrayList<>();
        for (Object obj : value) {
            lists.add(castType(fieldType, (String) obj));
        }
        return lists;
    }

}
