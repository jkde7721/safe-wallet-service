package com.wanted.safewallet.domain.user.business.service;

import com.wanted.safewallet.domain.budget.business.service.BudgetService;
import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserSchedulerService {

    private final UserService userService;
    private final BudgetService budgetService;
    private final ExpenditureService expenditureService;

    @Scheduled(cron = "0 0 23 ? * SUN") //매주 일요일 23시마다
    @Transactional
    public void withdrawUsers() {
        List<String> userIds = userService.getWithdrawnUserIds();
        budgetService.deleteByUserIds(userIds);
        expenditureService.deleteByUserIds(userIds);
        userService.withdrawByIds(userIds);
    }
}
