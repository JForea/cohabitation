package com.example.backend.intefaces;

import com.example.backend.dtos.inner.TimePeriod;

import java.time.YearMonth;

public interface ITimePeriodService {
    TimePeriod getTimePeriodByMonth(YearMonth month, short minutesOffset);
}
