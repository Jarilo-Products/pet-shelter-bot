package pro.sky.petshelterbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PetShelterBotApplication {

  public static void main(String[] args) {
    SpringApplication.run(PetShelterBotApplication.class, args);
  }

}
