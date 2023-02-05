package tech.patana.traccar;

import org.traccar.Context;
import org.traccar.model.Position;
import org.traccar.protocol.Protocol;

import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class DltForwarder extends Protocol {

    private final URL forwardUrl;

    public DltForwarder(URL forwardUrl) {
        this.forwardUrl = forwardUrl;
    }

    @Override
    public Object handlePosition(Position position) {
        try {
            HttpURLConnection connection = (HttpURLConnection) forwardUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JsonObject json = new JsonObject();
            json.addProperty("deviceId", position.getDeviceId());
            json.addProperty("latitude", position.getLatitude());
            json.addProperty("longitude", position.getLongitude());
            json.addProperty("timestamp", position.getFixTime().getTime());

            String payload = new Gson().toJson(json);

            connection.getOutputStream().write(payload.getBytes());
            connection.getOutputStream().flush();

            connection.getResponseCode();
        } catch (Exception error) {
            Context.getLogger().warning("Forwarding failed: " + error.getMessage());
        }
        return null;
    }

}
