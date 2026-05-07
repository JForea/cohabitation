package com.example.backend.repositories;

import com.example.backend.entities.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends ListCrudRepository<Event, Long> {
    @Query("""
        SELECT DISTINCT e.date
        FROM Event e
        WHERE e.createdBy.apartment.id = :apartmentId
            AND e.date BETWEEN :start AND :end
    """)
    List<LocalDate> findDistinctDatesByApartmentIdAndDateBetween(Integer apartmentId, LocalDate start, LocalDate end);
    List<Event> findAllByCreatedBy_Apartment_IdAndDate(Integer apartmentId, LocalDate date);
}
