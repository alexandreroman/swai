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

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SwaiController {
    private final SwaiService svc;
    private final ObservationRegistry reg;

    SwaiController(SwaiService svc, ObservationRegistry reg) {
        this.svc = svc;
        this.reg = reg;
    }

    @GetMapping(value = "/ai", produces = MediaType.TEXT_PLAIN_VALUE)
    String askAI(@RequestParam("q") String query) {
        return Observation.createNotStarted("askAi", reg)
                .highCardinalityKeyValue("query", query)
                .observe(() -> svc.askAI(query));
    }
}
