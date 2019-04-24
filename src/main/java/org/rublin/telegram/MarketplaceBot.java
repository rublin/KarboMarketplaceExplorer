package org.rublin.telegram;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.dto.OptimalOrderDto;
import org.rublin.dto.OptimalOrdersResult;
import org.rublin.dto.PairDto;
import org.rublin.dto.RateDto;
import org.rublin.dto.RateResponseDto;
import org.rublin.model.TelegramUser;
import org.rublin.provider.FiatRate;
import org.rublin.repository.TelegramUserRepository;
import org.rublin.service.OrderService;
import org.rublin.service.RateService;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.stream.Collectors.toList;
import static org.rublin.telegram.BotCommands.INFO;
import static org.rublin.telegram.BotCommands.NOTIFY;
import static org.rublin.telegram.TelegramKeyboardFactory.currencyKeyboard;
import static org.rublin.telegram.TelegramKeyboardFactory.defaultKeyboard;

@Slf4j
public class MarketplaceBot extends TelegramLongPollingBot {

    private static final int MAX_ORDERS_IN_MESSAGE = 50;
    private String username;
    private String token;
    private OrderService orderService;
    private TelegramUserRepository repository;
    private RateService rateService;
    private FiatRate privatRate;

    private Map<Long, LinkedList<BotCommands>> commandsHistory = new HashMap<>();
    private ConcurrentMap<Integer, TelegramUser> usersById = new ConcurrentHashMap<>();
    private ConcurrentMap<Long, TelegramUser> chatToUser = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Integer> statistic = new ConcurrentHashMap<>();

    public MarketplaceBot(OrderService orderService, TelegramUserRepository repository, RateService rateService, FiatRate privatRate, String username, String token) {
        this.orderService = orderService;
        this.username = username;
        this.token = token;
        this.repository = repository;
        this.rateService = rateService;
        this.privatRate = privatRate;
        init();
    }

    private void init() {
        List<TelegramUser> telegramUsers = repository.findAll();
        telegramUsers.forEach(u -> {
            usersById.put(u.getId(), u);
            chatToUser.put(u.getChatId(), u);
        });
    }

    public List<SendMessage> sendCustomMessage(String message) {
        List<SendMessage> messages = new ArrayList<>();
        for (ConcurrentMap.Entry entry : chatToUser.entrySet()) {
            Long chatId = (Long) entry.getKey();
            TelegramUser user = (TelegramUser) entry.getValue();
            String name = getContact(user);
            SendMessage sendMessage = createSendMessage(chatId);
            sendMessage.enableMarkdown(true);
            sendMessage.setText(String.format("Hi, *%s*!\n\n%s", name, message));
//            For test purpose only
//            if (chatId == 241931659)
//                messages.add(sendMessage);
        }
        return messages;
    }

