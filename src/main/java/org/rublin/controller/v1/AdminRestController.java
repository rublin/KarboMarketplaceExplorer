package org.rublin.controller.v1;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rublin.service.TelegramBotService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = AdminRestController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminRestController extends AbstractController {

    static final String URL = URL_PREFIX + "admin";

    private final TelegramBotService botService;

    @RequestMapping(value = "/send", method = RequestMethod.PUT)
    public String sendMessage(@RequestBody String message) {
        log.info("Receive send request with {} message", message);
        int count = botService.sendMessage(message);
        log.info("Message {} sent success for () users", message, count);

        return "Send success for " + count + " users";
    }
}
