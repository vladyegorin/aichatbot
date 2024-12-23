package vladyegorin;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Bot extends TelegramLongPollingBot {

    private String botToken;
    public Groq groqie;

    @Override
    public String getBotUsername() {
        return "chatgpttelegrammmm_bot";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public Bot() {
        // Load properties from config file in resources folder
        this.groqie = new Groq();
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("config.properties not found in resources folder.");
            }
            properties.load(input);
            botToken = properties.getProperty("TELEGRAM_BOT_TOKEN");
            System.out.println("Loaded bot token: " + botToken);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1); // Exit if the configuration file can't be loaded
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            var msg = update.getMessage();
            var user = msg.getFrom();
            var id = user.getId();

            System.out.println("\nNew message!");
            System.out.println("User ID: " + id);
            System.out.println("Username: " + user.getUserName());
            if (msg.hasText()) {
                System.out.println("Text message: " + msg.getText());
            } else {
                //IDontUnderstand(msg, id); // Implement error handling if necessary
            }

            String airesponse = null;
            String text = msg.getText();
            try {
                airesponse = groqie.sendMessage(text);
            } catch (IOException e) {
                airesponse = "Sorry, I couldn't process your request. Please try again later.";
                e.printStackTrace();  // Log the error
            }

            System.out.println("Response: " + airesponse);

            sendText(id, airesponse);  // Send response back to the user
        }
    }

    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) // Who are we sending a message to
                .text(what) // Message content
                .build();

        try {
            execute(sm); // Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e); // Any error will be printed here
        }
    }
}
