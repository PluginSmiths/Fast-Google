package net.ovonsame;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.services.translate.TranslateRequestInitializer;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TranslateHelper {
    @SuppressWarnings("unused")
    public static String translate(@NonNull String text, @NonNull String lang) {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        HttpRequestInitializer requestInitializer = new FastHttpRequestInitializer();
        String api = "";

        try {
            Translate translator = new Translate.Builder(httpTransport, jsonFactory, requestInitializer)
                    .setApplicationName("FastGoogle")
                    .build();

            Translate.Translations.List request = translator.translations()
                    .list(Collections.singletonList(text), lang)
                    .setKey(api);
            TranslationsListResponse response = request.execute();
            List<TranslationsResource> translations = response.getTranslations();

            return translations.getFirst().getTranslatedText();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }
    @SuppressWarnings("unused")
    public static String getLanguage(@NonNull String text) {
        try {
            String api = "";

            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            HttpRequestInitializer requestInitializer = new FastHttpRequestInitializer();

            Translate translateService = new Translate.Builder(httpTransport, jsonFactory, requestInitializer)
                    .setApplicationName("FastGoogle")
                    .setTranslateRequestInitializer(new TranslateRequestInitializer(api))
                    .build();
            ArrayList<String> lang = new ArrayList<>();
            lang.add(text);

            DetectionsListResponse detections = translateService.detections().detect(new DetectLanguageRequest()
                    .setQ(lang))
                    .execute();
            List<List<DetectionsResourceItems>> items = detections.getDetections();
            return items.getFirst().getFirst().getIsReliable() ? items.getFirst().getFirst().getLanguage() : null;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}