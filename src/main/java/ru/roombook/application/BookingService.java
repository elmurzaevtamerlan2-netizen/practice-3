package ru.roombook.application;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import ru.roombook.domain.Booking;
import ru.roombook.domain.TimeSlot;

public final class BookingService {
    private final Map<UUID, Booking> bookings = new LinkedHashMap<>();
    private final Consumer<Booking> statusListener;

    public BookingService(Consumer<Booking> statusListener) {
        this.statusListener = java.util.Objects.requireNonNull(statusListener);
    }

    public synchronized Booking request(String room, String requester, TimeSlot slot, String purpose) {
        boolean occupied = bookings.values().stream().anyMatch(
                booking -> booking.room().equals(room) && booking.slot().overlaps(slot));
        if (occupied) {
            throw new IllegalStateException("Room already reserved for this time");
        }
        return save(Booking.request(room, requester, slot, purpose));
    }

    public synchronized Booking confirm(UUID id) {
        Booking booking = bookings.get(id);
        if (booking == null) {
            throw new IllegalArgumentException("Unknown booking");
        }
        return save(booking.confirm());
    }

    public synchronized List<Booking> list() {
        return List.copyOf(bookings.values());
    }

    private Booking save(Booking booking) {
        bookings.put(booking.id(), booking);
        statusListener.accept(booking);
        return booking;
    }
}