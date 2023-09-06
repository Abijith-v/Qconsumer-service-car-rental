package com.example.kafkaconsumer.request;

import lombok.Data;

@Data
public class SseProcessingStatusPayload {

    private Long userId;
    private Long carId;
}
