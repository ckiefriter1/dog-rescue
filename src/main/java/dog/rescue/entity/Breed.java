// Copyright (c) 2023 by Promineo Tech.

package dog.rescue.entity;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * This entity class tells Spring JPA how to manage the breed table.
 * 
 * @author Promineo
 *
 */
@Entity
@Data
public class Breed {
  /**
   * This tells Spring JPA that the <em>breedId</em> field is the identity
   * (primary key) column and that the values are managed by MySQL.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long breedId;

  private String name;

  /**
   * This tells Spring JPA that this is the "owned" side of the many-to-many
   * relationship between dog and breed. The "mappedBy" attribute of the
   * ManyToMany attribute tells Spring JPA that the breeds field (not the name
   * of the table column!) in the Dog class defines the owning side of the
   * relationship.
   */
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @ManyToMany(mappedBy = "breeds")
  private Set<Dog> dogs = new HashSet<>();
}
