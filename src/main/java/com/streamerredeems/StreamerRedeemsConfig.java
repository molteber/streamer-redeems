package com.streamerredeems;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("streamerredeems")
public interface StreamerRedeemsConfig extends Config {
    @ConfigItem(
            keyName = "url",
            name = "URL",
            description = "The url of the Server-Sent Events endpoint",
            position = 0

    )
    default String url() {
        return "";
    }

    @ConfigSection(
            name = "Collection log settings",
            description = "",
            position = 1
    )
    String collectionLogSections = "collectionLogSettings";

    @ConfigItem(
            keyName = "redeemCollectionLog",
            name = "Activate collection log redeem",
            description = "",
            section = collectionLogSections,
            position = 0
    )
    default boolean redeemCollectionLog() {
        return false;
    }

    @ConfigItem(
            keyName = "collectionLogRedeem",
            name = "Collection log redeem name",
            description = "The name of the collection log redeem which will trigger a fake collection log",
            section = collectionLogSections,
            position = 1
    )
    default String collectionLogRedeem() {
        return "";
    }
}
