package com.bookingsystem.service;

import com.bookingsystem.model.dto.BookingDto;
import com.bookingsystem.model.entity.Booking;
import com.bookingsystem.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    Booking createBooking(UUID roomId, BookingDto dto, User guest);

    void cancelBooking(UUID id, User currentUser);

    void autoExpirePendingBookings();

    Booking findById(UUID id);

    List<Booking> findAllByGuest(UUID guestId);

    List<Booking> findAll();
}
