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

package com.broadcom.tanzu.demos.swai;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.List;
import java.util.function.Function;

@Configuration(proxyBeanMethods = false)
class SwaiConfig {
    @Bean
    @Description("Find all films. The result is a list with all films in the Star Wars series. Each entry includes a film identifier, a title, an episode number and a release date.")
    Function<NoArgRequest, List<Film>> films(StarWarsService svc) {
        return req -> svc.findFilms();
    }

    @Bean
    @Description("Find all planets. The result is a list of every planets mentioned in the Star Wars series. Each entry includes a planet identifier, a name and a population.")
    Function<NoArgRequest, List<Planet>> planets(StarWarsService svc) {
        return req -> svc.findPlanets();
    }

    @Bean
    @Description("Find characters by film. The result is a list of every characters appearing in a given Star Wars film. Each entry includes a people identifier, a name and an origin (homeworld).")
    Function<ByFilmRequest, List<People>> charactersByFilm(StarWarsService svc) {
        return req -> svc.findCharactersByFilm(req.filmId());
    }

    @Bean
    @Description("Find characters by name. The result is a list of characters matching the given name. Each entry includes a people identifier, a name and an origin (homeworld).")
    Function<ByNameRequest, List<People>> charactersByName(StarWarsService svc) {
        return req -> svc.findCharactersByName(req.name());
    }

    @Bean
    @Description("Find residents by planet. The result is a list of people living on a given planet. Each entry includes a people identifier, a name and an origin (homeworld).")
    Function<ByPlanetRequest, List<People>> residentsByPlanet(StarWarsService svc) {
        return req -> svc.findResidentsByPlanet(req.planetId());
    }

    @Bean
    @Description("Find planets by film. The result is a list of planets mentioned in a given Star Wars film. Each entry includes a planet identifier, a name and a population.")
    Function<ByFilmRequest, List<Planet>> planetsByFilm(StarWarsService svc) {
        return req -> svc.findPlanetsByFilm(req.filmId());
    }

    @JsonClassDescription("A request using a planet identifier")
    record ByPlanetRequest(
            @JsonProperty(required = true)
            @JsonPropertyDescription("Planet identifier") int planetId) {
    }

    @JsonClassDescription("A request using a film identifier")
    record ByFilmRequest(
            @JsonProperty(required = true)
            @JsonPropertyDescription("Film identifier") int filmId) {
    }

    @JsonClassDescription("A request using a character name")
    record ByNameRequest(
            @JsonProperty(required = true)
            @JsonPropertyDescription("Character name") String name) {
    }

    @JsonClassDescription("A request with no argument")
    record NoArgRequest(
            @JsonPropertyDescription("Empty argument (can be null)") String empty) {
    }
}
