package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.FundsNotEnoughException;
import com.db.awmd.challenge.exception.SameAccountException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private NotificationService notificationService;


    @Test
    public void addAccount()  {
        Account account = new Account("Id-123");
        account.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(account);

        assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
    }

    @Test
    public void addAccount_failsOnDuplicateId()  {
        String uniqueId = "Id-" + System.currentTimeMillis();
        Account account = new Account(uniqueId);
        this.accountsService.createAccount(account);

        try {
            this.accountsService.createAccount(account);
            fail("Should have failed when adding duplicate account");
        } catch (DuplicateAccountIdException ex) {
            assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
        }

    }

    @Test
    public void transfer() {
        Account accountFrom = new Account(UUID.randomUUID().toString(), new BigDecimal("100"));
        Account accountTo = new Account(UUID.randomUUID().toString(), new BigDecimal("200"));
        this.accountsService.createAccount(accountFrom);
        this.accountsService.createAccount(accountTo);

        BigDecimal transferAmount = new BigDecimal("50");
        Transfer transfer = new Transfer(accountFrom.getAccountId(), accountTo.getAccountId(), transferAmount);

        this.accountsService.transfer(transfer);

        assertThat(this.accountsService.getAccount(accountFrom.getAccountId()).getBalance()).isEqualTo(new BigDecimal("50"));

        verify(notificationService, Mockito.times(1)).notifyAboutTransfer(accountFrom, "Debited " + transferAmount + " from account " + accountFrom.getAccountId());
        verify(notificationService, Mockito.times(1)).notifyAboutTransfer(accountTo, "Credited " + transferAmount + " to account " + transferAmount);
    }

    @Test(expected = SameAccountException.class)
    public void transferSameAccount() {
        Account accountFrom = new Account(UUID.randomUUID().toString(), new BigDecimal("100"));

        this.accountsService.createAccount(accountFrom);

        BigDecimal transferAmount = new BigDecimal("50");
        Transfer transfer = new Transfer(accountFrom.getAccountId(), accountFrom.getAccountId(), transferAmount);

        this.accountsService.transfer(transfer);
    }

    @Test(expected = FundsNotEnoughException.class)
    public void transferFundsNotEnough() {
        Account accountFrom = new Account(UUID.randomUUID().toString(), new BigDecimal("100"));
        Account accountTo = new Account(UUID.randomUUID().toString(), new BigDecimal("200"));

        this.accountsService.createAccount(accountFrom);
        this.accountsService.createAccount(accountTo);

        BigDecimal transferAmount = new BigDecimal("300");
        Transfer transfer = new Transfer(accountFrom.getAccountId(), accountTo.getAccountId(), transferAmount);

        this.accountsService.transfer(transfer);
    }

}