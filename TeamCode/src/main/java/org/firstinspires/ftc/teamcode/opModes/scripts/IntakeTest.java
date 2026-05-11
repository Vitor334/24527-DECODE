package org.firstinspires.ftc.teamcode.opModes.scripts;

import static com.pedropathing.ivy.commands.Commands.branch;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.config.PedroConfig;
import org.firstinspires.ftc.teamcode.subsystems.RobotAPI;
import org.firstinspires.ftc.teamcode.utils.Alliance;

import java.util.LinkedHashMap;
import java.util.function.BooleanSupplier;

public class IntakeTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        Follower follower = PedroConfig.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72, 72, Math.toRadians(90)));
        follower.startTeleOpDrive();
        follower.update();

        RobotAPI robot = new RobotAPI(hardwareMap, follower.getPoseTracker(), Alliance.BLUE);
        LinkedHashMap<BooleanSupplier, Command> cases = new LinkedHashMap<>();
        cases.put(() -> gamepad1.a, robot.intakeCommand);
        cases.put(() -> gamepad1.y, robot.outtakeCommand);
        Command handlePosition = branch(cases);

        waitForStart();
        while (opModeIsActive()) {
            handlePosition.schedule();
        }
    }
}
