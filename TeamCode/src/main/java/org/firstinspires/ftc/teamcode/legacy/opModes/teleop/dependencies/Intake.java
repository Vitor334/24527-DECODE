package org.firstinspires.ftc.teamcode.legacy.opModes.teleop.dependencies;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public final class Intake {

    private static DcMotor motor;

    private Intake() {}

    public static void init(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, "intake");

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        stop();
    }

    // coleta
    public static void forward() {
        if (motor == null) return;
        motor.setPower(1.0);
    }

    // reverso
    public static void reverse() {

    }

    public static void stop() {
        if (motor == null) return;
        motor.setPower(0.0);
    }

    public static void setPower(double v) {
    }
}