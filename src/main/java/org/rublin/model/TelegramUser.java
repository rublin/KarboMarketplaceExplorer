package org.rublin.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class TelegramUser {
    @Id
    private Integer id;
    private String firstName;
    private String lastName;
    private String userName;
    private long chatId;
}
