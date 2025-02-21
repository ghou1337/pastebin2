package pl.pastebin.hash_generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pl.pastebin.hash_generator.service.HashPoolService;

@SpringBootApplication
@EnableScheduling
public class HashGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(HashGeneratorApplication.class, args);
	}
}
