// Copyright (c) 2023 by Promineo Tech.

package dog.rescue.controller;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import dog.rescue.controller.model.DogInfo;
import dog.rescue.controller.model.DogInfo.DogInfoBreed;
import dog.rescue.controller.model.DogInfo.DogInfoLocation;
import dog.rescue.controller.model.LocationData;
import dog.rescue.entity.Dog;
import dog.rescue.entity.Location;

/**
 * This class provides helper methods to {@link RescueControllerTest}. This
 * allows the actual tests to be simple and self-documenting.
 * 
 * @author Promineo
 *
 */
public class RescueControllerTestSupport {

  private static final String LOCATION_TABLE = "location";
  private static final String DOG_TABLE = "dog";
  private static final String DOG_BREED_TABLE = "dog_breed";
  private static final String BREED_TABLE = "breed";

  // @formatter:off
  private LocationData insertAddress1 = new LocationData(
      1L,
      "North Hills Dog Rescue Society",
      "52 Pine Street",
      "Abdingdon",
      "Maryland",
      "21009",
      "(410) 459-3200"
  );
  
  private LocationData insertAddress2 = new LocationData(
      2L,
      "Navarre Rescue",
      "42 Valley Farms Street",
      "Navarre",
      "Florida",
      "32556",
      "(850) 204-9485"
  );
  
  private LocationData updateAddress1 = new LocationData(
      1L,
      "Glenlake Dog Rescue Society",
      "8 East Glenlake Drive",
      "Wadsworth",
      "Ohio",
      "83241",
      "(330) 336-2105"
  );
  
  private DogInfo insertDog1 = new DogInfo(
      1L,
      "Ralphy",
      3,
      "Wheaten",
      Set.of(new DogInfoBreed(15L, "Chihuahua"), new DogInfoBreed(22L, "Boxer"))
  );
  
  private DogInfo insertDog2 = new DogInfo(
      2L,
      "Buford",
      6,
      "Black",
      Set.of(new DogInfoBreed(10L, "Poodle"), new DogInfoBreed(18L, "Border Collie"))
  );
  
  // @formatter:on

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private RescueController rescueController;

  /**
   * Returns one of two {@link LocationData} objects for the tests.
   * 
   * @param which Must be either 1 or 2. There is no validity checking on this.
   * @return A location with the primary key value set.
   */
  protected LocationData buildInsertLocation(int which) {
    return which == 1 ? insertAddress1 : insertAddress2;
  }

  /**
   * Calls {@link RescueController#createLocation(LocationData)} to insert a
   * location.
   * 
   * @param locationData The location data. This is converted to a location
   *        object with no location ID before adding it to the location table.
   * @return A complete location data object.
   */
  protected LocationData insertLocation(LocationData locationData) {
    Location location = locationData.toLocation();
    LocationData clone = new LocationData(location);

    clone.setLocationId(null);
    return rescueController.createLocation(clone);
  }

  /**
   * Counts the number of rows in the location table.
   * 
   * @return The number of rows.
   */
  protected int rowsInLocationTable() {
    return JdbcTestUtils.countRowsInTable(jdbcTemplate, LOCATION_TABLE);
  }

  /**
   * Calls {@link RescueControllerTest#retrieveLocation(Long)} to retrieve a
   * location given the ID.
   * 
   * @param locationId The location ID.
   * @return The location object.
   */
  protected LocationData retrieveLocation(Long locationId) {
    return rescueController.retrieveLocation(locationId);
  }

  /**
   * Insert two locations into the location table.
   * 
   * @return A list of the two locations.
   */
  protected List<LocationData> insertTwoLocations() {
    LocationData location1 = insertLocation(buildInsertLocation(1));
    LocationData location2 = insertLocation(buildInsertLocation(2));

    return List.of(location1, location2);
  }

  /**
   * Calls {@link RescueControllerTest#retrieveAllLocations()} to retrieve all
   * locations.
   * 
   * @return The list of locations.
   */
  protected List<LocationData> retrieveAllLocations() {
    return rescueController.retrieveAllLocations();
  }

  /**
   * Sort a list of locations by location ID so that expected and actual
   * location lists can be compared.
   * 
   * @param list The list to sort.
   * @return The sorted list.
   */
  protected List<LocationData> sorted(List<LocationData> list) {
    /*
     * The parameter list may be an immutable list. Convert to a modifiable list
     * before sorting.
     */
    List<LocationData> data = new LinkedList<>(list);

    data.sort(
        (loc1, loc2) -> (int)(loc1.getLocationId() - loc2.getLocationId()));

    return data;
  }

