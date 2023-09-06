package com.example.kafkaconsumer.controller;

import com.example.kafkaconsumer.request.SseProcessingStatusPayload;
import com.example.kafkaconsumer.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/qconsumer")
public class MainController {

    @Autowired
    private BookingService bookingService;

    @GetMapping(value = "/status/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sse(
            @RequestParam("userId") Long userId,
            @RequestParam("carId") Long carId
    ) {
        return bookingService.processSseRequest(userId, carId);
    }
}
