package com.example.kafkaconsumer.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentConfirmationPayload {

    private Long id;
    private Long carId;
    private Long userId;
}
