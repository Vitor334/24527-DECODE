package org.firstinspires.ftc.teamcode.opModes;

import static com.pedropathing.ivy.commands.Commands.branch;
import static dev.nextftc.bindings.Bindings.button;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.config.PedroConfig;
import org.firstinspires.ftc.teamcode.data.ReadWriteData;
import org.firstinspires.ftc.teamcode.subsystems.RobotAPI;
import org.firstinspires.ftc.teamcode.utils.Alliance;

import java.util.LinkedHashMap;
import java.util.function.BooleanSupplier;

import dev.nextftc.bindings.BindingManager;
import dev.nextftc.bindings.Button;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp", group = "Main")
public class TeleOp extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        Follower follower = PedroConfig.createFollower(hardwareMap);
        follower.setStartingPose(ReadWriteData.pose() != null?
                ReadWriteData.pose() : new Pose(72, 72, Math.toRadians(90)));
        follower.startTeleOpDrive();
        follower.update();

        RobotAPI robot = new RobotAPI(hardwareMap, follower.getPoseTracker(), Alliance.BLUE);

        LinkedHashMap<BooleanSupplier, Command> cases = new LinkedHashMap<>();
        cases.put(() -> gamepad1.a, robot.intakeCommand);
        cases.put(() -> gamepad1.y, robot.outtakeCommand);
        Command intake = branch(cases);

        waitForStart();
        while (opModeIsActive()) {
            follower.update();
            robot.update();

            follower.setTeleOpDrive(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x,
                    -gamepad1.right_stick_x,
                    false
            );

            intake.schedule();
        }
    }
}
