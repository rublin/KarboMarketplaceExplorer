package org.rublin.telegram;

import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OptimalOrdersResult;
import org.rublin.dto.PairDto;
import org.rublin.service.OrderService;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class MarketplaceBot extends TelegramLongPollingBot {

    private String username;
    private String token;
    private OrderService orderService;

    private Map<Long, LinkedList<BotCommands>> commandsHistory = new HashMap<>();


    public MarketplaceBot(OrderService orderService, String username, String token) {
        this.orderService = orderService;
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
                log.error("Telegram error {} when update received", e.getMessage());
                log.debug("onUpdateReceived exception: {}", e);
            }
        }
    }

    private void handleIncomingMessage(Message message) throws TelegramApiException {
        log.info("Telegram bot received message {} from {}", message.getText(), message.getFrom());

        String text = message.getText().toUpperCase();
        SendMessage sendMessageRequest;
        LinkedList<BotCommands> commands = commandsHistory.get(message.getChatId());
        BotCommands previousCommand = Objects.nonNull(commands) && !commands.isEmpty() ? commands.getLast() : null;

        if (text.equals(BotCommands.PRICE.name())) {
            addTOHistory(message.getChatId(), BotCommands.PRICE);
            sendMessageRequest = pricePrice(message);
        } else if (text.equals(BotCommands.BUY.name())) {
            addTOHistory(message.getChatId(), BotCommands.BUY);
            sendMessageRequest = buyCommand(message);
        } else if (text.equals(BotCommands.SELL.name())) {
            addTOHistory(message.getChatId(), BotCommands.SELL);
            sendMessageRequest = sellCommand(message);
        } else if (BotCommands.BUY_FOR_BTC.name().contains(text)
                && Objects.nonNull(previousCommand)
                && previousCommand == BotCommands.BUY) {
            addTOHistory(message.getChatId(), BotCommands.BUY_FOR_BTC);
            sendMessageRequest = amount(message);
        } else if (BotCommands.BUY_FOR_UAH.name().contains(text)
                && Objects.nonNull(previousCommand)
                && previousCommand == BotCommands.BUY) {
            addTOHistory(message.getChatId(), BotCommands.BUY_FOR_UAH);
            sendMessageRequest = amount(message);
        } else {

            if (previousCommand == BotCommands.BUY_FOR_BTC) {
                sendMessageRequest = buyCommand(message, Currency.BTC);
                clearHistory(message.getChatId());
            } else if (previousCommand == BotCommands.BUY_FOR_UAH) {
                sendMessageRequest = buyCommand(message, Currency.UAH);
                clearHistory(message.getChatId());
            } else {
                clearHistory(message.getChatId());
                sendMessageRequest = defaultCommand(message);
            }
        }

        sendMessage(sendMessageRequest);
    }

    private SendMessage buyCommand(Message message, Currency currency) {
        String text = message.getText();
        SendMessage sendMessage = createSendMessage(message.getChatId(), message.getMessageId(), defaultKeyboard());
        try {
            BigDecimal amount = BigDecimal.valueOf(Double.valueOf(text));
            OptimalOrdersResult optimalOrders = orderService.findOptimalOrders(PairDto.builder()
                            .buyCurrency(Currency.KRB)
                            .sellCurrency(currency)
                            .build(),
                    amount);
            sendMessage.enableMarkdown(true);
//            sendMessage.setText("333.8251900000UAH -> 844.5777300000KRB => BTCTRADE");
            sendMessage.setText(createOrdersResponse(optimalOrders));
        } catch (Exception e) {
            log.warn("Unexpected exception {}", e.getMessage());
            log.debug("Exception {}", e);
            sendMessage.setText("Something wrong, try again");
        }
        return sendMessage;
    }

    private String createOrdersResponse(OptimalOrdersResult optimalOrders) {
        StringBuilder builder = new StringBuilder();
        Currency sell = optimalOrders.getPair().getSellCurrency();
        Currency buy = optimalOrders.getPair().getBuyCurrency();
        builder.append("*");
        builder.append("You sell ");
        builder.append(optimalOrders.getAmount());
        builder.append(sell);
        builder.append(" and buy ");
//        builder.append()
        builder.append(buy);
        builder.append("*\n\n");
        for (OptimalOrderDto order : optimalOrders.getOptimalOrders()) {
            builder.append(order.getAmountToSale()).append(sell);
            builder.append(" -> ");
            builder.append(order.getAmountToBuy()).append(buy);
            builder.append(" => *").append(order.getMarketplace()).append("*\n");
        }

        return builder.toString();
    }

    private void clearHistory(Long chatId) {
        if (commandsHistory.containsKey(chatId)) {
            commandsHistory.put(chatId, new LinkedList<>());
        }
    }

    private void addTOHistory(Long chatId, BotCommands command) {
        if (commandsHistory.containsKey(chatId)) {
            commandsHistory.get(chatId).add(command);
        } else {
            LinkedList<BotCommands> commands = new LinkedList<>();
            commands.add(command);
            commandsHistory.put(chatId, commands);
        }
    }

    private SendMessage amount(Message message) {
        SendMessage sendMessage = createSendMessage(message.getChatId(), message.getMessageId(), null);
        sendMessage.setText("Type amount to buy");
        return sendMessage;
    }

    private SendMessage sellCommand(Message message) {
        SendMessage sendMessage = createSendMessage(message.getChatId(), message.getMessageId(), null);
        sendMessage.setText("Want to sell?");

        return sendMessage;
    }

    private SendMessage buyCommand(Message message) {
        SendMessage sendMessage = createSendMessage(message.getChatId(), message.getMessageId(), currencyKeyboard(Currency.BTC, Currency.UAH));
        sendMessage.setText("Want to buy?");

        return sendMessage;
    }

    private ReplyKeyboard currencyKeyboard(Currency... currencies) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        for (Currency currency : currencies) {
            KeyboardButton button = new KeyboardButton();
            button.setText(currency.name());
            row.add(button);
        }

        // Add the first row to the keyboard
        keyboard.add(row);
        // Create another keyboard row
        row = new KeyboardRow();
        keyboard.add(row);
        // Set the keyboard to the markup
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
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
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Create a keyboard row
        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton();
        button.setText("Sell");
        // Set each button, you can also use KeyboardButton objects if you need something else than text
        row.add(button);
        row.add("Price");
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
