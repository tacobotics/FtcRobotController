package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


public abstract class New_Super_Class extends LinearOpMode {
    static boolean blue = false;
    static boolean far = false;
    static boolean delay = false;
    public void runOpMode() throws InterruptedException {
        inits();
        waitForStart();
        run();
    }

    public abstract void inits();
    public abstract void run();
}
