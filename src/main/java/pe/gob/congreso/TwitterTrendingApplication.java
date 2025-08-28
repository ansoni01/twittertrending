package pe.gob.congreso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TwitterTrendingApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitterTrendingApplication.class, args);
	}

}
