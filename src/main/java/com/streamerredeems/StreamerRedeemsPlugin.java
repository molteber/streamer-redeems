package com.streamerredeems;

import com.google.gson.Gson;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.time.temporal.ChronoUnit;

@Slf4j
@PluginDescriptor(
        name = "Streamer redeems"
)
public class StreamerRedeemsPlugin extends Plugin {
    @Inject
    private ClientThread clientThread;

    @Inject
    private Client client;

    @Inject
    private StreamerRedeemsConfig config;

    @Inject
    private Gson gson;

    private Thread thread;

    private boolean loggedIn = false;

    @Override
    protected void startUp() {
        if (client.getGameState() == GameState.LOGGED_IN) {
            startRedeemListener();
        }
    }

    @Override
    protected void shutDown() {
        stopRedeemListener();
    }

    private void startRedeemListener() {
        restartRedeemListener();
    }

    private void stopRedeemListener() {
        if (thread == null || thread.isInterrupted()) {
            return;
        }

        thread.interrupt();
        thread = null;
    }

    private void setupRedeemListener() {
        stopRedeemListener();

        if (config.url().isBlank()) {
            return;
        }

        RedeemListenerRunner runner;
        try {
            runner = new RedeemListenerRunner(new RedeemerService(config, client, clientThread), gson);
        } catch (URISyntaxException e) {
            log.error("Invalid URL. Will not connect");
            throw new RuntimeException(e);
        }

        thread = new Thread(runner);
    }

    private void restartRedeemListener() {
        setupRedeemListener();

        if (thread == null) {
            return;
        }
        thread.start();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (!configChanged.getGroup().equals("streamerredeems")) {
            return;
        }
        restartRedeemListener();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
            startRedeemListener();
            return;
        }
        if (loggedIn) {
            loggedIn = false;
            stopRedeemListener();
        }
    }

    @Schedule(period = 10, unit = ChronoUnit.SECONDS, asynchronous = true)
    public void ensureServerListenerIsRunning() {
        if (thread == null || !thread.isAlive()) {
            restartRedeemListener();
        }
    }

    @Provides
    StreamerRedeemsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(StreamerRedeemsConfig.class);
    }
}
