package com.mandarin.discord.enums;

public enum GuildCategory {

    PB_JAVA("886268434004983812"),
    PB_CSHARP("926115862346358855"),
    PB_PYTHON("926116066294374430"),
    PB_JAVASCRIPT("926116191121068102"),
    PB_CPLUSPLUS("927305865223094323"),
    PB_IMPROVEMENT("926540884168826910"),
    PB_VOICE_CHANNELS("886268434004983817");

    String id;

    GuildCategory(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
