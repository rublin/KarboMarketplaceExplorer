package org.rublin.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Objects;

@Data
@Builder
public class TelegramUser {
    @Id
    private Integer id;
    private String firstName;
    private String lastName;
    private String userName;
    private long chatId;
    private Boolean admin;

    public boolean isAdmin() {
        return Objects.nonNull(admin) && admin;
    }
}
