package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class Transfer {

    @NotNull
    @NotEmpty(message = "From account cannot be null or empty.")
    private String accountFromId;

    @NotNull
    @NotEmpty(message = "To account cannot be null or empty.")
    private String accountToId;

    @NotNull
    @Min(value = 0, message = "Must be positive.")
    private BigDecimal transferAmount;

    @JsonCreator
    public Transfer(@JsonProperty("accountFromId") String accountFromId,
                    @JsonProperty("accountToId") String accountToId,
                    @JsonProperty("transferAmount") BigDecimal transferAmount) {
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.transferAmount = transferAmount;
    }
}
