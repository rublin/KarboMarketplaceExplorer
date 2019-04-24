package org.rublin.telegram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BotCommands {
    INFO ("Info"),
    NOTIFY ("Notify"),
    PRICE ("Price"),
    SELL ("Sell"),
    BUY ("Buy"),
    BUY_FOR_BTC("BUY_FOR_BTC"),
    BUY_FOR_LTC("BUY_FOR_LTC"),
    BUY_FOR_ETH("BUY_FOR_ETH"),
    BUY_FOR_UAH("BUY_FOR_UAH"),
    BUY_FOR_USD("BUY_FOR_USD"),
    BUY_FOR_RUR("BUY_FOR_RUR"),
    SELL_FOR_BTC("SELL_FOR_BTC"),
    SELL_FOR_LTC("SELL_FOR_LTC"),
    SELL_FOR_ETH("SELL_FOR_ETH"),
    SELL_FOR_UAH("SELL_FOR_UAH"),
    SELL_FOR_USD("SELL_FOR_USD"),
    SELL_FOR_RUR("SELL_FOR_RUR");

    private final String command;
}
