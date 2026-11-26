package ru.roombook.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public record TimeSlot(LocalDateTime start, LocalDateTime end) {
    public TimeSlot {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start must precede end");
        }
    }

    public boolean overlaps(TimeSlot other) {
        return start.isBefore(other.end()) && other.start().isBefore(end);
    }
}