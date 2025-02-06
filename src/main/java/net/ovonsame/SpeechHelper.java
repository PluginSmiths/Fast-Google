package net.ovonsame;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.texttospeech.v1.Texttospeech;
import com.google.api.services.texttospeech.v1.TexttospeechRequestInitializer;
import com.google.api.services.texttospeech.v1.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import com.google.api.services.speech.v1.Speech;
import com.google.api.services.speech.v1.SpeechRequestInitializer;
import com.google.api.services.speech.v1.model.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.FileInputStream;
import java.util.Optional;

public class SpeechHelper {
    private static final String API_KEY = "YOUR_API_KEY"; // Укажите ваш API-ключ
    @SuppressWarnings("unused")
    public static File textToSpeech(@NonNull String text, @NonNull String languageCode, @NonNull String gender, double speekingRate, double pitch) {
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            HttpRequestInitializer requestInitializer = new FastHttpRequestInitializer();

            Texttospeech speechService = new Texttospeech.Builder(httpTransport, jsonFactory, requestInitializer)
                    .setApplicationName("FastGoogle")
                    .setTexttospeechRequestInitializer(new TexttospeechRequestInitializer(API_KEY))
                    .build();

            SynthesisInput input = new SynthesisInput().setText(text);

            VoiceSelectionParams voice = new VoiceSelectionParams()
                    .setLanguageCode(languageCode)
                    .setSsmlGender(gender);

            AudioConfig audioConfig = new AudioConfig()
                    .setAudioEncoding("OGG_OPUS")
                    .setSpeakingRate(speekingRate)
                    .setPitch(pitch);

            SynthesizeSpeechRequest request = new SynthesizeSpeechRequest()
                    .setInput(input)
                    .setVoice(voice)
                    .setAudioConfig(audioConfig);

            SynthesizeSpeechResponse response = speechService.text().synthesize(request).execute();

            byte[] audioBytes = Base64.getDecoder().decode(response.getAudioContent());
            File outputFile = new File("FastGoogle" + System.currentTimeMillis() + ".ogg");
            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                out.write(audioBytes);
            }

            return outputFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File("FastGoogle" + System.currentTimeMillis() + ".ogg");
    }
    @SuppressWarnings("unused")
    public static Optional<String> speechToText(@NonNull File audioFile, @NonNull String languageCode) {
        try {

            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            HttpRequestInitializer requestInitializer = new FastHttpRequestInitializer();

            Speech speechService = new Speech.Builder(httpTransport, jsonFactory, requestInitializer)
                    .setApplicationName("FastGoogle")
                    .setSpeechRequestInitializer(new SpeechRequestInitializer(API_KEY))
                    .build();

            byte[] audioBytes;
            try (FileInputStream inputStream = new FileInputStream(audioFile)) {
                audioBytes = inputStream.readAllBytes();
            }

            String base64Audio = Base64.getEncoder().encodeToString(audioBytes);

            RecognitionAudio audio = new RecognitionAudio().setContent(base64Audio);

            RecognitionConfig config = new RecognitionConfig()
                    .setEncoding("OGG_OPUS")
                    .setLanguageCode(languageCode)
                    .setEnableAutomaticPunctuation(true);

            RecognizeRequest request = new RecognizeRequest()
                    .setConfig(config)
                    .setAudio(audio);

            RecognizeResponse response = speechService.speech().recognize(request).execute();

            if (response.getResults() != null && !response.getResults().isEmpty()) {
                return Optional.of(response.getResults().getFirst().getAlternatives().getFirst().getTranscript());
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}