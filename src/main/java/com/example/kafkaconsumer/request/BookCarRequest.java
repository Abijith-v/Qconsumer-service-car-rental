package com.example.kafkaconsumer.request;

import lombok.Data;

import java.util.Date;

@Data
public class BookCarRequest {

    private String userEmail;
    private Long carId;
    private Date pickupDate;
    private Date dropOffDate;
    private String additionalServices;
    private String status;
}
