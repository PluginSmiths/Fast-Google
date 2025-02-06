package net.ovonsame;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import com.google.common.io.BaseEncoding;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Collections;
import java.io.FileInputStream;
import java.io.IOException;

public class EmailHelper {

    public static void sendEmail(String to, String subject, String bodyText) throws Exception {
        InputStream in = new FileInputStream("src/airy-legacy-449915-c4-26d4d75ef9b7.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(List.of("https://www.googleapis.com/auth/gmail.send"));

        Gmail service = new Gmail.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), new HttpCredentialsAdapter(credentials))
                .setApplicationName("FastGoogle")
                .build();

        MimeMessage email = createEmail(to, subject, bodyText);

        sendMessage(service, "fastgoogle2@airy-legacy-449915-c4.iam.gserviceaccount.com", email);
    }

    private static MimeMessage createEmail(String to, String subject, String bodyText) throws MessagingException {
        Properties props = System.getProperties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress("fastgoogle2@airy-legacy-449915-c4.iam.gserviceaccount.com"));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);

        return email;
    }

    private static Message sendMessage(Gmail service, String userId, MimeMessage emailContent) throws MessagingException, IOException {
        // Переводим MimeMessage в Base64 строку
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        emailContent.writeTo(byteArrayOutputStream);
        byte[] emailBytes = byteArrayOutputStream.toByteArray();

        String encodedEmail = BaseEncoding.base64Url().encode(emailBytes);

        Message message = new Message();
        message.setRaw(encodedEmail);

        return service.users().messages().send(userId, message).execute();
    }

    public static void main(String[] args) {
        try {
            sendEmail("aroslavkopejcenko@gmail.com", "Test Subject", "This is the email body text.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
