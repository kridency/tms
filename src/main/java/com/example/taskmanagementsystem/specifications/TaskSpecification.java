package com.example.taskmanagementsystem.specifications;

import com.example.taskmanagementsystem.entities.Task;
import com.example.taskmanagementsystem.entities.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Optional;

public record TaskSpecification(Map<String, User> criteria) implements Specification<Task> {
    @Override
    public Predicate toPredicate(@Nullable Root<Task> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder builder) {
            return criteria.entrySet().stream()
                .filter(entry -> Optional.ofNullable(entry.getValue()).isPresent())
                .map(entry -> {
                    assert root != null;
                    return builder.equal(root.get(entry.getKey()), entry.getValue());
                })
                .reduce(builder::and).orElse(null);
    }
}
