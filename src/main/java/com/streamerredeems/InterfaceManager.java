package com.streamerredeems;

import net.runelite.api.WidgetNode;
import net.runelite.api.widgets.Widget;

public class InterfaceManager {
    public volatile boolean hasOpen = false;
    public volatile WidgetNode notificationNode;
    public volatile Widget notificationWidget;
}
