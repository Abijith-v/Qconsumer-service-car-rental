package com.example.kafkaconsumer.repository;

import com.example.kafkaconsumer.model.BookingSseEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingSseEventRepository extends MongoRepository<BookingSseEvent, String> {

    @Query("{'userId' : ?0, 'carId' : ?1}")
    public BookingSseEvent findByUserIdAndCarId(Long userId, Long carId);
}
