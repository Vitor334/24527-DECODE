package org.firstinspires.ftc.teamcode.beta.config;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.teamcode.beta.utils.Alliance;

@Configurable
public class FieldConfig {

    private static final double UP    = Math.toRadians(90);
    private static final double DOWN  = Math.toRadians(270);
    private static final double LEFT  = Math.toRadians(180);
    private static final double RIGHT = Math.toRadians(0);

    // ----- Blue side ----------------------------------------------------------------------------
    public static Pose BLUE_GOAL         = new Pose(12, 132);
    public static Pose BLUE_HUMAN_PLAYER = new Pose(10, 9, LEFT);

    public static Pose BLUE_LOW_START    = new Pose(56, 8, UP);
    public static Pose BLUE_HIGH_START   = new Pose(0, 0, 0);

    public static Pose BLUE_LOW_SHOT     = new Pose(56, 12, Math.toRadians(110));
    public static Pose BLUE_HIGH_SHOT    = new Pose(0, 0, 0);

    public static Pose BLUE_LOW_PARK     = new Pose(36, 24, UP);
    public static Pose BLUE_HIGH_PARK    = new Pose(0, 0, 0);

    public static Pose BLUE_PRE_GATE     = new Pose(20, 70, DOWN);
    public static Pose BLUE_GATE         = new Pose(16.5, 70, DOWN);

    public static Pose BLUE_UP_ROW      = new Pose(20, 83, LEFT);
    public static Pose BLUE_MID_ROW     = new Pose(20, 63, LEFT);
    public static Pose BLUE_LOW_ROW     = new Pose(20, 38, LEFT);

    public static Pose BLUE_PRE_UP_ROW  = new Pose(39.5, 83, LEFT);
    public static Pose BLUE_PRE_MID_ROW = new Pose(39.5, 63, LEFT);
    public static Pose BLUE_PRE_LOW_ROW = new Pose(39.5, 38, LEFT);

    public static Pose BLUE_CYCLE = new Pose(12, 50, Math.toRadians(120));

    // ----- Red side -----------------------------------------------------------------------------

    public static Pose RED_GOAL         = new Pose(132, 132);
    public static Pose RED_HUMAN_PLAYER = new Pose(134, 9, RIGHT);

    public static Pose RED_LOW_START    = new Pose(88, 8, UP);
    public static Pose RED_HIGH_START   = new Pose(0, 0, 0);

    public static Pose RED_LOW_SHOT     = new Pose(128, 12, Math.toRadians(70));
    public static Pose RED_HIGH_SHOT    = new Pose(0, 0, 0);

    public static Pose RED_LOW_PARK     = new Pose(108, 24, UP);
    public static Pose RED_HIGH_PARK    = new Pose(0, 0, 0);

    public static Pose RED_PRE_GATE     = new Pose(124, 70, DOWN);
    public static Pose RED_GATE         = new Pose(127.5, 70, DOWN);

    public static Pose RED_UP_ROW      = new Pose(124, 83, LEFT);
    public static Pose RED_MID_ROW     = new Pose(124, 63, LEFT);
    public static Pose RED_LOW_ROW     = new Pose(124, 38, LEFT);

    public static Pose RED_PRE_UP_ROW  = new Pose(104.5, 83, LEFT);
    public static Pose RED_PRE_MID_ROW = new Pose(104.5, 63, LEFT);
    public static Pose RED_PRE_LOW_ROW = new Pose(104.5, 38, LEFT);

    public static Pose RED_CYCLE = new Pose(134, 50, Math.toRadians(90));

    // Getter Methods -----------------------------------------------------------------------------

    public static Pose highPark(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_HIGH_PARK : RED_HIGH_PARK;
    }

    public static Pose lowPark(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_LOW_PARK : RED_LOW_PARK;
    }

    public static Pose highShot(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_HIGH_SHOT : RED_HIGH_SHOT;
    }

    public static Pose lowShot(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_LOW_SHOT : RED_LOW_SHOT;
    }

    public static Pose highStart(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_HIGH_START : RED_HIGH_START;
    }

    public static Pose lowStart(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_LOW_START : RED_LOW_START;
    }

    public static Pose humanPlayer(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_HUMAN_PLAYER : RED_HUMAN_PLAYER;
    }

    public static Pose goal(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_GOAL : RED_GOAL;
    }

    public static Pose gate(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_GATE : RED_GATE;
    }

    public static Pose preGate(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_PRE_GATE : RED_PRE_GATE;
    }

    public static Pose preLowRow(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_PRE_LOW_ROW : RED_PRE_LOW_ROW;
    }

    public static Pose lowRow(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_LOW_ROW : RED_LOW_ROW;
    }

    public static Pose preMidRow(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_PRE_MID_ROW : RED_PRE_MID_ROW;
    }

    public static Pose midRow(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_MID_ROW : RED_MID_ROW;
    }

    public static Pose preUpRow(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_PRE_UP_ROW : RED_PRE_UP_ROW;
    }

    public static Pose upRow(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_UP_ROW : RED_UP_ROW;
    }

    public static Pose cycle(Alliance alliance) {
        return alliance == Alliance.BLUE ?
                BLUE_CYCLE : RED_CYCLE;
    }
}
