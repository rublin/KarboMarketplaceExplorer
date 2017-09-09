package org.rublin.dto;

import lombok.Builder;
import lombok.Data;
import org.rublin.Currency;

@Data
@Builder
public class PairDto {
    private Currency sellCurrency;
    private Currency buyCurrency;
}
