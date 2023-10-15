package ma.ac.uir.projets8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class ProjetS8Application{

	public static void main(String[] args) {


		SpringApplication.run(ProjetS8Application.class, args);
	}

}
