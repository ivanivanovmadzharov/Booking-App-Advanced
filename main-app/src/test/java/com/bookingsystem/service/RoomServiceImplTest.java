package com.bookingsystem.service;

import com.bookingsystem.exception.UnauthorizedException;
import com.bookingsystem.model.dto.RoomDto;
import com.bookingsystem.model.entity.Room;
import com.bookingsystem.model.entity.User;
import com.bookingsystem.model.enums.UserRole;
import com.bookingsystem.repository.RoomRepository;
import com.bookingsystem.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock private RoomRepository roomRepository;
    @InjectMocks private RoomServiceImpl roomService;

    private User host;
    private Room room;
    private RoomDto roomDto;

    @BeforeEach
    void setUp() {
        host = new User();
        host.setId(UUID.randomUUID());
        host.setUsername("hostuser");
        host.setRole(UserRole.HOST);

        room = new Room();
        room.setId(UUID.randomUUID());
        room.setTitle("Test Room");
        room.setHost(host);

        roomDto = new RoomDto();
        roomDto.setTitle("Updated Room");
        roomDto.setDescription("A nice description here");
        roomDto.setLocation("Sofia");
        roomDto.setPricePerNight(new BigDecimal("80.00"));
        roomDto.setMaxGuests(2);
        roomDto.setAvailable(true);
    }

    @Test
    void createRoom_setsHostAndSaves() {
        when(roomRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Room result = roomService.createRoom(roomDto, host);

        assertThat(result.getHost()).isEqualTo(host);
        assertThat(result.getTitle()).isEqualTo("Updated Room");
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void deleteRoom_byNonOwnerNonAdmin_throwsUnauthorized() {
        User stranger = new User();
        stranger.setId(UUID.randomUUID());
        stranger.setRole(UserRole.GUEST);

        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> roomService.deleteRoom(room.getId(), stranger))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void deleteRoom_byAdmin_succeeds() {
        User admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setRole(UserRole.ADMIN);

        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        roomService.deleteRoom(room.getId(), admin);

        verify(roomRepository).delete(room);
    }

    @Test
    void findAllAvailable_returnsOnlyAvailableRooms() {
        when(roomRepository.findAllByAvailableTrue()).thenReturn(List.of(room));

        List<Room> result = roomService.findAllAvailable();

        assertThat(result).hasSize(1);
        verify(roomRepository).findAllByAvailableTrue();
    }
}
