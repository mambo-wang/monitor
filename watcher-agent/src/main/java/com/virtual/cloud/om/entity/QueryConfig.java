package com.virtual.cloud.om.entity;

import lombok.Data;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 封装查询条件。
 *
 * @author NAME
 */
@Data
public class QueryConfig implements Serializable {

    /** 序列化 ID。 */
    private static final long serialVersionUID = 1L;

    /** 排序顺序枚举值。 */
    public enum SortDir {
        /** 不排序。 */
        None,
        /** 逆序。 */
        Desc,
        /** 顺序。 */
        Asc
    }

    /** 字段比较关系。 */
    public enum Criteria {
        Equals,
        NotEquals,
        Greater,
        Less,
        Like,
        In
    }

    /** 排序顺序。 */
    private SortDir sortDir = SortDir.None;

    /** 排序列。 */
    private String sortField = null;

    /** 查询开始位置。 */
    private int offset = 0;

    /** 查询返回的数据量。 */
    private int limit = 0;

    /** 查询条件信息。 */
    private Map<String, Object> equalsRestrictions = new HashMap<>();
    private Map<String, Object> notEqualsRestrictions = new HashMap<>();
    private Map<String, Date> greaterRestrictions = new HashMap<>();
    private Map<String, Date> lessRestrictions = new HashMap<>();
    private Map<String, Object> likeRestrictions = new HashMap<>();
    private Map<String, List<Long>> inRestrictions = new HashMap<>();


    private Map<String, SortDir> sortMaps;

    /** 缺省构造方法，构造查询条件。 */
    public QueryConfig() {
    }

    /**
     * 构造查询条件。
     *
     * @param sortDir 排序顺序。
     * @param sortField 排序列。
     */
    public QueryConfig(SortDir sortDir, String sortField) {
        setSortInfo(sortDir, sortField);
    }

    /**
     * 构造查询条件。
     *
     * @param offset 查询开始位置。
     * @param limit 查询返回的数据量。
     */
    public QueryConfig(int offset, int limit) {
        setPagingInfo(offset, limit);
    }

    /**
     * 构造查询条件。
     *
     * @param sortDir 排序顺序。
     * @param sortField 排序列。
     * @param offset 查询开始位置。
     * @param limit 查询返回的数据量。
     */
    public QueryConfig(SortDir sortDir, String sortField, int offset, int limit) {
        setSortInfo(sortDir, sortField);
        setPagingInfo(offset, limit);
    }

    // ------------------------------------------------------------------- 访问方法

    public SortDir getSortDir() {
        return sortDir;
    }

