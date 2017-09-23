package org.rublin.telegram;

import com.sun.xml.internal.ws.api.model.MEP;
import lombok.extern.slf4j.Slf4j;
import org.rublin.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            try {
                if (update.hasMessage()) {
                    Message message = update.getMessage();
                    if (message.hasText() || message.hasLocation()) {
                        handleIncomingMessage(message);
                    }
                }
            } catch (Exception e) {
                log.error("Unexpected error {} when update received", e.getMessage());
                log.debug("onUpdateReceived exception: {}", e);
            }

            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            SendMessage message = new SendMessage().setChatId(chat_id);
            log.debug("Telegram bot received message {} from {}", update.getMessage().getText(), update.getMessage().getContact());

            try {
                sendMessage(message); // Call method to send the message
            } catch (TelegramApiException e) {
                log.warn("Telegram error {}", e.getMessage());
            }
        }
    }

    private void handleIncomingMessage(Message message) throws TelegramApiException {

        String text = message.getText().toUpperCase();
        SendMessage sendMessageRequest;
        if (text.equals(BotCommands.PRICE.name())) {
            sendMessageRequest = pricePrice(message);
        } else if (text.equals(BotCommands.BUY.name())) {
            sendMessageRequest = buyCommand(message);
        } else if (text.equals(BotCommands.SELL.name())) {
            sendMessageRequest = sellCommand(message);
        } else {
            sendMessageRequest = defaultCommand(message);
        }


        sendMessage(sendMessageRequest);
    }

    private SendMessage sellCommand(Message message) {
        SendMessage sendMessage = createSendMessage(message.getChatId(), message.getMessageId(), null);
        sendMessage.setText("Want to sell?");

        return sendMessage;
    }

    private SendMessage buyCommand(Message message) {
        SendMessage sendMessage = createSendMessage(message.getChatId(), message.getMessageId(), null);
        sendMessage.setText("Want to buy?");

        return sendMessage;
    }

    private SendMessage pricePrice(Message message) {
        SendMessage sendMessage = createSendMessage(message.getChatId(), message.getMessageId(), null);
        sendMessage.setText("Karbo price is TO_THE_MOON");
        return sendMessage;
    }

    private SendMessage defaultCommand(Message message) {
        SendMessage sm = createSendMessage(message.getChatId(), message.getMessageId(), defaultKeyboard());
        sm.setText("Unsupported command");

        return sm;
    }

    private SendMessage createSendMessage(Long chatId, Integer messageId, ReplyKeyboard keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setReplyToMessageId(messageId);
        message.setReplyMarkup(keyboard);
        message.enableMarkdown(Objects.nonNull(keyboard));
        return message;
    }

    private ReplyKeyboardMarkup defaultKeyboard() {
        // Create ReplyKeyboardMarkup object
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Create a keyboard row
        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton();
        button.setText("Sell");
        // Set each button, you can also use KeyboardButton objects if you need something else than text
        row.add("Price");
        row.add(button);
        row.add("Buy");
        // Add the first row to the keyboard
        keyboard.add(row);
        // Create another keyboard row
//                row = new KeyboardRow();
//                // Set each button for the second line
//                row.add("Row 2 Button 1");
//                row.add("Row 2 Button 2");
//                row.add("Row 2 Button 3");
//                // Add the second row to the keyboard
//                keyboard.add(row);
        // Set the keyboard to the markup
        keyboardMarkup.setKeyboard(keyboard);
        // Add it to the message
        return keyboardMarkup;
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
