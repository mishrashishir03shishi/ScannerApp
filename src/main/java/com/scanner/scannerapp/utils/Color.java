package com.scanner.scannerapp.utils;

public enum Color {

    RED(1),
    GREEN(0),
    WHITE(-1);

    private final int code;

    Color(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Color fromCode(int code) {
        for (Color color : values()) {
            if (color.code == code) {
                return color;
            }
        }
        throw new IllegalArgumentException("No color with code: " + code);
    }
}
