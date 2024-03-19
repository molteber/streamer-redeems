package com.streamerredeems;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RedeemListenerRunner implements Runnable {
    private final RedeemerService redeemerService;
    private final Gson gson;

    public RedeemListenerRunner(RedeemerService redeemerService, Gson gson) {
        this.redeemerService = redeemerService;
        this.gson = gson;
    }

    @Override
    public void run() {
        try {
            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder(redeemerService.serviceUri).GET().build();
            var lines = client.send(request, HttpResponse.BodyHandlers.ofLines()).body();

            lines.forEach(line -> {
                if (!line.startsWith("data: ")) {
                    return;
                }
                redeemerService.redeem(gson.fromJson(line.substring(6), RedeemEvent.class));
            });
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
