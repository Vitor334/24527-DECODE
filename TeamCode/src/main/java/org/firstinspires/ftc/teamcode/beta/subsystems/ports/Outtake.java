package org.firstinspires.ftc.teamcode.beta.subsystems.ports;

public interface Outtake {
    void setPower(double power);
    void release();
    void block();
    double getVelocity();
    boolean isAtVelocity(double velocity, double tolerance);
}
