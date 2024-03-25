// Copyright (c) 2023 by Promineo Tech.

package dog.rescue.controller.model;

import java.util.HashSet;
import java.util.Set;
import dog.rescue.entity.Breed;
import dog.rescue.entity.Dog;
import dog.rescue.entity.Location;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is a Data Transfer Object (DTO) that is used to input data to the
 * application and return results from the application. It is the same as the
 * entity classes but does not contain the recursive variables or any JPA
 * annotations.
 * 
 * This class contains two inner classes, both of which are used externally by
 * the application. To facilitate this the classes are both marked with the
 * <em>public</em> and <em>static</em> keywords.
 * 
 * @Data typically adds a zero-argument constructor but this is removed if we
 *       add another constructor. Since Jackson requires a zero-argument
 *       constructor to marshal/unmarshal JSON, the zero-argument constructor is
 *       added back with the NoArgsConstructor annotation.
 * 
 * @author Promineo
 *
 */
@Data
@NoArgsConstructor
public class LocationData {
  private Long locationId;
  private String businessName;
  private String streetAddress;
  private String city;
  private String state;
  private String zip;
  private String phone;
  private Set<DogData> dogs = new HashSet<>();

  /**
   * This constructor converts a Location entity to a LocationData object.
   * 
   * @param location The Location entity.
   */
  public LocationData(Location location) {
    this.locationId = location.getLocationId();
    this.businessName = location.getBusinessName();
    this.streetAddress = location.getStreetAddress();
    this.city = location.getCity();
    this.state = location.getState();
    this.zip = location.getZip();
    this.phone = location.getPhone();

    for(Dog dog : location.getDogs()) {
      this.dogs.add(new DogData(dog));
    }
  }

  /**
   * This constructor is used for the tests to create a LocationData object from
   * constants or literals.
   * 
   * @param locationId The location ID (primary key)
   * @param businessName The name of the business
   * @param streetAddress The street address of the business
   * @param city The address city
   * @param state The address state
   * @param zip The address zip code
   * @param phone The business phone number
   */
  public LocationData(Long locationId, String businessName,
      String streetAddress, String city, String state, String zip,
      String phone) {
    this.locationId = locationId;
    this.businessName = businessName;
    this.streetAddress = streetAddress;
    this.city = city;
    this.state = state;
    this.zip = zip;
    this.phone = phone;
  }

  /**
   * Convert from a LocationData object to a Location entity.
   * 
   * @return The location entity.
   */
  public Location toLocation() {
    Location location = new Location();

    location.setLocationId(locationId);
    location.setBusinessName(businessName);
    location.setStreetAddress(streetAddress);
    location.setCity(city);
    location.setState(state);
    location.setZip(zip);
    location.setPhone(phone);

    for(DogData dogData : dogs) {
      location.getDogs().add(dogData.toDog());
    }

    return location;
  }

  /**
   * This is the DogData inner class that contains the same fields as the Dog
   * entity class without recursion in the class variables.
   * 
   * @author Promineo
   *
   */
  @Data
  @NoArgsConstructor
  public static class DogData {
    private Long dogId;
    private String name;
    private int age;
    private String color;
    private Set<BreedData> breeds = new HashSet<>();

    /**
     * Convert from a Dog entity object to a DogData object.
     * 
     * @param dog The Dog entity
     */
    public DogData(Dog dog) {
      this.dogId = dog.getDogId();
      this.name = dog.getName();
      this.age = dog.getAge();
      this.color = dog.getColor();

      for(Breed breed : dog.getBreeds()) {
        this.breeds.add(new BreedData(breed));
      }
    }

    /**
     * Convert from a DogData object to a Dog entity object.
     * 
     * @return The Dog entity object.
     */
    public Dog toDog() {
      Dog dog = new Dog();

      dog.setDogId(dogId);
      dog.setName(name);
      dog.setAge(age);
      dog.setColor(color);

      for(BreedData breedData : breeds) {
        dog.getBreeds().add(breedData.toBreed());
      }

      return dog;
    }
  }

  /**
   * This inner class represents a Breed entity without the recursive variables
   * or JPA semantics.
   * 
   * @author Promineo
   *
   */
  @Data
  @NoArgsConstructor
  public static class BreedData {
    private Long breedId;
    private String name;

    /**
     * Convert from a Breed entity object to a BreedData object.
     * 
     * @param breed The Breed entity object.
     */
    public BreedData(Breed breed) {
      this.breedId = breed.getBreedId();
      this.name = breed.getName();
    }

    /**
     * Convert from a BreedData object to a Breed entity object.
     * 
     * @return The Breed entity object.
     */
    public Breed toBreed() {
      Breed breed = new Breed();

      breed.setBreedId(breedId);
      breed.setName(name);

      return breed;
    }
  }
}
