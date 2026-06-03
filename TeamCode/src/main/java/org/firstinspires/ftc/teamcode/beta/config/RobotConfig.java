package org.firstinspires.ftc.teamcode.beta.config;

import com.bylazar.configurables.annotations.Configurable;

import java.util.function.Function;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.control.feedforward.BasicFeedforwardParameters;

@Configurable
public class RobotConfig {

    public static final double TRANSFER_SPEED = 0.8 ;

    // ------ Intake ------------------------------------------------------------------------------

    public static double TRANSFER_TIMEOUT   = 3000;

    // ------ Gate --------------------------------------------------------------------------------

    public static final double GATE_MIN     = 0.425;
    public static final double GATE_MAX     = 0.595;
    public static final double GATE_OPEN    = 0;
    public static final double GATE_CLOSED  = 1;

    // ------ Outtake -----------------------------------------------------------------------------

    public static double SPINUP_THRESHOLD = 70; // 150 RPM
    public static long SPINUP_TIMEOUT     = 5000;

    public static PIDCoefficients FLYWHEEL_PID = new PIDCoefficients(
            1.5e-4,
            0,
            1e-2
    );

    // kV: 141 -> 150
    public static BasicFeedforwardParameters FLYWHEEL_FEEDFORWARD = new BasicFeedforwardParameters(
            2e-4,
            1e-3,
            1e-1
    );

    // ------ Getters -----------------------------------------------------------------------------

    public static ControlSystem controller() {
        return ControlSystem.builder()
                .velPid(FLYWHEEL_PID)
                .basicFF(FLYWHEEL_FEEDFORWARD)
                .build();
    }

}
