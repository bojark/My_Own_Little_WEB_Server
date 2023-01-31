package ru.bojark.hwjws_01.misc;

public enum Colors {
    RESET("\033[0;31m"),
    GREEN("\033[0;32m"),
    YELLOW_BOLD("\033[1;33m"),
    BLUE_BOLD("\033[1;34m"),
    CYAN("\033[0;36m"),
    WHITE("\033[0;37m");

    private final String code;

    Colors(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}