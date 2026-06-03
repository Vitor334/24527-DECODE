package org.firstinspires.ftc.teamcode.beta.subsystems.adapters;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.beta.config.RobotConfig;
import org.firstinspires.ftc.teamcode.beta.subsystems.ports.Outtake;

public class Flywheel implements Outtake {

    private final DcMotorEx leftFlywheel;
    private final DcMotorEx rightFlywheel;
    private final Servo gate;

    public Flywheel(HardwareMap hardwareMap) {
        leftFlywheel = hardwareMap.get(DcMotorEx.class, "leftFlywheel");
        rightFlywheel = hardwareMap.get(DcMotorEx.class, "rightFlywheel");

        leftFlywheel.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFlywheel.setDirection(DcMotorSimple.Direction.FORWARD);

        leftFlywheel.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        rightFlywheel.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        leftFlywheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        rightFlywheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);

        leftFlywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        rightFlywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        gate = hardwareMap.get(Servo.class, "gate");
        gate.scaleRange(RobotConfig.GATE_MIN, RobotConfig.GATE_MAX);
        gate.setPosition(RobotConfig.GATE_CLOSED);
    }

    @Override
    public void setPower(double power) {
        leftFlywheel.setPower(power);
        rightFlywheel.setPower(power);
    }

    @Override
    public void setVelocity(double velocity) {
        leftFlywheel.setVelocity(velocity);
        rightFlywheel.setVelocity(velocity);
    }

    @Override
    public void release() {
        gate.setPosition(RobotConfig.GATE_OPEN);
    }

    @Override
    public void block() {
        gate.setPosition(RobotConfig.GATE_CLOSED);
    }

    @Override
    public double getVelocity() {
        return leftFlywheel.getVelocity();
    }

    @Override
    public boolean isAtVelocity(double velocity, double tolerance) {
        return velocity > leftFlywheel.getVelocity() - tolerance ||
                velocity < leftFlywheel.getVelocity() + tolerance;
    }
}