  /**
   * Calls {@link RescueController#updateLocation(Long, LocationData)} to update
   * the given location.
   * 
   * @param locationData The location with an existing ID and modified data.
   * @return The modified location.
   */
  protected LocationData updateLocation(LocationData locationData) {
    return rescueController.updateLocation(locationData.getLocationId(),
        locationData);
  }

  /**
   * Build a {@link LocationData} object for the update operation.
   * 
   * @return The location.
   */
  protected LocationData buildUpdateLocation() {
    return updateAddress1;
  }

  /**
   * Build a {@link DogInfo} object with the {@link LocationData} set.
   * 
   * @param locationData The location data.
   * @param dogInfo The original dog info.
   * @return A dog info object with the location data set.
   */
  protected DogInfo buildDog(LocationData locationData, DogInfo dogInfo) {
    Location location = locationData.toLocation();
    DogInfoLocation dogLocation = new DogInfoLocation(location);

    DogInfo result = new DogInfo(dogInfo.getDogId(), dogInfo.getName(),
        dogInfo.getAge(), dogInfo.getColor(), dogInfo.getBreeds());

    result.setLocation(dogLocation);
    return result;
  }

  /**
   * Returns one of the two common {@link DogInfo} objects.
   * 
   * @param which This must be 1 or 2. There is no validity check.
   * @return The requested DogInfo object.
   */
  protected DogInfo buildInsertDog(int which) {
    return which == 1 ? insertDog1 : insertDog2;
  }

  /**
   * This clones one of the common {@link DogInfo} objects and then calls
   * {@link RescueController#insertDog(Long, DogInfo)} to insert the dog row.
   * 
   * @param locationId The location ID.
   * @param which The common DogInfo object to use (must be 1 or 2).
   * @return The inserted Dog entity converted to a DogInfo object.
   */
  protected DogInfo insertDog(Long locationId, int which) {
    DogInfo dogInfo = buildInsertDog(which);
    Dog dog = dogInfo.toDog();
    DogInfo request = new DogInfo(dog);

    request.setDogId(null);
    return rescueController.insertDog(locationId, request);
  }

  /**
   * Counts the number of rows in the dog table.
   * 
   * @return The number of rows.
   */
  protected int rowsInDogTable() {
    return JdbcTestUtils.countRowsInTable(jdbcTemplate, DOG_TABLE);
  }

  /**
   * Calls {@link RescueController#deleteLocation(Long)} to delete the location,
   * dog, and dog breed rows.
   * 
   * @param locationId The ID of the location to delete.
   * @return A map containing a success message.
   */
  protected Map<String, String> deleteLocation(Long locationId) {
    return rescueController.deleteLocation(locationId);
  }

  /**
   * Counts the number of rows in the breed table.
   * 
   * @return The number of rows.
   */
  protected int rowsInBreedTable() {
    return JdbcTestUtils.countRowsInTable(jdbcTemplate, BREED_TABLE);
  }

  /**
   * Counts the number of rows in the dog breed join table.
   * 
   * @return The number of rows.
   */
  protected int rowsInDogBreedTable() {
    return JdbcTestUtils.countRowsInTable(jdbcTemplate, DOG_BREED_TABLE);
  }

  /**
   * Insert a location and two dog rows.
   * 
   * @return The ID of the inserted location.
   */
  protected Long insertLocationAndTwoDogs() {
    LocationData location = insertLocation(buildInsertLocation(1));
    Long locationId = location.getLocationId();

    insertDog(locationId, 1);
    insertDog(locationId, 2);

    return locationId;
  }

  /**
   * Tests that the number of rows in the dog, breed, and dog_breed tables are
   * correct after adding them in {@link #insertLocationAndTwoDogs()}.
   */
  protected void assertLocationAndDogRowsAreAddedCorrectly() {
    assertThat(rowsInLocationTable()).isOne();
    assertThat(rowsInDogTable()).isEqualTo(2);
    assertThat(rowsInDogBreedTable()).isEqualTo(4);
  }

  /**
   * Tests that the dog, breed, and dog_breed tables are gone after deleting
   * the location row in {@link #deleteLocation(Long)}.
   */
  protected void assertLocationAndDogRowsAreGoneAfterDeletion() {
    assertThat(rowsInLocationTable()).isZero();
    assertThat(rowsInDogTable()).isZero();
    assertThat(rowsInDogBreedTable()).isZero();
  }

}
