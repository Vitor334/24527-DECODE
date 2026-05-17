package org.firstinspires.ftc.teamcode.legacy.opModes.teleop.dependencies;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakeReverse {

    private static DcMotor motor;

    private IntakeReverse() {}

    public static void init(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, "intake");

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        stop();
    }

    public static void run() {
        if (motor == null) return;
        motor.setPower(-0.5);
    }

    public static void stop() {
        if (motor == null) return;
        motor.setPower(0.0);
    }
}