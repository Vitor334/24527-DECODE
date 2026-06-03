package org.firstinspires.ftc.teamcode.beta.subsystems;

import com.pedropathing.geometry.Pose;
import com.pedropathing.localization.PoseTracker;

import org.firstinspires.ftc.teamcode.beta.config.RobotConfig;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;

public class PhysicsAPI {

    private final PoseTracker poseTracker;
    private final ControlSystem controller;

    public PhysicsAPI(PoseTracker poseTracker) {
        this.poseTracker = poseTracker;
        this.controller = RobotConfig.controller();
    }

    public double getPower(Pose target) {
        double ticks = interpolate(target.distanceFrom(poseTracker.getPose()));
        controller.setGoal(new KineticState(0, ticks));
        return controller.calculate();
    }

    // Add min ad max values
    private double interpolate(double x) {
        return Math.pow(x, 3);
    }
}
