package com.example.kafkaconsumer.service;

import com.example.kafkaconsumer.model.Car;
import com.example.kafkaconsumer.model.CarBooking;
import com.example.kafkaconsumer.model.Users;
import com.example.kafkaconsumer.repository.BookingRepository;
import com.example.kafkaconsumer.repository.CarRepository;
import com.example.kafkaconsumer.repository.UserRepository;
import com.example.kafkaconsumer.request.BookCarRequest;
import jakarta.transaction.Transactional;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BookingService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

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
                            .setAdditionalServices(bookingRequest.getAdditionalServices())
                            .setStatus(bookingRequest.getStatus());
                    // Save booking
                    bookingRepository.save(booking);
                    // Set car as booked
                    car.setBooked(true);
                    carRepository.save(car);
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

    private boolean isCarAvailableForBooking(Long carId) {
        CarBooking carBooking = bookingRepository.findByCarId(carId).orElse(null);
        return carBooking == null;
    }
}
