package org.firstinspires.ftc.teamcode.beta.opModes.tuning;

import static com.pedropathing.ivy.Scheduler.schedule;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.beta.config.FieldConfig;
import org.firstinspires.ftc.teamcode.beta.config.PedroConfig;
import org.firstinspires.ftc.teamcode.beta.config.RobotConfig;
import org.firstinspires.ftc.teamcode.beta.subsystems.RobotAPI;
import org.firstinspires.ftc.teamcode.beta.utils.Alliance;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;

@TeleOp(name = "Interpolator Tuner", group = "Tuning")
public class InterpolatorTuner extends LinearOpMode {

    private double vel = 800;

    @Override
    public void runOpMode() throws InterruptedException {

        ControlSystem cs = RobotConfig.controller();

        Scheduler.reset();

        Follower follower = PedroConfig.createFollower(hardwareMap);
        follower.setStartingPose(FieldConfig.lowStart(Alliance.RED));
        follower.update();

        RobotAPI robot = new RobotAPI(hardwareMap, follower.getPoseTracker(), Alliance.RED);

        waitForStart();

        while (opModeIsActive()) {
            Scheduler.execute();
            cs.setGoal(new KineticState(0, vel));
            double pow = cs.calculate();

            if (gamepad1.rightTriggerWasPressed()) Scheduler.schedule(robot.shoot(pow));
            if (gamepad1.dpadUpWasPressed() && this.vel < 3000) this.vel += 50;
            if (gamepad1.dpadDownWasPressed() && this.vel > 0) this.vel -= 50;

            telemetry.addData("Desired velocity", this.vel);
            telemetry.addData("real velocity", robot.vel());
            telemetry.update();
        }
    }
}
