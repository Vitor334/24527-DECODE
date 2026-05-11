package org.firstinspires.ftc.teamcode.subsystems;

import static com.pedropathing.ivy.groups.Groups.sequential;

import com.pedropathing.geometry.Pose;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.behaviors.ConflictBehavior;
import com.pedropathing.localization.PoseTracker;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.config.FieldConfig;
import org.firstinspires.ftc.teamcode.config.RobotConfig;
import org.firstinspires.ftc.teamcode.subsystems.adapters.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.adapters.Limelight;
import org.firstinspires.ftc.teamcode.subsystems.adapters.RubberBand;
import org.firstinspires.ftc.teamcode.subsystems.ports.Intake;
import org.firstinspires.ftc.teamcode.subsystems.ports.Outtake;
import org.firstinspires.ftc.teamcode.subsystems.ports.Vision;
import org.firstinspires.ftc.teamcode.utils.Alliance;
import org.firstinspires.ftc.teamcode.utils.Timeout;

import java.util.function.Function;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;

public class RobotAPI {

    // TODO: Add Manual power shot

    // Hardware
    private Outtake outtake;
    private Intake intake;
    private final Vision vision;

    public RobotAPI(HardwareMap hardwareMap, PoseTracker poseTracker, Alliance alliance) {
        this.outtake = new Flywheel(hardwareMap);
        this.intake = new RubberBand(hardwareMap);
        this.vision = new Limelight(hardwareMap, alliance);
        this.goal = alliance == Alliance.BLUE? FieldConfig.BLUE_GOAL : FieldConfig.RED_GOAL;
        this.poseTracker = poseTracker;
    }

    // CONTROL VARIABLES
    private double power = 0;
    private Pose goal;
    private PoseTracker poseTracker;
    private ControlSystem flywheelController = RobotConfig.controller();
    private Function<Double, Double> interpolator = RobotConfig.interpolator();
    private Timeout spinupTimeout   = new Timeout(
            () -> this.outtake.isAtVelocity(RobotConfig.SPINUP_THRESHOLD, 100),
            RobotConfig.SPINUP_TIMEOUT
    );
    private Timeout transferTimeout = new Timeout(
            () -> false,
            RobotConfig.TRANSFER_TIMEOUT
    );

    // PUBLIC API

    public Command shootCommand = sequential(
            this.spinup,
            this.transfer
    );

    // TODO -> Ajustar comandos de tiro
    public Command nearShotCommand = sequential(
            this.spinup,
            this.transfer
    );

    public Command intakeCommand = Command.build()
            .setExecute(() -> this.intake.setPower(1))
            .setEnd(endCondition -> this.intake.setPower(0))
            .setConflictBehavior(ConflictBehavior.OVERRIDE)
            .setPriority(0)
            .requiring(intake);

    public Command outtakeCommand = Command.build()
            .setExecute(() -> this.intake.setPower(-1))
            .setEnd(endCondition -> this.intake.setPower(0))
            .setConflictBehavior(ConflictBehavior.OVERRIDE)
            .setPriority(0)
            .requiring(intake);
    
    public Command idleFlywheel = Command.build()
            .setExecute(() -> this.outtake.setPower(this.power * 0.3))
            .setEnd(endCondition -> this.outtake.setPower(0))
            .setConflictBehavior(ConflictBehavior.CANCEL)
            .setPriority(0)
            .requiring(outtake);

    public void update() {
        double distance = poseTracker.getPose().distanceFrom(goal);
        KineticState setpoint = new KineticState(0, interpolator.apply(distance));
        flywheelController.setGoal(setpoint);
        power = flywheelController.calculate();
    }
    
    public double getAngle() {
        Double tx = vision.getTx();
        if (tx != null) {
            return tx;
        }
        
        Pose currentPose = poseTracker.getPose();
        return Math.atan2(goal.getY() - currentPose.getY(), goal.getX() - currentPose.getX());
    }

    // PRIVATE API
    private final Command spinup = Command.build()
            .setExecute(() -> this.outtake.setPower(this.power))
            .setDone(spinupTimeout::check)
            .setEnd(endCondition -> this.outtake.release())
            .setConflictBehavior(ConflictBehavior.OVERRIDE)
            .setPriority(1)
            .requiring(outtake);

    private final Command transfer = Command.build()
            .setExecute(() -> this.intake.setPower(RobotConfig.TRANSFER_SPEED))
            .setDone(transferTimeout::check)
            .setEnd(endCondition -> {
                intake.setPower(0);
                outtake.block();
            })
            .setConflictBehavior(ConflictBehavior.OVERRIDE)
            .setPriority(1)
            .requiring(intake);
}
