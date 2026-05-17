package org.firstinspires.ftc.teamcode.beta.opModes.scripts;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.beta.data.ReadWriteData;
import org.firstinspires.ftc.teamcode.beta.utils.Alliance;

@TeleOp(name = "Read/Write Test", group = "Test")
public class ReadWriteTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        String message = "This OpMode shows the internal storage of the robot " +
                "recorded between autonomous and teleop modes. " +
                "The data is stored in \"MatchDataFile.txt\"";
        while (opModeInInit()) {
            telemetry.addLine(message);
            telemetry.update();
        }
        waitForStart();
        while (opModeIsActive()) {

            telemetry.addLine("Press X to clear file");
            telemetry.addLine("Press A to generate random data");
            telemetry.addLine();

            if (gamepad1.xWasPressed()) ReadWriteData.clear();
            if (gamepad1.aWasPressed()) ReadWriteData.write(
                    Math.random() > 0.5? Alliance.BLUE : Alliance.RED,
                    new Pose(
                            Math.random() * 144,
                            Math.random() * 144,
                            Math.toRadians(Math.random() * 360)
                    )
            );

            Pose pose = ReadWriteData.pose();
            Alliance alliance = ReadWriteData.alliance();

            if (pose != null || alliance != null) {
                assert pose != null;
                telemetry.addLine(pose.toString());
                assert alliance != null;
                telemetry.addLine(alliance.value() == 1? "Alliance: BLUE" : "Alliance: RED");
            } else {
                telemetry.addLine("No data found");
            }

            telemetry.update();
        }
        requestOpModeStop();
    }
}
