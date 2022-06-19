package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.FundsNotEnoughException;
import com.db.awmd.challenge.exception.SameAccountException;
import org.springframework.stereotype.Component;

@Component
public class TransferRequestValidatorImpl implements TransferRequestValidator {

    @Override
    public void validate(Account accountFrom, Account accountTo, Transfer transfer) throws AccountNotFoundException, FundsNotEnoughException {

        if (accountFrom == null) {
            throw new AccountNotFoundException("From account not found or null.");
        }

        if (accountTo == null) {
            throw new AccountNotFoundException("To account not found or null.");
        }

        if (accountFrom.getAccountId().equals(accountTo.getAccountId())) {
            throw new SameAccountException("Source and target account cannot be same.");
        }

        if (transfer.getTransferAmount().compareTo(accountFrom.getBalance()) > 0) {
            throw new FundsNotEnoughException("Transfer amount exceeds the available funds.");
        }
    }
}
