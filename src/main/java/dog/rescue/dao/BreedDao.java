// Copyright (c) 2023 by Promineo Tech.

package dog.rescue.dao;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import dog.rescue.entity.Breed;

/**
 * This interface extends the Spring JPA interface JPA repository. Spring
 * creates the backing class for the methods in this interface.
 * 
 * @author Promineo
 *
 */
public interface BreedDao extends JpaRepository<Breed, Long> {

  /**
   * Spring JPA creates the implementing method to find a list of Breed entities
   * given the breed names.
   * 
   * @param breedNames A set of breed names.
   * @return A set of Breed entities.
   */
  Set<Breed> findByNameIn(Set<String> breedNames);
}
