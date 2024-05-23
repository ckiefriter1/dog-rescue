// Copyright (c) 2023 by Promineo Tech.

package dog.rescue.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import dog.rescue.controller.model.DogInfo;
import dog.rescue.controller.model.LocationData;
import dog.rescue.service.RescueService;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is the controller. Spring routes HTTP requests to methods in this
 * class. The controller methods call methods in the service layer and return
 * the results. The @RequestMapping annotation specifies that every HTTP request
 * URI must start with "/dog_rescue".
 * 
 * @author Promineo
 *
 */
@RestController
@RequestMapping("/dog_rescue")
@Slf4j
public class RescueController {
  /* Spring will inject the rescue service in this variable. */
  @Autowired
  private RescueService rescueService;

  /**
   * This method creates a dog rescue location. The location data is formatted
   * as JSON. It is passed in the HTTP request payload. To call this method,
   * send a POST HTTP request to http://localhost:8080/dog_rescue/location.
   * 
   * @param locationData The input location data to add. It must not create the
   *        primary key value.
   * @return A locationData object with the primary key value.
   */
  @PostMapping("/location")
  @ResponseStatus(code = HttpStatus.CREATED)
  public LocationData createLocation(@RequestBody LocationData locationData) {
    log.info("Creating location {}", locationData);
    return rescueService.saveLocation(locationData);
  }

 /**
   * This method modifies a dog rescue location. The location data is formatted
   * as JSON. It is passed in the HTTP request payload. To call this method,
   * send a PUT HTTP request to http://localhost:8080/dog_rescue/location/{ID}
   * where {ID} is the location ID.
   * 
   * @param locationId The ID of the location to modify.
   * @param locationData The modified location data.
   * @return The locationData object containing the modified data.
   */
  @PutMapping("/location/{locationId}")
  public LocationData updateLocation(@PathVariable Long locationId,
      @RequestBody LocationData locationData) {
    locationData.setLocationId(locationId);
    log.info("Updating location {}", locationData);
    return rescueService.saveLocation(locationData);
  }

  /**
   * Retrieve all details of the location with the given ID. To call this
   * method, send an HTTP GET request to
   * http://localhost:8080/dog_rescue/location/{ID} where {ID} is the ID of the
   * location to retrieve.
   * 
   * @param locationId The ID of the location to retrieve.
   * @return The location details.
   */
  @GetMapping("/location/{locationId}")
  public LocationData retrieveLocation(@PathVariable Long locationId) {
    log.info("Retrieving location with ID={}", locationId);
    return rescueService.retrieveLocationById(locationId);
  }

  /**
   * Retrieve all locations with details. To call this method, send an HTTP GET
   * request to http://localhost:8080/dog_rescue/location.
   * 
   * @return A list of all current locations and dogs.
   */
  @GetMapping("/location")
  public List<LocationData> retrieveAllLocations() {
    log.info("Retrieving all locations");
    return rescueService.retrieveAllLocations();
  }

  /**
   * Add a dog to a location. To call this method, send an HTTP POST request to
   * http://localhost:8080/dog_rescue/location/{locationID}/dog where
   * {locationId} is the location ID.
   * 
   * @param locationId The ID of the location in which to add a dog. This is
   *        passed in the URI as described above.
   * @param dogInfo The dog information. This is passes as JSON in the request
   *        body.
   * @return List of all dogs at a given location.
   */
  @GetMapping("/location/{locationId}/dog")
  public List<DogInfo> retrieveAllDogsAtLocation(@PathVariable Long locationId) {
    log.info("Retrieving all the dogs at a given location: ", locationId);
    return rescueService.retrieveAllDogsAtLocation(locationId);
  }

  /**
   * Add a dog to a location. To call this method, send an HTTP POST request to
   * http://localhost:8080/dog_rescue/location/{locationID}/dog where
   * {locationId} is the location ID.
   * 
   * @param locationId The ID of the location in which to add a dog. This is
   *        passed in the URI as described above.
   * @param dogInfo The dog information. This is passes as JSON in the request
   *        body.
   * @return The dog object with location information.
   */
  @PostMapping("/location/{locationId}/dog")
  @ResponseStatus(code = HttpStatus.CREATED)
  public DogInfo insertDog(@PathVariable Long locationId,
      @RequestBody DogInfo dogInfo) {
    log.info("Creating dog {} at location ID={}", dogInfo, locationId);
    return rescueService.saveDog(locationId, dogInfo);
  }

  /**
   * Delete a location, all dog records and all dog breed join table records.
   * This does not delete breed records. To call this method, send an HTTP
   * DELETE request to http://localhost:8080/dog_rescue/location/{locationId}
   * where {locationId} is the ID of the location to delete.
   * 
   * @param locationId The ID of the location to delete.
   * @return A message indicating success.
   */
  @DeleteMapping("/location/{locationId}")
  public Map<String, String> deleteLocation(@PathVariable Long locationId) {
    log.info("Deleting location with ID={}", locationId);
    rescueService.deleteLocation(locationId);

    return Map.of("message",
        "Location with ID=" + locationId + " was successfully deleted.");
  }
}
