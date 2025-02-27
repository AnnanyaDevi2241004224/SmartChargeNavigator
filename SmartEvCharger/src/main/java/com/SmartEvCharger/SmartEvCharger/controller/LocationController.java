package com.SmartEvCharger.SmartEvCharger.controller;

import com.SmartEvCharger.SmartEvCharger.model.Location;
import com.SmartEvCharger.SmartEvCharger.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*") // Allow all frontend origins
@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        List<Location> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    @PostMapping
    public ResponseEntity<Location> addLocation(@RequestBody Location location) {
        Location savedLocation = locationService.addLocation(location);
        return ResponseEntity.ok(savedLocation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        Optional<Location> location = Optional.ofNullable(locationService.getLocationById(id));
        return location.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.ok("Location with ID " + id + " deleted successfully.");
    }

    // Find the nearest EV charger using latitude & longitude with improved provider matching
    @GetMapping("/nearest")
    public ResponseEntity<?> getNearestCharger(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam String provider) {

        List<Location> nearestChargers = locationService.findNearestCharger(latitude, longitude, provider);

        if (nearestChargers.isEmpty()) {
            return ResponseEntity.status(404).body("No available charger found for provider: " + provider);
        }

        return ResponseEntity.ok(nearestChargers);
    }
}
