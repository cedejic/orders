package com.demo.orders.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConversionUtils {

    private ConversionUtils() {}

    public static BigDecimal toBigDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_EVEN);
    }
}