    private void doExecute(List<SendMessage> messages) {
        messages.forEach(m -> {
            try {
                execute(m);
                Thread.sleep(100);
                log.debug("Successfully sent message to {}", m.getChatId());
            } catch (TelegramApiException e) {
                log.error("Execute error {}", e);
            } catch (InterruptedException e) {
                log.error("Sleep failed: {}", e.getMessage());
            }
        });
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                if (update.hasMessage()) {
                    Message message = update.getMessage();
                    log.debug("Received message from {} with text: {}", message.getFrom().getId(), message.getText());
                    TelegramUser telegramUser = processUserInformation(message.getChatId(), message.getFrom());
                    if (message.hasText() || message.hasLocation()) {
                        handleIncomingMessage(message, telegramUser.isAdmin());
                    }
                }
            } catch (Exception e) {
                log.error("Telegram error {} when update received", e.getMessage());
                log.debug("onUpdateReceived exception: {}", e);
            }
        }
    }

    private TelegramUser processUserInformation(Long chatId, User user) {
        Integer id = user.getId();
        TelegramUser userFromMap = usersById.getOrDefault(id, null);
        if (Objects.isNull(userFromMap)) {
            TelegramUser telegramUser = TelegramUser.builder()
                    .id(id)
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .userName(user.getUserName())
                    .chatId(chatId)
                    .build();
            log.info("Try to save new user {}", user);
            userFromMap = repository.save(telegramUser);
            log.info("User saved {}", userFromMap);
            usersById.putIfAbsent(id, telegramUser);
            chatToUser.putIfAbsent(chatId, telegramUser);
        }
        Integer count = statistic.getOrDefault(id, null);
        if (Objects.isNull(count)) {
            statistic.put(id, 1);
        } else {
            statistic.put(id, ++count);
        }
        return userFromMap;
    }

    private void handleIncomingMessage(Message message, boolean admin) throws TelegramApiException {
        log.info("Telegram bot received message {} from {}", message.getText(), message.getFrom());

        String text = message.getText().toUpperCase();
        List<SendMessage> messageList = new ArrayList<>();
        LinkedList<BotCommands> commands = commandsHistory.get(message.getChatId());
        BotCommands previousCommand = Objects.nonNull(commands) && !commands.isEmpty() ? commands.getLast() : null;

        CommunicationDto communicationDto = CommunicationDto.builder()
                .admin(admin)
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
                .text(message.getText())
                .from(message.getFrom())
                .build();
        Currency currency = Currency.getCurrency(text);
        if (text.equalsIgnoreCase("/START")) {
            messageList.add(start(communicationDto));
        } else if (text.equalsIgnoreCase(INFO.getCommand())) {
            messageList.add(info(communicationDto));
        } else if (text.equalsIgnoreCase(NOTIFY.getCommand())) {
            addTOHistory(message.getChatId(), NOTIFY);
            SendMessage sendMessage = createSendMessage(message.getChatId());
            sendMessage.setText("Type your message. It will receive each bot user");
            messageList.add(sendMessage);
        } else if (previousCommand == NOTIFY) {
            messageList.addAll(sendCustomMessage(message.getText()));
            clearHistory(message.getChatId());
        } else if (text.equalsIgnoreCase(BotCommands.PRICE.name())) {
            addTOHistory(message.getChatId(), BotCommands.PRICE);
            messageList.add(price(communicationDto));
        } else if (text.equalsIgnoreCase(BotCommands.BUY.name())) {
            addTOHistory(message.getChatId(), BotCommands.BUY);
            messageList.add(buyCommand(message));
        } else if (text.equalsIgnoreCase(BotCommands.SELL.name())) {
            addTOHistory(message.getChatId(), BotCommands.SELL);
            messageList.add(sellCommand(message));
        } else if (Objects.nonNull(currency)
                && Objects.nonNull(previousCommand)
                && previousCommand == BotCommands.BUY) {
            addTOHistory(message.getChatId(), BotCommands.valueOf("BUY_FOR_".concat(currency.name())));
            messageList.add(amount(message, currency));
        } else if (Objects.nonNull(currency)
                && Objects.nonNull(previousCommand)
                && previousCommand == BotCommands.SELL) {
            addTOHistory(message.getChatId(), BotCommands.valueOf("SELL_FOR_".concat(currency.name())));
            messageList.add(amount(message, Currency.KRB));
        } else {
            if (Objects.nonNull(previousCommand) && previousCommand.toString().startsWith("BUY_FOR_")) {
                String currencyStr = previousCommand.toString().substring(8);
                Currency toCurrency = Currency.valueOf(currencyStr);
                log.info("Received BUY request for {} currency and {} amount", currencyStr, text);
                Optional<BigDecimal> amountOpt = getAmount(text);
                if (amountOpt.isPresent()) {
                    BigDecimal amount = amountOpt.get();
                    messageList.addAll(buyCommand(communicationDto, amount, toCurrency));
                    if (toCurrency == Currency.UAH) {
                        RateDto rate = privatRate.rate(PairDto.builder()
                                .sellCurrency(Currency.UAH)
                                .buyCurrency(Currency.USD)
                                .build());
                        if (Objects.nonNull(rate)) {
                            amount = amount.divide(rate.getBuyRate(), BigDecimal.ROUND_HALF_UP);
                            messageList.addAll(buyCommand(communicationDto, amount, Currency.USD));
                        }
                    } else if (toCurrency == Currency.USD) {
                        RateDto rate = privatRate.rate(PairDto.builder()
                                .sellCurrency(Currency.UAH)
                                .buyCurrency(Currency.USD)
                                .build());
                        if (Objects.nonNull(rate)) {
                            amount = amount.multiply(rate.getSaleRate());
                            messageList.addAll(buyCommand(communicationDto, amount, Currency.UAH));
                        }
                    }
                } else {
                    messageList.add(amountError(communicationDto));
                }

                clearHistory(message.getChatId());
            } else if (Objects.nonNull(previousCommand) && previousCommand.toString().startsWith("SELL_FOR_")) {
                String currencyStr = previousCommand.toString().substring(9);
                log.info("Received SELL request for {} currency and {} amount", currencyStr, text);
                messageList.addAll(sellCommand(communicationDto, Currency.valueOf(currencyStr)));
                clearHistory(message.getChatId());
            } else {
                clearHistory(message.getChatId());
                messageList.add(defaultCommand(communicationDto));
            }
        }

        doExecute(messageList);
    }

    private SendMessage amountError(CommunicationDto message) {
        SendMessage sendMessage = createSendMessage(message.getChatId(), message.getMessageId(), defaultKeyboard(message.isAdmin()));
        sendMessage.setText("Amount format is wrong. Please, use only digitals and . as a separator");
        return sendMessage;
    }

    private Optional<BigDecimal> getAmount(String text) {
        BigDecimal amount = null;
        try {
            String strAmount = text.replaceAll(",", ".");
            amount = BigDecimal.valueOf(Double.valueOf(strAmount));
        } catch (Exception e) {
            log.warn("Wrong amount {} format: {}", text, e.getMessage());
        }
        return Optional.ofNullable(amount);
    }

    private SendMessage info(CommunicationDto message) {
        SendMessage sendMessage = createSendMessage(message.getChatId(), message.getMessageId(), defaultKeyboard(message.isAdmin()));
        StringBuilder builder = new StringBuilder();
        builder.append("*Price* will return [Karbo](https://karbo.io) price to other currencies\n");
        builder.append("*Sell* will return optimal sell orders\n");
        builder.append("*Buy* will return optimal buy orders\n\n");
        builder.append("Source code is available [here](https://github.com/rublin/KarboMarketplaceExplorer)\n\n");
        builder.append("Address for donate: \n" +
                "*donate.rublin.org* or\n" +
                "*KaAxHCPtJaFGDq4xLn3fASf3zVrAmqyE4359zn3r3deVjCeM3CYq7K4Y1pkfZkjfRd1W2VPXVZdA5RBdpc4Vzamo1H4F5qZ*");
        sendMessage.setText(builder.toString());
        return sendMessage;
    }

    private SendMessage start(CommunicationDto message) {
        SendMessage sendMessage = createSendMessage(message.getChatId(), message.getMessageId(), defaultKeyboard(message.isAdmin()));
        sendMessage.enableMarkdown(true);
        StringBuilder builder = new StringBuilder();
        builder.append("Hello, *").append(getContact(message.getFrom())).append("*\n\n");
        builder.append("You are welcomed by the *Karbo marketplace bot*");
        sendMessage.setText(builder.toString());
        return sendMessage;
    }

    private String getContact(User user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String userName = user.getUserName();
        if (Objects.nonNull(firstName)) {
            return firstName;
        } else if (Objects.nonNull(userName)) {
            return userName;
        } else if (Objects.nonNull(lastName)) {
            return lastName;
        }
        return "anonymous user";
    }

    private String getContact(TelegramUser user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String userName = user.getUserName();
        if (Objects.nonNull(firstName)) {
            return firstName;
        } else if (Objects.nonNull(userName)) {
            return userName;
        } else if (Objects.nonNull(lastName)) {
            return lastName;
        }
        return "anonymous user";
    }

    private List<SendMessage> buyCommand(CommunicationDto message, BigDecimal amount, Currency currency) {
        try {
//            BigDecimal amount = BigDecimal.valueOf(Double.valueOf(text));
            List<OptimalOrdersResult> optimalOrders = orderService.findOptimalOrders(PairDto.builder()
                            .buyCurrency(Currency.KRB)
                            .sellCurrency(currency)
                            .build(),
                    amount);

            return splitAndPrepare(optimalOrders, message);
        } catch (Exception e) {
            log.warn("Unexpected exception {}", e.getMessage());
            log.debug("Exception {}", e);
            SendMessage sendMessage = createSendMessage(message.getChatId(), message.getMessageId(), defaultKeyboard(message.isAdmin()));
            sendMessage.setText("Something wrong, try again");
            return Collections.singletonList(sendMessage);
        }
    }

    private List<SendMessage> sellCommand(CommunicationDto message, Currency currency) {
        String text = message.getText();
        try {
            BigDecimal amount = BigDecimal.valueOf(Double.valueOf(text));
            List<OptimalOrdersResult> optimalOrders = orderService.findOptimalOrders(PairDto.builder()
                            .buyCurrency(currency)
                            .sellCurrency(Currency.KRB)
                            .build(),
                    amount);
            return splitAndPrepare(optimalOrders, message);
        } catch (Exception e) {
            log.warn("Unexpected exception {}", e.getMessage());
            log.debug("Exception {}", e);
            SendMessage sendMessage = createSendMessage(message.getChatId(), message.getMessageId(), defaultKeyboard(message.isAdmin()));
            sendMessage.setText("Something wrong, try again");
            return Collections.singletonList(sendMessage);
        }
    }

    private List<SendMessage> splitAndPrepare(List<OptimalOrdersResult> optimalOrders, CommunicationDto message) {
        List<SendMessage> messageList = new LinkedList<>();
        for (OptimalOrdersResult order : optimalOrders) {
            List<String> ordersResponse = createOrdersResponse(order);
            for (String stringMessage : ordersResponse) {
                SendMessage sendMessage = createSendMessage(message.getChatId(), message.getMessageId(), defaultKeyboard(message.isAdmin()));
                sendMessage.enableMarkdown(true);
                sendMessage.setText(stringMessage);
                messageList.add(sendMessage);
            }
        }
        return messageList;
    }

    private String createPriceResponse(List<RateDto> rates) {
        StringBuilder builder = new StringBuilder();
        TradePlatform marketplace = rates.get(0).getMarketplace();
        builder.append("*").append(marketplace).append("*\n");

        List<RateDto> sorted = rates.stream().sorted(Comparator.comparing(r -> r.getTarget().name())).collect(toList());
        for (RateDto rate : sorted) {
            builder.append(rate.getTarget())
                    .append(": ")
                    .append(rate.getSaleRate().stripTrailingZeros().toPlainString())
                    .append("  ||  ")
                    .append(rate.getBuyRate().stripTrailingZeros().toPlainString())
                    .append("  ")
//                    .append(rate.getMarketplace())
//                    .append("* ")
                    .append(Objects.nonNull(rate.getInfo()) ? rate.getInfo() : "")
                    .append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }

    private String createPriceResponse(RateResponseDto rate) {
        Map<Currency, List<RateDto>> byCurrency = rate.getByCurrency();
        StringBuilder builder = new StringBuilder();
        Map<TradePlatform, List<RateDto>> byMarketplace = rate.getByMarketplace();
        String result = Arrays.stream(TradePlatform.values())
                .map(byMarketplace::get)
                .filter(Objects::nonNull)
                .map(this::createPriceResponse)
                .reduce("\n", String::concat);

        return result;
    }

    private List<String> createOrdersResponse(OptimalOrdersResult optimalOrders) {
        List<String> stringMessages = new LinkedList<>();
        StringBuilder builder = new StringBuilder();
        Currency sell = optimalOrders.getPair().getSellCurrency();
        Currency buy = optimalOrders.getPair().getBuyCurrency();
        List<OptimalOrderDto> orderList = optimalOrders.getOptimalOrders();
//        builder.append("*");
        builder.append("You sell *");
        builder.append(optimalOrders.getAmountSell().toPlainString()).append(sell);
        builder.append("*\nYou buy *");
        builder.append(optimalOrders.getAmountBuy().toPlainString()).append(buy);
        builder.append("*\nFirst rate *").append(orderList.get(0).getRate());
        builder.append("*\nAverage rate *").append(optimalOrders.getAverageRate());
        builder.append("*\nLast rate *").append(orderList.get(orderList.size() - 1).getRate());
        builder.append("*\n\n");
        stringMessages.add(builder.toString());

        List<List<OptimalOrderDto>> partition = Lists.partition(orderList, MAX_ORDERS_IN_MESSAGE);
        for (List<OptimalOrderDto> part : partition) {
            builder = new StringBuilder();
            builder.append("*Rate* -> *Amount to sell* -> *Amount to buy* => *Trade platform*\n");
            for (OptimalOrderDto order : part) {
                builder.append(order.getRate().stripTrailingZeros().toPlainString());
                builder.append(" -> ");
                builder.append(order.getAmountToSale().stripTrailingZeros().toPlainString()).append(sell);
                builder.append(" -> ");
                builder.append(order.getAmountToBuy().stripTrailingZeros().toPlainString()).append(buy);
                builder.append(" => *").append(order.getMarketplace()).append("*\n");
            }
            stringMessages.add(builder.toString());
        }
        log.info("Created {} messages from {} orders", stringMessages.size(), orderList.size());

        return stringMessages;
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

    private SendMessage amount(Message message, Currency currency) {
        SendMessage sendMessage = createSendMessage(message.getChatId(), message.getMessageId(), null);
        sendMessage.setText("Type amount of ".concat(currency.name()));
        return sendMessage;
    }

    private SendMessage sellCommand(Message message) {
        SendMessage sendMessage = createSendMessage(message.getChatId(),
                message.getMessageId(),
                currencyKeyboard());
        sendMessage.setText("Select currency what you want to convert with Karbo");

        return sendMessage;
    }

    private SendMessage buyCommand(Message message) {
        SendMessage sendMessage = createSendMessage(message.getChatId(),
                message.getMessageId(),
                currencyKeyboard());
        sendMessage.setText("Select currency what you want to convert with Karbo");

        return sendMessage;
    }

    private SendMessage price(CommunicationDto message) {
        SendMessage sendMessage = createSendMessage(message.getChatId(), message.getMessageId(), defaultKeyboard(message.isAdmin()));
        RateResponseDto rates = rateService.getCurrentRate();
        sendMessage.setText(createPriceResponse(rates));
        return sendMessage;
    }

    private SendMessage defaultCommand(CommunicationDto message) {
        SendMessage sm = createSendMessage(message.getChatId(), message.getMessageId(), defaultKeyboard(message.isAdmin()));
        sm.setText("Unsupported command");

        return sm;
    }

    private SendMessage createSendMessage(Long chatId, Integer messageId, ReplyKeyboard keyboard) {
        SendMessage message = createSendMessage(chatId);
        message.setReplyToMessageId(messageId);
        message.setReplyMarkup(keyboard);
        message.enableMarkdown(Objects.nonNull(keyboard));
        return message;
    }

    private SendMessage createSendMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        return message;
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
