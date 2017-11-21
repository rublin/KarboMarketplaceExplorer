package org.rublin;

public enum Currency {
    KRB,
    UAH,
    USD,
    BTC,
    BCH,
    LTC,
    DOGE,
    RUR;

    public static Currency getCurrency(String currency) {
        try {
            return Currency.valueOf(currency.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
