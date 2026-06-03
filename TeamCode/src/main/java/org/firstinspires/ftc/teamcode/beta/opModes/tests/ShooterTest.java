package org.firstinspires.ftc.teamcode.beta.opModes.tests;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.beta.config.PedroConfig;
import org.firstinspires.ftc.teamcode.beta.subsystems.RobotAPI;
import org.firstinspires.ftc.teamcode.beta.utils.Alliance;

@TeleOp(name="Shooter Test", group = "Test")
public class ShooterTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        Follower follower = PedroConfig.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72, 72, Math.toRadians(90)));
        follower.startTeleOpDrive();
        follower.update();

        RobotAPI robot = new RobotAPI(hardwareMap, follower.getPoseTracker(), Alliance.RED);
        robot.update();

        waitForStart();
        while (opModeIsActive()) {
            robot.update();

            follower.setTeleOpDrive(
                    -gamepad1.left_stick_x,
                    -gamepad1.left_stick_y,
                    -gamepad1.right_stick_x
            );
            follower.update();

            if (gamepad1.dpadUpWasPressed()) robot.manual();
            if (gamepad1.aWasPressed()) robot.shoot(800);
        }
    }
}
