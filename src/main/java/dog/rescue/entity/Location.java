// Copyright (c) 2023 by Promineo Tech.

package dog.rescue.entity;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

/**
 * This class is used by Spring JPA to manage data I/O on the breed table. It
 * defines the relationship between the breed table and the dog table.
 * 
 * @author Promineo
 *
 */
@Entity
@Data
public class Location {
  /**
   * These annotations tell Spring JPA that the <em>locationId</em> field is the
   * primary key field and that the values in the primary key are managed by
   * MySQL.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long locationId;

  private String businessName;
  private String streetAddress;
  private String city;
  private String state;
  private String zip;
  private String phone;

  /**
   * This defines the one-to-many relationship between the location and dog
   * tables. This is the "owner" side of the relationship. The "mappedBy"
   * attributes causes Hibernate to create a bidirectional one-to-many
   * relationship.
   */
  @OneToMany(mappedBy = "location", cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<Dog> dogs = new HashSet<>();
}
