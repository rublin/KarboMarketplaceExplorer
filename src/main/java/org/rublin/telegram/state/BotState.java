package org.rublin.telegram.state;

import org.rublin.Currency;

import java.math.BigInteger;

public interface BotState {
    void idle();

    void buying();
    void choosingBuyingCurrency(Currency whatExchangeFrom);
    void typingBuyingAmount(BigInteger amountToSell);

    void selling();
    void choosingSellingCurrency(Currency whatExchangeFor);
    void typingSellingAmount(BigInteger amountToSell);
}
