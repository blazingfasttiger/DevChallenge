package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class Account {

    @NotNull
    @NotEmpty(message = "Account Id cannot be null")
    private final String accountId;

    @NotNull
    @Min(value = 0, message = "Initial balance must be positive.")
    private BigDecimal balance;

    @JsonIgnore
    Lock lockObject = new ReentrantLock();

    public Account(String accountId) {
        this.accountId = accountId;
        this.balance = BigDecimal.ZERO;
    }

    @JsonCreator
    public Account(@JsonProperty("accountId") String accountId,
                   @JsonProperty("balance") BigDecimal balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public void credit(BigDecimal amount) {
        if (lockObject.tryLock()) {
            try {
                this.balance = balance.add(amount);
            } finally {
                lockObject.unlock();
            }
        }
    }

    public void debit(BigDecimal amount) {
        if (lockObject.tryLock()) {
            try {
                final BigDecimal newBalance = balance.subtract(amount);
                if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalStateException("Amount withdrawn cannot be greater than balance.");
                }
                this.balance = newBalance;
            } finally {
                lockObject.unlock();
            }
        }


    }
}
