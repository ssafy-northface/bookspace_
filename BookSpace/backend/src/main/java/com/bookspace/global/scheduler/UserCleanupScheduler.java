package com.bookspace.global.scheduler;

import com.bookspace.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCleanupScheduler {

    private final UserService userService;

//    // 테스트용 30초마다 실행 (30초마다 로그창에서 삭제된 회원 있는지 확인 가능)
//    @Scheduled(cron = "0/30 * * * * *")


    // 매일 자정에 inactive 14일 된 유저 영구 삭제
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void cleanupExpiredInactiveUsers(){

        int deletedCount = userService.deleteExpiredInactiveUsers();

        if (deletedCount > 0) {
            log.info("Batch hard deleted {} expired inactive users", deletedCount);
        } else {
            log.info("No expired inactive users to hard delete");
        }

    }

}
