package org.rublin.telegram.state;

import org.rublin.Currency;

import java.math.BigInteger;

public class IdleState implements BotState {

    @Override
    public void idle() {

    }

    @Override
    public void buying() {

    }

    @Override
    public void choosingBuyingCurrency(Currency whatExchangeFrom) {

    }

    @Override
    public void typingBuyingAmount(BigInteger amountToSell) {

    }

    @Override
    public void selling() {

    }

    @Override
    public void choosingSellingCurrency(Currency whatExchangeFor) {

    }

    @Override
    public void typingSellingAmount(BigInteger amountToSell) {

    }
}
