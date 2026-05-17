package org.firstinspires.ftc.teamcode.beta.opModes.scripts;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.beta.config.PedroConfig;
import org.firstinspires.ftc.teamcode.beta.subsystems.RobotAPI;
import org.firstinspires.ftc.teamcode.beta.utils.Alliance;

@TeleOp(name = "Vision Test", group = "Test")
public class VisionTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Follower follower = PedroConfig.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(24, 24, Math.toRadians(90)));
        follower.startTeleOpDrive();
        follower.update();
        RobotAPI robot = new RobotAPI(hardwareMap, follower.getPoseTracker(), Alliance.RED);
        waitForStart();
        while (opModeIsActive()) {
            follower.update();
            telemetry.addData("Angle", Math.toDegrees(robot.getAngle()));
            telemetry.update();
        }
    }
}
