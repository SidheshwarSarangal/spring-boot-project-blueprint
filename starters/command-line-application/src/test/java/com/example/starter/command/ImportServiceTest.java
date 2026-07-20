package com.example.starter.command;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class ImportServiceTest {
    @Test
    void importsForName() {
        assertThat(new ImportService().importFor("customer-a"))
            .isEqualTo("Imported data for customer-a");
    }
}
