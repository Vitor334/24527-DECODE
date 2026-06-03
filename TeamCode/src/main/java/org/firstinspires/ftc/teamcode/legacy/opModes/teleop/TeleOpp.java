package org.firstinspires.ftc.teamcode.legacy.opModes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.legacy.opModes.teleop.dependencies.Driverzao;
import org.firstinspires.ftc.teamcode.legacy.opModes.teleop.dependencies.Gate;
import org.firstinspires.ftc.teamcode.legacy.opModes.teleop.dependencies.Intake;
import org.firstinspires.ftc.teamcode.legacy.opModes.teleop.dependencies.IntakeMacro;
import org.firstinspires.ftc.teamcode.legacy.opModes.teleop.dependencies.IntakeReverse;
import org.firstinspires.ftc.teamcode.legacy.opModes.teleop.dependencies.Launcher;

@TeleOp(name = "TeleOp Nacional", group = "Main")
public class TeleOpp extends OpMode {

    private Launcher launcher;
    private Driverzao drive;

    private enum MacroState { IDLE, SERVO_DELAY, FEEDING }
    private MacroState state = MacroState.IDLE;
    private final ElapsedTime timer = new ElapsedTime();

    // trims do lançador
    private boolean dpadUpLast   = false;
    private boolean dpadDownLast = false;

    // ajuste do intake no disparo
    private boolean dpadLeftLast  = false;
    private boolean dpadRightLast = false;

    // ajuste do tempo do macro
    private double feedingTime = 2.0; // segundos
    private boolean yLast = false;
    private boolean xLast = false;

    // trava do disparo
    private boolean r2Last = false;

    // seleção de modo de tiro
    private boolean l1Last = false;
    private boolean l2Last = false;
    private enum ShotMode { NEAR, FAR }
    private ShotMode shotMode = ShotMode.NEAR;

    @Override
    public void init() {
        drive = new Driverzao(hardwareMap);

        DcMotorEx flywheel  = hardwareMap.get(DcMotorEx.class, "flywheel");
        DcMotorEx flywheel2 = hardwareMap.get(DcMotorEx.class, "flywheel2");

        launcher = new Launcher(flywheel, flywheel2);

        Intake.init(hardwareMap);
        IntakeReverse.init(hardwareMap);
        Gate.init(hardwareMap);

        IntakeMacro.init(hardwareMap);

        telemetry.addData("Status", "Macro pronto");
        telemetry.update();
    }

    @Override
    public void start() {
        shotMode = ShotMode.NEAR;
        launcher.startNear();
    }

    @Override
    public void loop() {


        // ---------------- Drive
        drive.update(gamepad1);

        // ---------------- Ajuste fino do lançador (ticks/s)
        if (gamepad1.dpad_up && !dpadUpLast) {
            launcher.adjustNear(20);
            launcher.adjustFar(20);
            updateLauncherMode();
        }
        if (gamepad1.dpad_down && !dpadDownLast) {
            launcher.adjustNear(-20);
            launcher.adjustFar(-20);
            updateLauncherMode();
        }
        dpadUpLast = gamepad1.dpad_up;
        dpadDownLast = gamepad1.dpad_down;

        // ---------------- Ajuste fino do intake no disparo
        if (gamepad1.dpad_right && !dpadRightLast) IntakeMacro.adjust(0.02);
        if (gamepad1.dpad_left  && !dpadLeftLast)  IntakeMacro.adjust(-0.02);
        dpadRightLast = gamepad1.dpad_right;
        dpadLeftLast  = gamepad1.dpad_left;

        // ---------------- Ajuste do tempo do macro
        if (gamepad1.y && !yLast) feedingTime += 0.1;
        if (gamepad1.x && !xLast) feedingTime -= 0.1;

        if (feedingTime < 0.5) feedingTime = 0.5;
        if (feedingTime > 3.5) feedingTime = 3.5;

        yLast = gamepad1.y;
        xLast = gamepad1.x;

        // ---------------- Seleção de modo de tiro
        boolean l2 = gamepad1.left_trigger > 0.5;

        if (gamepad1.left_bumper && !l1Last) {
            shotMode = ShotMode.NEAR;
            launcher.startNear();
        }

        if (l2 && !l2Last) {
            shotMode = ShotMode.FAR;
            launcher.startFar();
        }

        l1Last = gamepad1.left_bumper;
        l2Last = l2;

        // ---------------- Intake manual normal (R1 e B)
        if (state == MacroState.IDLE) {
            if (gamepad1.b) {
                IntakeReverse.run();
            } else if (gamepad1.right_bumper) {
                Intake.forward();
            } else {
                Intake.stop();
                IntakeReverse.stop();
            }
        }

        // ---------------- Macro de disparo
        boolean r2 = gamepad1.right_trigger > 0.5;

        switch (state) {
            case IDLE:
                if (r2 && !r2Last) {
                    Gate.open();
                    timer.reset();
                    state = MacroState.SERVO_DELAY;
                }
                break;

            case SERVO_DELAY:
                if (timer.seconds() >= 0.10) {
                    IntakeMacro.run();
                    timer.reset();
                    state = MacroState.FEEDING;
                }
                break;

            case FEEDING:
                if (timer.seconds() >= feedingTime) {
                    IntakeMacro.stop();
                    Gate.close();
                    state = MacroState.IDLE;
                }
                break;
        }

        r2Last = r2;

        telemetry.addData("Estado", state);
        telemetry.addData("Modo de tiro", shotMode);
        telemetry.addData("Launcher target (ticks/s)",
                shotMode == ShotMode.NEAR ? launcher.getNearVelocity() : launcher.getFarVelocity());
        telemetry.addData("Shoot intake power", IntakeMacro.getTargetPower());
        telemetry.addData("Feeding time (s)", feedingTime);
        telemetry.update();
    }

    @Override
    public void stop() {
        launcher.stop();
        IntakeMacro.stop();
    }

    private void updateLauncherMode() {
        if (shotMode == ShotMode.NEAR) launcher.startNear();
        else launcher.startFar();
    }
}