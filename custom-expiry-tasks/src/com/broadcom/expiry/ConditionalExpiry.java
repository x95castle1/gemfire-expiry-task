package com.broadcom.expiry;

import org.apache.geode.cache.CustomExpiry;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.Region.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.Properties;

public class ConditionalExpiry implements CustomExpiry, Declarable {

    private static final Logger logger = LogManager.getLogger(ConditionalExpiry.class);

    @Override
    public ExpirationAttributes getExpiry(Entry entry) {
        Object value = entry.getValue();

        long priceTimestamp = extractPriceTimestamp(value);
        if (priceTimestamp == 0) {
            logger.warn("No priceTimestamp found for key: " + entry.getKey() + ". Using region default.");
            return null;
        }

        long currentTime = System.currentTimeMillis();
        long ageInDays = (currentTime - priceTimestamp) / (1000 * 60 * 60 * 24);

        // Custom logic based on priceTimestamp age
        if (ageInDays > 730) { // Older than 2 years
            logger.info("Price data older than 2 years for key: " + entry.getKey() + ". Expiring in 1 second.");
            return new ExpirationAttributes(1, ExpirationAction.DESTROY);
        } else if (ageInDays > 365) { // 1-2 years old
            logger.info("Price data between 1-2 years old for key: " + entry.getKey() + ". Expiring in 1 day.");
            return new ExpirationAttributes(86400, ExpirationAction.DESTROY);
        } else {
            logger.info("Price data less than 1 year old for key: " + entry.getKey() + ". Using region default.");
            return null; // Use region default
        }
    }

    private long extractPriceTimestamp(Object value) {
        logger.info("Value type: " + (value != null ? value.getClass().getName() : "null") + " for value: " + value);

        if (value instanceof org.apache.geode.pdx.PdxInstance) {
            // Handle PDX serialized objects
            org.apache.geode.pdx.PdxInstance pdx = (org.apache.geode.pdx.PdxInstance) value;
            try {
                logger.info("<<< TYPE IS PDX INSTANCE >>> ");
                return (Long) pdx.getField("priceTimestamp");
            } catch (Exception e) {
                logger.warn("Failed to get priceTimestamp from PDX: " + e.getMessage());
                return 0;
            }
        }
        logger.warn("Unknown value type: " + (value != null ? value.getClass().getName() : "null"));
        return 0;
    }
}