package com.example.backend.specifications;

import com.example.backend.entities.Task;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecifications {
    public static Specification<Task> byApartment(Integer id) {
        return (root, query, cb) -> {
            if (id == null) return null;
            return cb.equal(root.get("createdBy").get("apartment").get("id"), id);
        };
    }

    public static Specification<Task> assignedToUser(Integer id) {
        return ((root, query, cb) ->
            id == null ? null : cb.equal(root.get("assignedTo").get("id"), id));
    }

    public static Specification<Task> taskDoneStatusIs(Boolean done) {
        return ((root, query, cb) ->
                done == null ? null : cb.isTrue(done
                        ? cb.isNotNull(root.get("completedBy"))
                        : cb.isNull(root.get("completedBy"))
                )
        );
    }
}
