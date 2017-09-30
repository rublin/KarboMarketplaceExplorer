package org.rublin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.rublin.Currency;

@Data
@Builder
public class PairDto {
    private Currency sellCurrency;
    private Currency buyCurrency;
    @JsonIgnore
    public boolean isBought() {
        return buyCurrency == Currency.KRB;
    }
}
