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
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile("!fake")
class StarWarsServiceImpl implements StarWarsService {
    private final Logger logger = LoggerFactory.getLogger(StarWarsServiceImpl.class);
    private final Swapi api;
    private final StarWarsServiceHelper helper;
    private final ObservationRegistry reg;

    StarWarsServiceImpl(Swapi api, StarWarsServiceHelper helper, ObservationRegistry reg) {
        this.api = api;
        this.helper = helper;
        this.reg = reg;
    }

    private static int getResourceId(URL u) {
        var p = u.getPath();
        if (p.endsWith("/")) {
            p = p.substring(0, p.length() - 1);
        }
        final int i = p.lastIndexOf("/");
        if (i == -1) {
            throw new IllegalArgumentException("Cannot find resource id in URL: " + u);
        }
        return Integer.parseInt(p.substring(i + 1));
    }

    @Override
    @Cacheable(value = "residents-by-planet", key = "#planetId")
    public List<People> findResidentsByPlanet(int planetId) {
        return Observation.createNotStarted("findResidentsByPlanet", reg)
                .lowCardinalityKeyValue("planetId", String.valueOf(planetId))
                .observe(() -> doFindResidentsByPlanet(planetId));
    }

    private List<People> doFindResidentsByPlanet(int planetId) {
        logger.debug("Looking up planet by id {}", planetId);
        final var swapiPlanet = api.findPlanetById(planetId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown planet id: " + planetId));

        final var residents = new ArrayList<People>(swapiPlanet.residents().size());
        for (final var r : swapiPlanet.residents()) {
            final var rid = getResourceId(r);
            final var people = api.findPeopleById(rid).orElseThrow(() -> new IllegalArgumentException("Unknown people id: " + rid));
            logger.debug("Found resident from planet {}: {}", planetId, people.name());
            final var hwid = getResourceId(people.homeworld());
            final var hw = helper.lookupPlanet(hwid);
            residents.add(new People(rid, people.name(), new Planet(hwid, hw.name(), hw.population())));
        }
        return residents;
    }

    @Override
    @Cacheable(value = "films")
    public List<Film> findFilms() {
        return Observation.createNotStarted("findFilms", reg).observe(this::doFindFilms);
    }

    private List<Film> doFindFilms() {
        logger.debug("Looking up films");
        final var swapiFilms = api.findAllFilms().results();

        final var films = new ArrayList<Film>(swapiFilms.size());
        for (final var f : swapiFilms) {
            logger.debug("Found film: {}", f.title());
            films.add(new Film(getResourceId(f.url()), f.title(), f.episodeId(), f.releaseDate()));
        }
        return films;
    }

    @Override
    @Cacheable(value = "planets")
    public List<Planet> findPlanets() {
        return Observation.createNotStarted("findPlanets", reg).observe(this::doFindPlanets);
    }

    private List<Planet> doFindPlanets() {
        logger.debug("Looking up planets");
        final var swapiPlanets = api.findAllPlanets().results();

        final var planets = new ArrayList<Planet>(swapiPlanets.size());
        for (final var p : swapiPlanets) {
            logger.debug("Found planet: {}", p.name());
            planets.add(new Planet(getResourceId(p.url()), p.name(), p.population()));
        }
        return planets;
    }

    @Override
    @Cacheable(value = "planets-by-film", key = "#filmId")
    public List<Planet> findPlanetsByFilm(int filmId) {
        return Observation.createNotStarted("findPlanetsByFilm", reg)
                .lowCardinalityKeyValue("filmId", String.valueOf(filmId))
                .observe(() -> doFindPlanetsByFilm(filmId));
    }

    private List<Planet> doFindPlanetsByFilm(int filmId) {
        logger.debug("Looking up planets appearing in film id: {}", filmId);
        final var planetIds = helper.lookupSwapiFilm(filmId).planets().stream().map(StarWarsServiceImpl::getResourceId).toList();

        final var planets = new ArrayList<Planet>(planetIds.size());
        for (final var planetId : planetIds) {
            final var p = helper.lookupPlanet(planetId);
            logger.debug("Found planet appearing in film {}: {}", filmId, p.name());
            planets.add(p);
        }
        return planets;
    }

    @Override
    @Cacheable(value = "characters-by-film", key = "#filmId")
    public List<People> findCharactersByFilm(int filmId) {
        return Observation.createNotStarted("findCharactersByFilm", reg)
                .lowCardinalityKeyValue("filmId", String.valueOf(filmId))
                .observe(() -> doFindCharactersByFilm(filmId));
    }

    private List<People> doFindCharactersByFilm(int filmId) {
        logger.debug("Looking up characters appearing in film id: {}", filmId);
        final var characterIds = helper.lookupSwapiFilm(filmId).characters().stream().map(StarWarsServiceImpl::getResourceId).toList();

        final var characters = new ArrayList<People>(characterIds.size());
        for (final var cid : characterIds) {
            final var p = api.findPeopleById(cid).orElseThrow(() -> new IllegalArgumentException("Unknown people id: " + cid));
            logger.debug("Found character appearing in film {}: {}", filmId, p.name());
            final var hwid = getResourceId(p.homeworld());
            characters.add(new People(cid, p.name(), helper.lookupPlanet(hwid)));
        }
        return characters;
    }

    @Override
    @Cacheable(value = "characters", key = "#name")
    public List<People> findCharactersByName(String name) {
        return Observation.createNotStarted("findCharactersByName", reg)
                .lowCardinalityKeyValue("name", name)
                .observe(() -> doFindCharactersByName(name));
    }

    private List<People> doFindCharactersByName(String name) {
        logger.debug("Looking up characters by name: {}", name);
        final var swapiPeople = api.findPeopleByName(name).results();

        final var characters = new ArrayList<People>(swapiPeople.size());
        for (final var p : swapiPeople) {
            logger.debug("Found character with name {}: {}", name, p.name());
            final var hwid = getResourceId(p.homeworld());
            characters.add(new People(getResourceId(p.url()), p.name(), helper.lookupPlanet(hwid)));
        }
        return characters;
    }
}
