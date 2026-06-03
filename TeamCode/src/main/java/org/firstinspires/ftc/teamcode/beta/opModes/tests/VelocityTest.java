package org.firstinspires.ftc.teamcode.beta.opModes.tests;

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
@TeleOp(name = "Velocity Test", group = "Test")
public class VelocityTest extends OpMode {

    // ── Configurable parameters ────────────────────────────────────────────-

    public static double SETPOINT = 800;

    // ── Estado interno ──────────────────────────────────────────────────────

    private List<LynxModule> hubs;
    private Outtake outtake;
    private final TelemetryManager graph = PanelsTelemetry.INSTANCE.getTelemetry();

    // ── Ciclo de vida ───────────────────────────────────────────────────────

    @Override
    public void init() {
        hubs = hardwareMap.getAll(LynxModule.class);
        hubs.forEach(h -> h.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL));

        outtake = new Flywheel(hardwareMap);
    }

    @Override
    public void loop() {
        hubs.forEach(LynxModule::clearBulkCache);
        outtake.setVelocity(SETPOINT);
        updateTelemetry();
    }

    @Override
    public void stop() {
        hubs.forEach(h -> h.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO));
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private void updateTelemetry() {
        double vel = outtake.getVelocity();

        graph.addData("velocity", vel);
        graph.addData("setpoint", SETPOINT);
        graph.update();

        telemetry.addData("velocity", vel);
        telemetry.addData("setpoint", SETPOINT);
        telemetry.addData("error", Math.abs(SETPOINT - vel));
        telemetry.update();
    }
}