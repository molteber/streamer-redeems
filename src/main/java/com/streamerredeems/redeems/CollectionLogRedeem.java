package com.streamerredeems.redeems;

import com.streamerredeems.InterfaceManager;
import com.streamerredeems.RedeemEvent;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.widgets.WidgetModalMode;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.util.Text;

public class CollectionLogRedeem implements IRedeem {
    private static final int SCRIPT_ID = 3343; // NOTIFICATION_DISPLAY_INIT

    private static final int COMPONENT_ID = WidgetUtil.packComponentId(303, 2); // (interfaceId << 16) | childId

    private static final int INTERFACE_ID = 660;

    private final Client client;
    private final ClientThread clientThread;

    private final InterfaceManager interfaceManager = new InterfaceManager();

    public CollectionLogRedeem(Client client, ClientThread clientThread) {
        this.client = client;
        this.clientThread = clientThread;
    }

    public void redeem(RedeemEvent event) {
        if (interfaceManager.hasOpen) {
            // If there is already one open, we don't want to queue a popup because streamer can be spammed, and they will take forever to disappear
            return;
        }

        clientThread.invokeLater(() -> {
            if (interfaceManager.hasOpen) {
                return true;
            }

            interfaceManager.hasOpen = true;

            interfaceManager.notificationNode = client.openInterface(
                    COMPONENT_ID,
                    INTERFACE_ID,
                    WidgetModalMode.MODAL_CLICKTHROUGH
            );
            interfaceManager.notificationWidget = client.getWidget(INTERFACE_ID, 1);

            var collectionLogItem = event.message == null || event.message.isBlank() ? "Bazinga!" : Text.escapeJagex(event.message);
            client.runScript(SCRIPT_ID, "Collection log", "New item: <br><br><col=ffffff>" + collectionLogItem + "</col>", -1);

            StringBuilder logChatMessage = new StringBuilder("You have a funny feeling like you're being pranked");
            if (event.redeemedBy != null && !event.redeemedBy.isBlank()) {
                logChatMessage
                        .append(" by ")
                        .append("<u>")
                        .append(Text.escapeJagex(event.redeemedBy))
                        .append("</u>");
            }
            logChatMessage.append("!");
            client.addChatMessage(
                    ChatMessageType.GAMEMESSAGE,
                    "",
                    logChatMessage.toString(),
                    ""
            );

            return true;
        });

        clientThread.invokeLater(() -> {
            assert interfaceManager.notificationWidget != null;

            if (interfaceManager.notificationWidget.getWidth() > 0) {
                return false;
            }

            // Close the interface
            client.closeInterface(interfaceManager.notificationNode, true);
            interfaceManager.hasOpen = false;

            // Invoke done
            return true;
        });
    }
}
