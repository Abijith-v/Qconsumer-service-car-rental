package com.example.kafkaconsumer.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    // TTL - 1 week
    @PostConstruct
    public void initTTLIndexes() {
        mongoTemplate.indexOps("booking_sse_event").ensureIndex(new Index().on("createdAt", Sort.Direction.ASC).expire(60L, TimeUnit.SECONDS));
    }
}
