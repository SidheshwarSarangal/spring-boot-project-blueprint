package com.example.starter.cleanup;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class CleanupServiceTest {
    @Test
    void countsRepeatableRuns() {
        CleanupService service = new CleanupService();
        service.expireOldItems();
        service.expireOldItems();
        assertThat(service.runCount()).isEqualTo(2);
    }
}
