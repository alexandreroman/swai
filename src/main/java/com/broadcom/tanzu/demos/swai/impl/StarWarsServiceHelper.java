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

import com.broadcom.tanzu.demos.swai.Planet;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
class StarWarsServiceHelper {
    private final Swapi api;

    StarWarsServiceHelper(Swapi api) {
        this.api = api;
    }

    @Cacheable(value = "films", key = "#filmId")
    public SwapiFilm lookupSwapiFilm(int filmId) {
        return api.findFilmById(filmId).orElseThrow(() -> new IllegalArgumentException("Unknown film id: " + filmId));
    }

    @Cacheable(value = "planets", key = "#planetId")
    public Planet lookupPlanet(int planetId) {
        final var p = api.findPlanetById(planetId).orElseThrow(() -> new IllegalArgumentException("Unknown planet id: " + planetId));
        return new Planet(planetId, p.name(), p.population());
    }
}
