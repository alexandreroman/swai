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

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Optional;

@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
interface Swapi {
    @GetExchange("/films/")
    SwapiFilmResults findAllFilms();

    @GetExchange("/planets/")
    SwapiPlanetResults findAllPlanets();

    @GetExchange("/planets/{id}")
    Optional<SwapiPlanet> findPlanetById(@PathVariable("id") int id);

    @GetExchange("/films/{id}")
    Optional<SwapiFilm> findFilmById(@PathVariable("id") int id);

    @GetExchange("/people/{id}")
    Optional<SwapiPeople> findPeopleById(@PathVariable("id") int id);

    @GetExchange("/people/?search={name}")
    SwapiPeopleResults findPeopleByName(@PathVariable("name") String name);
}
