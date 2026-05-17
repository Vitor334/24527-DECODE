package org.firstinspires.ftc.teamcode.legacy.opModes.teleop.dependencies;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public final class IntakeMacro {

    private static DcMotor motor;

    private static double targetPower = 0.65;

    private IntakeMacro() {}

    public static void init(HardwareMap hardwareMap) {

        motor = hardwareMap.get(DcMotor.class, "intake");

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        stop();
    }

    public static void run() {
        if (motor == null) return;
        motor.setPower(targetPower);
    }

    public static void stop() {
        if (motor == null) return;
        motor.setPower(0.0);
    }

    public static void adjust(double delta) {

        targetPower += delta;

        if (targetPower > 1.0) targetPower = 1.0;
        if (targetPower < 0.0) targetPower = 0.0;
    }

    public static double getTargetPower() {
        return targetPower;
    }
}