// Copyright (c) 2023 by Promineo Tech.

package dog.rescue.entity;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * This entity class tells Spring JPA how to manage I/O for the dog table. JPA
 * does not create the table but it inserts, updates and deletes data from it.
 * This also defines the relationship between the dog and location table
 * (many-to-one) and between the dog and breed table (many-to-many).
 * 
 * @author Promineo
 *
 */
@Entity
@Data
public class Dog {
  /**
   * These annotations tell Spring JPA that the <em>dogId</em> field if the
   * primary key (identity field) and that MySQL will manage the values of the
   * identity column.
   * 
   * Note that all fields except the primary key field are annotated by
   * EqualsAndHashCode.Exclude. This means that you can change any field in the
   * dog row. It also has the effect of only allowing one dog to be added at a
   * time. This is because dog objects are added to a Set. If more than one dog
   * with a {@code null} primary key is added, the later additions will
   * overwrite the earlier ones.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long dogId;

  @EqualsAndHashCode.Exclude
  private String name;

  @EqualsAndHashCode.Exclude
  private int age;

  @EqualsAndHashCode.Exclude
  private String color;

  /**
   * This defines the "owned" side of the one-to-many relationship between
   * location and dog. A dog can have only one location but a location can have
   * many dogs. Note that the recursive location variable is also annotated with
   * ToString.Exclude. This is required to deal with recursion in case a
   * Location entity is printed.
   */
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @ManyToOne
  @JoinColumn(name = "location_id", nullable = false)
  private Location location;

  /**
   * This defines the "owner" side of the many-to-many relationship between dog
   * and breed. It tells Spring JPA how the join table is formatted.
   */
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @ManyToMany(cascade = CascadeType.PERSIST)
  @JoinTable(name = "dog_breed", joinColumns = @JoinColumn(name = "dog_id"),
      inverseJoinColumns = @JoinColumn(name = "breed_id"))
  private Set<Breed> breeds = new HashSet<>();
}
