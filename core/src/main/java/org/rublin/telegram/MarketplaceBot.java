package org.rublin.telegram;

import lombok.extern.slf4j.Slf4j;
import org.rublin.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MarketplaceBot extends TelegramLongPollingBot {

    private String username;
    private String token;

    @Autowired
    private OrderService orderService;

    public MarketplaceBot(String username, String token) {
        this.username = username;
        this.token = token;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            SendMessage message = new SendMessage().setChatId(chat_id);
            log.debug("Telegram bot received message {} from {}", update.getMessage().getText(), update.getMessage().getContact());
            if (message_text.equals("/start")) {
                //ToDo

            } else if (message_text.startsWith("/markup")) {

                message.setText("Keyboard");
                // Create ReplyKeyboardMarkup object
                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                // Create the keyboard (list of keyboard rows)
                List<KeyboardRow> keyboard = new ArrayList<>();
                // Create a keyboard row
                KeyboardRow row = new KeyboardRow();
                // Set each button, you can also use KeyboardButton objects if you need something else than text
                row.add("Karbo price");
                row.add("Row 1 Button 2");
                row.add("Row 1 Button 3");
                // Add the first row to the keyboard
                keyboard.add(row);
                // Create another keyboard row
                row = new KeyboardRow();
                // Set each button for the second line
                row.add("Row 2 Button 1");
                row.add("Row 2 Button 2");
                row.add("Row 2 Button 3");
                // Add the second row to the keyboard
                keyboard.add(row);
                // Set the keyboard to the markup
                keyboardMarkup.setKeyboard(keyboard);
                // Add it to the message
                message.setReplyMarkup(keyboardMarkup);
            } else if (message_text.equals("Karbo price")) {
                message.setText("Karbo price is ToTheMoon");

            } else {
                message = new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId())
                        .setText("Unknown command");
            }

            try {
                sendMessage(message); // Call method to send the message
            } catch (TelegramApiException e) {
                log.warn("Telegram error {}", e.getMessage());
            }
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

}
