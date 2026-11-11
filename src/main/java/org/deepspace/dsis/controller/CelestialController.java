package org.deepspace.dsis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/v1/celestial")
public class CelestialController {

    private static final List<Map<String, Object>> BODIES = new ArrayList<>();

    static {
        // 1. Mars
        Map<String, Object> mars = new LinkedHashMap<>();
        mars.put("id", "mars");
        mars.put("name", "Mars");
        mars.put("type", "Terrestrial Planet");
        mars.put("tagline", "The Red Desert World of Volcanoes and Ancient Valleys");
        mars.put("radiusKm", 3389.5);
        mars.put("massKg", "6.417 × 10²³ kg");
        mars.put("surfaceGravity", 3.72);
        mars.put("meanTempC", -63.0);
        mars.put("tempRange", "-140°C to +20°C");
        mars.put("distanceFromSunKm", "227.9 million km (1.524 AU)");
        mars.put("orbitalPeriodDays", 687.0);
        mars.put("dayLengthHours", 24.6);
        mars.put("moonsCount", 2);
        mars.put("moons", List.of("Phobos", "Deimos"));
        mars.put("atmosphere", Map.of("CO2", "95.3%", "Nitrogen", "2.6%", "Argon", "1.9%", "Oxygen", "0.13%"));
        mars.put("features", List.of(
                "Olympus Mons: Tallest shield volcano in the solar system (21.9 km high, ~600 km wide).",
                "Valles Marineris: Vast canyon system over 4,000 km long and up to 7 km deep.",
                "Jezero Crater: Ancient lake basin and delta explored by Perseverance rover for biosignatures."
        ));
        mars.put("explorationMissions", List.of(
                Map.of("name", "Perseverance Rover", "agency", "NASA", "year", "2020 - Present", "status", "ACTIVE"),
                Map.of("name", "Curiosity Rover", "agency", "NASA", "year", "2011 - Present", "status", "ACTIVE"),
                Map.of("name", "Hope Orbiter", "agency", "UAESA", "year", "2020 - Present", "status", "ACTIVE")
        ));
        mars.put("facts", List.of(
                "Its reddish hue is caused by iron(III) oxide (rust) across the regolith.",
                "Sunsets on Mars appear blue to the human eye due to dust particle scattering.",
                "Possesses seasonal polar ice caps of dry ice (CO2) and water ice."
        ));
        BODIES.add(mars);

        // 2. Europa
        Map<String, Object> europa = new LinkedHashMap<>();
        europa.put("id", "europa");
        europa.put("name", "Europa");
        europa.put("type", "Jovian Icy Moon");
        europa.put("tagline", "Ocean World Hidden Beneath a Global Crust of Ice");
        europa.put("radiusKm", 1560.8);
        europa.put("massKg", "4.800 × 10²² kg");
        europa.put("surfaceGravity", 1.315);
        europa.put("meanTempC", -160.0);
        europa.put("tempRange", "-220°C to -140°C");
        europa.put("distanceFromHost", "670,900 km from Jupiter");
        europa.put("orbitalPeriodDays", 3.55);
        europa.put("dayLengthHours", 85.2);
        europa.put("moonsCount", 0);
        europa.put("atmosphere", Map.of("Molecular Oxygen", "99.0%", "Water Vapor", "Trace"));
        europa.put("features", List.of(
                "Subsurface Ocean: Estimated 60 - 150 km deep liquid ocean beneath a 15 - 25 km ice shell.",
                "Conamara Chaos: Chaotic terrain of fractured, rafted ice plates indicating vigorous convection.",
                "Lineae: Global intersecting red-brown linear fractures caused by Jupiter tidal flexing."
        ));
        europa.put("explorationMissions", List.of(
                Map.of("name", "Galileo Orbiter", "agency", "NASA", "year", "1995 - 2003", "status", "COMPLETED"),
                Map.of("name", "Europa Clipper", "agency", "NASA", "year", "2024 - 2030", "status", "EN ROUTE")
        ));
        europa.put("facts", List.of(
                "Europa likely harbors more liquid water than all Earth's oceans combined.",
                "Its smooth surface is one of the most reflective in the Solar System.",
                "Tidal friction from Jupiter keeps its ocean warm enough to prevent complete freezing."
        ));
        BODIES.add(europa);

        // 3. Titan
        Map<String, Object> titan = new LinkedHashMap<>();
        titan.put("id", "titan");
        titan.put("name", "Titan");
        titan.put("type", "Saturnian Giant Moon");
        titan.put("tagline", "Methane Lakes and Organic Haze in Deep Cryo-Suspension");
        titan.put("radiusKm", 2574.7);
        titan.put("massKg", "1.345 × 10²³ kg");
        titan.put("surfaceGravity", 1.352);
        titan.put("meanTempC", -179.2);
        titan.put("tempRange", "-182°C to -175°C");
        titan.put("distanceFromHost", "1,221,870 km from Saturn");
        titan.put("orbitalPeriodDays", 15.94);
        titan.put("dayLengthHours", 382.6);
        titan.put("moonsCount", 0);
        titan.put("atmosphere", Map.of("Nitrogen", "95.0%", "Methane", "4.9%", "Hydrogen", "0.1%"));
        titan.put("features", List.of(
                "Kraken Mare: Largest liquid hydrocarbon sea (over 400,000 km²).",
                "Ligeia Mare: Methane-dominated northern sea with organic-rich sediment channels.",
                "Equatorial Dune Fields: Dark organic dunes formed by hydrocarbon sand grains."
        ));
        titan.put("explorationMissions", List.of(
                Map.of("name", "Cassini-Huygens", "agency", "NASA / ESA", "year", "1997 - 2017", "status", "COMPLETED"),
                Map.of("name", "Dragonfly Rotorcraft", "agency", "NASA", "year", "2028 Launch", "status", "IN DEVELOPMENT")
        ));
        titan.put("facts", List.of(
                "Dense atmosphere with surface pressure 1.45 bar (50% higher than Earth's).",
                "Only moon with dense clouds and liquid seas on its surface.",
                "Low gravity and dense atmosphere mean humans could fly by strapping on artificial wings."
        ));
        BODIES.add(titan);

        // 4. Venus
        Map<String, Object> venus = new LinkedHashMap<>();
        venus.put("id", "venus");
        venus.put("name", "Venus");
        venus.put("type", "Terrestrial Planet");
        venus.put("tagline", "Infernal Runaway Greenhouse World of Volcanic Basalt");
        venus.put("radiusKm", 6051.8);
        venus.put("massKg", "4.867 × 10²⁴ kg");
        venus.put("surfaceGravity", 8.87);
        venus.put("meanTempC", 464.0);
        venus.put("tempRange", "440°C to 480°C");
        venus.put("distanceFromSunKm", "108.2 million km (0.723 AU)");
        venus.put("orbitalPeriodDays", 224.7);
        venus.put("dayLengthHours", 5832.5);
        venus.put("moonsCount", 0);
        venus.put("atmosphere", Map.of("CO2", "96.5%", "Nitrogen", "3.5%", "Sulfur Dioxide", "0.015%"));
        venus.put("features", List.of(
                "Maxwell Montes: Massive mountain range rising 11 km above mean radius.",
                "Pancake Domes: Circular flat-topped volcanic domes extruded from viscous lava.",
                "Sulfuric Acid Cloud Decks: Opaque global cloud layer between 48 - 68 km altitude."
        ));
        venus.put("explorationMissions", List.of(
                Map.of("name", "Magellan", "agency", "NASA", "year", "1989 - 1994", "status", "COMPLETED"),
                Map.of("name", "Akatsuki", "agency", "JAXA", "year", "2010 - Present", "status", "ACTIVE")
        ));
        venus.put("facts", List.of(
                "Hottest planet in the solar system due to runaway greenhouse effect.",
                "Rotates backwards (retrograde) compared to almost all other planets.",
                "Surface pressure is 92 times that of Earth (equal to 900m ocean depth)."
        ));
        BODIES.add(venus);

        // 5. Enceladus
        Map<String, Object> enceladus = new LinkedHashMap<>();
        enceladus.put("id", "enceladus");
        enceladus.put("name", "Enceladus");
        enceladus.put("type", "Saturnian Cryovolcanic Moon");
        enceladus.put("tagline", "The Geyser Moon Spraying Organic Oceans into Deep Space");
        enceladus.put("radiusKm", 252.1);
        enceladus.put("massKg", "1.080 × 10²⁰ kg");
        enceladus.put("surfaceGravity", 0.113);
        enceladus.put("meanTempC", -198.0);
        enceladus.put("tempRange", "-210°C to -130°C at geysers");
        enceladus.put("distanceFromHost", "238,000 km from Saturn");
        enceladus.put("orbitalPeriodDays", 1.37);
        enceladus.put("dayLengthHours", 32.9);
        enceladus.put("moonsCount", 0);
        enceladus.put("atmosphere", Map.of("Water Vapor", "91.0%", "Nitrogen", "4.0%", "CO2", "3.2%"));
        enceladus.put("features", List.of(
                "Tiger Stripes (Sulci): Four sub-parallel thermal fractures across the south pole venting vapor.",
                "Cryovolcanic Plumes: Supersonic jets venting ocean spray hundreds of km into space.",
                "Saturn's E-Ring: Enceladus continuously replenishes Saturn's diffuse outer E-ring."
        ));
        enceladus.put("explorationMissions", List.of(
                Map.of("name", "Cassini Orbiter", "agency", "NASA / ESA", "year", "2004 - 2017", "status", "COMPLETED"),
                Map.of("name", "Enceladus Orbilander", "agency", "NASA Decadal", "year", "2030s Concept", "status", "PROPOSED")
        ));
        enceladus.put("facts", List.of(
                "Cassini detected molecular hydrogen (H2) and organic molecules inside vented plumes.",
                "Highest geometric albedo in the solar system (over 99%), reflecting almost all sunlight.",
                "Warm hydrothermal vents at its ocean floor resemble Earth's deep sea alkaline vents."
        ));
        BODIES.add(enceladus);

        // 6. Earth's Moon
        Map<String, Object> moon = new LinkedHashMap<>();
        moon.put("id", "moon");
        moon.put("name", "Earth's Moon (Luna)");
        moon.put("type", "Terrestrial Natural Satellite");
        moon.put("tagline", "Earth's Celestial Partner and Gateway to the Deep Solar Frontier");
        moon.put("radiusKm", 1737.4);
        moon.put("massKg", "7.342 × 10²² kg");
        moon.put("surfaceGravity", 1.62);
        moon.put("meanTempC", -20.0);
        moon.put("tempRange", "-130°C to +120°C");
        moon.put("distanceFromHost", "384,400 km from Earth");
        moon.put("orbitalPeriodDays", 27.3);
        moon.put("dayLengthHours", 708.7);
        moon.put("moonsCount", 0);
        moon.put("atmosphere", Map.of("Helium", "29.0%", "Neon", "29.0%", "Hydrogen", "23.0%", "Argon", "19.0%"));
        moon.put("features", List.of(
                "Shackleton Crater: South polar crater with permanently shadowed regions harboring water ice.",
                "Oceanus Procellarum: Massive lunar mare basalt plain covering over 4 million km².",
                "Mare Tranquillitatis: Historic Apollo 11 landing site."
        ));
        moon.put("explorationMissions", List.of(
                Map.of("name", "Apollo Program", "agency", "NASA", "year", "1969 - 1972", "status", "COMPLETED"),
                Map.of("name", "Chandrayaan-3", "agency", "ISRO", "year", "2023", "status", "COMPLETED"),
                Map.of("name", "Artemis III", "agency", "NASA", "year", "2026+", "status", "PLANNED")
        ));
        moon.put("facts", List.of(
                "Tidally locked with Earth; the same lunar hemisphere always faces our planet.",
                "Regolith consists of sharp, glass-like microscopic fragments un-eroded by wind or weather.",
                "Water ice at the lunar south pole can provide drinking water and rocket propellant."
        ));
        BODIES.add(moon);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getCelestialBodies() {
        return ResponseEntity.ok(BODIES);
    }
}
