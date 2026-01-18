package com.example.log4u.common.infra.local_cache.message_publisher;

public interface MessagePublisher {

    void publish(String channel, String message);
}
