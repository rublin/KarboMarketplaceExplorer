package org.rublin.service;

import lombok.extern.slf4j.Slf4j;
import org.rublin.provider.MarketplaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScheduleService {

    @Autowired
    private TelegramBotService botService;

    @Autowired
    private RateService rateService;

    @Autowired
    private MarketplaceService marketplaceService;

 //   @Scheduled(cron = "0 30 15 6 * *")
//    @Scheduled(fixedRate = 60000)
    public void askForDonation() {
        String message = "Do you like using this bot? Hope so...\n" +
                "KARBO address (KRB) for donation\n\n" +
                "*donate.rublin.org* or\n" +
                "*KaAxHCPtJaFGDq4xLn3fASf3zVrAmqyE4359zn3r3deVjCeM3CYq7K4Y1pkfZkjfRd1W2VPXVZdA5RBdpc4Vzamo1H4F5qZ*";
        log.info("Ask for donation: \n {}", message);
        botService.sendMessage(message);
    }

    @Scheduled(fixedDelay = 120000)
    public void updateCache() {
        log.info("cache update start");
        long start = System.currentTimeMillis();
        marketplaceService.createCache();
//        rateService.updateCacheRate();
        log.info("cache update takes {} ms", System.currentTimeMillis() - start);
    }
}
