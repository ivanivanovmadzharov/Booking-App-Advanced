package com.bookingsystem.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BookingConfirmedEventListener {

    private static final Logger log = LoggerFactory.getLogger(BookingConfirmedEventListener.class);

    @EventListener
    public void onBookingConfirmed(BookingConfirmedEvent event) {
        log.info("Booking confirmed event received: booking ID={}, room={}, guest={}",
                event.getBooking().getId(),
                event.getBooking().getRoom().getTitle(),
                event.getBooking().getGuest().getUsername());
    }
}
