package org.firstinspires.ftc.teamcode.legacy.Autospipi;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public final class Gate {

    private static Servo servo;

    private Gate() {}

    public static void init(HardwareMap hardwareMap) {

        servo = hardwareMap.get(Servo.class, "gate");
        servo.setDirection(Servo.Direction.FORWARD);

        // Limita o range primeiro
        servo.scaleRange(0.425, 0.595); // 305

        // Começa FECHADO
        close();
    }

    // ---- INVERTIDO ----
    public static void open() {
        if (servo == null) return;
        servo.setPosition(0);   // antes era 1
    }

    public static void close() {
        if (servo == null) return;
        servo.setPosition(1);   // antes era 0
    }
}