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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "app.swapi.url=http://localhost:${wiremock.server.port}")
@AutoConfigureWireMock(port = 0)
class SwapiTests {
    @Autowired
    private Swapi api;

    @Test
    void testFindPlanetById() throws MalformedURLException {
        stubFor(get(urlEqualTo("/planets/1"))
                .willReturn(aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "name": "Tatooine",
                                    "rotation_period": "23",
                                    "orbital_period": "304",
                                    "diameter": "10465",
                                    "climate": "arid",
                                    "gravity": "1 standard",
                                    "terrain": "desert",
                                    "surface_water": "1",
                                    "population": "200000",
                                    "residents": [
                                        "https://swapi.dev/api/people/1/",
                                        "https://swapi.dev/api/people/2/",
                                        "https://swapi.dev/api/people/4/",
                                        "https://swapi.dev/api/people/6/",
                                        "https://swapi.dev/api/people/7/",
                                        "https://swapi.dev/api/people/8/",
                                        "https://swapi.dev/api/people/9/",
                                        "https://swapi.dev/api/people/11/",
                                        "https://swapi.dev/api/people/43/",
                                        "https://swapi.dev/api/people/62/"
                                    ],
                                    "films": [
                                        "https://swapi.dev/api/films/1/",
                                        "https://swapi.dev/api/films/3/",
                                        "https://swapi.dev/api/films/4/",
                                        "https://swapi.dev/api/films/5/",
                                        "https://swapi.dev/api/films/6/"
                                    ],
                                    "created": "2014-12-09T13:50:49.641000Z",
                                    "edited": "2014-12-20T20:58:18.411000Z",
                                    "url": "https://swapi.dev/api/planets/1/"
                                }
                                """)));

        final var opt = api.findPlanetById(1);
        assertThat(opt).contains(
                new SwapiPlanet("Tatooine", "200000",
                        new URL("https://swapi.dev/api/planets/1/"),
                        List.of(
                                new URL("https://swapi.dev/api/people/1/"),
                                new URL("https://swapi.dev/api/people/2/"),
                                new URL("https://swapi.dev/api/people/4/"),
                                new URL("https://swapi.dev/api/people/6/"),
                                new URL("https://swapi.dev/api/people/7/"),
                                new URL("https://swapi.dev/api/people/8/"),
                                new URL("https://swapi.dev/api/people/9/"),
                                new URL("https://swapi.dev/api/people/11/"),
                                new URL("https://swapi.dev/api/people/43/"),
                                new URL("https://swapi.dev/api/people/62/")
                        )));
    }
}
