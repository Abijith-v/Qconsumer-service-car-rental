package com.example.kafkaconsumer.service;

import com.example.kafkaconsumer.model.BookingSseEvent;
import com.example.kafkaconsumer.repository.BookingSseEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingSseEventsService {

    @Autowired
    private BookingSseEventRepository bookingSseEventRepository;

    public BookingSseEvent getPendingSseEvents(Long userId, Long carId) {
        return bookingSseEventRepository.findByUserIdAndCarId(userId, carId);
    }
}
