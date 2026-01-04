package com.jumio.roms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String timezone = "Asia/Kolkata";

    private Pricing pricing = new Pricing();
    private Payment payment = new Payment();

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public Pricing getPricing() { return pricing; }
    public void setPricing(Pricing pricing) { this.pricing = pricing; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    public static class Pricing {
        private BigDecimal serviceTaxRate = new BigDecimal("0.18");
        private BigDecimal deliveryCharge = new BigDecimal("40.00");

        public BigDecimal getServiceTaxRate() { return serviceTaxRate; }
        public void setServiceTaxRate(BigDecimal serviceTaxRate) { this.serviceTaxRate = serviceTaxRate; }

        public BigDecimal getDeliveryCharge() { return deliveryCharge; }
        public void setDeliveryCharge(BigDecimal deliveryCharge) { this.deliveryCharge = deliveryCharge; }
    }

    public static class Payment {
        private int maxAttempts = 3;

        public int getMaxAttempts() { return maxAttempts; }
        public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
    }
}
