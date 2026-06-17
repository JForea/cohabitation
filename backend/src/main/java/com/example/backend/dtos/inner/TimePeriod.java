package com.example.backend.dtos.inner;

import java.time.Instant;

public record TimePeriod(
        Instant start,
        Instant end
) {}
