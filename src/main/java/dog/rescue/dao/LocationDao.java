// Copyright (c) 2023 by Promineo Tech.

package dog.rescue.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import dog.rescue.entity.Location;

/**
 * This interface extends the Spring JPA interface JPA repository. Spring
 * creates the backing class for the methods in this interface.
 * 
 * @author Promineo
 *
 */
public interface LocationDao extends JpaRepository<Location, Long> {

}
