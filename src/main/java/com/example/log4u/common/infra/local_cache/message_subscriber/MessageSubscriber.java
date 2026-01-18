package com.example.log4u.common.infra.local_cache.message_subscriber;

import org.springframework.data.redis.connection.MessageListener;

public interface MessageSubscriber {

    void addMessageListener(String channel, MessageListener listener);

    String parseMessage(byte[] message);
}
