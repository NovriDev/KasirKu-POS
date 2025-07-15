package com.novdev.kasirku.Utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatRupiah {
    public static String formatRupiah(double number) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return "Rp" + formatter.format(number);
    }

}
