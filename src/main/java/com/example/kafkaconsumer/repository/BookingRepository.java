package com.example.kafkaconsumer.repository;

import com.example.kafkaconsumer.model.Car;
import com.example.kafkaconsumer.model.CarBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<CarBooking, Long> {

    public Optional<CarBooking> findByCarId(Long carId);
}
