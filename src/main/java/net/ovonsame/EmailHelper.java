package net.ovonsame;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

public class EmailHelper {
    @SuppressWarnings("unused")
    public static void sendEmail(@NonNull String text, @NonNull String sender, @NonNull String getter, @NonNull String credentials) {
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            InputStream in = EmailHelper.class.getClassLoader().getResourceAsStream(credentials);
            if (in == null) throw new FileNotFoundException("File " + credentials + " was not found");
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    jsonFactory,
                    clientSecrets,
                    Collections.singletonList("https://www.googleapis.com/auth/gmail.send")
            ).setAccessType("offline").build();

            Credential credential = new Credential(Credential.AccessMethod);

            Gmail emailService = new Gmail.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName("FastGoogle")
                    .build();

            String emailContent = "From: " + sender + "\n" +
                    "To: " + getter + "\n" +
                    "Subject: FastGoogle\n\n" +
                    text;

            String encodedEmail = Base64.getUrlEncoder().encodeToString(emailContent.getBytes(StandardCharsets.UTF_8));

            Message message = new Message();
            message.setRaw(encodedEmail);

            emailService.users().messages().send(sender, message).execute();
            System.out.println("Письмо успешно отправлено!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
