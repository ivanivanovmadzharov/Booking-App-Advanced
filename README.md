# StayEasy — Room Booking System (Spring Advanced)

Two-service architecture built for the Spring Advanced Regular Exam.

## Services

| Service | Port | Database |
|---------|------|----------|
| main-app | 8080 | booking_db |
| review-service | 8081 | review_db |

## Requirements
- Java 17+
- Maven 3.8+
- MySQL 8+ running locally (root/12345 by default — edit `application.properties` if different)

## Running the system

**Start review-service first** (main-app calls it via Feign):

```bash
cd review-service
mvn spring-boot:run
```

Then in a second terminal:

```bash
cd main-app
mvn spring-boot:run
```
**Or:**
- Run `ReviewServiceApplication.java`
- Run `MainAppApplication.java`

Visit **http://localhost:8080**

## Demo accounts (auto-seeded)

| Username  | Password  | Role  |
|-----------|-----------|-------|
| admin     | admin123  | ADMIN |
| hostuser  | host123   | HOST  |
| guestuser | guest123  | GUEST |

## Pages (10 total)
1. Home (`/`)
2. Login (`/login`)
3. Register (`/register`)
4. Browse Rooms (`/rooms`)
5. Room Detail + Booking + Reviews (`/rooms/{id}`)
6. Room Form — create & edit (`/rooms/new`, `/rooms/{id}/edit`)
7. My Rooms — host management (`/rooms/my`)
8. My Bookings (`/bookings/my`)
9. My Profile (`/profile`)
10. Admin Dashboard (`/admin`)
11. Admin — Manage Users (`/admin/users`)
12. Admin — All Bookings (`/admin/bookings`)

## Features
- Session-based security with CSRF protection
- Role-based access: GUEST / HOST / ADMIN
- Room listing with Spring Cache (`availableRooms`)
- Booking with overlap detection and date validation
- Spring Event published on every confirmed booking
- Scheduled cron job: auto-expires stale PENDING bookings at 2am daily
- Scheduled fixed-rate job: refreshes rooms cache every hour
- Reviews via Feign Client → review-service REST API
- Admin can manage all user roles and cancel any booking
- Profile editing (full name, email, phone)

## Review Service Scheduled Jobs
- Cron (midnight daily): flags low-rated reviews (< 3 stars) older than 30 days
- Fixed-rate (every 6h): deletes all flagged reviews

## Running Tests

```bash
# Unit + integration tests for main-app
cd main-app && mvn test

# Unit + API + integration tests for review-service
cd review-service && mvn test
```

