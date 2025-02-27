package com.SmartEvCharger.SmartEvCharger.service;

import com.SmartEvCharger.SmartEvCharger.model.Location;
import com.SmartEvCharger.SmartEvCharger.repo.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location addLocation(Location location) {
        return locationRepository.save(location);
    }

    public Location getLocationById(Long id) {
        return locationRepository.findById(id).orElse(null);
    }

    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }

    // -------------------- Nearest Charger with Improved Provider Matching --------------------
    public List<Location> findNearestCharger(double latitude, double longitude, String provider) {
        List<Location> allLocations = locationRepository.findAll();
        List<Location> providerLocations = new ArrayList<>();

        // Allow partial provider name matching (e.g., "Tata" for "Tata Power")
        for (Location loc : allLocations) {
            if (loc.getProvider().toLowerCase().contains(provider.toLowerCase())) {
                providerLocations.add(loc);
            }
        }

        if (providerLocations.isEmpty()) return new ArrayList<>(); // No chargers for provider

        // Find the nearest charger using the Haversine formula
        Location nearestLocation = null;
        double minDistance = Double.MAX_VALUE;

        for (Location loc : providerLocations) {
            double distance = haversineDistance(latitude, longitude, loc.getLatitude(), loc.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                nearestLocation = loc;
            }
        }

        return nearestLocation != null ? List.of(nearestLocation) : new ArrayList<>();
    }

    // Haversine formula to calculate distance between two points
    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }
}
