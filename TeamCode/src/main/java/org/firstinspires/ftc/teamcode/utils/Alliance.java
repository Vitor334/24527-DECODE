package org.firstinspires.ftc.teamcode.utils;

public enum Alliance {
    BLUE (1),
    RED  (0);

    private final int value;

    Alliance(int i) {
        this.value = i;
    }

    public int value() {
        return value;
    }

    public static Alliance fromValue(int i) {
        return i == 1 ? BLUE : RED;
    }
}