## Project Structure
```
booking-system/
├── README.md
│
├── main-app/                        # Main web application (port 8080)
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/bookingsystem/
│       │   │   ├── MainAppApplication.java
│       │   │   ├── client/
│       │   │   │   └── ReviewClient.java              # Feign Client → review-service
│       │   │   ├── config/
│       │   │   │   ├── CustomUserDetailsService.java
│       │   │   │   ├── DataInitializer.java
│       │   │   │   ├── SecurityConfig.java
│       │   │   │   └── UserPrincipal.java
│       │   │   ├── controller/
│       │   │   │   ├── AdminController.java
│       │   │   │   ├── AuthController.java
│       │   │   │   ├── BookingController.java
│       │   │   │   ├── ErrorPageController.java
│       │   │   │   ├── HomeController.java
│       │   │   │   ├── ProfileController.java
│       │   │   │   ├── ReviewController.java
│       │   │   │   └── RoomController.java
│       │   │   ├── event/
│       │   │   │   ├── BookingConfirmedEvent.java
│       │   │   │   └── BookingConfirmedEventListener.java
│       │   │   ├── exception/
│       │   │   │   ├── BookingConflictException.java
│       │   │   │   ├── GlobalExceptionHandler.java
│       │   │   │   ├── ResourceNotFoundException.java
│       │   │   │   └── UnauthorizedException.java
│       │   │   ├── model/
│       │   │   │   ├── dto/
│       │   │   │   │   ├── BookingDto.java
│       │   │   │   │   ├── ProfileDto.java
│       │   │   │   │   ├── RegisterDto.java
│       │   │   │   │   ├── ReviewDto.java
│       │   │   │   │   └── RoomDto.java
│       │   │   │   ├── entity/
│       │   │   │   │   ├── Booking.java
│       │   │   │   │   ├── Room.java
│       │   │   │   │   └── User.java
│       │   │   │   └── enums/
│       │   │   │       ├── BookingStatus.java
│       │   │   │       └── UserRole.java
│       │   │   ├── repository/
│       │   │   │   ├── BookingRepository.java
│       │   │   │   ├── RoomRepository.java
│       │   │   │   └── UserRepository.java
│       │   │   ├── scheduler/
│       │   │   │   └── BookingScheduler.java          # Cron + fixed-rate jobs
│       │   │   └── service/
│       │   │       ├── BookingService.java
│       │   │       ├── ReviewService.java
│       │   │       ├── RoomService.java
│       │   │       ├── UserService.java
│       │   │       └── impl/
│       │   │           ├── BookingServiceImpl.java
│       │   │           ├── ReviewServiceImpl.java
│       │   │           ├── RoomServiceImpl.java
│       │   │           └── UserServiceImpl.java
│       │   └── resources/
│       │       ├── application.properties
│       │       ├── static/css/
│       │       │   └── style.css
│       │       └── templates/
│       │           ├── fragments/
│       │           │   └── navbar.html
│       │           ├── admin/
│       │           │   ├── bookings.html
│       │           │   ├── dashboard.html
│       │           │   └── users.html
│       │           ├── auth/
│       │           │   ├── login.html
│       │           │   └── register.html
│       │           ├── bookings/
│       │           │   └── my-bookings.html
│       │           ├── error/
│       │           │   ├── 403.html
│       │           │   ├── 404.html
│       │           │   ├── 500.html
│       │           │   └── conflict.html
│       │           ├── rooms/
│       │           │   ├── detail.html
│       │           │   ├── form.html
│       │           │   ├── list.html
│       │           │   └── my-rooms.html
│       │           ├── user/
│       │           │   └── profile.html
│       │           └── home.html
│       └── test/java/com/bookingsystem/
│           ├── MainAppApplicationTests.java
│           ├── controller/
│           │   └── AuthControllerTest.java
│           ├── integration/
│           │   └── MainAppIntegrationTest.java
│           └── service/
│               ├── BookingServiceImplTest.java
│               ├── RoomServiceImplTest.java
│               └── UserServiceImplTest.java
│
└── review-service/                  # Review microservice (port 8081)
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/reviewservice/
        │   │   ├── ReviewServiceApplication.java
        │   │   ├── controller/
        │   │   │   └── ReviewController.java          # REST API /api/reviews
        │   │   ├── exception/
        │   │   │   ├── GlobalExceptionHandler.java
        │   │   │   └── ReviewNotFoundException.java
        │   │   ├── model/
        │   │   │   ├── dto/
        │   │   │   │   ├── ReviewRequestDto.java
        │   │   │   │   └── ReviewResponseDto.java
        │   │   │   └── entity/
        │   │   │       └── Review.java
        │   │   ├── repository/
        │   │   │   └── ReviewRepository.java
        │   │   ├── scheduler/
        │   │   │   └── ReviewScheduler.java           # Cron + fixed-rate jobs
        │   │   └── service/
        │   │       ├── ReviewService.java
        │   │       └── impl/
        │   │           └── ReviewServiceImpl.java
        │   └── resources/
        │       └── application.properties
        └── test/java/com/reviewservice/
            ├── ReviewServiceIntegrationTest.java
            ├── controller/
            │   └── ReviewControllerTest.java
            └── service/
                └── ReviewServiceImplTest.java
```
