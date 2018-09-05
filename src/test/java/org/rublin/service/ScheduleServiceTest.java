package org.rublin.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ScheduleServiceTest {

    @Autowired
    ScheduleService scheduleService;

    @MockBean
    TelegramBotService telegramBotService;

    @Test
    public void updateCache() {
        scheduleService.updateCache();
        int threadCount = Thread.activeCount();
        for (int i = 0; i <= 3; i++)
            scheduleService.updateCache();
        int threadNewCount = Thread.activeCount();
        assertTrue(String.format("Old thread count is %d; new thread count is %d", threadCount, threadNewCount), threadNewCount - threadCount < 5);
    }
}