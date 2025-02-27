document.addEventListener("DOMContentLoaded", function () {
    console.log("JavaScript Loaded");

    const providerSelect = document.getElementById("provider");
    const findChargerBtn = document.getElementById("findChargerBtn");
    const resultsDiv = document.getElementById("results");
    let mapDiv = document.getElementById("map");

    // Ensure there's only one map div
    if (!mapDiv) {
        mapDiv = document.createElement("div");
        mapDiv.id = "map";
        mapDiv.style = "width: 100%; height: 400px; margin-top: 20px;";
        resultsDiv.appendChild(mapDiv);
    }

    let map;

    // Initialize Google Map
    function initMap(latitude = 20.2959847, longitude = 85.8246101, zoomLevel = 12) {
        map = new google.maps.Map(document.getElementById("map"), {
            center: { lat: latitude, lng: longitude },
            zoom: zoomLevel,
        });
    }

    // Ensure the map is initialized on page load
    initMap();

    if (providerSelect && findChargerBtn) {
        providerSelect.addEventListener("change", function () {
            findChargerBtn.disabled = !this.value;
        });

        findChargerBtn.addEventListener("click", function () {
            const selectedProvider = providerSelect.value.trim();
            resultsDiv.innerHTML = `<p>Searching for ${selectedProvider} EV chargers...</p>`;

            if (!navigator.geolocation) {
                resultsDiv.innerHTML = "<p>Geolocation is not supported by your browser.</p>";
                return;
            }

            navigator.geolocation.getCurrentPosition(position => {
                const { latitude, longitude } = position.coords;
                initMap(latitude, longitude, 14);

                fetch(`http://localhost:8080/api/locations/nearest?latitude=${latitude}&longitude=${longitude}&provider=${encodeURIComponent(selectedProvider)}`)
                    .then(response => {
                        if (!response.ok) throw new Error("No charger found for this provider.");
                        return response.json();
                    })
                    .then(data => {
                        resultsDiv.innerHTML = "<h2>Nearest EV Charger</h2>";

                        if (!Array.isArray(data) || data.length === 0) {
                            resultsDiv.innerHTML += "<p>No chargers found for this provider.</p>";
                            return;
                        }

                        const ul = document.createElement("ul");
                        data.forEach(charger => {
                            const li = document.createElement("li");
                            li.innerHTML = `<strong>${charger.name}</strong> - Location: (${charger.latitude}, ${charger.longitude})`;
                            ul.appendChild(li);

                            // Add Marker to Map
                            new google.maps.Marker({
                                position: { lat: charger.latitude, lng: charger.longitude },
                                map: map,
                                title: charger.name,
                            });
                        });

                        resultsDiv.appendChild(ul);
                    })
                    .catch(error => {
                        console.error("Error fetching chargers:", error);
                        resultsDiv.innerHTML = "<p>Error fetching chargers. Please try again.</p>";
                    });
            }, error => {
                resultsDiv.innerHTML = "<p>Failed to get location. Enable location access.</p>";
            });
        });
    }

    const allLocationsBtn = document.getElementById("allLocationsBtn");
    if (allLocationsBtn) {
        allLocationsBtn.addEventListener("click", function () {
            resultsDiv.innerHTML = "<p>Loading locations...</p>";
            allLocationsBtn.disabled = true;
            initMap();

            fetch("http://localhost:8080/api/locations")
                .then(response => {
                    if (!response.ok) throw new Error("Failed to fetch locations.");
                    return response.json();
                })
                .then(locations => {
                    resultsDiv.innerHTML = "<h2>All EV Charger Locations</h2>";

                    if (!Array.isArray(locations) || locations.length === 0) {
                        resultsDiv.innerHTML += "<p>No locations available.</p>";
                        return;
                    }

                    const ul = document.createElement("ul");
                    locations.forEach(location => {
                        const li = document.createElement("li");
                        li.innerHTML = `<strong>${location.name}</strong> - Provider: ${location.provider}`;
                        ul.appendChild(li);

                        // Add Marker to Map
                        new google.maps.Marker({
                            position: { lat: location.latitude, lng: location.longitude },
                            map: map,
                            title: location.name,
                        });
                    });

                    resultsDiv.appendChild(ul);
                })
                .catch(error => {
                    console.error("Error fetching locations:", error);
                    resultsDiv.innerHTML = "<p>Error fetching locations. Please try again.</p>";
                })
                .finally(() => {
                    allLocationsBtn.disabled = false;
                });
        });
    }

    const backToHomeBtn = document.getElementById("backToHome");
    if (backToHomeBtn) {
        backToHomeBtn.addEventListener("click", function () {
            window.location.href = "index.html";
        });
    }

    // Highlight Active Navigation Link
    const navLinks = document.querySelectorAll(".nav-link");
    const currentPage = window.location.pathname.split("/").pop();
    navLinks.forEach(link => {
        if (link.getAttribute("href") === currentPage) {
            link.classList.add("active");
        }
    });
});
