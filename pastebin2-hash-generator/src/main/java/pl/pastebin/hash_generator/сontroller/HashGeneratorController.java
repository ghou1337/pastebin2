package pl.pastebin.hash_generator.сontroller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pastebin.hash_generator.service.HashPoolService;

@RestController
@RequestMapping("/api")
public class HashGeneratorController {
    private final HashPoolService hashPoolService;

    public HashGeneratorController(HashPoolService hashPoolService) {
        this.hashPoolService = hashPoolService;
    }

    @GetMapping("/get")
    public String getHash() {
        return hashPoolService.getHashFromPool();
    }
}