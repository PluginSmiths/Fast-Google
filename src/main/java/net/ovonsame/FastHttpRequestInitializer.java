package net.ovonsame;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;

public class FastHttpRequestInitializer implements HttpRequestInitializer {
    @Override
    public void initialize(HttpRequest request) {
        request.setConnectTimeout(30000);
        request.setReadTimeout(30000);
    }
}
