package com.mandarin.discord.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class ApplicationLogger implements ApplicationLoggable {

    private final Map<Class<?>, Logger> loggerMap = new HashMap<>();

    public Logger getLogger(Class<?> loggerCLass) {
        if (!loggerMap.containsKey(loggerCLass)) {

            Logger logger = LoggerFactory.getLogger(loggerCLass);
            loggerMap.put(loggerCLass, logger);
            return logger;
        } else {
            return loggerMap.get(loggerCLass);
        }
    }

    @Override
    public Map<Class<?>, Logger> getLoggerMap() {
        return loggerMap;
    }
}
