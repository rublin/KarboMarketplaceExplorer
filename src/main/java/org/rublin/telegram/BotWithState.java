package org.rublin.telegram;

import org.rublin.telegram.state.BotState;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BotWithState {
    private BotState idle;
    private BotState buying;
    private BotState buyingCurrencyChoose;
    private BotState buyingAmountEntered;
    private BotState selling;
    private BotState sellingCurrencyChoose;
    private BotState sellingAmountEntered;

    private Map<Long, BotState> currentState;

}
