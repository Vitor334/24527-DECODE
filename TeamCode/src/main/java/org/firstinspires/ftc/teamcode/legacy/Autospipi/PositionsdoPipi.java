package org.firstinspires.ftc.teamcode.legacy.Autospipi;

import com.pedropathing.geometry.Pose;

public final class PositionsdoPipi {

    // Evita instancia acidental
    private PositionsdoPipi() {}

    // POSIÇÕES
    //===================================================
    //Posições do vermelho

    public static final Pose START =  //Posição inicial
            new Pose(119, 130, Math.toRadians(45));

    public static final Pose SHOT =  //Posição de lançamento
            new Pose(83, 95.8, Math.toRadians(45));

    public static final Pose COLLECT1 = //Posição de coleta 1
            new Pose(130, 83.7, Math.toRadians(0));

    public static final Pose COLLECT2 = //Posição de coleta 2
            new Pose(136.7, 58, Math.toRadians(0));

    public static final Pose OPEN_GATE =  //abrir gate
            new Pose(128.2, 74.7, Math.toRadians(90));

    public static final Pose COLLECT3 =  //Coletar logo após as bolinhas sairem do gate
            new Pose(136.7, 35.2, Math.toRadians(90));

    public static final Pose SAIDA =  //Sair da zona apos terminar tudo
            new Pose(98.1, 79.8, Math.toRadians(90));


    /* Sequencia de posições para fazer autonomo de 12 artefatos,
    apenas abrindo o gate, de perto.

    START
    SHOT
    COLLECT1_POS
    SHOT
    COLLECT2_POS
    SHOT
    COLLECT3_POS
    SHOT
    SAIDA

     */
}