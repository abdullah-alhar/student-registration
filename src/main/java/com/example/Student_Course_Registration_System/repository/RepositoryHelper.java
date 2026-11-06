package com.example.Student_Course_Registration_System.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Small shared helper for the repository classes. */
final class RepositoryHelper {

    private RepositoryHelper() {}

    /** Turns a list into a "lookup by ID" map, e.g. all students keyed by studentId. */
    static <T> Map<String, T> byId(List<T> items, Function<T, String> idOf) {
        Map<String, T> map = new HashMap<>();
        for (T item : items) {
            map.put(idOf.apply(item), item);
        }
        return map;
    }
}
