package ru.roombook.domain;

import java.util.Objects;
import java.util.UUID;

public record Booking(UUID id, String room, String requester, TimeSlot slot,
                      String purpose, Status status) {
    public enum Status { PENDING, CONFIRMED }

    public static Booking request(String room, String requester, TimeSlot slot, String purpose) {
        Objects.requireNonNull(slot);
        if (room.isBlank() || requester.isBlank() || purpose.isBlank()) {
            throw new IllegalArgumentException("Room, requester and purpose are required");
        }
        return new Booking(UUID.randomUUID(), room, requester, slot, purpose, Status.PENDING);
    }

    public Booking confirm() {
        if (status != Status.PENDING) {
            throw new IllegalStateException("Only a pending request can be confirmed");
        }
        return new Booking(id, room, requester, slot, purpose, Status.CONFIRMED);
    }
}