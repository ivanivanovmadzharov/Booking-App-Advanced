package com.bookingsystem.scheduler;

import com.bookingsystem.service.BookingService;
import com.bookingsystem.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingScheduler.class);

    private final BookingService bookingService;
    private final RoomService roomService;

    public BookingScheduler(BookingService bookingService, RoomService roomService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    /**
     * Cron job: runs every day at 2am.
     * Cancels any bookings that have been in PENDING status for over 24 hours.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void expirePendingBookings() {
        log.info("Running scheduled cron job: expiring stale pending bookings");
        bookingService.autoExpirePendingBookings();
    }

    /**
     * Fixed-rate job: runs every hour.
     * Evicts the available rooms cache to ensure listings stay fresh.
     */
    @Scheduled(fixedRate = 3_600_000)
    @CacheEvict(value = "availableRooms", allEntries = true)
    public void refreshRoomsCache() {
        log.info("Running scheduled fixed-rate job: evicting available rooms cache");
    }
}
