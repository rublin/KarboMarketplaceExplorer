package org.rublin.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class CryptorpiaPairsEntity {

    @Value("${provider.cryptopia.pair}")
    private String pairString;
}
