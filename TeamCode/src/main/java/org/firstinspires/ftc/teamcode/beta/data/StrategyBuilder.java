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

    public Command lowPreload() {
        return sequential(
                followPath(lowStart, lowShot),
                // TODO: Add shooting command
                followPath(lowShot, lowPark)
        );
    }

    public Command lowMain() {
        return sequential(
                followPath(lowStart, lowShot),
                // TODO: Add shooting command       (1)
                followIntake(lowShot, humanPlayer), // Intake (1)
                followPath(humanPlayer, lowShot),
                // TODO: Add shooting command       (2)
                followPath(lowShot, preMidRow),
                followIntake(preMidRow, midRow),    // Intake (2)
                followPath(midRow, preGate),
                followPath(preGate, gate),          // Gate
                waitMs(600),
                followPath(gate, lowShot),
                // TODO: Add shooting command       (3)
                followPath(lowShot, preLowRow),
                followIntake(preLowRow, lowRow),    // Intake (3)
                followPath(lowRow, lowShot),
                // TODO: Add shooting command       (4)
                followPath(lowShot, preUpRow),
                followIntake(preUpRow, upRow),      // Intake (4)
                followPath(upRow, lowShot),
                // TODO: Add shooting command       (5)
                followPath(lowShot, lowPark)
        );
    }

    public Command lowCycle() { // Requires PPG inside robot
        return sequential(
                followPath(lowStart, lowShot),
                // TODO: Add shooting command       (1)
                followPath(lowShot, preMidRow),
                // TODO: Add intake command
                followPath(preMidRow, midRow),
                followPath(midRow, lowShot),
                // TODO: Add shooting command       (2)
                followPath(lowShot, preLowRow),
                // TODO: Add intake command
                followPath(preLowRow, lowRow),
                followPath(lowRow, lowShot),
                // TODO: Add shooting command       (3)
                followPath(lowShot, humanPlayer),
                // TODO: Add intake command
                followPath(humanPlayer, lowShot),
                repeat(
                        sequential(
                                // TODO: Add shooting command       (Setup)
                                followPath(lowShot, cycle),
                                // TODO: Add intake command
                                followPath(cycle, lowShot)
                        ), 3
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

    private Command followIntake(Pose... poses) {
        return deadline(
                followPath(poses),
                robot.intakeCommand
        );
    }
}
