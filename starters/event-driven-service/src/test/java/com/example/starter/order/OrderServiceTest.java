package com.example.starter.order;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class OrderServiceTest {
    @Test
    void ignoresDuplicateEvent() {
        OrderService service = new OrderService();
        assertThat(service.process("event-1")).isTrue();
        assertThat(service.process("event-1")).isFalse();
        assertThat(service.processedCount()).isEqualTo(1);
    }
}
