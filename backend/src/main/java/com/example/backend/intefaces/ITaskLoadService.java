package com.example.backend.intefaces;

import com.example.backend.entities.Profile;
import com.example.backend.entities.TaskRepeatRule;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public interface ITaskLoadService {
    Map<Long, Double> calculateProfileLoad(
            TaskRepeatRule rule,
            Collection<Profile> candidates,
            LocalDate calculationStart,
            LocalDate calculationEnd
    );
    Map<Long, Double> calculateProfileLoad(
            Integer apartmentId,
            Collection<Profile> candidates,
            LocalDate calculationStart,
            LocalDate calculationEnd
    );
}
