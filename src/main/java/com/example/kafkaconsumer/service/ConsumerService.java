package com.example.kafkaconsumer.service;

import com.example.kafkaconsumer.model.Car;
import com.example.kafkaconsumer.model.CarBooking;
import com.example.kafkaconsumer.request.BookCarRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    @Autowired
    private BookingService bookingService;

    @KafkaListener(topics = "car-booking-topic", groupId = "car-rental-consumer-group")
    public void consumeBookingRequest(String bookingRequestJson) {
//        System.out.println(msg);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            BookCarRequest bookingRequest = objectMapper.readValue(bookingRequestJson, BookCarRequest.class);
            bookingService.bookCar(bookingRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
