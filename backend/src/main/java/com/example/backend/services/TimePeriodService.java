package com.example.backend.services;

import com.example.backend.dtos.inner.TimePeriod;
import com.example.backend.intefaces.ITimePeriodService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;

@Service
public class TimePeriodService implements ITimePeriodService {
    public TimePeriod getTimePeriodByMonth(YearMonth month, short minutesOffset) {
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(
                minutesOffset * 60
        );

        Instant start = month
                .atDay(1)
                .atStartOfDay(offset)
                .toInstant();

        Instant end = month
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay(offset)
                .toInstant();

        return new TimePeriod(
                start,
                end
        );
    }
}
