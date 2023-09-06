package com.example.kafkaconsumer.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "booking_sse_event")
@Data
@Accessors(chain = true)
public class BookingSseEvent {
    @Id
    private String id;
    private Long userId;
    private Long carId;
    private Long bookingId;
}
