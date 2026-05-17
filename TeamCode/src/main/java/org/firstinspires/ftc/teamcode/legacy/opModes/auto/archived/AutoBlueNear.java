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
@Autonomous(name = "AutoBlueNear")
public class AutoBlueNear extends OpMode {

    private Follower follower;
    private Launcher launcher;

    public static Pose START_POS =
            new Pose(16.595573440643868, 119.1549295774648, Math.toRadians(144));

    public static Pose SHOT_POS =
            new Pose(55.050301810865186, 91.84708249496983, Math.toRadians(140));

    public static Pose ALIGN1_POS =
            new Pose(55.050301810865186, 84.0523138832998, Math.toRadians(180));

    public static Pose COLLECT1_POS =
            new Pose(18.458752515090536, 84.0523138832998, Math.toRadians(180));

    public static Pose ALIGN2_POS =
            new Pose(55.050301810865186, 60.16297786720324, Math.toRadians(180));

    public static Pose COLLECT2_POS =
            new Pose(16.458752515090536, 59.79074446680082, Math.toRadians(180));

    public static Pose ALIGN3_POS =
            new Pose(55.050301810865186, 35.82494969818917, Math.toRadians(180));

    public static Pose AJUSTE =
            new Pose(18.050301810865186, 59.79074446680082, Math.toRadians(180));

    public static Pose COLLECT3_POS =
            new Pose(16.458752515090536, 35.79074446680082, Math.toRadians(180));

    private PathChain goToShot;

    private PathChain goToAlign1;
    private PathChain goToCollect1;
    private PathChain backToShot1;

    private PathChain goToAlign2;
    private PathChain goToCollect2;
    private PathChain goToAjuste;
    private PathChain backToShot2;

    private PathChain goToAlign3;
    private PathChain goToCollect3;
    private PathChain backToShot3;

    private int pathState = 0;

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

        buildPaths();
    }

    @Override
    public void start() {

        follower.setStartingPose(START_POS);
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
                    Intake.setPower(0.2);
                    pathState++;
                }
                break;

            case 7:
                follower.followPath(backToShot1);
                pathState++;
                break;

            case 8:
                if (!follower.isBusy()) {
                    Intake.stop();
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
                    follower.followPath(goToAjuste);
                    pathState++;
                }
                break;

            case 14:
                if (!follower.isBusy()) {
                    Intake.setPower(0.2);
                    pathState++;
                }
                break;

            case 15:
                follower.followPath(backToShot2);
                pathState++;
                break;

            case 16:
                if (!follower.isBusy()) {
                    Intake.stop();
                    shootState = 0;
                    pathState++;
                }
                break;

            case 17:
                if (runShooterCycle()) {
                    shootState = 0;
                    pathState++;
                }
                break;

            case 18:
                follower.followPath(goToAlign3);
                pathState++;
                break;

            case 19:
                if (!follower.isBusy()) pathState++;
                break;

            case 20:
                Intake.forward();
                follower.followPath(goToCollect3);
                pathState++;
                break;

            case 21:
                if (!follower.isBusy()) {
                    Intake.setPower(0.2);
                    pathState++;
                }
                break;

            case 22:
                follower.followPath(backToShot3);
                pathState++;
                break;

            case 23:
                if (!follower.isBusy()) {
                    Intake.stop();
                    shootState = 0;
                    pathState++;
                }
                break;

            case 24:
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

    private void buildPaths() {

        goToShot = follower.pathBuilder()
                .addPath(new BezierLine(START_POS, SHOT_POS))
                .setLinearHeadingInterpolation(
                        START_POS.getHeading(),
                        SHOT_POS.getHeading()
                )
                .build();

        goToAlign1 = follower.pathBuilder()
                .addPath(new BezierLine(SHOT_POS, ALIGN1_POS))
                .setLinearHeadingInterpolation(
                        SHOT_POS.getHeading(),
                        ALIGN1_POS.getHeading()
                )
                .build();

        goToCollect1 = follower.pathBuilder()
                .addPath(new BezierLine(ALIGN1_POS, COLLECT1_POS))
                .setLinearHeadingInterpolation(
                        ALIGN1_POS.getHeading(),
                        COLLECT1_POS.getHeading()
                )
                .build();

        backToShot1 = follower.pathBuilder()
                .addPath(new BezierLine(COLLECT1_POS, SHOT_POS))
                .setLinearHeadingInterpolation(
                        COLLECT1_POS.getHeading(),
                        SHOT_POS.getHeading()
                )
                .build();

        goToAlign2 = follower.pathBuilder()
                .addPath(new BezierLine(SHOT_POS, ALIGN2_POS))
                .setLinearHeadingInterpolation(
                        SHOT_POS.getHeading(),
                        ALIGN2_POS.getHeading()
                )
                .build();

        goToCollect2 = follower.pathBuilder()
                .addPath(new BezierLine(ALIGN2_POS, COLLECT2_POS))
                .setLinearHeadingInterpolation(
                        ALIGN2_POS.getHeading(),
                        COLLECT2_POS.getHeading()
                )
                .build();

        goToAjuste = follower.pathBuilder()
                .addPath(new BezierLine(COLLECT2_POS, AJUSTE))
                .setLinearHeadingInterpolation(
                        COLLECT2_POS.getHeading(),
                        AJUSTE.getHeading()
                )
                .build();

        backToShot2 = follower.pathBuilder()
                .addPath(new BezierLine(AJUSTE, SHOT_POS))
                .setLinearHeadingInterpolation(
                        AJUSTE.getHeading(),
                        SHOT_POS.getHeading()
                )
                .build();

        goToAlign3 = follower.pathBuilder()
                .addPath(new BezierLine(SHOT_POS, ALIGN3_POS))
                .setLinearHeadingInterpolation(
                        SHOT_POS.getHeading(),
                        ALIGN3_POS.getHeading()
                )
                .build();

        goToCollect3 = follower.pathBuilder()
                .addPath(new BezierLine(ALIGN3_POS, COLLECT3_POS))
                .setLinearHeadingInterpolation(
                        ALIGN3_POS.getHeading(),
                        COLLECT3_POS.getHeading()
                )
                .build();

        backToShot3 = follower.pathBuilder()
                .addPath(new BezierLine(COLLECT3_POS, SHOT_POS))
                .setLinearHeadingInterpolation(
                        COLLECT3_POS.getHeading(),
                        SHOT_POS.getHeading()
                )
                .build();
    }

    private boolean runPreloadCycle() {

        switch (shootState) {

            case 0:
                shootTimer.reset();
                shootState = 1;
                break;

            case 1:
                if (shootTimer.seconds() >= 2.23) {
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

}
*/