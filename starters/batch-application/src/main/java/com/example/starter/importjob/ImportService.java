package com.example.starter.importjob;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;

@Service
public class ImportService {
    private final List<String> imported = new CopyOnWriteArrayList<>();

    public int importSample() {
        imported.addAll(List.of("alpha", "beta", "gamma"));
        return imported.size();
    }

    public int importedCount() {
        return imported.size();
    }
}
