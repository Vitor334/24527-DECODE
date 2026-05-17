package org.firstinspires.ftc.teamcode.beta.subsystems;

import static com.pedropathing.ivy.groups.Groups.sequential;

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
import org.firstinspires.ftc.teamcode.beta.utils.Timeout;

import java.util.function.Function;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;

public class RobotAPI {

    // TODO: Add Manual power shot

    // Hardware
    private final Outtake outtake;
    private final Intake intake;
    private final Vision vision;

    // Control variables
    private double power = 0;
    private final Pose goal;
    private final PoseTracker poseTracker;
    private final ControlSystem flywheelController;
    private final Function<Double, Double> interpolator;
    private final Timeout spinupTimeout;
    private final Timeout transferTimeout;

    // Private commands (primitivos — sem dependência de outros commands)
    private final Command spinup;
    private final Command transfer;

    // Public commands
    public final Command intakeCommand;
    public final Command outtakeCommand;
    public final Command idleFlywheel;
    public final Command shootCommand;
    public final Command nearShotCommand; // TODO -> Ajustar comandos de tiro

    public RobotAPI(HardwareMap hardwareMap, PoseTracker poseTracker, Alliance alliance) {
        // 1. Hardware
        this.outtake = new Flywheel(hardwareMap);
        this.intake = new RubberBand(hardwareMap);
        this.vision = new Limelight(hardwareMap, alliance);
        this.goal = alliance == Alliance.BLUE ? FieldConfig.BLUE_GOAL : FieldConfig.RED_GOAL;
        this.poseTracker = poseTracker;

        // 2. Controle (não dependem de commands)
        this.flywheelController = RobotConfig.controller();
        this.interpolator = RobotConfig.interpolator();
        this.spinupTimeout = new Timeout(
                () -> this.outtake.isAtVelocity(RobotConfig.SPINUP_THRESHOLD, 100),
                RobotConfig.SPINUP_TIMEOUT
        );
        this.transferTimeout = new Timeout(
                () -> false,
                RobotConfig.TRANSFER_TIMEOUT
        );

        // 3. Commands primitivos (dependem apenas de hardware e timeouts)
        this.spinup = Command.build()
                .setExecute(() -> this.outtake.setPower(this.power))
                .setDone(spinupTimeout::check)
                .setEnd(endCondition -> this.outtake.release())
                .setConflictBehavior(ConflictBehavior.OVERRIDE)
                .setPriority(1)
                .requiring(outtake);

        this.transfer = Command.build()
                .setExecute(() -> this.intake.setPower(RobotConfig.TRANSFER_SPEED))
                .setDone(transferTimeout::check)
                .setEnd(endCondition -> {
                    intake.setPower(0);
                    outtake.block();
                })
                .setConflictBehavior(ConflictBehavior.OVERRIDE)
                .setPriority(1)
                .requiring(intake);

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

        this.idleFlywheel = Command.build()
                .setExecute(() -> this.outtake.setPower(this.power * 0.3))
                .setEnd(endCondition -> this.outtake.setPower(0))
                .setConflictBehavior(ConflictBehavior.CANCEL)
                .setPriority(0)
                .requiring(outtake);

        // 4. Commands compostos (dependem de spinup e transfer — devem vir por último)
        this.shootCommand = sequential(this.spinup, this.transfer);
        this.nearShotCommand = sequential(this.spinup, this.transfer);
    }

    // PUBLIC API

    public void update() {
        double distance = poseTracker.getPose().distanceFrom(goal);
        KineticState setpoint = new KineticState(0, interpolator.apply(distance));
        flywheelController.setGoal(setpoint);
        power = flywheelController.calculate();
    }

    public double getAngle() {
        //Double tx = vision.getTx();
        //if (tx != null) {
        //    return tx;
        //}

        Pose currentPose = poseTracker.getPose();
        return Math.atan2(goal.getY() - currentPose.getY(), goal.getX() - currentPose.getX());
    }
}