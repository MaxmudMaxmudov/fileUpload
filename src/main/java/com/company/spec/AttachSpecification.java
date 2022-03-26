package com.company.spec;

import com.company.entity.AttachEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AttachSpecification {


    public static Specification<AttachEntity> status(Boolean visible) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("visible"), visible));
    }

    public static Specification<AttachEntity> like(String field, String value) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get(field), "%" + value + "%");
        };
    }

    public static Specification<AttachEntity> greaterThanOrEqualTo(String field, LocalDate date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get(field),
                        LocalDateTime.of(date, LocalTime.MIN));

    }

    public static Specification<AttachEntity> betweenSize(Long fromSize, Long toSize){
        return ((root, query, criteriaBuilder) -> {
            return criteriaBuilder.between(root.get("size"),fromSize,toSize);
        });
    }

    public static Specification<AttachEntity> lessThanOrEqualTo(String field, LocalDate date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get(field),
                        LocalDateTime.of(date, LocalTime.MAX));
    }




}
