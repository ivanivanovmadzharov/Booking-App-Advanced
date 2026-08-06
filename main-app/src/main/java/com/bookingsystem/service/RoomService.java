package com.bookingsystem.service;

import com.bookingsystem.model.dto.RoomDto;
import com.bookingsystem.model.entity.Room;
import com.bookingsystem.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface RoomService {

    Room createRoom(RoomDto dto, User host);

    Room updateRoom(UUID id, RoomDto dto, User currentUser);

    void deleteRoom(UUID id, User currentUser);

    Room findById(UUID id);

    List<Room> findAllAvailable();

    List<Room> findAllByHost(UUID hostId);

    List<Room> findAll();
}
