package com.example.demo.repositories.specifications;

import com.example.demo.models.entities.Event;
import com.example.demo.models.enums.EventType;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;

public final class EventSpecification {

    private EventSpecification() {
    }

    public static Specification<Event> titleContains(String search) {
        return (root, query, cb) ->
                isBlank(search)
                        ? cb.conjunction()
                        : cb.like(
                        cb.lower(root.get("title")),
                        "%" + search.toLowerCase() + "%"
                );
    }

    public static Specification<Event> hasType(EventType type) {
        return (root, query, cb) ->
                type == null
                        ? cb.conjunction()
                        : cb.equal(root.get("eventType"), type);
    }

    public static Specification<Event> hasGenreName(String genreName) {
        return (root, query, cb) ->
                isBlank(genreName)
                        ? cb.conjunction()
                        : cb.equal(genreJoin(root).get("name"), genreName);
    }

    public static Specification<Event> hasGenreId(String genreId) {
        return (root, query, cb) ->
                isBlank(genreId)
                        ? cb.conjunction()
                        : cb.equal(genreJoin(root).get("id"), genreId);
    }

    public static Specification<Event> isFuture() {
        return (root, query, cb) ->
                cb.greaterThan(root.get("dateTime"), LocalDateTime.now());
    }

    public static Specification<Event> hasAvailableSeats() {
        return (root, query, cb) ->
                cb.greaterThan(root.get("availableSeats"), 0);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static Join<?, ?> genreJoin(Root<Event> root) {
        return root.join("genre", JoinType.LEFT);
    }
}