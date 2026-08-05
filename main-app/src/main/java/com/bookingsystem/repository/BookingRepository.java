package com.bookingsystem.repository;

import com.bookingsystem.model.entity.Booking;
import com.bookingsystem.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findAllByGuestId(UUID guestId);

    List<Booking> findAllByRoomId(UUID roomId);

    List<Booking> findAllByStatus(BookingStatus status);

    List<Booking> findAllByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime before);

    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.room.id = :roomId
              AND b.status <> :cancelled
              AND b.checkIn < :checkOut
              AND b.checkOut > :checkIn
            """)
    boolean existsOverlappingBooking(@Param("roomId") UUID roomId,
                                     @Param("checkIn") LocalDate checkIn,
                                     @Param("checkOut") LocalDate checkOut,
                                     @Param("cancelled") BookingStatus cancelled);
}
