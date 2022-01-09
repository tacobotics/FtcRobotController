package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "TacoTele2")
public class TacoTele2 extends teleinit {

    @Override
    public void run() {
        waitForStart();
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                drive.driving(gamepad1.right_stick_y,gamepad1.left_stick_y,gamepad1.left_bumper);
                intake.sensorStuff(gamepad1.a, gamepad1.b);
                intake.duck(gamepad2.left_trigger, gamepad2.right_trigger);
                intake.intaking(gamepad1.right_trigger, gamepad1.left_trigger);
                lift.armState(gamepad1.y, gamepad1.left_trigger, gamepad1.right_trigger, gamepad1.x, gamepad1.dpad_left, gamepad1.dpad_up, gamepad1.dpad_right, gamepad1.dpad_down);
                telemetry.addData("Lift", Lift.winch.getCurrentPosition());
                telemetry.addData("Prox", intake.prox());
                telemetry.addData("AUTODETECT OVERRIDE", intake.override);
                telemetry.addData("Intake", intake.take.getCurrentPosition());
                telemetry.update();
            }
        }
    }
}
