package com.example.wallet.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Map;

// TODO: Lengkapi class ini agar bisa melakukan binding properti dengan prefix "wallet" dari application.properties.
// Hint: Kamu perlu anotasi seperti @ConfigurationProperties (dengan prefix = "wallet") untuk melakukan binding konfigurasi.
@Component
@ConfigurationProperties(prefix = "wallet")
public class WalletProperties {

    private BigDecimal minSpendAmount = BigDecimal.ZERO;
    private BigDecimal maxSpendAmount = BigDecimal.valueOf(100000);
    private Map<String, BigDecimal> tierMultipliers = Map.of();

    public BigDecimal getMinSpendAmount() {
        return minSpendAmount;
    }

    public void setMinSpendAmount(BigDecimal minSpendAmount) {
        this.minSpendAmount = minSpendAmount;
    }

    public BigDecimal getMaxSpendAmount() {
        return maxSpendAmount;
    }

    public void setMaxSpendAmount(BigDecimal maxSpendAmount) {
        this.maxSpendAmount = maxSpendAmount;
    }

    public Map<String, BigDecimal> getTierMultipliers() {
        return tierMultipliers;
    }

    public void setTierMultipliers(Map<String, BigDecimal> tierMultipliers) {
        this.tierMultipliers = tierMultipliers;
    }
}
