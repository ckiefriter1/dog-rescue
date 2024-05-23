// Copyright (c) 2023 by Promineo Tech.

package dog.rescue.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dog.rescue.controller.model.DogInfo;
import dog.rescue.controller.model.LocationData;
import dog.rescue.dao.BreedDao;
import dog.rescue.dao.DogDao;
import dog.rescue.dao.LocationDao;
import dog.rescue.entity.Breed;
import dog.rescue.entity.Dog;
import dog.rescue.entity.Location;

/**
 * This class manages the service layer for the REST API. It is responsible for
 * managing transactions, for gathering data from the DAO layer, and for
 * converting the data into a form that is required by the I/O (controller)
 * layer.
 * 
 * @author Promineo
 *
 */
@Service
public class RescueService {

  /** This tells Spring to inject the location DAO object. */
  @Autowired
  private LocationDao locationDao;

  @Autowired
  private BreedDao breedDao;

  @Autowired
  private DogDao dogDao;

  /**
   * Save the location. If the location ID is null, the location is inserted. If
   * the location ID is not null, the location is updated.
   * 
   * @param locationData The data to insert or modify.
   * @return The saved location data.
   */
  @Transactional(readOnly = false)
  public LocationData saveLocation(LocationData locationData) {
    Location location = locationData.toLocation();
    Location dbLocation = locationDao.save(location);

    return new LocationData(dbLocation);
  }

  /**
   * Retrieve a single location by locationID.
   * 
   * @param locationId The location ID.
   * @return The location data.
   */
  @Transactional(readOnly = true)
  public LocationData retrieveLocationById(Long locationId) {
    Location location = findLocationById(locationId);
    return new LocationData(location);
  }

  /**
   * This returns a location entity if it finds a matching location ID or else
   * throws an exception if there is no match for the location ID.
   * 
   * @param locationId The ID of the location.
   * @return A location entity.
   * @throws NoSuchElementException Thrown if the location ID is not found in
   *         the location table.
   */
  private Location findLocationById(Long locationId) {
    return locationDao.findById(locationId)
        .orElseThrow(() -> new NoSuchElementException(
            "Location with ID=" + locationId + " was not found."));
  }

  /**
   * Retrieve all locations as a list of location entities and convert to a list
   * of location data objects. The list is sorted by business name.
   * 
   * @return A list of location data objects.
   */
  @Transactional(readOnly = true)
  public List<LocationData> retrieveAllLocations() {
    /*
     * Uncomment the code below to retrieve, sort, and convert location data
     * using a loop.
     */
    // List<Location> locationEntities = locationDao.findAll();
    // List<LocationData> locationDtos = new LinkedList<>();
    //
    // locationEntities
    // .sort((loc1, loc2) -> sortLocationByBusinessName(loc1, loc2));
    //
    // for(Location location : locationEntities) {
    // LocationData locationData = new LocationData(location);
    // locationDtos.add(locationData);
    // }
    //
    // return locationDtos;

    // @formatter:off

    /*
     * Uncomment the code below to retrieve, sort, and convert location data
     * using a stream and lambda expressions.
     */
//    return locationDao.findAll()
//        .stream()
//        .sorted((loc1, loc2) -> sortLocationByBusinessName(loc1, loc2))
//        .map(loc -> new LocationData(loc))
//        .toList();

    /*
     * Uncomment the code below to retrieve, sort, and convert location data
     * using a stream and a method references.
     */
     return locationDao.findAll()
         .stream()
         .sorted(this::sortLocationByBusinessName)
         .map(LocationData::new)
         .toList();
    // @formatter:on
  }

  /**
   * This helps implement the Comparator interface in the sort methods, above.
   * It takes two location objects as parameters.
   * 
   * @param loc1 The first location.
   * @param loc2 The second location.
   * @return If the business name of the first location is alphabetically
   *         "greater" than the second, a positive integer is returned. If the
   *         business name of the first location is alphabetically "less" than
   *         the second, a negative number is returned. If the two business
   *         names are the same, zero is returned.
   */
  private int sortLocationByBusinessName(Location loc1, Location loc2) {
    return loc1.getBusinessName().compareTo(loc2.getBusinessName());
  }

