package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

//@TeleOp(name = "TacoTele2")
public class TacoTele2 extends teleinit {

    @Override
    public void run() {
        waitForStart();
        intake.take.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intake.take.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                drive.driveBRR(gamepad1.right_stick_y,gamepad1.left_stick_y,gamepad1.left_bumper);
                intake.duck(gamepad2.left_trigger, gamepad2.right_trigger, gamepad2.right_bumper);
                intake.intakingStates(gamepad1.right_trigger, gamepad1.left_trigger);
                intake.sensorStuff(gamepad1.a, gamepad1.b);
                liftClean.armState(gamepad2.y, gamepad1.left_trigger, gamepad1.right_trigger, gamepad2.x, gamepad2.dpad_left,
                        gamepad2.dpad_right, gamepad2.a, gamepad1.y, gamepad2.b, gamepad2.dpad_down);
                telemetry.addData("Lift", LiftClean.winch.getCurrentPosition());
                telemetry.addData("Blue", blue);
                telemetry.addData("Override", intake.override);
                telemetry.addData("Prox", Intake.prox());
                telemetry.update();
            }
        }
    }
}
