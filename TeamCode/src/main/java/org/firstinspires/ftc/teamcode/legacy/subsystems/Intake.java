package org.firstinspires.ftc.teamcode.legacy.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Configurable
public class Intake {

    private static DcMotor motor;
    public static double TRANSFER_SPEED = 0.35;

    public Intake(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, "intake");
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void forward(boolean isShooting) {
        if (!isShooting) motor.setPower(1);
    }

    public void reverse(boolean isShooting) {
        if (!isShooting) motor.setPower(-1);
    }

    public void stop(boolean isShooting) {
        if (!isShooting) motor.setPower(0);
    }

    public void transfer() {
        motor.setPower(0.5);
    }

    public void stopTransfer() {
        motor.setPower(0);
    }
}