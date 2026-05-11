package org.firstinspires.ftc.teamcode.opModes.auto.red;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.config.PedroConfig;
import org.firstinspires.ftc.teamcode.data.ReadWriteData;
import org.firstinspires.ftc.teamcode.data.StrategyBuilder;
import org.firstinspires.ftc.teamcode.subsystems.RobotAPI;
import org.firstinspires.ftc.teamcode.utils.Alliance;

@Autonomous(name = "[RED] Low Main", group = "Low")
public class LowMain extends LinearOpMode {

    @Override
    public void runOpMode() {

        Scheduler.reset();

        Follower follower       = PedroConfig.createFollower(hardwareMap);
        RobotAPI robot          = new RobotAPI(hardwareMap, follower.getPoseTracker(), Alliance.RED);
        StrategyBuilder builder = new StrategyBuilder(robot, follower, Alliance.RED);
        Command strategy = builder.lowMain();

        waitForStart();

        if (opModeIsActive()) {
            strategy.schedule();
            strategy.execute();
        }

        ReadWriteData.write(Alliance.RED, follower.getPose());

        requestOpModeStop();
    }
}