  /**
   * Save a dog record and associate it with a location.
   * 
   * @param locationId The ID of the location on which to add the dog record.
   * @param dogInfo The dog information passed to the application as JSON in the
   *        HTTP request body.
   * @return The dog information, including the primary key value.
   * @throws NoSuchElementException Thrown if the location does not exist.
   */
  @Transactional(readOnly = false)
  public DogInfo saveDog(Long locationId, DogInfo dogInfo) {
    Location location = findLocationById(locationId);
    Set<String> breedNames = extractBreedNames(dogInfo);
    Set<Breed> breeds = breedDao.findByNameIn(breedNames);

    Long dogId = dogInfo.getDogId();
    Dog dog = findOrCreateDog(dogId);

    copyDogFields(dog, dogInfo);

    dog.setBreeds(breeds);
    location.getDogs().add(dog);
    dog.setLocation(location);

    return new DogInfo(dogDao.save(dog));
  }

  /**
   * Copy matching field values in the client-supplied JSON to the dog entity.
   * 
   * @param dog The target entity.
   * @param dogInfo The source JSON converted to a Java object.
   */
  private void copyDogFields(Dog dog, DogInfo dogInfo) {
    dog.setAge(dogInfo.getAge());
    dog.setColor(dogInfo.getColor());
    dog.setName(dogInfo.getName());
  }

  /**
   * Return a Dog entity retrieved from the dog table or an empty Dog object.
   * 
   * @param dogId The dog ID of the row to retrieve.
   * @return If the dog ID is not {@code null}, the dog row is returned. If the
   *         dog ID is {@code null} an empty dog object is returned.
   */
  private Dog findOrCreateDog(Long dogId) {
    return Objects.nonNull(dogId) ? findDogById(dogId) : new Dog();
  }

  /**
   * Retrieves a dog object with the given ID.
   * 
   * @param dogId The dog ID.
   * @return The matching dog object.
   * @throws NoSuchElementException Thrown if there is no matching dog object.
   */
  private Dog findDogById(Long dogId) {
    return dogDao.findById(dogId).orElseThrow(() -> new NoSuchElementException(
        "Dog with ID=" + dogId + " was not found."));
  }

  /**
   * Extract the breed names from the breed objects in the dog info object. This
   * list of names is used to retrieve the Breed entities associated with the
   * given dog.
   * 
   * @param dogInfo The dog info object.
   * @return A set of breed names.
   */
  private Set<String> extractBreedNames(DogInfo dogInfo) {
    // @formatter:off
    return dogInfo.getBreeds()
        .stream()
        .map(breed -> breed.getName())
        .collect(Collectors.toSet());
    // @formatter:on

    /*
     * Uncomment the code below to use a loop instead of a Stream to extract the
     * breed names.
     */
    // Set<String> names = new HashSet<>();
    //
    // for(DogInfoBreed breed : dogInfo.getBreeds()) {
    // names.add(breed.getName());
    // }
    //
    // return names;
  }

  /**
   * Delete the location, including all related dog and dog_breed rows.
   * 
   * @param locationId The ID of the location to delete.
   */
  @Transactional(readOnly = false)
  public void deleteLocation(Long locationId) {
    Location location = findLocationById(locationId);
    locationDao.delete(location);
  }

  /*
   * Retrieve all dogs at a given location.
   */
  public List<DogInfo> retrieveAllDogsAtLocation(Long locationId) {
	
	List<Dog> fullDogList = dogDao.findAll();
	List<Dog> filteredDogList = new ArrayList<Dog>();
	
	/*
	 * Query all dogs and filter out dogs for only the chosen location.
	 */
	Iterator<Dog> listIterator = fullDogList.iterator();
	while (listIterator.hasNext()) {
		Dog d = listIterator.next();
		if (d.getLocation().getLocationId() == locationId) {
			filteredDogList.add(d);
		}
	}
	
	List<DogInfo> dl = new ArrayList<DogInfo>();
	Iterator<Dog> i = filteredDogList.iterator();
	while (i.hasNext()) {
		DogInfo di = new DogInfo(i.next());
		di.setLocation(null);
		dl.add(di);
	}
	
	return dl;

}
}
