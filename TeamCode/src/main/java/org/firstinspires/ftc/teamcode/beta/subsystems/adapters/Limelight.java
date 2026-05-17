package org.firstinspires.ftc.teamcode.beta.subsystems.adapters;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.beta.subsystems.ports.Vision;
import org.firstinspires.ftc.teamcode.beta.utils.Alliance;

public class Limelight implements Vision {
    
    private final Limelight3A limelight;
    
    public Limelight(HardwareMap hardwareMap, Alliance alliance) {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(alliance == Alliance.BLUE? 0 : 1);
        limelight.start();
    }

    @Override
    public Double getTx() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            return Math.toRadians(result.getTx());
        }
        return null;
    }
}
