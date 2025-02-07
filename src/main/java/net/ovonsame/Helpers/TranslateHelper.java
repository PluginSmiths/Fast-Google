package net.ovonsame.Helpers;

import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.services.translate.TranslateRequestInitializer;
import net.ovonsame.Account;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TranslateHelper {
    @SuppressWarnings("unused")
    public static String translate(@NonNull String text, @NonNull String lang, @NonNull Account account) {
        try {
            Translate translator = new Translate.Builder(account.getHttpTransport(), account.getJsonFactory(), account.getRequestInitializer())
                    .setApplicationName(account.getApplicationName())
                    .build();

            Translate.Translations.List request = translator.translations()
                    .list(Collections.singletonList(text), lang)
                    .setKey(account.getKey());
            TranslationsListResponse response = request.execute();
            List<TranslationsResource> translations = response.getTranslations();

            return translations.getFirst().getTranslatedText();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }
    @SuppressWarnings("unused")
    public static String getLanguage(@NonNull String text, @NonNull Account account) {
        try {
            Translate translateService = new Translate.Builder(account.getHttpTransport(), account.getJsonFactory(), account.getRequestInitializer())
                    .setApplicationName("FastGoogle")
                    .setTranslateRequestInitializer(new TranslateRequestInitializer(account.getKey()))
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