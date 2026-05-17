package org.firstinspires.ftc.teamcode.legacy.opModes.teleop.dependencies;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@Configurable
public class Launcher {

    private final DcMotorEx flywheelMaster;
    private final DcMotorEx flywheelSlave;

    // =========================
    // Configurável no painel
    // =========================

    // ticks por segundo
    public static double velocityNear = 1475;
    public static double velocityFar  = 1625;

    public static double nearTrim = 0.0;
    public static double farTrim  = 0.0;

    // ticks por volta do encoder
    // MUITO IMPORTANTE ajustar corretamente no painel
    public static double TICKS_PER_REV = 28.0; // exemplo: goBILDA 5202/5203

    // =========================

    private boolean launcherOn = false;

    public Launcher(DcMotorEx flywheelMaster,
                    DcMotorEx flywheelSlave) {

        this.flywheelMaster = flywheelMaster;
        this.flywheelSlave  = flywheelSlave;

        flywheelMaster.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheelSlave.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        flywheelMaster.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheelSlave.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        stop();
    }

    // =====================
    // uso no Principal
    // =====================

    public void startNear() {
        setVelocity(velocityNear + nearTrim);
    }

    public void startFar() {
        setVelocity(velocityFar + farTrim);
    }

    // =====================
    // ajustes por código
    // =====================

    public void adjustNear(double delta) {
        nearTrim += delta;
    }

    public void adjustFar(double delta) {
        farTrim += delta;
    }

    // =====================

    private void setVelocity(double velocity) {

        launcherOn = true;

        // um gira invertido fisicamente
        flywheelMaster.setVelocity(-velocity);
        flywheelSlave.setVelocity( velocity);
    }

    public void stop() {

        launcherOn = false;

        flywheelMaster.setVelocity(0);
        flywheelSlave.setVelocity(0);
    }

    public boolean isOn() {
        return launcherOn;
    }

    // -------------------
    // targets
    // -------------------

    public double getNearVelocity() {
        return velocityNear + nearTrim;
    }

    public double getFarVelocity() {
        return velocityFar + farTrim;
    }

    // -------------------
    // ticks / segundo
    // -------------------

    public double getMasterVelocity() {
        return flywheelMaster.getVelocity();
    }

    public double getSlaveVelocity() {
        return flywheelSlave.getVelocity();
    }

    // -------------------
    // RPM real
    // -------------------

    public double getMasterRPM() {
        return Math.abs(getMasterVelocity()) * 60.0 / TICKS_PER_REV;
    }

    public double getSlaveRPM() {
        return Math.abs(getSlaveVelocity()) * 60.0 / TICKS_PER_REV;
    }

    private final ElapsedTime timer = new ElapsedTime();
    private boolean timing = false;

    public boolean importantFunction() {

        // Se é a primeira vez chamando a função neste ciclo, inicie o timer
        if (!timing) {
            timer.reset();
            timing = true;
        }

        // Verifica a condição das rodas (flywheels)
        boolean conditionMet =
                Math.abs(flywheelMaster.getVelocity()) > 1500 ||
                        Math.abs(flywheelSlave.getVelocity()) > 1500;

        // Verifica se o tempo limite foi atingido
        boolean timeExceeded = timer.seconds() >= 1.8;

        // Se a condição foi cumprida OU o tempo excedeu, finaliza a ação
        if (conditionMet || timeExceeded) {
            timing = false; // Reseta a flag para a próxima vez que você precisar usar a função
            return true;
        }

        // Se ainda não cumpriu a condição nem passou do tempo, continua rodando
        return false;
    }
}