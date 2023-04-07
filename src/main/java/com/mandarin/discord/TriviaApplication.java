package com.mandarin.discord;

import com.mandarin.discord.config.ApplicationConfiguration;
import com.mandarin.discord.util.ApplicationLogger;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TriviaApplication {
    private static final Logger logger = new ApplicationLogger().getLogger(TriviaApplication.class);

    public static void main(String[] args) {

        try {
            ApplicationConfiguration.buildDefaultApplicationConfig();
        } catch (Exception e) {
            logger.warn("Error: " + e.getMessage() +
                    System.lineSeparator() + "Stack Trace : " + System.lineSeparator() +
                    Arrays.stream(e.getStackTrace())
                            .map(StackTraceElement::toString)
                            .collect(Collectors.joining(System.lineSeparator())));
        }
    }


}
