package org.firstinspires.ftc.teamcode.beta.data;

import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.deadline;
import static com.pedropathing.ivy.groups.Groups.repeat;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.*;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Curve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.commands.Commands;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.teamcode.beta.config.FieldConfig;
import org.firstinspires.ftc.teamcode.beta.subsystems.RobotAPI;
import org.firstinspires.ftc.teamcode.beta.utils.Alliance;

public class StrategyBuilder {

    // ------ GLOBAL VARIABLES --------------------------------------------------------------------

    private final Pose lowStart, highStart;
    private final Pose lowPark,  highPark;
    private final Pose lowShot,  highShot;
    private final Pose humanPlayer;

    private final Pose preLowRow, lowRow;
    private final Pose preMidRow, midRow;
    private final Pose preUpRow,  upRow;
    private final Pose preGate,   gate;
    private final Pose cycle;

    // ------ INITIALIZATION ----------------------------------------------------------------------

    private final Follower follower;
    private final RobotAPI robot;

    public StrategyBuilder(RobotAPI robot, Follower follower, Alliance alliance) {
        this.follower = follower;
        this.robot = robot;

        this.lowStart       = FieldConfig.lowStart      (alliance);
        this.highStart      = FieldConfig.highStart     (alliance);
        this.lowPark        = FieldConfig.lowPark       (alliance);
        this.highPark       = FieldConfig.highPark      (alliance);
        this.lowShot        = FieldConfig.lowShot       (alliance);
        this.highShot       = FieldConfig.highShot      (alliance);
        this.humanPlayer    = FieldConfig.humanPlayer   (alliance);

        this.preLowRow      = FieldConfig.preLowRow     (alliance);
        this.lowRow         = FieldConfig.lowRow        (alliance);
        this.preMidRow      = FieldConfig.preMidRow     (alliance);
        this.midRow         = FieldConfig.midRow        (alliance);
        this.preUpRow       = FieldConfig.preUpRow      (alliance);
        this.upRow          = FieldConfig.upRow         (alliance);
        this.preGate        = FieldConfig.preGate       (alliance);
        this.gate           = FieldConfig.gate          (alliance);
        this.cycle          = FieldConfig.cycle         (alliance);
    }

    // ------ PUBLIC API --------------------------------------------------------------------------

    private static double FAR_VEL = 1630;
    private static double IDLE    = 1000;

    public Command lowPreload() {
        return sequential(
                followPath(lowStart, lowShot),
                robot.shoot(FAR_VEL, 6000, 0.35),
                followPath(lowShot, lowPark)
        );
    }

    public Command lowSupport() {
        return sequential(
                followPath(lowStart, lowShot),
                robot.shoot(FAR_VEL, 6000, 0.35),
                deadline(
                        sequential(
                                followPath(midRow, preMidRow),
                                followPath(preLowRow, lowShot)
                        ),
                        robot.run(FAR_VEL + 150)
                ),
                robot.shoot(FAR_VEL, 2000, 0.35),
                followPath(lowShot, lowPark)
        );
    }

    public Command lowMain() {
        return sequential(
                deadline(
                        followPath(lowStart, lowShot),
                        robot.run(2150)
                ),
                robot.shoot(FAR_VEL, 3500, 0.35),
                deadline(
                        sequential(
                                followPath(lowShot, preMidRow),
                                followPath(preMidRow, midRow)
                        ),
                        robot.intakeCommand,
                        robot.run(FAR_VEL + 150)
                ),
                deadline(
                        sequential(
                                followPath(midRow, preMidRow),
                                followPath(preLowRow, lowShot)
                        ),
                        robot.run(FAR_VEL + 150)
                ),
                robot.shoot(FAR_VEL, 2000, 0.35),
                deadline(
                        sequential(
                                followPath(lowShot, preLowRow),
                                followPath(preLowRow, lowRow)
                        ),
                        robot.intakeCommand,
                        robot.run(FAR_VEL + 150)
                ),
                deadline(
                        followPath(lowRow, lowShot),
                        robot.run(FAR_VEL + 150)
                ),
                robot.shoot(FAR_VEL, 2000, 0.35),
                followPath(lowShot, lowPark)
        );
    }

    public Command lowFarm() {
        return sequential(
                deadline(
                        followPath(lowStart, lowShot),
                        robot.run(2150)
                ),
                robot.shoot(FAR_VEL, 3500, 0.35),
                repeat(
                        sequential(
                                deadline(
                                        followPath(lowShot, humanPlayer),
                                        robot.intakeCommand,
                                        robot.run(FAR_VEL + 150)
                                ),
                                robot.shoot(FAR_VEL, 2000, 0.35)
                        ),
                        5
                ),
                followPath(lowShot, lowPark)
        );
    }

    // ------ INTERNAL METHODS --------------------------------------------------------------------

    private Command followPath(Pose... poses) {
        if (poses.length <= 1) throw new IllegalArgumentException("Please give at least two poses");
        Curve path = poses.length == 2? new BezierLine(poses[0], poses[1]) : new BezierCurve(poses);
        PathChain pathChain = follower.pathBuilder()
                .addPath(path)
                .setLinearHeadingInterpolation(poses[0].getHeading(), poses[poses.length - 1].getHeading())
                .build();
        return follow(this.follower, pathChain);
    }

    private PathChain linear(Pose... poses) {
        if (poses.length <= 1) throw new IllegalArgumentException("Please give at least two poses");
        Curve path = poses.length == 2? new BezierLine(poses[0], poses[1]) : new BezierCurve(poses);
        return follower.pathBuilder()
                .addPath(path)
                .setLinearHeadingInterpolation(poses[0].getHeading(), poses[poses.length - 1].getHeading())
                .build();
    }

    private PathChain constant(double heading, Pose... poses) {
        if (poses.length <= 1) throw new IllegalArgumentException("Please give at least two poses");
        Curve path = poses.length == 2? new BezierLine(poses[0], poses[1]) : new BezierCurve(poses);
        return follower.pathBuilder()
                .addPath(path)
                .setConstantHeadingInterpolation(heading)
                .build();
    }
}
