package com.bookingsystem.config;

import com.bookingsystem.model.entity.Room;
import com.bookingsystem.model.entity.User;
import com.bookingsystem.model.enums.UserRole;
import com.bookingsystem.repository.RoomRepository;
import com.bookingsystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                            RoomRepository roomRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = createUser("admin", "admin@stayeasy.com", "admin123", UserRole.ADMIN);
        User host = createUser("hostuser", "host@stayeasy.com", "host123", UserRole.HOST);
        createUser("guestuser", "guest@stayeasy.com", "guest123", UserRole.GUEST);

        createRoom("Cozy Mountain Cabin",
                "A charming wooden cabin nestled in the mountains, perfect for a quiet getaway with stunning views.",
                "Bansko, Bulgaria", new BigDecimal("65.00"), 4, host);

        createRoom("Modern City Apartment",
                "Stylish 2-bedroom apartment in the heart of the city, walking distance to all major attractions.",
                "Sofia, Bulgaria", new BigDecimal("90.00"), 3, host);

        createRoom("Seaside Studio",
                "Compact studio just steps from the beach, ideal for couples or solo travelers.",
                "Varna, Bulgaria", new BigDecimal("50.00"), 2, admin);
    }

    private User createUser(String username, String email, String password, UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }

    private void createRoom(String title, String description, String location,
                             BigDecimal price, int maxGuests, User host) {
        Room room = new Room();
        room.setTitle(title);
        room.setDescription(description);
        room.setLocation(location);
        room.setPricePerNight(price);
        room.setMaxGuests(maxGuests);
        room.setAvailable(true);
        room.setHost(host);
        roomRepository.save(room);
    }
}
