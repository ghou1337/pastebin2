package pl.pastebin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class PastebinApplication {
	public static void main(String[] args) {
		SpringApplication.run(PastebinApplication.class, args);
	}

}
