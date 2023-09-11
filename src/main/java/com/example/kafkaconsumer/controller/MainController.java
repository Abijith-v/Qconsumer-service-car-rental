package com.example.kafkaconsumer.controller;

import com.example.kafkaconsumer.model.CarBooking;
import com.example.kafkaconsumer.request.PaymentConfirmationPayload;
import com.example.kafkaconsumer.request.SseProcessingStatusPayload;
import com.example.kafkaconsumer.service.BookingService;
import com.example.kafkaconsumer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/qconsumer")
public class MainController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/status/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sse(
            @RequestParam("userId") Long userId,
            @RequestParam("carId") Long carId,
            @RequestHeader("Authorization") String token
    ) {
        String message = "Invalid token";
        if (userService.validateToken(token)) {
            try {
                return bookingService.processSseRequest(userId, carId);
            } catch (Exception e) {
                message = e.getMessage();
            }
        }
        try {
            SseEmitter emitter = new SseEmitter();
            emitter.send(SseEmitter.event().name("error").data(message));
            emitter.complete(); // Close the connection
            return emitter;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @PostMapping("/sse/payment/confirmation")
    public void sendPaymentConfirmation(@RequestHeader("Authorization") String token, @RequestBody PaymentConfirmationPayload carBookingPayload) {
        bookingService.saveSseEventAndSendMsg(carBookingPayload, "Payment confirmed");
    }
}
