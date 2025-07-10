package com.potatalk.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class WebSocketMetrics {

    private final MeterRegistry meterRegistry;
    private final Counter messageCounter;
    private final Timer messageLatency;

    public WebSocketMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.messageCounter = meterRegistry.counter("chat.websocket.messages.sent");
        this.messageLatency = meterRegistry.timer("chat.websocket.message.latency");
    }

    public void recordMessage(Runnable sendLogic) {
        messageCounter.increment();
        messageLatency.record(sendLogic); // 메시지 전송 시간 측정
    }
}
