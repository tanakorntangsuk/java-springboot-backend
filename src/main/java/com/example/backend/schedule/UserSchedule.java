package com.example.backend.schedule;

import com.example.backend.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UserSchedule {

    private final UserService userService;

    public UserSchedule(UserService userService) {
        this.userService = userService;
    }

    // Schedule None
    // 1 => second
    // 2 => minute
    // 3 => hour
    // 4 => day
    // 5 => month
    // 6 => year

    /**
     * Every minute
     */
    @Scheduled(cron = "0 * * * * *", zone = "Asia/Bangkok")
    public void testEveryMinute() {
        log.info("Hello, What's up");
    }

    /**
     * Every day 00:00
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Bangkok")
    public void testEveryMidNight() {
        log.info("Hello, What's up");
    }

    /**
     * Every day 14:45
     */
    @Scheduled(cron = "0 45 14 * * *", zone = "Asia/Bangkok")
    public void testEveryDayNineAM() {
        log.info("Hay, What's up");
    }
}
