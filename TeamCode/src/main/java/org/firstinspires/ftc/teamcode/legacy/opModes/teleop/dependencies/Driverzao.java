package org.firstinspires.ftc.teamcode.legacy.opModes.teleop.dependencies;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Driverzao {

    DcMotor lf, lr, rf, rr;

    // ajuste fino — comece com 0.97 ou 0.98
    private static final double LEFT_COMP  = 1.00;
    private static final double RIGHT_COMP = 0.97;

    public Driverzao(HardwareMap hw) {
        lf = hw.get(DcMotor.class, "leftFront");
        lr = hw.get(DcMotor.class, "leftRear");
        rf = hw.get(DcMotor.class, "rightFront");
        rr = hw.get(DcMotor.class, "rightRear");

        lf.setDirection(DcMotorSimple.Direction.FORWARD);
        lr.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.FORWARD);
        rr.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void update(Gamepad gp) {

        double y  = -gp.left_stick_y;
        double x  =  gp.left_stick_x * 1.1;
        double rx =  gp.right_stick_x;

        if (Math.abs(y)  < 0.05) y  = 0.0;
        if (Math.abs(x)  < 0.05) x  = 0.0;
        if (Math.abs(rx) < 0.05) rx = 0.0;

        double fl = y + x + rx;
        double lrPower = y - x + rx;
        double fr = y - x - rx;
        double rrPower = y + x - rx;

        double max = Math.max(
                Math.abs(fl),
                Math.max(
                        Math.abs(fr),
                        Math.max(Math.abs(lrPower), Math.abs(rrPower))
                )
        );

        if (max > 1.0) {
            fl /= max;
            fr /= max;
            lrPower /= max;
            rrPower /= max;
        }

        // compensação por lado
        fl *= LEFT_COMP;
        lrPower *= LEFT_COMP;
        fr *= RIGHT_COMP;
        rrPower *= RIGHT_COMP;

        lf.setPower(fl);
        lr.setPower(lrPower);
        rf.setPower(fr);
        rr.setPower(rrPower);
    }
}