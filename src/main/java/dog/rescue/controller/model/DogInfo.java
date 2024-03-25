// Copyright (c) 2023 by Promineo Tech.

package dog.rescue.controller.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import dog.rescue.entity.Breed;
import dog.rescue.entity.Dog;
import dog.rescue.entity.Location;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class moves dog-centric information throughout the various parts of the
 * application. This class is the similar to the {@link Dog} entity but does not
 * have the recursive variables that make printing and converting to/from JSON
 * so problematic.
 * 
 * @author Promineo
 *
 */
@Data
@NoArgsConstructor
public class DogInfo {
  private Long dogId;
  private String name;
  private int age;
  private String color;
  private DogInfoLocation location;
  private Set<DogInfoBreed> breeds = new HashSet<>();

  /**
   * This constructor converts from a {@link Dog} entity object to a DogInfo
   * object.
   * 
   * @param dog The Dog object.
   */
  public DogInfo(Dog dog) {
    this.dogId = dog.getDogId();
    this.name = dog.getName();
    this.age = dog.getAge();
    this.color = dog.getColor();

    if(Objects.nonNull(dog.getLocation())) {
      this.location = new DogInfoLocation(dog.getLocation());
    }

    for(Breed breed : dog.getBreeds()) {
      this.breeds.add(new DogInfoBreed(breed));
    }
  }

  /**
   * This constructor is used by the tests to create a common DogInfo object.
   * 
   * @param dogId The ID of the Dog row.
   * @param name The name of the dog.
   * @param age The age of the dog.
   * @param color The color of the dog.
   * @param breeds A set of dog breeds.
   */
  public DogInfo(Long dogId, String name, int age, String color,
      Set<DogInfoBreed> breeds) {
    this.dogId = dogId;
    this.name = name;
    this.age = age;
    this.color = color;
    this.breeds = breeds;
  }

  /**
   * Converts from a DogInfo object to a Dog object.
   * 
   * @return The Dog object.
   */
  public Dog toDog() {
    Dog dog = new Dog();

    dog.setAge(age);
    dog.setColor(color);
    dog.setDogId(dogId);
    dog.setName(name);

    for(DogInfoBreed breed : breeds) {
      dog.getBreeds().add(breed.toBreed());
    }

    return dog;
  }

  /**
   * This inner class is similar to the Location object but does not have the
   * recursive variables.
   * 
   * @author Promineo
   *
   */
  @Data
  @NoArgsConstructor
  public static class DogInfoLocation {
    private Long locationId;
    private String businessName;
    private String streetAddress;
    private String city;
    private String state;
    private String zip;
    private String phone;

    /**
     * Convert from a {@link Location} entity to a DogInfoLocation object.
     * 
     * @param location The location entity.
     */
    public DogInfoLocation(Location location) {
      this.locationId = location.getLocationId();
      this.businessName = location.getBusinessName();
      this.streetAddress = location.getStreetAddress();
      this.city = location.getCity();
      this.state = location.getState();
      this.zip = location.getZip();
      this.phone = location.getPhone();
    }
  }

  /**
   * This inner class is similar to the {@link Breed} class but without the
   * recursive variables.
   * 
   * @author Promineo
   *
   */
  @Data
  @NoArgsConstructor
  public static class DogInfoBreed {
    private Long breedId;
    private String name;

    /**
     * This constructor converts from a {@link Breed} object to a DogInfoBreed
     * object.
     * 
     * @param breed The Breed to convert.
     */
    public DogInfoBreed(Breed breed) {
      this.breedId = breed.getBreedId();
      this.name = breed.getName();
    }

    /**
     * This constructor is used by the tests to create common Breed data.
     * 
     * @param breedId The ID of the Breed object.
     * @param name The breed name.
     */
    public DogInfoBreed(Long breedId, String name) {
      this.breedId = breedId;
      this.name = name;
    }

    /**
     * Convert from this DogInfoBreed object to a Breed object.
     * 
     * @return The Breed object.
     */
    public Breed toBreed() {
      Breed breed = new Breed();

      breed.setBreedId(breedId);
      breed.setName(name);

      return breed;
    }
  }

}
