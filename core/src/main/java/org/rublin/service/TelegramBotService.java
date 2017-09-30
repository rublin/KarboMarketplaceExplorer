package org.rublin.service;

import lombok.extern.slf4j.Slf4j;
import org.rublin.telegram.MarketplaceBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class TelegramBotService {

    @Value("${telegram.username}")
    private String username;

    @Value("${telegram.token}")
    private String token;

    @Autowired
    private OrderService orderService;
    private TelegramBotsApi telegramBotsApi;

    @PostConstruct
    public void registerBot() {
        ApiContextInitializer.init();
        telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new MarketplaceBot(orderService, username, token));
            log.info("Telegram bot register success");
        } catch (TelegramApiRequestException e) {
            log.warn("Bot registration error {}", e.getMessage());
        } catch (Throwable throwable) {
            log.error("Something bad happened {}", throwable.getMessage());
        }
    }
}
