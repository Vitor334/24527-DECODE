package org.firstinspires.ftc.teamcode.config;

import java.util.function.Function;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.control.feedforward.BasicFeedforwardParameters;

public class RobotConfig {

    // ------ Intake ------------------------------------------------------------------------------

    public static double TRANSFER_SPEED     = 0.8;
    public static double TRANSFER_TIMEOUT   = 3;

    // ------ Gate --------------------------------------------------------------------------------

    public static final double GATE_MIN     = 0.425;
    public static final double GATE_MAX     = 0.595;
    public static final double GATE_OPEN    = 0;
    public static final double GATE_CLOSED  = 1;

    // ------ Outtake -----------------------------------------------------------------------------

    public static final double SPINUP_THRESHOLD = 35;
    public static double SPINUP_TIMEOUT         = 3;
    public static double IDLE_MULTIPLIER        = 0.3;
    public static double FAR_VELOCITY           = 100;
    public static double NEAR_VELOCITY          = 100;

    public static PIDCoefficients FLYWHEEL_PID = new PIDCoefficients(
            0.001,
            0,
            0
    );

    public static BasicFeedforwardParameters FLYWHEEL_FEEDFORWARD = new BasicFeedforwardParameters(
            0.0001,
            0.003,
            0.015
    );

    private static final Function<Double, Double> interpolator = new Function<Double, Double>() {
        @Override
        public Double apply(Double aDouble) {
            return 0.0;
        }
    };

    // ------ Getters -----------------------------------------------------------------------------

    public static Function<Double, Double> interpolator() {
        return interpolator;
    }

    public static ControlSystem controller() {
        return ControlSystem.builder()
                .velPid(FLYWHEEL_PID)
                .basicFF(FLYWHEEL_FEEDFORWARD)
                .build();
    }

}
