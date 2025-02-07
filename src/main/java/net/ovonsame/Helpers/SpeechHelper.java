package net.ovonsame.Helpers;

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
import net.ovonsame.Account;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.FileInputStream;
import java.util.Optional;

public class SpeechHelper {
    @SuppressWarnings("unused")
    public static File textToSpeech(@NonNull String text, @NonNull String languageCode, @NonNull String gender, double speekingRate, double pitch, @NonNull Account account) {
        try {
            Texttospeech speechService = new Texttospeech.Builder(account.getHttpTransport(), account.getJsonFactory(), account.getRequestInitializer())
                    .setApplicationName(account.getApplicationName())
                    .setTexttospeechRequestInitializer(new TexttospeechRequestInitializer(account.getKey()))
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
            File outputFile = new File(account.getApplicationName() + System.currentTimeMillis() + ".ogg");
            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                out.write(audioBytes);
            }

            return outputFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File(account.getApplicationName() + System.currentTimeMillis() + ".ogg");
    }
    @SuppressWarnings("unused")
    public static Optional<String> speechToText(@NonNull File audioFile, @NonNull String languageCode, @NonNull Account account) {
        try {
            Speech speechService = new Speech.Builder(account.getHttpTransport(), account.getJsonFactory(), account.getRequestInitializer())
                    .setApplicationName("FastGoogle")
                    .setSpeechRequestInitializer(new SpeechRequestInitializer(account.getKey()))
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