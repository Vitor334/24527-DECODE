package org.firstinspires.ftc.teamcode.beta.opModes.scripts;

import static com.pedropathing.ivy.commands.Commands.branch;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.beta.config.PedroConfig;
import org.firstinspires.ftc.teamcode.beta.subsystems.RobotAPI;
import org.firstinspires.ftc.teamcode.beta.utils.Alliance;

import java.util.LinkedHashMap;
import java.util.function.BooleanSupplier;

@TeleOp(name = "Intake Test", group = "Test")
public class IntakeTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        Scheduler.reset();

        Follower follower = PedroConfig.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72, 72, Math.toRadians(90)));
        follower.startTeleOpDrive();
        follower.update();

        RobotAPI robot = new RobotAPI(hardwareMap, follower.getPoseTracker(), Alliance.BLUE);
        LinkedHashMap<BooleanSupplier, Command> cases = new LinkedHashMap<>();
        cases.put(() -> gamepad1.a, robot.intakeCommand);
        cases.put(() -> gamepad1.y, robot.outtakeCommand);
        Command run = branch(cases);

        waitForStart();
        while (opModeIsActive()) {
            run.schedule();
            run.execute();
        }
    }
}
