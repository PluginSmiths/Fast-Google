package net.ovonsame;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Account {
    private final String accountFile;
    private @Nullable JsonObject jsonData;

    @SuppressWarnings("unused")
    public Account(@Nonnull String accountFile) throws IOException {
        this.accountFile = accountFile;
        loadJsonData();
    }

    private void loadJsonData() {
        try {
            InputStream stream = new ClassPathResource("account/" + accountFile).getInputStream();
            String jsonString = new String(stream.readAllBytes());
            jsonData = JsonParser.parseString(jsonString).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public String getApplicationName() {
        loadJsonData();
        return jsonData.get("application_name").getAsString();
    }

    @SuppressWarnings("unused")
    public String getKey() {
        loadJsonData();
        return jsonData.get("key").getAsString();
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        loadJsonData();
        return jsonData.get("email").getAsString();
    }

    @SuppressWarnings("unused")
    public File getFile() {
        loadJsonData();
        try {
            String fileName = jsonData.get("file").getAsString();
            ClassPathResource resource = new ClassPathResource("account/" + fileName);
            return resource.getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unused")
    public JsonFactory getJsonFactory() {
        return GsonFactory.getDefaultInstance();
    }

    @SuppressWarnings("unused")
    public HttpRequestInitializer getRequestInitializer() {
        return new FastHttpRequestInitializer();
    }

    @SuppressWarnings("unused")
    public HttpTransport getHttpTransport() {
        return new NetHttpTransport();
    }
}
