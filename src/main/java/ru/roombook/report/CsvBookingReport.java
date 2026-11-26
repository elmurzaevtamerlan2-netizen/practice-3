package ru.roombook.report;

import java.io.IOException;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import ru.roombook.domain.Booking;

public final class CsvBookingReport {
    public void write(List<Booking> bookings, Appendable destination) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("room", "start", "end", "status").build();
        try (CSVPrinter printer = new CSVPrinter(destination, format)) {
            for (Booking booking : bookings) {
                printer.printRecord(booking.room(), booking.slot().start(),
                        booking.slot().end(), booking.status());
            }
        }
    }
}