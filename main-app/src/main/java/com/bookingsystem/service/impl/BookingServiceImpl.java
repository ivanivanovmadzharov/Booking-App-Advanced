package com.bookingsystem.service.impl;

import com.bookingsystem.event.BookingConfirmedEvent;
import com.bookingsystem.exception.BookingConflictException;
import com.bookingsystem.exception.ResourceNotFoundException;
import com.bookingsystem.exception.UnauthorizedException;
import com.bookingsystem.model.dto.BookingDto;
import com.bookingsystem.model.entity.Booking;
import com.bookingsystem.model.entity.Room;
import com.bookingsystem.model.entity.User;
import com.bookingsystem.model.enums.BookingStatus;
import com.bookingsystem.model.enums.UserRole;
import com.bookingsystem.repository.BookingRepository;
import com.bookingsystem.service.BookingService;
import com.bookingsystem.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final ApplicationEventPublisher eventPublisher;

    public BookingServiceImpl(BookingRepository bookingRepository,
                               RoomService roomService,
                               ApplicationEventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.roomService = roomService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Booking createBooking(UUID roomId, BookingDto dto, User guest) {
        Room room = roomService.findById(roomId);

        if (!dto.getCheckOut().isAfter(dto.getCheckIn())) {
            throw new BookingConflictException("Check-out date must be after check-in date");
        }

        boolean overlaps = bookingRepository.existsOverlappingBooking(
                roomId, dto.getCheckIn(), dto.getCheckOut(), BookingStatus.CANCELLED);
        if (overlaps) {
            throw new BookingConflictException("This room is already booked for the selected dates");
        }

        long nights = ChronoUnit.DAYS.between(dto.getCheckIn(), dto.getCheckOut());
        BigDecimal totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = new Booking();
        booking.setCheckIn(dto.getCheckIn());
        booking.setCheckOut(dto.getCheckOut());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setGuest(guest);
        booking.setRoom(room);
        booking.setCreatedAt(LocalDateTime.now());

        Booking saved = bookingRepository.save(booking);
        log.info("Booking created: {} for room {} by {}", saved.getId(), roomId, guest.getUsername());
        eventPublisher.publishEvent(new BookingConfirmedEvent(this, saved));
        return saved;
    }

    @Override
    public void cancelBooking(UUID id, User currentUser) {
        Booking booking = findById(id);
        boolean isOwner = booking.getGuest().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("You are not allowed to cancel this booking");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        log.info("Booking {} cancelled by {}", id, currentUser.getUsername());
    }

    @Override
    public void autoExpirePendingBookings() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<Booking> expired = bookingRepository.findAllByStatusAndCreatedAtBefore(BookingStatus.PENDING, cutoff);
        expired.forEach(b -> b.setStatus(BookingStatus.CANCELLED));
        bookingRepository.saveAll(expired);
        log.info("Auto-expired {} pending bookings", expired.size());
    }

    @Override
    public Booking findById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }

    @Override
    public List<Booking> findAllByGuest(UUID guestId) {
        return bookingRepository.findAllByGuestId(guestId);
    }

    @Override
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }
}
