package com.bookingsystem.service.impl;

import com.bookingsystem.exception.ResourceNotFoundException;
import com.bookingsystem.exception.UnauthorizedException;
import com.bookingsystem.model.dto.RoomDto;
import com.bookingsystem.model.entity.Room;
import com.bookingsystem.model.entity.User;
import com.bookingsystem.model.enums.UserRole;
import com.bookingsystem.repository.RoomRepository;
import com.bookingsystem.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomServiceImpl.class);

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    @CacheEvict(value = "availableRooms", allEntries = true)
    public Room createRoom(RoomDto dto, User host) {
        Room room = new Room();
        mapDtoToEntity(dto, room);
        room.setHost(host);
        Room saved = roomRepository.save(room);
        log.info("Room created: {} by host {}", saved.getTitle(), host.getUsername());
        return saved;
    }

    @Override
    @CacheEvict(value = "availableRooms", allEntries = true)
    public Room updateRoom(UUID id, RoomDto dto, User currentUser) {
        Room room = findById(id);
        assertOwnerOrAdmin(room, currentUser);
        mapDtoToEntity(dto, room);
        Room saved = roomRepository.save(room);
        log.info("Room updated: {} by user {}", id, currentUser.getUsername());
        return saved;
    }

    @Override
    @CacheEvict(value = "availableRooms", allEntries = true)
    public void deleteRoom(UUID id, User currentUser) {
        Room room = findById(id);
        assertOwnerOrAdmin(room, currentUser);
        roomRepository.delete(room);
        log.info("Room deleted: {} by user {}", id, currentUser.getUsername());
    }

    @Override
    public Room findById(UUID id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    @Override
    @Cacheable("availableRooms")
    public List<Room> findAllAvailable() {
        log.info("Loading available rooms from database (cache miss)");
        return roomRepository.findAllByAvailableTrue();
    }

    @Override
    public List<Room> findAllByHost(UUID hostId) {
        return roomRepository.findAllByHostId(hostId);
    }

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    private void assertOwnerOrAdmin(Room room, User currentUser) {
        boolean isOwner = room.getHost().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("You are not allowed to modify this room");
        }
    }

    private void mapDtoToEntity(RoomDto dto, Room room) {
        room.setTitle(dto.getTitle());
        room.setDescription(dto.getDescription());
        room.setLocation(dto.getLocation());
        room.setPricePerNight(dto.getPricePerNight());
        room.setMaxGuests(dto.getMaxGuests());
        room.setImageUrl(dto.getImageUrl());
        room.setAvailable(dto.isAvailable());
    }
}
