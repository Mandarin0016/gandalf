package com.mandarin.discord.util;

import org.slf4j.Logger;

import java.util.Map;

public interface ApplicationLoggable {

    Logger getLogger(Class<?> loggerCLass);

    Map<Class<?>, Logger> getLoggerMap();
}
