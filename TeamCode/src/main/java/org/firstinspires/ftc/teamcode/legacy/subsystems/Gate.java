package org.firstinspires.ftc.teamcode.legacy.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Gate {

    private final Servo servo;

    public Gate(HardwareMap hardwareMap) {
        servo = hardwareMap.get(Servo.class, "gate");
        servo.setDirection(Servo.Direction.FORWARD);
        servo.scaleRange(0.425, 0.595);
        close();
    }

    public void open() {
        servo.setPosition(0);
    }

    public void close() {
        servo.setPosition(1);
    }

    public boolean isOpen() {
        return servo.getPosition() == 0;
    }

    public boolean isClosed() {
        return servo.getPosition() == 1;
    }
}