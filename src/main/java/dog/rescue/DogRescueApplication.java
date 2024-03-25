// Copyright (c) 2023 by Promineo Tech.

package dog.rescue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class contains the entry point to the Java application (the
 * {@link #main(String[])} method. The main method simply starts Spring Boot.
 * 
 * @author Promineo
 *
 */
@SpringBootApplication
public class DogRescueApplication {

  /**
   * Starts Spring Boot as a Web application.
   * 
   * @param args Unused.
   */
  public static void main(String[] args) {
    SpringApplication.run(DogRescueApplication.class, args);
  }

}
