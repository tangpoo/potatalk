package com.potatalk.chatroomservice.publisher;

import reactor.core.publisher.Mono;

public interface ChatRoomPublisher {

    Mono<String> sendAddTopicEvent(String roomId);
}
