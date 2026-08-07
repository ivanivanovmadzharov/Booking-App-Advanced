package com.bookingsystem.service;

import com.bookingsystem.exception.BookingConflictException;
import com.bookingsystem.exception.UnauthorizedException;
import com.bookingsystem.model.dto.BookingDto;
import com.bookingsystem.model.entity.Booking;
import com.bookingsystem.model.entity.Room;
import com.bookingsystem.model.entity.User;
import com.bookingsystem.model.enums.BookingStatus;
import com.bookingsystem.model.enums.UserRole;
import com.bookingsystem.repository.BookingRepository;
import com.bookingsystem.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private RoomService roomService;
    @Mock private ApplicationEventPublisher eventPublisher;
    @InjectMocks private BookingServiceImpl bookingService;

    private Room room;
    private User guest;
    private BookingDto validDto;

    @BeforeEach
    void setUp() {
        room = new Room();
        room.setId(UUID.randomUUID());
        room.setPricePerNight(new BigDecimal("100.00"));

        guest = new User();
        guest.setId(UUID.randomUUID());
        guest.setUsername("guestuser");
        guest.setRole(UserRole.GUEST);

        validDto = new BookingDto();
        validDto.setCheckIn(LocalDate.now().plusDays(1));
        validDto.setCheckOut(LocalDate.now().plusDays(4));
    }

    @Test
    void createBooking_withValidDates_savesBooking() {
        when(roomService.findById(room.getId())).thenReturn(room);
        when(bookingRepository.existsOverlappingBooking(any(), any(), any(), any())).thenReturn(false);
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.createBooking(room.getId(), validDto, guest);

        assertThat(result.getTotalPrice()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void createBooking_whenCheckoutBeforeCheckin_throwsConflict() {
        validDto.setCheckOut(LocalDate.now().plusDays(1));
        validDto.setCheckIn(LocalDate.now().plusDays(3));

        when(roomService.findById(room.getId())).thenReturn(room);

        assertThatThrownBy(() -> bookingService.createBooking(room.getId(), validDto, guest))
                .isInstanceOf(BookingConflictException.class)
                .hasMessageContaining("Check-out date must be after");
    }

    @Test
    void createBooking_whenOverlapping_throwsConflict() {
        when(roomService.findById(room.getId())).thenReturn(room);
        when(bookingRepository.existsOverlappingBooking(any(), any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking(room.getId(), validDto, guest))
                .isInstanceOf(BookingConflictException.class)
                .hasMessageContaining("already booked");
    }

    @Test
    void cancelBooking_byOwner_setsStatusCancelled() {
        Booking booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setGuest(guest);
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        bookingService.cancelBooking(booking.getId(), guest);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void cancelBooking_byOtherUser_throwsUnauthorized() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setRole(UserRole.GUEST);

        Booking booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setGuest(guest);
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancelBooking(booking.getId(), otherUser))
                .isInstanceOf(UnauthorizedException.class);
    }
}
