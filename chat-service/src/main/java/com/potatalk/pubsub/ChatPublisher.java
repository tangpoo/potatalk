package com.potatalk.pubsub;

import com.potatalk.dto.ChatMessageDto;

public interface ChatPublisher {

    void publish(ChatMessageDto message);
}
