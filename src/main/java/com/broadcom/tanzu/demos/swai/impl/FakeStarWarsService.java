/*
 * Copyright (c) 2024 Broadcom, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.broadcom.tanzu.demos.swai.impl;

import com.broadcom.tanzu.demos.swai.Film;
import com.broadcom.tanzu.demos.swai.People;
import com.broadcom.tanzu.demos.swai.Planet;
import com.broadcom.tanzu.demos.swai.StarWarsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Profile("fake")
class FakeStarWarsService implements StarWarsService {
    private final Logger logger = LoggerFactory.getLogger(FakeStarWarsService.class);

    private final List<Film> films = List.of(
            new Film(1, "Here we go again", 1, LocalDate.of(2020, 3, 1)),
            new Film(2, "Oops, I did it again", 2, LocalDate.of(2022, 4, 1))
    );
    private final Planet sandtopia = new Planet(1, "Sandtopia", "1,000");
    private final Planet frozen = new Planet(2, "Frozen", "12,000");
    private final People javaTheHutt = new People(1, "Java The Hutt", sandtopia);
    private final People elsa = new People(2, "Elsa Organa", frozen);

    @Override
    public List<Film> findFilms() {
        logger.debug("Loading films: {}", films);
        return films;
    }

    @Override
    public List<Planet> findPlanets() {
        final var planets = List.of(sandtopia, frozen);
        logger.debug("Loading planets: {}", planets);
        return planets;
    }

    @Override
    public List<Planet> findPlanetsByFilm(int filmId) {
        final var planets = switch (filmId) {
            case 1 -> List.of(sandtopia);
            case 2 -> List.of(sandtopia, frozen);
            default -> throw new IllegalArgumentException("Unknown film id: " + filmId);
        };
        logger.debug("Loading planets appearing in film {}: {}", filmId, planets);
        return planets;
    }

    @Override
    public List<People> findCharactersByFilm(int filmId) {
        final var characters = switch (filmId) {
            case 1 -> List.of(javaTheHutt);
            case 2 -> List.of(javaTheHutt, elsa);
            default -> throw new IllegalArgumentException("Unknown film id: " + filmId);
        };
        logger.debug("Loading characters appearing in film {}: {}", filmId, characters);
        return characters;
    }

    @Override
    public List<People> findCharactersByName(String name) {
        return List.of(javaTheHutt, elsa).stream().filter(p -> p.name().equalsIgnoreCase(name)).toList();
    }

    @Override
    public List<People> findResidentsByPlanet(int planetId) {
        final var residents = switch (planetId) {
            case 1 -> List.of(javaTheHutt);
            case 2 -> List.of(elsa);
            default -> throw new IllegalArgumentException("Unknown planet id: " + planetId);
        };
        logger.debug("Loading residents living on planet {}: {}", planetId, residents);
        return residents;
    }
}
