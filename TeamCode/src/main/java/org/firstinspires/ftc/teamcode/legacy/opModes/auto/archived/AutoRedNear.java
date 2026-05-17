package org.firstinspires.ftc.teamcode.legacy.opModes.auto.archived;/*package org.firstinspires.ftc.teamcode.opModes.auto.v1;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.opModes.teleop.v1.dependencies.Gate;
import org.firstinspires.ftc.teamcode.opModes.teleop.v1.dependencies.Intake;
import org.firstinspires.ftc.teamcode.opModes.teleop.v1.dependencies.Launcher;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Configurable
@Autonomous(name = "Vermelho Perto", group = "V1")
public class AutoRedNear extends OpMode {

    private Follower follower;
    private Launcher launcher;

    // ==================================================
    // MEDIDA DO CAMPO (usada para espelhamento em Y)
    // ==================================================
    private static final double FIELD_Y = 144.0;

    // ================= POSES (ORIGINAIS DO AZUL) =================

    public static Pose START_POS =
            new Pose(16.595573440643868, 119.1549295774648, Math.toRadians(144));

    public static Pose SHOT_POS =
            new Pose(55.050301810865186, 91.84708249496983, Math.toRadians(140));

    public static Pose ALIGN1_POS =
            new Pose(55.050301810865186, 84.0523138832998, Math.toRadians(180));

    public static Pose COLLECT1_POS =
            new Pose(25.458752515090536, 84.0523138832998, Math.toRadians(180));

    public static Pose ALIGN2_POS =
            new Pose(55.050301810865186, 60.16297786720324, Math.toRadians(180));

    public static Pose COLLECT2_POS =
            new Pose(27.458752515090536, 59.79074446680082, Math.toRadians(180));

    public static Pose ALIGN3_POS =
            new Pose(55.050301810865186, 35.82494969818917, Math.toRadians(180));

    public static Pose COLLECT3_POS =
            new Pose(27.458752515090536, 35.79074446680082, Math.toRadians(180));

    // ================= POSES ESPELHADAS (RED) =================

    private Pose START_RED;
    private Pose SHOT_RED;
    private Pose ALIGN1_RED;
    private Pose COLLECT1_RED;
    private Pose ALIGN2_RED;
    private Pose COLLECT2_RED;
    private Pose ALIGN3_RED;
    private Pose COLLECT3_RED;

    // ================= PATHS =================

    private PathChain goToShot;

    private PathChain goToAlign1;
    private PathChain goToCollect1;
    private PathChain backToShot1;

    private PathChain goToAlign2;
    private PathChain goToCollect2;
    private PathChain backToShot2;

    private PathChain goToAlign3;
    private PathChain goToCollect3;
    private PathChain backToShot3;

    // ================= STATE =================

    private int pathState = 0;

    // ================= SHOOT =================

    private int shootState = 0;
    private boolean preloadFinished = false;

    private final ElapsedTime shootTimer = new ElapsedTime();

    @Override
    public void init() {

        follower = Constants.createFollower(hardwareMap);
        follower.setMaxPower(0.8);

        DcMotorEx flywheel  = hardwareMap.get(DcMotorEx.class, "flywheel");
        DcMotorEx flywheel2 = hardwareMap.get(DcMotorEx.class, "flywheel2");

        launcher = new Launcher(flywheel, flywheel2);

        Gate.init(hardwareMap);
        Intake.init(hardwareMap);

        // gera todas as poses do lado vermelho
        START_RED    = mirrorPose(START_POS);
        SHOT_RED     = mirrorPose(SHOT_POS);
        ALIGN1_RED   = mirrorPose(ALIGN1_POS);
        COLLECT1_RED = mirrorPose(COLLECT1_POS);
        ALIGN2_RED   = mirrorPose(ALIGN2_POS);
        COLLECT2_RED = mirrorPose(COLLECT2_POS);
        ALIGN3_RED   = mirrorPose(ALIGN3_POS);
        COLLECT3_RED = mirrorPose(COLLECT3_POS);

        buildPaths();
    }

    @Override
    public void start() {

        follower.setStartingPose(START_RED);

        launcher.startNear();

        follower.update();
    }

    @Override
    public void loop() {

        switch (pathState) {

            case 0:
                follower.followPath(goToShot);
                pathState++;
                break;

            case 1:
                if (!follower.isBusy()) {
                    shootState = 0;
                    preloadFinished = false;
                    pathState++;
                }
                break;

            case 2:
                if (!preloadFinished) {
                    if (runPreloadCycle()) {
                        preloadFinished = true;
                        shootState = 0;
                        pathState++;
                    }
                }
                break;

            case 3:
                follower.followPath(goToAlign1);
                pathState++;
                break;

            case 4:
                if (!follower.isBusy()) pathState++;
                break;

            case 5:
                Intake.forward();
                follower.followPath(goToCollect1);
                pathState++;
                break;

            case 6:
                if (!follower.isBusy()) {
                    Intake.stop();
                    pathState++;
                }
                break;

            case 7:
                follower.followPath(backToShot1);
                pathState++;
                break;

            case 8:
                if (!follower.isBusy()) {
                    shootState = 0;
                    pathState++;
                }
                break;

            case 9:
                if (runShooterCycle()) {
                    shootState = 0;
                    pathState++;
                }
                break;

            case 10:
                follower.followPath(goToAlign2);
                pathState++;
                break;

            case 11:
                if (!follower.isBusy()) pathState++;
                break;

            case 12:
                Intake.forward();
                follower.followPath(goToCollect2);
                pathState++;
                break;

            case 13:
                if (!follower.isBusy()) {
                    Intake.stop();
                    pathState++;
                }
                break;

            case 14:
                follower.followPath(backToShot2);
                pathState++;
                break;

            case 15:
                if (!follower.isBusy()) {
                    shootState = 0;
                    pathState++;
                }
                break;

            case 16:
                if (runShooterCycle()) {
                    shootState = 0;
                    pathState++;
                }
                break;

            case 17:
                follower.followPath(goToAlign3);
                pathState++;
                break;

            case 18:
                if (!follower.isBusy()) pathState++;
                break;

            case 19:
                Intake.forward();
                follower.followPath(goToCollect3);
                pathState++;
                break;

            case 20:
                if (!follower.isBusy()) {
                    Intake.stop();
                    pathState++;
                }
                break;

            case 21:
                follower.followPath(backToShot3);
                pathState++;
                break;

            case 22:
                if (!follower.isBusy()) {
                    shootState = 0;
                    pathState++;
                }
                break;

            case 23:
                if (runShooterCycle()) {
                    shootState = 0;
                    pathState++;
                }
                break;
        }

        follower.update();

        telemetry.addData("pathState", pathState);
        telemetry.addData("shootState", shootState);
    }

    // ==================================================

    private void buildPaths() {

        goToShot = follower.pathBuilder()
                .addPath(new BezierLine(START_RED, SHOT_RED))
                .setLinearHeadingInterpolation(
                        START_RED.getHeading(),
                        SHOT_RED.getHeading()
                )
                .build();

        goToAlign1 = follower.pathBuilder()
                .addPath(new BezierLine(SHOT_RED, ALIGN1_RED))
                .setLinearHeadingInterpolation(
                        SHOT_RED.getHeading(),
                        ALIGN1_RED.getHeading()
                )
                .build();

        goToCollect1 = follower.pathBuilder()
                .addPath(new BezierLine(ALIGN1_RED, COLLECT1_RED))
                .setLinearHeadingInterpolation(
                        ALIGN1_RED.getHeading(),
                        COLLECT1_RED.getHeading()
                )
                .build();

        backToShot1 = follower.pathBuilder()
                .addPath(new BezierLine(COLLECT1_RED, SHOT_RED))
                .setLinearHeadingInterpolation(
                        COLLECT1_RED.getHeading(),
                        SHOT_RED.getHeading()
                )
                .build();

        goToAlign2 = follower.pathBuilder()
                .addPath(new BezierLine(SHOT_RED, ALIGN2_RED))
                .setLinearHeadingInterpolation(
                        SHOT_RED.getHeading(),
                        ALIGN2_RED.getHeading()
                )
                .build();

        goToCollect2 = follower.pathBuilder()
                .addPath(new BezierLine(ALIGN2_RED, COLLECT2_RED))
                .setLinearHeadingInterpolation(
                        ALIGN2_RED.getHeading(),
                        COLLECT2_RED.getHeading()
                )
                .build();

        backToShot2 = follower.pathBuilder()
                .addPath(new BezierLine(COLLECT2_RED, SHOT_RED))
                .setLinearHeadingInterpolation(
                        COLLECT2_RED.getHeading(),
                        SHOT_RED.getHeading()
                )
                .build();

        goToAlign3 = follower.pathBuilder()
                .addPath(new BezierLine(SHOT_RED, ALIGN3_RED))
                .setLinearHeadingInterpolation(
                        SHOT_RED.getHeading(),
                        ALIGN3_RED.getHeading()
                )
                .build();

        goToCollect3 = follower.pathBuilder()
                .addPath(new BezierLine(ALIGN3_RED, COLLECT3_RED))
                .setLinearHeadingInterpolation(
                        ALIGN3_RED.getHeading(),
                        COLLECT3_RED.getHeading()
                )
                .build();

        backToShot3 = follower.pathBuilder()
                .addPath(new BezierLine(COLLECT3_RED, SHOT_RED))
                .setLinearHeadingInterpolation(
                        COLLECT3_RED.getHeading(),
                        SHOT_RED.getHeading()
                )
                .build();
    }

    // ==================================================
    // ESPALHAMENTO DA POSE
    // ==================================================
    private Pose mirrorPose(Pose p) {
        return new Pose(
                p.getX(),
                FIELD_Y - p.getY(),
                -p.getHeading()
        );
    }

    // ==================================================
    // PRELOAD
    // ==================================================
    private boolean runPreloadCycle() {

        switch (shootState) {

            case 0:
                shootTimer.reset();
                shootState = 1;
                break;

            case 1:
                if (shootTimer.seconds() >= 1.5) {
                    Gate.open();
                    shootTimer.reset();
                    shootState = 2;
                }
                break;

            case 2:
                if (shootTimer.seconds() >= 0.30) {
                    Intake.forward();
                    shootTimer.reset();
                    shootState = 3;
                }
                break;

            case 3:
                if (shootTimer.seconds() >= 1.0) {
                    Intake.stop();
                    Gate.close();
                    shootState = 4;
                }
                break;

            case 4:
                return true;
        }

        return false;
    }

    // ==================================================
    // TIRO NORMAL
    // ==================================================
    private boolean runShooterCycle() {

        switch (shootState) {

            case 0:
                launcher.startNear();
                shootTimer.reset();
                shootState = 1;
                break;

            case 1:
                if (shootTimer.seconds() >= 1.5) {
                    Gate.open();
                    shootTimer.reset();
                    shootState = 2;
                }
                break;

            case 2:
                if (shootTimer.seconds() >= 0.30) {
                    Intake.forward();
                    shootTimer.reset();
                    shootState = 3;
                }
                break;

            case 3:
                if (shootTimer.seconds() >= 1.0) {
                    Intake.stop();
                    Gate.close();
                    shootState = 4;
                }
                break;

            case 4:
                return true;
        }

        return false;
    }
}*/
