package org.firstinspires.ftc.teamcode.beta.opModes.auto.blue;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.beta.config.PedroConfig;
import org.firstinspires.ftc.teamcode.beta.data.ReadWriteData;
import org.firstinspires.ftc.teamcode.beta.data.StrategyBuilder;
import org.firstinspires.ftc.teamcode.beta.subsystems.RobotAPI;
import org.firstinspires.ftc.teamcode.beta.utils.Alliance;

@Autonomous(name = "[BLUE] Low Main", group = "Low")
public class LowMain extends LinearOpMode {

    @Override
    public void runOpMode() {

        Scheduler.reset();

        Follower follower       = PedroConfig.createFollower(hardwareMap);
        RobotAPI robot          = new RobotAPI(hardwareMap, follower.getPoseTracker(), Alliance.BLUE);
        StrategyBuilder builder = new StrategyBuilder(robot, follower, Alliance.BLUE);
        Command strategy = builder.lowMain();

        waitForStart();

        if (opModeIsActive()) {
            strategy.schedule();
            strategy.execute();
        }

        ReadWriteData.write(Alliance.BLUE, follower.getPose());

        requestOpModeStop();
    }
}
