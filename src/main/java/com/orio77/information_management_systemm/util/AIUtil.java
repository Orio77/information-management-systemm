package com.orio77.information_management_systemm.util;

import org.springframework.ai.chat.model.ChatResponse;

public class AIUtil {

    public static void logResponse(ChatResponse response, org.slf4j.Logger log) {
        log.debug("Received response with {} results",
                response.getResults().size());
        response.getResults().forEach(
                gen -> log.debug("\n\nGeneration text: {}",
                        gen.getOutput().getText()));
    }
}
