package com.mandarin.discord.config;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import static com.mandarin.discord.listener.EventListenerRegistry.EVENT_LISTENERS;

public class ApplicationConfiguration {

    private static final Dotenv config = Dotenv.configure().filename("configuration.env").load();
    private static boolean isProdLoaded;
    private static ShardManager shardManager;

    public static void buildDefaultApplicationConfig() {

        String token;

        if (System.getProperty("os.name").contains(config.get("LOCAL_MACHINE_INDICATOR"))) {

            isProdLoaded = false;
            token = config.get("TOKEN_WIP");
        } else {

            isProdLoaded = true;
            token = config.get("TOKEN");
        }

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.watching("for challenge!"));
        builder.enableIntents(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES
        );
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.enableCache(CacheFlag.ONLINE_STATUS);

        shardManager = builder.build();

        loadEventListeners(shardManager);
    }

    private static void loadEventListeners(ShardManager shardManager) {

        for (ListenerAdapter eventListener : EVENT_LISTENERS) {
            shardManager.addEventListener(eventListener);
        }
    }

    public static ShardManager getShardManager() {
        return shardManager;
    }

    public static Dotenv getDefaultInternalEnvConfig() {
        return config;
    }

    public static boolean isProdLoaded() {
        return isProdLoaded;
    }
}
