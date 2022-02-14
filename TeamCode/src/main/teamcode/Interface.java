package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

//@TeleOp(name = "Interface")
public class Interface extends teleinit {

    @Override
    public void run() {
        waitForStart();
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                liftClean.turret.setPosition(.5);
            }
        }
    }
}
