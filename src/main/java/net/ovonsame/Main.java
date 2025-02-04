package net.ovonsame;

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.texttospeech.v1.Texttospeech;
import com.google.api.services.texttospeech.v1.model.SynthesisInput;
import com.google.api.services.texttospeech.v1.model.SynthesizeSpeechRequest;
import com.google.api.services.texttospeech.v1.model.SynthesizeSpeechResponse;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslateTextRequest;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        HttpRequestInitializer requestInitializer = new FastHttpRequestInitializer();
        String api = "";

        try {
            Translate translator = new Translate.Builder(httpTransport, jsonFactory, requestInitializer)
                    .setApplicationName("FastGoogle")
                    .build();

            Translate.Translations.List request = translator.translations()
                    .list(Collections.singletonList("Hello"), "ru")
                    .setKey(api);
            TranslationsListResponse response = request.execute();
            List<TranslationsResource> translations = response.getTranslations();

            System.out.println(translations.getFirst().getTranslatedText());

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Texttospeech speech = new Texttospeech.Builder(httpTransport, jsonFactory, requestInitializer)
                    .setApplicationName("FastGoogle")
                    .build();
            SynthesizeSpeechResponse response = speech.text().synthesize(new SynthesizeSpeechRequest().setInput(new SynthesisInput().setText("Hello"))).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}