package pl.pastebin.hash_generator.scheduler;

import org.springframework.stereotype.Component;
import pl.pastebin.hash_generator.service.HashPoolService;
import org.springframework.scheduling.annotation.Scheduled;

@Component
public class HashPoolScheduler {
    private final HashPoolService hashPoolService;

    public HashPoolScheduler(HashPoolService hashPoolService) {
        this.hashPoolService = hashPoolService;
    }

    @Scheduled(fixedDelay = 5000)
    public void maintainHashPool() {
        hashPoolService.fillPoolAsync();
    }
}
