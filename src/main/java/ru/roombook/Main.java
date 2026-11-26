package ru.roombook;

import java.io.IOException;
import java.time.LocalDateTime;
import ru.roombook.application.BookingService;
import ru.roombook.domain.TimeSlot;
import ru.roombook.report.CsvBookingReport;

public final class Main {
    public static void main(String[] args) throws IOException {
        var service = new BookingService(booking ->
                System.out.printf("Booking for %s: %s%n", booking.room(), booking.status()));
        var start = LocalDateTime.of(2026, 12, 1, 15, 0);
        var slot = new TimeSlot(start, start.plusHours(1));
        var request = service.request("A-301", "student-demo", slot, "Project workshop");
        service.confirm(request.id());

        try {
            service.request("A-301", "teacher-demo", slot, "Consultation");
            throw new AssertionError("An overlapping request was accepted");
        } catch (IllegalStateException conflict) {
            System.out.println(conflict.getMessage());
        }

        var output = new StringBuilder();
        new CsvBookingReport().write(service.list(), output);
        System.out.print(output);
    }
}