package com.example.demo.repositories.specifications;

import com.example.demo.models.entities.Event;
import com.example.demo.models.enums.EventType;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

public class EventSpecification {

    public static Specification<Event> hasTitleContaining(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isBlank()) {
                return criteriaBuilder.conjunction(); // всегда true
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")),
                    "%" + search.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Event> hasEventType(EventType eventType) {
        return (root, query, criteriaBuilder) -> {
            if (eventType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("eventType"), eventType);
        };
    }

    public static Specification<Event> hasGenreName(String genreName) {
        return (root, query, criteriaBuilder) -> {
            if (genreName == null || genreName.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            Join<Object, Object> genreJoin = root.join("genre", JoinType.LEFT);
            return criteriaBuilder.equal(genreJoin.get("name"), genreName);
        };
    }

    public static Specification<Event> hasGenreId(String genreId) {
        return (root, query, criteriaBuilder) -> {
            if (genreId == null || genreId.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            Join<Object, Object> genreJoin = root.join("genre", JoinType.LEFT);
            return criteriaBuilder.equal(genreJoin.get("id"), genreId);
        };
    }

    // Можно добавить дополнительные фильтры при необходимости
    public static Specification<Event> isFuture() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.greaterThan(root.get("dateTime"), criteriaBuilder.currentTimestamp());
        };
    }

    public static Specification<Event> hasAvailableSeats() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.greaterThan(root.get("availableSeats"), 0);
        };
    }
}