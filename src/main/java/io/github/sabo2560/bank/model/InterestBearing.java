package io.github.sabo2560.bank.model;

import java.math.BigDecimal;

public interface InterestBearing {
    BigDecimal getInterestRate();
    void applyInterest();
}
