package com.example.starter.importjob;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class ImportServiceTest {
    @Test
    void importsSampleItems() {
        ImportService service = new ImportService();
        assertThat(service.importSample()).isEqualTo(3);
        assertThat(service.importedCount()).isEqualTo(3);
    }
}
