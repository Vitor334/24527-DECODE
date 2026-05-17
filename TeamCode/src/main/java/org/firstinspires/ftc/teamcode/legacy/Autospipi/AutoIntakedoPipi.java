package org.firstinspires.ftc.teamcode.legacy.Autospipi;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public final class AutoIntakedoPipi {

    private static DcMotor motor;

    // valores padrão (você pode tunar depois)
    private static final double FULL_POWER = 1.0;
    private static final double HOLD_POWER = 0.2;

    private AutoIntakedoPipi() {}

    public static void init(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, "intake");

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        stop();
    }

    // ===== MODOS =====

    // coleta máxima
    public static void collect() {
        setPower(FULL_POWER);
    }

    // segurar peça (voltando)
    public static void hold() {
        setPower(HOLD_POWER);
    }

    // alimentar launcher
    public static void feed() {
        setPower(FULL_POWER);
    }

    public static void reverse() {
        setPower(-FULL_POWER);
    }

    public static void stop() {
        setPower(0.0);
    }

    // ===== CONTROLE BASE =====

    public static void setPower(double power) {
        if (motor == null) return;

        if (power > 1.0) power = 1.0;
        if (power < -1.0) power = -1.0;

        motor.setPower(power);
    }
}