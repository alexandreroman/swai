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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
class SwaiService {
    private final Logger logger = LoggerFactory.getLogger(SwaiService.class);

    private final ChatClient chatClient;
    private final ObservationRegistry reg;
    @Value("classpath:/system-prompt.st")
    private Resource systemPromptRes;
    @Value("classpath:/ask-prompt.st")
    private Resource askPromptRes;

    SwaiService(ChatClient chatClient, ObservationRegistry reg) {
        this.chatClient = chatClient;
        this.reg = reg;
    }

    String askAI(String query) {
        final var sysMsg = new SystemPromptTemplate(systemPromptRes).createMessage();
        final var askMsg = new PromptTemplate(askPromptRes).createMessage(Map.of(
                "question", query
        ));

        final var prompt = new Prompt(List.of(sysMsg, askMsg));
        return Observation.createNotStarted("sendPrompt", reg)
                .observe(() -> {
                    logger.debug("Sending prompt:\n{}", prompt.getContents());
                    return chatClient.call(prompt).getResult().getOutput().getContent();
                });
    }
}
