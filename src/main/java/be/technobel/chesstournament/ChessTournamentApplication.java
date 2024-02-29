package be.technobel.chesstournament;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class ChessTournamentApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChessTournamentApplication.class, args);
	}

}
