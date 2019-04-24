package org.rublin.telegram;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;

import static org.rublin.Currency.BTC;
import static org.rublin.Currency.ETH;
import static org.rublin.Currency.LTC;
import static org.rublin.Currency.RUR;
import static org.rublin.Currency.UAH;
import static org.rublin.Currency.USD;

class TelegramKeyboardFactory {
    static ReplyKeyboardMarkup defaultKeyboard(boolean admin) {
        ReplyKeyboardMarkup keyboardMarkup = initKeyboard();
        keyboardMarkup.getKeyboard().add(createKeyboardRow("Sell", "Price", "Buy"));
        if (admin) {
            keyboardMarkup.getKeyboard().add(createKeyboardRow("Notify", "Info"));
        } else {
            keyboardMarkup.getKeyboard().add(createKeyboardRow("Info"));
        }
        return keyboardMarkup;
    }

    static ReplyKeyboard currencyKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = initKeyboard();

        keyboardMarkup.getKeyboard().add(createKeyboardRow(BTC.name(), LTC.name(), ETH.name()));
        keyboardMarkup.getKeyboard().add(createKeyboardRow(UAH.name(), USD.name(), RUR.name()));
        return keyboardMarkup;
    }

    private static ReplyKeyboardMarkup initKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setKeyboard(new ArrayList<>());

        return keyboardMarkup;
    }

    private static KeyboardRow createKeyboardRow(String... commands) {
        KeyboardRow row = new KeyboardRow();
        Arrays.stream(commands)
                .forEach(row::add);
        return row;
    }
}
