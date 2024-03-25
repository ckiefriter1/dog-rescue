// Copyright (c) 2023 by Promineo Tech.

package dog.rescue.controller;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import dog.rescue.DogRescueApplication;
import dog.rescue.controller.model.DogInfo;
import dog.rescue.controller.model.LocationData;

/**
 * This class tests the operations coded in the dog rescue applications. The
 * Spring Boot Test Framework has the ability to test at any level from unit
 * tests of methods up to testing against the application running under an
 * embedded Tomcat server.
 * 
 * This class takes a middle-of-the-road approach. Tests are performed against
 * the controller class. Spring Boot creates an application context for the
 * tests so that almost the entire application is tested.
 * 
 * The live MySQL database is replaced by an in-memory H2 database. Before each
 * test, all tables are dropped, recreated and populated so that each test
 * always starts in a known state. This means that there is no way that a test
 * failure can pollute the database for any other test.
 * 
 * Using this approach, the only part of the application that is not tested is
 * marshaling and unmarshaling from JSON to Java and vice versa, as well as any
 * Bean Validation that may be performed. Whereas this approach <em>could</em>
 * be taken, it makes the tests more complex and less understandable. Students
 * have had to overcome a conceptual hurdle with this kind of testing in the
 * past, so the current approach has been adopted to mitigate the
 * misunderstanding.
 * 
 * Here are explanations for the class-level annotations:
 * 
 * @SpringBootTest This sets the Web environment to none, meaning that an
 *                 embedded Tomcat server is not used to run the application.
 *                 This also sets the configuration class for the test to the
 *                 main application class. This allows Spring Boot to run the
 *                 component scan correctly, and initiates auto-configuration.
 * 
 * @ActiveProfiles This sets the profile for the test to "test". The result of
 *                 this is that Spring Boot will look for application-test.yaml
 *                 on the classpath. It then merges the contents of
 *                 application-test.yaml with the main application configuration
 *                 file application.yaml. This allows us to overwrite the
 *                 database so that an in-memory database is used for the tests.
 * 
 * @Sql This loads two scripts: schema.sql and data.sql. When Spring Boot loads
 *      and executes schema.sql, all tables are dropped and recreated. When
 *      Spring Boot loads and executes data.sql, the breed table is populated
 *      with the dog breed names. Both file names are prefixed with
 *      "classpath:", which means that Spring Boot will look for the files in
 *      the runtime classpath (src/test/resources).
 * 
 * @SqlConfig This allows us to set the file encoding in the scripts to UTF-8.
 *            This is necessary as some of the data is not in the ANSI character
 *            set.
 * 
 * @author Promineo
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.NONE,
    classes = DogRescueApplication.class)
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
@SqlConfig(encoding = "utf-8")
class RescueControllerTest extends RescueControllerTestSupport {

  /**
   * This tests the controller method
   * {@link RescueController#createLocation(LocationData)}. In this test, a
   * location is inserted into the location table and compared with the expected
   * value.
   */
  @Test
  void testInsertLocation() {
    // Given: A location request
    LocationData request = buildInsertLocation(1);
    LocationData expected = buildInsertLocation(1);

    // When: the location is added to the location table
    LocationData actual = insertLocation(request);

    // Then: the location returned is what is expected
    assertThat(actual).isEqualTo(expected);

    // And: there is one row in the location table.
    assertThat(rowsInLocationTable()).isOne();
  }

  /**
   * This tests the controller method
   * {@link RescueController#retrieveLocation(Long)}. In this test, a location
   * is inserted into the location table. The controller method is called to
   * retrieve the location and it is compared to the expected value.
   */
  @Test
  void testRetrieveLocation() {
    // Given a location
    LocationData location = insertLocation(buildInsertLocation(1));
    LocationData expected = buildInsertLocation(1);

    // When: the location is retrieved by location ID
    LocationData actual = retrieveLocation(location.getLocationId());

    // Then: the actual location is equal to the expected location.
    assertThat(actual).isEqualTo(expected);
  }

  /**
   * This tests the controller method
   * {@link RescueController#retrieveAllLocations()}. In this test, two
   * locations are inserted into the database. Then they are retrieved and
   * compared with the expected values. To alleviate any issues involving the
   * sort order between the expected locations and the actual locations
   * returned, both actual and expected locations are sorted before comparing
   * them.
   */
  @Test
  void testRetrieveAllLocations() {
    // Given: two locations
    List<LocationData> expected = insertTwoLocations();

    // When: all locations are retrieved
    List<LocationData> actual = retrieveAllLocations();

    // Then: the retrieved locations are the same as expected.
    assertThat(sorted(actual)).isEqualTo(sorted(expected));
  }

  /**
   * This tests the controller method
   * {@link RescueController#updateLocation(Long, LocationData)}. In this test,
   * a location is inserted. Then the location is updated with new details. Then
   * the returned location is compared to the expected (updated) location.
   */
  @Test
  void testUpdateLocation() {
    // Given: a location and an update request
    insertLocation(buildInsertLocation(1));
    LocationData expected = buildUpdateLocation();

    // When: the location is updated
    LocationData actual = updateLocation(expected);

    // Then: the location is returned as expected
    assertThat(actual).isEqualTo(expected);

    // And: there is one row in the location table
    assertThat(rowsInLocationTable()).isOne();
  }

  /**
   * This tests {@link RescueController#insertDog(Long, DogInfo)}. In this test,
   * a location is inserted. Then a dog is inserted and added to the location.
   * Finally, the dog result is compared to the expected dog object.
   */
  @Test
  void testInsertDog() {
    // Given: a location and a dog
    LocationData location = insertLocation(buildInsertLocation(1));

    DogInfo request = buildInsertDog(1);
    DogInfo expected = buildDog(location, request);

    // When: the dog is inserted
    DogInfo actual = insertDog(location.getLocationId(), 1);

    // Then: the dog ID is set and the location is set in the dog object
    assertThat(actual).isEqualTo(expected);

    // And: there is a single row in the dog table.
    assertThat(rowsInDogTable()).isOne();
  }

  /**
   * This tests {@link RescueController#deleteLocation(Long)}. In this test, a
   * location and two dogs are inserted into the database, along with the 
   * appropriate join table rows.
   */
  @Test
  void testDeleteLocationWithDogs() {
    // Given: two dogs and a location
    Long locationId = insertLocationAndTwoDogs();
    int breedRows = rowsInBreedTable();

    assertLocationAndDogRowsAreAddedCorrectly();

    // When: the location is deleted
    deleteLocation(locationId);

    // Then: there are no location, dog, or dog_breed rows
    assertLocationAndDogRowsAreGoneAfterDeletion();

    // And: the number of breed rows has not changed
    assertThat(rowsInBreedTable()).isEqualTo(breedRows);
  }

}
