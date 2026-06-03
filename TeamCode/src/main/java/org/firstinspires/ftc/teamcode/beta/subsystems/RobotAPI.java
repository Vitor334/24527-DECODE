package org.firstinspires.ftc.teamcode.beta.subsystems;

import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.*;

import com.pedropathing.geometry.Pose;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.behaviors.ConflictBehavior;
import com.pedropathing.localization.PoseTracker;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.beta.config.FieldConfig;
import org.firstinspires.ftc.teamcode.beta.config.RobotConfig;
import org.firstinspires.ftc.teamcode.beta.subsystems.adapters.Flywheel;
import org.firstinspires.ftc.teamcode.beta.subsystems.adapters.Limelight;
import org.firstinspires.ftc.teamcode.beta.subsystems.adapters.RubberBand;
import org.firstinspires.ftc.teamcode.beta.subsystems.ports.Intake;
import org.firstinspires.ftc.teamcode.beta.subsystems.ports.Outtake;
import org.firstinspires.ftc.teamcode.beta.subsystems.ports.Vision;
import org.firstinspires.ftc.teamcode.beta.utils.Alliance;

import java.util.EnumMap;

import dev.nextftc.control.ControlSystem;

public class RobotAPI {

    // TODO: remove update()

    // ------ Intake ------------------------------------------------------------------------------

    private Intake intake;
    public Command intakeCommand;
    public Command outtakeCommand;

    private void setupIntake(HardwareMap hardwareMap) {

        this.intake = new RubberBand(hardwareMap);

        this.intakeCommand = Command.build()
                .setExecute(() -> this.intake.setPower(1))
                .setEnd(endCondition -> this.intake.setPower(0))
                .setConflictBehavior(ConflictBehavior.OVERRIDE)
                .setPriority(0)
                .requiring(intake);

        this.outtakeCommand = Command.build()
                .setExecute(() -> this.intake.setPower(-1))
                .setEnd(endCondition -> this.intake.setPower(0))
                .setConflictBehavior(ConflictBehavior.OVERRIDE)
                .setPriority(0)
                .requiring(intake);
    }

    public Command transfer(double power) {
        return Command.build()
                .setExecute(() -> this.intake.setPower(power))
                .setEnd(endCondition -> this.intake.setPower(0))
                .setConflictBehavior(ConflictBehavior.OVERRIDE)
                .setPriority(1)
                .requiring(intake);
    }

    // ------ Outtake -----------------------------------------------------------------------------

    private Outtake outtake;
    private final EnumMap<OuttakeState, Command> cases = new EnumMap<>(OuttakeState.class);
    private OuttakeState currentState = OuttakeState.IDLE;
    public Command shootCommand;
    public Command idle;

    private void buildOuttake(HardwareMap hardwareMap) {

        this.outtake = new Flywheel(hardwareMap);
        outtake.block();

        this.idle = infinite(() -> this.outtake.setPower(this.power * 0.3)).requiring(this.outtake);

        cases.put(OuttakeState.IDLE, idle);
        cases.put(OuttakeState.SHOOTING, idle);
        shootCommand = match(() -> currentState, cases);
    }

    public Command run(double velocity) {
        return Command.build()
                .setExecute(() -> this.outtake.setVelocity(velocity))
                .setEnd(endCondition -> this.outtake.setPower(0))
                .setConflictBehavior(ConflictBehavior.OVERRIDE)
                .setPriority(0)
                .requiring(outtake);
    }

    public Command shoot(double targetVelocity) {
        return deadline(
                sequential(
                        waitMs(RobotConfig.SPINUP_TIMEOUT),
                        instant(() -> outtake.release()),
                        waitMs(10),
                        deadline(
                                waitMs(RobotConfig.TRANSFER_TIMEOUT),
                                transfer(RobotConfig.TRANSFER_SPEED)
                        ),
                        instant(() -> outtake.block()),
                        waitMs(10)
                ),
                run(targetVelocity)
        );
    }

    public Command shoot(double targetVelocity, double timeout, double transferPower) {
        return deadline(
                sequential(
                        waitMs(timeout),
                        instant(() -> outtake.release()),
                        waitMs(10),
                        deadline(
                                waitMs(RobotConfig.TRANSFER_TIMEOUT),
                                transfer(transferPower)
                        ),
                        instant(() -> outtake.block()),
                        waitMs(10)
                ),
                run(targetVelocity)
        );
    }

    public double vel() {return outtake.getVelocity();}

    // ------ Vision ------------------------------------------------------------------------------

    private final Vision vision;

    public Double rawVisionAngle() {
        Double tx = vision.getTx();
        if (tx == null) return null;
        Pose pose = poseTracker.getPose();
        double heading = pose.getHeading();
        return heading + tx;
    }

    public Double rawPoseAngle() {
        Pose pose = poseTracker.getPose();
        return Math.atan2(goal.getY() - pose.getY(),
                          goal.getX() - pose.getX());
    }

    public double getAngle() {
        double odometryAngle = rawPoseAngle();

        Double tx = vision.getTx();
        if (tx == null) {
            return odometryAngle;
        }

        double visionAngle = rawVisionAngle();

        double confidence = 1.0 - (Math.abs(tx) / 20);
        double alpha = 0.35 * confidence;

        return blend(visionAngle, odometryAngle, alpha);
    }

    private double blend(double a, double b, double alpha) {
        double x = alpha * Math.cos(a) + (1 - alpha) * Math.cos(b);
        double y = alpha * Math.sin(a) + (1 - alpha) * Math.sin(b);
        return Math.atan2(y, x);
    }

    // ------ Control -----------------------------------------------------------------------------

    /*
     * Nota: The power variable does not get uploaded in the commands, so it may be necessary to add
     * a secondary math class for the specific robot calculations
     *
     * the power variable may be static
     */

    private double power = 1200;
    private final Pose goal;
    private final PoseTracker poseTracker;
    private final ControlSystem flywheelController;

    // ------ Constructor -------------------------------------------------------------------------

    public RobotAPI(HardwareMap hardwareMap, PoseTracker poseTracker, Alliance alliance) {

        setupIntake(hardwareMap);
        buildOuttake(hardwareMap);
        this.vision = new Limelight(hardwareMap, alliance);

        this.goal = alliance == Alliance.BLUE ? FieldConfig.BLUE_GOAL : FieldConfig.RED_GOAL;
        this.poseTracker = poseTracker;

        this.flywheelController = RobotConfig.controller();
    }

    // ------ Public methods ----------------------------------------------------------------------

    private boolean automatic = true;

    public void update() {
        if (automatic) automatic = false;
    }

    public void manual() {
        this.automatic = false;
        this.power = 1400;
    }

    // ------ Other -------------------------------------------------------------------------------

    private static boolean roughlyEquals(double value, double target) {
        return Math.abs(value) >= target - RobotConfig.SPINUP_THRESHOLD &&
                Math.abs(value) <= target + RobotConfig.SPINUP_THRESHOLD;
    }
}