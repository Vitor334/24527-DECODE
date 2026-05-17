package org.firstinspires.ftc.teamcode.legacy.Autospipi;

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

@Configurable
@Autonomous(name = "Auto Pipi Simples", group = "Auto")
public class Autonomo_Collect1_Collect2_OpenGate_Perto extends OpMode {

    private Follower follower;
    private Launcher launcher;

    private PathChain startToShot;

    private PathChain shotToC1;
    private PathChain c1ToGate;

    private PathChain gateToShot;

    private PathChain shotToC2;
    private PathChain c2ToShot;


    private PathChain shotToExit;

    private int pathState = 0;
    private int shootState = 0;

    private final ElapsedTime timer = new ElapsedTime();

    @Override
    public void init() {

        follower = PedroConfig.createFollower(hardwareMap);

        // VELOCIDADE MAIS CONTROLADA
        follower.setMaxPower(0.8);

        DcMotorEx fly1 = hardwareMap.get(DcMotorEx.class, "flywheel");
        DcMotorEx fly2 = hardwareMap.get(DcMotorEx.class, "flywheel2");

        launcher = new Launcher(fly1, fly2);

        AutoIntakedoPipi.init(hardwareMap);
        Gate.init(hardwareMap);

        buildPaths();
    }

    @Override
    public void start() {

        follower.setStartingPose(PositionsdoPipi.START);
        follower.update();
    }

    @Override
    public void loop() {

        switch (pathState) {

            // ===== PRELOAD =====
            case 0:
                follower.followPath(startToShot);
                pathState++;
                break;

            case 1:
                if (!follower.isBusy()) {
                    AutoIntakedoPipi.stop();
                    shootState = 0;
                    pathState++;
                }
                break;

            case 2:
                if (runShooterCycle()) {
                    pathState++;
                }
                break;

            // ===== CYCLE 1 =====
            case 3:
                AutoIntakedoPipi.collect();
                follower.followPath(shotToC1);
                pathState++;
                break;

            case 4:
                if (!follower.isBusy()) {

                    AutoIntakedoPipi.hold();

                    // IR PRO OPEN GATE
                    follower.followPath(c1ToGate);

                    pathState++;
                }
                break;

            // ESPERA 2 SEGUNDOS NO GATE
            case 5:
                if (!follower.isBusy()) {

                    timer.reset();
                    pathState++;
                }
                break;

            case 6:
                if (timer.seconds() >= 2.0) {

                    follower.followPath(gateToShot);
                    pathState++;
                }
                break;

            case 7:
                if (!follower.isBusy()) {

                    AutoIntakedoPipi.stop();
                    shootState = 0;
                    pathState++;
                }
                break;

            case 8:
                if (runShooterCycle()) {
                    pathState++;
                }
                break;

            // ===== CYCLE 2 =====
            case 9:
                AutoIntakedoPipi.collect();
                follower.followPath(shotToC2);
                pathState++;
                break;

            case 10:
                if (!follower.isBusy()) {

                    AutoIntakedoPipi.hold();
                    follower.followPath(c2ToShot);

                    pathState++;
                }
                break;

            case 11:
                if (!follower.isBusy()) {

                    AutoIntakedoPipi.stop();
                    shootState = 0;

                    pathState++;
                }
                break;

            case 12:
                if (runShooterCycle()) {
                    pathState++;
                }
                break;



            case 13:
                if (!follower.isBusy()) {

                    AutoIntakedoPipi.stop();
                    shootState = 0;

                    pathState++;
                }
                break;

            case 14:
                if (runShooterCycle()) {
                    pathState++;
                }
                break;

            // ===== SAÍDA =====
            case 15:
                AutoIntakedoPipi.stop();

                follower.followPath(shotToExit);

                pathState++;
                break;

            case 16:
                // terminou
                break;
        }

        follower.update();

        telemetry.addData("pathState", pathState);
        telemetry.addData("shootState", shootState);

        Pose pose = follower.getPose();

        telemetry.addData("X", pose.getX());
        telemetry.addData("Y", pose.getY());
        telemetry.addData("Heading", Math.toDegrees(pose.getHeading()));
    }

    // ===== PATHS =====
    private void buildPaths() {

        // ===== PRELOAD =====
        startToShot = line(
                PositionsdoPipi.START,
                PositionsdoPipi.SHOT
        );

        // ===== CYCLE 1 =====
        shotToC1 = curve(
                PositionsdoPipi.SHOT,
                new Pose(88.2, 80.6, Math.toRadians(0)),
                PositionsdoPipi.COLLECT1
        );

        // COLLECT 1 -> OPEN GATE
        c1ToGate = curve(
                PositionsdoPipi.COLLECT1,
                new Pose(117.6, 54.9, Math.toRadians(90)),
                PositionsdoPipi.OPEN_GATE
        );

        // OPEN GATE -> SHOT
        gateToShot = curve(
                PositionsdoPipi.OPEN_GATE,
                new Pose(117.6, 54.9, Math.toRadians(90)),
                PositionsdoPipi.SHOT
        );

        // ===== CYCLE 2 =====
        shotToC2 = curve(
                PositionsdoPipi.SHOT,
                new Pose(73.5, 53, Math.toRadians(0)),
                PositionsdoPipi.COLLECT2
        );

        c2ToShot = curve(
                PositionsdoPipi.COLLECT2,
                new Pose(73.5, 53, Math.toRadians(0)),
                PositionsdoPipi.SHOT
        );



        // ===== EXIT =====
        shotToExit = line(
                PositionsdoPipi.SHOT,
                PositionsdoPipi.SAIDA
        );
    }

    // ===== LINHA =====
    private PathChain line(Pose start, Pose end) {

        return follower.pathBuilder()
                .addPath(new BezierLine(
                        start,
                        end
                ))
                .setLinearHeadingInterpolation(
                        start.getHeading(),
                        end.getHeading()
                )
                .build();
    }

    // ===== CURVA =====
    private PathChain curve(Pose start, Pose control, Pose end) {

        return follower.pathBuilder()
                .addPath(new BezierCurve(
                        start,
                        control,
                        end
                ))
                .setLinearHeadingInterpolation(
                        start.getHeading(),
                        end.getHeading()
                )
                .build();
    }

    // ===== SHOOT =====
    private boolean runShooterCycle() {

        switch (shootState) {

            case 0:
                launcher.startNear();

                timer.reset();
                shootState = 1;
                break;

            case 1:
                if (timer.seconds() >= 1.5) {

                    Gate.open();

                    timer.reset();
                    shootState = 2;
                }
                break;

            case 2:
                if (timer.seconds() >= 0.3) {

                    AutoIntakedoPipi.feed();

                    timer.reset();
                    shootState = 3;
                }
                break;

            case 3:
                if (timer.seconds() >= 1.0) {

                    AutoIntakedoPipi.stop();

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