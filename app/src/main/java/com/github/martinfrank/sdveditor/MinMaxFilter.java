package com.github.martinfrank.sdveditor;

import android.text.InputFilter;
import android.text.Spanned;

public class MinMaxFilter implements InputFilter {

    private final int min;
    private final int max;

    public MinMaxFilter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if(isInRange(input)){
                return null;
            }
        } catch (NumberFormatException e) {
            //this case is handled with the return value
        }
        return "";
    }

    private boolean isInRange(int value) {
        return value <= max && value >= min;
    }
}
