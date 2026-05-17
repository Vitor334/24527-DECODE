package org.firstinspires.ftc.teamcode.beta.opModes.tuning;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.beta.subsystems.adapters.Flywheel;
import org.firstinspires.ftc.teamcode.beta.subsystems.ports.Outtake;

import java.util.List;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;

/**
 * Tuner para o Flywheel com controle PID + Feed-Forward.
 *
 * Ordem de tuning sugerida:
 *   1. kV  — sustenta o setpoint em regime permanente
 *   2. kS  — vence o atrito estático (offset de partida)
 *   3. kA  — compensa inércia / delay de aceleração
 *   4. kP/kI/kD — elimina erro residual
 */

@Configurable
@TeleOp(name = "Flywheel Tuner", group = "Scripts")
public class FlywheelTuner extends OpMode {

    // ── Configurable parameters ────────────────────────────────────────────-

    public static double SETPOINT = 800;

    public static double kV = 0;
    public static double kA = 0;
    public static double kS = 0;

    public static double kP = 0;
    public static double kI = 0;
    public static double kD = 0;

    // ── Estado interno ──────────────────────────────────────────────────────

    private List<LynxModule> hubs;
    private Outtake outtake;
    private ControlSystem controlSystem;

    private final ElapsedTime timer = new ElapsedTime();
    private final TelemetryManager graph = PanelsTelemetry.INSTANCE.getTelemetry();

    // ── Ciclo de vida ───────────────────────────────────────────────────────

    @Override
    public void init() {
        hubs = hardwareMap.getAll(LynxModule.class);
        hubs.forEach(h -> h.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL));

        outtake = new Flywheel(hardwareMap);
        controlSystem = buildControlSystem();
        timer.reset();
    }

    @Override
    public void loop() {
        hubs.forEach(LynxModule::clearBulkCache);
        buildControlSystem();
        outtake.setPower(controlSystem.calculate());
        updateTelemetry();
    }

    @Override
    public void stop() {
        hubs.forEach(h -> h.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO));
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private ControlSystem buildControlSystem() {
        ControlSystem cs = ControlSystem.builder()
                .velPid(kP, kI, kD)
                .basicFF(kV, kA, kS)
                .build();
        cs.setGoal(new KineticState(0, SETPOINT));
        return cs;
    }

    private void updateTelemetry() {
        graph.addData("velocity", outtake.getVelocity());
        graph.addData("setpoint", SETPOINT);
        graph.update();
    }
}