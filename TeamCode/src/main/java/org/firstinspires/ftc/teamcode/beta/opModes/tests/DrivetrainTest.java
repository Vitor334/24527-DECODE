package org.firstinspires.ftc.teamcode.beta.opModes.tests;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.beta.config.PedroConfig;

@TeleOp(name = "Drivetrain Test", group = "Test")
public class DrivetrainTest extends LinearOpMode {

    @Override
    public void runOpMode() {

        Follower follower = PedroConfig.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72, 72, Math.toRadians(90)));
        follower.startTeleOpDrive();
        follower.update();

        waitForStart();
        while (opModeIsActive()) {
            follower.setTeleOpDrive(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                -gamepad1.right_stick_x,
                false
            );
            follower.update();
        }
    }
}
