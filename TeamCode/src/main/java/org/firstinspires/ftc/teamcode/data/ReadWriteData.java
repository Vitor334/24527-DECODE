package org.firstinspires.ftc.teamcode.data;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.utils.Alliance;

import java.io.File;

public class ReadWriteData {

    public static void write(Alliance alliance, Pose pose) {

        File file = AppUtil.getInstance().getSettingsFile("MatchDataFile.txt");

        String data = alliance.value() + "," + pose.getX() + "," + pose.getY() + "," + pose.getHeading();

        ReadWriteFile.writeFile(file, data);

    }

    public static Pose pose() {

        File file = AppUtil.getInstance().getSettingsFile("MatchDataFile.txt");

        String raw = ReadWriteFile.readFile(file).trim();

        if (raw.isEmpty()) return null;

        String[] parts = raw.split(",");

        double[] values = new double[4];

        for (int i = 0; i < 4 && i < parts.length; i++) {
            values[i] = Double.parseDouble(parts[i].trim());
        }

        return new Pose(
                values[1],
                values[2],
                values[3]
        );
    }

    public static Alliance alliance() {

        File file = AppUtil.getInstance().getSettingsFile("MatchDataFile.txt");

        String raw = ReadWriteFile.readFile(file).trim();

        if (raw.isEmpty()) return null;

        String[] parts = raw.split(",");

        double[] values = new double[4];

        for (int i = 0; i < 4 && i < parts.length; i++) {
            values[i] = Double.parseDouble(parts[i].trim());
        }

        return Alliance.fromValue((int)values[0]);
    }

    public static void clear() {

        File file = AppUtil.getInstance().getSettingsFile("MatchDataFile.txt");

        ReadWriteFile.writeFile(file, "");
    }
}