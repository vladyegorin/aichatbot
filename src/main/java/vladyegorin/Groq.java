package vladyegorin;

import okhttp3.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Groq {
    private static final String API_URL = "https://api.groq.com/v1/chat";
    private static final String API_KEY = "your-api-key"; // Replace with your API key
    public String apikey;
    private final OkHttpClient client;

    public Groq() {
        this.client = new OkHttpClient();
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("config.properties not found in resources folder.");
            }
            properties.load(input);
            apikey = properties.getProperty("AI-API-KEY");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1); // Exit if the configuration file can't be loaded
        }
    }



    public String sendMessage(String userInput) throws IOException {
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                "{\"message\":\"" + userInput + "\"}"
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }
}
