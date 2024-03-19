package com.streamerredeems;

import com.streamerredeems.redeems.CollectionLogRedeem;
import com.streamerredeems.redeems.IRedeem;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

public class RedeemerService {
    private final StreamerRedeemsConfig config;
    private final Client client;
    private final ClientThread clientThread;
    public final URI serviceUri;

    private volatile Hashtable<String, IRedeem> redeemers = new Hashtable<>();

    public RedeemerService(StreamerRedeemsConfig config, Client client, ClientThread clientThread) throws URISyntaxException {
        this.config = config;
        this.client = client;
        this.clientThread = clientThread;

        serviceUri = new URI(config.url());

        addCollectionLogRedeem();
    }

    public void redeem(RedeemEvent event)
    {
        if (event.redeem == null || event.redeem.isBlank() || event.redeem.isEmpty()) {
            return;
        }

        if (redeemers.containsKey(event.redeem)) {
            redeemers.get(event.redeem).redeem(event);
        }
    }
    private void addCollectionLogRedeem()
    {
        if (!config.redeemCollectionLog()) {
            return;
        }

        if (config.collectionLogRedeem().isEmpty() || config.collectionLogRedeem().isBlank()) {
            return;
        }

        redeemers.put(config.collectionLogRedeem(), new CollectionLogRedeem(client, clientThread));
    }
}
