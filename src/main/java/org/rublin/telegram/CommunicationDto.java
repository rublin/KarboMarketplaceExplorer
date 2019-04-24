package org.rublin.telegram;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.api.objects.User;

@Data
@Builder
public class CommunicationDto {
    private long chatId;
    private int messageId;
    private boolean admin;
    private User from;
    private String text;
}
