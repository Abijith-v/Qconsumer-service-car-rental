package com.example.kafkaconsumer.service;

import com.example.kafkaconsumer.model.BookingSseEvent;
import com.example.kafkaconsumer.model.Car;
import com.example.kafkaconsumer.model.CarBooking;
import com.example.kafkaconsumer.model.Users;
import com.example.kafkaconsumer.repository.BookingRepository;
import com.example.kafkaconsumer.repository.BookingSseEventRepository;
import com.example.kafkaconsumer.repository.CarRepository;
import com.example.kafkaconsumer.repository.UserRepository;
import com.example.kafkaconsumer.request.BookCarRequest;
import com.example.kafkaconsumer.request.PaymentConfirmationPayload;
import jakarta.transaction.Transactional;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class BookingService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingSseEventRepository bookingSseEventRepository;

    @Autowired
    private BookingSseEventsService bookingSseEventsService;


    @Transactional
    public void bookCar(BookCarRequest bookingRequest) {
        try {
            Users user = userRepository.findByEmail(bookingRequest.getUserEmail()).orElse(null);
            if (isCarAvailableForBooking(bookingRequest.getCarId()) && user != null) {
                CarBooking booking = new CarBooking();
                Car car = carRepository.findById(bookingRequest.getCarId()).orElse(null);
                if (car != null) {
                    booking.setCar(car)
                            .setUser(user)
                            .setPickupDate(bookingRequest.getPickupDate())
                            .setDropOffDate(bookingRequest.getDropOffDate())
                            .setAdditionalServices(bookingRequest.getAdditionalServices());
//                            .setStatus(bookingRequest.getStatus());
                    // Save booking
                    CarBooking newBooking = bookingRepository.save(booking);
                    PaymentConfirmationPayload newBookingPayload = new PaymentConfirmationPayload();
                    newBookingPayload.setCarId(car.getId())
                                    .setUserId(user.getUserId())
                                    .setId(newBooking.getId());
                    // Set car as booked
                    car.setBooked(true);
                    carRepository.save(car);
                    saveSseEventAndSendMsg(newBookingPayload, "Payment pending");
                } else {
                    System.out.println("Car not found!");
                }
            } else {
                System.out.println("Car already booked");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveSseEventAndSendMsg(PaymentConfirmationPayload newBooking, String message) {
        // Save SSE message in mongo that booking is done

        BookingSseEvent sseEvent = bookingSseEventRepository.findByUserIdAndCarId(
                newBooking.getUserId(),
                newBooking.getCarId()
        ).orElse(new BookingSseEvent());
        sseEvent.setUserId(newBooking.getUserId())
                .setCarId(newBooking.getCarId())
                .setBookingId(newBooking.getId())
                .setMessage(message);

        System.out.println("Saving SSE " + message);
        bookingSseEventRepository.save(sseEvent);
    }

    private boolean isCarAvailableForBooking(Long carId) {
        CarBooking carBooking = bookingRepository.findByCarId(carId).orElse(null);
        return carBooking == null;
    }

    public SseEmitter processSseRequest(Long userId, Long carId) throws Exception {

        SseEmitter emitter = new SseEmitter();

        // Schedule a task to periodically check for updates
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            BookingSseEvent sseEvent = bookingSseEventsService.getPendingSseEvents(userId, carId);
            if (sseEvent != null) {
                this.sendSseMessageToClients(sseEvent, emitter);
            } else {
                try {
                    throw new Exception("No events for this user and car at the moment");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 5, TimeUnit.SECONDS); // Check for updates every 5 seconds
        // Complete the emitter
        emitter.onCompletion(() -> {
            executor.shutdown();
            emitter.complete();
        });

        return emitter;
    }

    // Method to send SSE messages
    public void sendSseMessageToClients(BookingSseEvent sseEvent, SseEmitter emitter) {

        try {
            if (sseEvent != null) {
                // Send SSE event with a "message" field
                emitter.send(SseEmitter.event().data(sseEvent.getMessage() + ", Unique ID : " + sseEvent.getBookingId()));
                if (sseEvent.getMessage().endsWith("confirmed")) {
                    emitter.complete();
                }
            } else {
                emitter.send(SseEmitter.event().data("No booking requests"));
            }
        } catch (IOException e) {
            // Handle exceptions (e.g., client disconnected)
            System.out.println(e.getMessage());
        }
    }
}
