package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }


    @Override
    public boolean updateAccounts(List<Account> accountList) {
        accountList.forEach(this::updateAccount);
        System.out.println(accounts);
        return true;
    }

    private void updateAccount(Account newAccount) {
        String accountId = newAccount.getAccountId();
        accounts.computeIfPresent(accountId, (key, account) -> {
            account.setBalance(newAccount.getBalance());
            return account;
        });
    }

}
