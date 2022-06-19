package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;

@Service
@Slf4j
public class AccountsService {

    @Getter
    private final AccountsRepository accountsRepository;

    @Getter
    private final NotificationService notificationService;

    @Autowired
    private TransferRequestValidator transferRequestValidator;


    @Autowired
    public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
        this.accountsRepository = accountsRepository;
        this.notificationService = notificationService;
    }

    public void createAccount(Account account) {
        this.accountsRepository.createAccount(account);
    }

    public Account getAccount(String accountId) {
        return this.accountsRepository.getAccount(accountId);
    }

    public void transfer(Transfer transfer) {
        final Account accountFrom = accountsRepository.getAccount(transfer.getAccountFromId());
        final Account accountTo = accountsRepository.getAccount(transfer.getAccountToId());
        final BigDecimal transferAmount = transfer.getTransferAmount();

        transferRequestValidator.validate(accountFrom, accountTo, transfer);

        Lock lock1 = accountFrom.getLockObject();
        Lock lock2 = accountTo.getLockObject();

        if (lock1.tryLock()) {
            try {
                if (lock2.tryLock()) {
                    try {
                        log.info("Source: {}", accountFrom);
                        log.info("Destination: {}", accountTo);

                        accountFrom.debit(transferAmount);
                        accountTo.credit(transferAmount);

                        List<Account> accountList = new CopyOnWriteArrayList<>();
                        accountList.add(accountFrom);
                        accountList.add(accountTo);

                        boolean isSuccessful = accountsRepository.updateAccounts(accountList);
                        if (isSuccessful) {
                            notificationService.notifyAboutTransfer(accountFrom, "Debited " + transferAmount + " from account " + accountFrom.getAccountId());
                            notificationService.notifyAboutTransfer(accountTo, "Credited " + transferAmount + " to account " + transferAmount);
                        }
                    } finally {
                        lock2.unlock();
                    }
                }
            } finally {
                lock1.unlock();
            }
        }
    }
}
