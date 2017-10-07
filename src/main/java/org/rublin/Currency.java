package org.rublin;

public enum Currency {
    KRB,
    BTC,
    UAH,
    USD,
    RUR;

    public static Currency getCurrency(String currency) {
        try {
            return Currency.valueOf(currency.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
