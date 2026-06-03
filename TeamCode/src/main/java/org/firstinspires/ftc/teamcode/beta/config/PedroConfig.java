package org.firstinspires.ftc.teamcode.beta.config;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.configurables.annotations.Sorter;
import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.PredictiveBrakingCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * This class serves as a centralized configuration hub for the Pedro Pathing library.
 * It defines the constants and parameters required for robot movement, including
 * PIDF coefficients, drivetrain motor configurations, localization settings using
 * the GoBilda Pinpoint driver, and path constraints.
 * <p>
 * This class is annotated with {@link Configurable}, allowing for real-time tuning
 * of movement parameters during development and testing.
 */
@Configurable
public class PedroConfig {

    /**
     * Primary PIDF coefficients used to correct translational error (X and Y position).
     * These ensure the robot stays on the intended geometric path.
     */
    @Sorter(sort = 4)
    public static PIDFCoefficients translationalPIDF = new PIDFCoefficients(
            0.1,
            0,
            0.015,
            0.05
    );

    /**
     * Primary PIDF coefficients used to correct heading (rotational) error.
     * These ensure the robot maintains the correct orientation while following a path.
     */
    @Sorter(sort = 5)
    public static PIDFCoefficients headingPIDF = new PIDFCoefficients(
            1.2,
            0,
            0.1,
            0.015
    );

    /**
     * Primary Filtered PIDF coefficients for the drivetrain velocity control.
     * The filter helps smooth out high-frequency noise in velocity readings for
     * more stable power delivery.
     */
    @Sorter(sort = 6)
    public static FilteredPIDFCoefficients drivePIDF = new FilteredPIDFCoefficients(
            0.015,
            0,
            0.001,
            0.6,
            0.05
    );

    /**
     * Secondary PIDF coefficients for translational error.
     * Often used for specific path segments or high-speed maneuvers where
     * different tuning is required than the primary set.
     */
    @Sorter(sort = 7)
    public static PIDFCoefficients secondaryTranslationalPIDF = new PIDFCoefficients(
            0.0015,
            0,
            0.0015,
            0.01
    );

    /**
     * Secondary PIDF coefficients for heading error.
     * Allows for specialized rotational tuning during specific autonomous behaviors.
     */
    @Sorter(sort = 8)
    public static PIDFCoefficients secondaryHeadingPIDF = new PIDFCoefficients(
            0.2,
            0,
            0.1,
            0
    );

    /**
     * Secondary Filtered PIDF coefficients for drivetrain velocity.
     * Provides an alternate velocity control profile, useful when robot mass
     * or friction characteristics change (e.g., after picking up a heavy game element).
     */
    @Sorter(sort = 9)
    public static FilteredPIDFCoefficients secondaryDrivePIDF = new FilteredPIDFCoefficients(
            0.3,
            0,
            0.03,
            0.6,
            0.01
    );

    /**
     * Configuration constants for the Pedro Pathing follower, including mass,
     * PIDF coefficients for translational and heading control, Kalman filter settings,
     * and centripetal scaling parameters.
     */
    @Sorter(sort = 0)
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(12.5)
            .useSecondaryTranslationalPIDF(true)
            .useSecondaryHeadingPIDF(true)
            .useSecondaryDrivePIDF(true)
            .forwardZeroPowerAcceleration(-30.46855240)
            .lateralZeroPowerAcceleration(-89.08834036)
            .translationalPIDFCoefficients(translationalPIDF)
            .secondaryTranslationalPIDFCoefficients(secondaryTranslationalPIDF)
            .headingPIDFCoefficients(headingPIDF)
            .secondaryHeadingPIDFCoefficients(secondaryHeadingPIDF)
            .drivePIDFCoefficients(drivePIDF)
            .secondaryDrivePIDFCoefficients(secondaryDrivePIDF)
            .driveKalmanFilterModelCovariance(6)
            .driveKalmanFilterDataCovariance(1)
            .centripetalScaling(0.005);

    /**
     * Configuration for the mecanum drivetrain, including motor hardware names,
     * motor directions, and calibrated velocity characteristics for the robot.
     */
    @Sorter(sort = 1)
    public static MecanumConstants driveConstants = new MecanumConstants()
            .leftFrontMotorName("leftFront")
            .leftRearMotorName("leftRear")
            .rightFrontMotorName("rightFront")
            .rightRearMotorName("rightRear")
            .leftFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .yVelocity(64.72830752)
            .xVelocity(85.19865405)
            .maxPower(1);

    /**
     * Configuration constants for the Pinpoint localizer.
     * Defines the hardware mapping, encoder resolution, directions, and physical pod offsets
     * specifically for the GoBilda Pinpoint sensor system.
     */
    @Sorter(sort = 2)
    public static PinpointConstants localizerConstants = new PinpointConstants()
            .hardwareMapName("pinpoint")
            .distanceUnit(DistanceUnit.INCH)
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .forwardPodY(6.55)
            .strafePodX(-6.68);

    /**
     * The default constraints for path following, defining the maximum velocity, acceleration,
     * and other physical limits for the robot's movement.
     */
    @Sorter(sort = 3)
    public static PathConstraints pathConstraints = new PathConstraints(
            0.99,
            100,
            1,
            1
    );

    /**
     * Creates and initializes a new Follower instance using the pre-defined constants
     * for the drivetrain, localizer, and path constraints.
     *
     * @param hardwareMap the hardware map required for accessing motors and sensors
     * @return a configured Follower object for autonomous path following
     */
    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .build();
    }
}
