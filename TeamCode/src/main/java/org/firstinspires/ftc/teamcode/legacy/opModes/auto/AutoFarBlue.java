package org.firstinspires.ftc.teamcode.legacy.opModes.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.beta.config.PedroConfig;
import org.firstinspires.ftc.teamcode.legacy.opModes.teleop.dependencies.Gate;
import org.firstinspires.ftc.teamcode.legacy.opModes.teleop.dependencies.Intake;
import org.firstinspires.ftc.teamcode.legacy.opModes.teleop.dependencies.Launcher;

@Configurable
@Autonomous(name = "Azul Longe", group = "V1")
public class AutoFarBlue extends OpMode {

    private Follower follower;
    private Launcher launcher;

    public static Pose START_POS = new Pose(57, 9, Math.toRadians(90));
    public static Pose SHOT_POS  = new Pose(57, 13, Math.toRadians(111));
    public static Pose HIGH_SHOT = new Pose(60, 82, Math.toRadians(130));

    public static Pose LOW_ROW     = new Pose(24, 36, Math.toRadians(180));
    public static Pose LOW_ROW_CP  = new Pose(72, 32);

    public static Pose MID_ROW     = new Pose(24, 64, Math.toRadians(180));
    public static Pose MID_ROW_CP  = new Pose(72, 54);

    public static Pose HIGH_ROW    = new Pose(24, 88, Math.toRadians(180));
    public static Pose HIGH_ROW_CP = new Pose(72, 86);

    public static Pose PARK = new Pose(33, 9, Math.toRadians(90));

    @Override
    public void init() {
        follower = PedroConfig.createFollower(hardwareMap);

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
        follower.update();
    }

    ElapsedTime timer = new ElapsedTime();
    boolean firing = false;
    int pathState = 0;
    @Override
    public void loop() {
        switch (pathState) {
            case 0:
                launcher.startFar();
                if (!follower.isBusy()) {
                    follower.followPath(scorePreload);
                    pathState++;
                }
                break;

            case 7:
            case 4:
            case 1:
                launcher.startFar();
                if (!follower.isBusy() && follower.atPose(SHOT_POS, 2, 2)) {
                    if (runShooterCycle()) {
                        resetShooterCycle();
                        pathState++;
                    }
                }
                break;

            case 2:
                launcher.startFar();
                if (!follower.isBusy()) {
                    follower.followPath(collectFirst);
                    pathState++;
                }
                break;

            case 3:
                launcher.startFar();
                if (!follower.isBusy()) {
                    follower.followPath(returnFirst);
                    pathState++;
                }
                break;

            case 5:
                launcher.startFar();
                if (!follower.isBusy()) {
                    follower.followPath(collectSecond);
                    pathState++;
                }
                break;

            case 6:
                launcher.startFar();
                if (!follower.isBusy()) {
                    follower.followPath(returnSecond);
                    pathState++;
                }
                break;

            case 8:
                launcher.startFar();
                if (!follower.isBusy()) {
                    follower.followPath(collectThird);
                    pathState++;
                }
                break;

            case 9:
                launcher.startFar();
                if (!follower.isBusy()) {
                    follower.followPath(returnThird);
                    pathState++;
                }
                break;

            case 10:
                launcher.startFar();
                if (!follower.isBusy() && follower.atPose(HIGH_SHOT, 2, 2)) {
                    if (runShooterCycle()) {
                        resetShooterCycle();
                        pathState++;
                    }
                }
                break;

            case 11:
                if (!follower.isBusy()) {
                    follower.followPath(park);
                    pathState++;
                }
                break;
        }
        follower.update();

        telemetry.addData("shootState", shootState);
        telemetry.addData("slaveVel", launcher.getSlaveVelocity());
        telemetry.addData("masterVel", launcher.getMasterVelocity());
        telemetry.addData("timer", timer.seconds());
        telemetry.addData("Y", follower.getPose().getY());
    }

    private PathChain scorePreload;
    private PathChain collectFirst, collectSecond, collectThird, collectFourth;
    private PathChain returnFirst, returnSecond, returnThird, returnFourth;
    private PathChain park;

    private void buildPaths() {
        scorePreload = follower.pathBuilder()
                .addPath(new BezierLine(START_POS, SHOT_POS))
                .setLinearHeadingInterpolation(START_POS.getHeading(), SHOT_POS.getHeading())
                .build();

        collectFirst = follower.pathBuilder()
                .addPath(new BezierCurve(SHOT_POS, LOW_ROW_CP, LOW_ROW_CP, LOW_ROW))
                .setLinearHeadingInterpolation(SHOT_POS.getHeading(), LOW_ROW.getHeading())
                .addParametricCallback(0, intake())
                .build();

        returnFirst = follower.pathBuilder()
                .addPath(new BezierLine(LOW_ROW, SHOT_POS))
                .setLinearHeadingInterpolation(LOW_ROW.getHeading(), SHOT_POS.getHeading())
                .build();

        collectSecond = follower.pathBuilder()
                .addPath(new BezierCurve(SHOT_POS, MID_ROW_CP, MID_ROW_CP, MID_ROW))
                .setLinearHeadingInterpolation(SHOT_POS.getHeading(), MID_ROW.getHeading())
                .addParametricCallback(0, intake())
                .build();

        returnSecond = follower.pathBuilder()
                .addPath(new BezierLine(MID_ROW, SHOT_POS))
                .setLinearHeadingInterpolation(MID_ROW.getHeading(), SHOT_POS.getHeading())
                .build();

        collectThird = follower.pathBuilder()
                .addPath(new BezierCurve(SHOT_POS, HIGH_ROW_CP, HIGH_ROW_CP, HIGH_ROW))
                .setLinearHeadingInterpolation(SHOT_POS.getHeading(), HIGH_ROW.getHeading())
                .addParametricCallback(0, intake())
                .build();

        returnThird = follower.pathBuilder()
                .addPath(new BezierCurve(HIGH_ROW, HIGH_ROW_CP, HIGH_SHOT))
                .setLinearHeadingInterpolation(MID_ROW.getHeading(), HIGH_SHOT.getHeading())
                .build();

        park = follower.pathBuilder()
                .addPath(new BezierLine(HIGH_SHOT, PARK))
                .setLinearHeadingInterpolation(HIGH_SHOT.getHeading(), PARK.getHeading())
                .build();

    }

    private int shootState = 0;
    private boolean runShooterCycle() {
        switch (shootState) {
            case 0: // spin-up
                launcher.startFar();

                if (launcher.importantFunction()) {
                    Gate.open();
                    timer.reset();
                    shootState = 1;
                }
                break;

            case 1: // alimentando
                Intake.forward();
                if (timer.seconds() >= 1.2) {
                    Intake.stop();
                    Gate.close();
                    shootState = 2;
                    timer.reset();
                }
                break;

            case 2:
                return true;
        }
        return false;
    }

    private void resetShooterCycle() {
        shootState = 0;
        firing = false;
        timer.reset();
    }

    private Runnable intake() {
        return Intake::forward;
    }
}
