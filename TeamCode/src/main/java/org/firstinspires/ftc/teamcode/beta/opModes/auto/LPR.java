package org.firstinspires.ftc.teamcode.beta.opModes.auto;

import static com.pedropathing.ivy.Scheduler.schedule;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.beta.config.FieldConfig;
import org.firstinspires.ftc.teamcode.beta.config.PedroConfig;
import org.firstinspires.ftc.teamcode.beta.data.ReadWriteData;
import org.firstinspires.ftc.teamcode.beta.data.StrategyBuilder;
import org.firstinspires.ftc.teamcode.beta.subsystems.RobotAPI;
import org.firstinspires.ftc.teamcode.beta.utils.Alliance;

@Autonomous(name = "[RED] Low Preload", group = "Low")
public class LPR extends LinearOpMode {

    @Override
    public void runOpMode() {

        Scheduler.reset();

        Follower follower = PedroConfig.createFollower(hardwareMap);
        follower.setStartingPose(FieldConfig.lowStart(Alliance.RED));
        follower.update();

        RobotAPI robot = new RobotAPI(hardwareMap, follower.getPoseTracker(), Alliance.RED);
        robot.update();

        StrategyBuilder builder = new StrategyBuilder(robot, follower, Alliance.RED);
        Command strategy = builder.lowPreload();

        waitForStart();
        schedule(strategy);

        while (opModeIsActive()) {

            Scheduler.execute();
            follower.update();
            robot.update();

            if (strategy.done()) {
                ReadWriteData.write(Alliance.RED, follower.getPose());
                requestOpModeStop();
            }
        }
    }
}