    public String getSortField() {
        return sortField;
    }
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    /**
     * 设置排序信息。
     *
     * @param sortDir 牌序列。
     * @param sortField 排序字段。
     * @return 当前查询条件。
     */
    public QueryConfig setSortInfo(SortDir sortDir, String sortField) {
        this.sortDir = sortDir != null ? sortDir : SortDir.None;
        this.sortField = sortField;
        return this;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
     * 设置分页配置信息。
     *
     * @param offset 查询开始位置。
     * @param limit 查询返回的数据量。
     * @return 当前查询条件。
     */
    public QueryConfig setPagingInfo(int offset, int limit) {
        this.offset = offset >= 0 ? offset : 0;
        this.limit = limit >= 0 ? limit : 0;
        return this;
    }

    /**
     * 增加查询条件。字段相同的查询条件只能增加一次，重复增加时会将上一次增加的值覆盖。
     *
     * @param field 查询字段名（不一定是真实字段名，查询方法由设置方与使用方内部协商确定）。
     * @param value 查询条件值。
     * @return 当前查询条件。
     */
    public QueryConfig addRestriction(String field, Object value) {
        if (equalsRestrictions == null) {
            equalsRestrictions = new HashMap<String, Object>();
        }
        equalsRestrictions.put(field, value);
        return this;
    }

    public QueryConfig addInRestriction(String field, List<Long> value) {
        inRestrictions.put(field, value);
        return this;
    }

    public QueryConfig addRestriction(String field, Object value, Criteria criteria) {

        if(Objects.isNull(value)){
            return this;
        }

        if(criteria == Criteria.Equals){
            equalsRestrictions.put(field, value);
        }
        if(criteria == Criteria.NotEquals) {
            notEqualsRestrictions.put(field, value);
        }
        if(criteria == Criteria.Greater){
            Date timeFrom = (Date) value;
            timeFrom.setHours(0);
            timeFrom.setMinutes(0);
            timeFrom.setSeconds(0);
            greaterRestrictions.put(field, timeFrom);
        }
        if(criteria == Criteria.Less) {
            Date timeTo = (Date) value;
            timeTo.setHours(23);
            timeTo.setMinutes(59);
            timeTo.setSeconds(59);
            lessRestrictions.put(field, timeTo);
        }
        if(criteria == Criteria.Like){
            likeRestrictions.put(field, value);
        }
        return this;
    }

    /**
     * 返回特定查询条件的值。
     *
     * @param field 查询字段名。
     * @return 查询字段的值。如果没有该对应值，则返回 <code>null</code>。
     */
    public Object getRestriction(String field) {
        if (equalsRestrictions == null) {
            return null;
        }
        return equalsRestrictions.get(field);
    }

    /**
     * 返回所有查询条件信息。
     *
     * @return 查询条件信息。如果没有任何查询条件，可能返回 <code>null</code>。
     */
    public Map<String, Object> getEqualsRestrictions() {
        if (equalsRestrictions == null) {
            return null;
        }
        return Collections.unmodifiableMap(equalsRestrictions);
    }

    public Map getRestrictions() {
        if (equalsRestrictions == null) {
            return null;
        }
        return Collections.unmodifiableMap(equalsRestrictions);
    }

    /**
     * 删除特定查询条件值。
     *
     * @param field 需要删除的查询字段名。
     * @return 被删除的查询条件。如果无此条件，则返回 <code>null</code>。
     */
    public Object removeRestriction(String field) {
        if (equalsRestrictions == null) {
            return null;
        }
        return equalsRestrictions.remove(field);
    }

    /**
     * 返回该查询条件是否进行排序。
     *
     * @return 该查询条件是否进行排序。
     */
    public boolean isSort() {
        return sortDir != null && sortDir != SortDir.None && sortField != null;
    }

    /**
     * 返回该查询条件是否进行分页。
     *
     * @return 该查询条件是否进行分页。
     */
    public boolean isPaging() {
        return limit > 0;
    }

    public Sort getSort() {
        Sort sort = null;
        if(StringUtils.isEmpty(sortField)){
            return sort;
        }
        if(sortDir == SortDir.Asc){
            return Sort.by(Sort.Direction.ASC, sortField);
        } else {
            return Sort.by(Sort.Direction.DESC, sortField);
        }
    }
//
//    public  <T> Specification<T> getSpecification(CriteriaBuilder criteriaBuilder, Class clazz){
//
//        CriteriaQuery<T> crit = criteriaBuilder.createQuery(clazz);
//        Root<T> root = crit.from(clazz);
//            List<Predicate> predicates = new ArrayList<>();
//            for (Map.Entry<String, Object> entry1 : getEqualsRestrictions().entrySet()) {
//                if(Objects.nonNull(entry1.getValue())){
//                    predicates.add(criteriaBuilder.equal(root.get(entry1.getKey()), entry1.getValue()));
//                }
//            }
//            for(Map.Entry<String, Object> entry : getNotEqualsRestrictions().entrySet()) {
//                if(Objects.nonNull(entry.getValue())){
//                    predicates.add(criteriaBuilder.notEqual(root.get(entry.getKey()), entry.getValue()));
//                }
//            }
//            for (Map.Entry<String, Date> entry2 : getGreaterRestrictions().entrySet()) {
//                if(Objects.nonNull(entry2.getValue())) {
//                    predicates.add(criteriaBuilder.greaterThan(root.get(entry2.getKey()).as(Date.class), entry2.getValue()));
//                }
//            }
//            for (Map.Entry<String, Date> entry : getLessRestrictions().entrySet()) {
//                if (Objects.nonNull(entry.getValue())) {
//                    predicates.add(criteriaBuilder.lessThan(root.get(entry.getKey()).as(Date.class), entry.getValue()));
//                }
//            }
//            for (Map.Entry<String, Object> entry : getLikeRestrictions().entrySet()) {
//                if (Objects.nonNull(entry.getValue())) {
//                    predicates.add(criteriaBuilder.like(root.get(entry.getKey()), "%" + entry.getValue() + "%"));
//                }
//            }
//            for (Map.Entry<String, List<Long>> entry : getInRestrictions().entrySet()) {
//                if (Objects.nonNull(entry.getValue())) {
//                    Expression<String> expression = root.get(entry.getKey());
//                    predicates.add(expression.in(entry.getValue()));
//                }
//            }
//            criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
//            return null;
//    }

//    public  <T> Specification<T> getSpecification(){
////        return (Specification<T>) (root, criteriaQuery, criteriaBuilder) -> {
////            List<Predicate> predicates = new ArrayList<>();
////            for (Map.Entry<String, Object> entry1 : getEqualsRestrictions().entrySet()) {
////                if(Objects.nonNull(entry1.getValue())){
////                    predicates.add(criteriaBuilder.equal(root.get(entry1.getKey()), entry1.getValue()));
////                }
////            }
////            for(Map.Entry<String, Object> entry : getNotEqualsRestrictions().entrySet()) {
////                if(Objects.nonNull(entry.getValue())){
////                    predicates.add(criteriaBuilder.notEqual(root.get(entry.getKey()), entry.getValue()));
////                }
////            }
////            for (Map.Entry<String, Date> entry2 : getGreaterRestrictions().entrySet()) {
////                if(Objects.nonNull(entry2.getValue())) {
////                    predicates.add(criteriaBuilder.greaterThan(root.get(entry2.getKey()).as(Date.class), entry2.getValue()));
////                }
////            }
////            for (Map.Entry<String, Date> entry : getLessRestrictions().entrySet()) {
////                if (Objects.nonNull(entry.getValue())) {
////                    predicates.add(criteriaBuilder.lessThan(root.get(entry.getKey()).as(Date.class), entry.getValue()));
////                }
////            }
////            for (Map.Entry<String, List<Long>> entry : getInRestrictions().entrySet()) {
////                if (Objects.nonNull(entry.getValue())) {
////                    Expression<String> expression = root.get(entry.getKey());
////                    predicates.add(expression.in(entry.getValue()));
////                }
////            }
////            criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
////            return null;
////        };
////    }
}
