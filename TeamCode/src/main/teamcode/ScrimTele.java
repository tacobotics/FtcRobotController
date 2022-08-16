package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "ScrimTele")
public class ScrimTele extends scriminit {

    @Override
    public void run() {
        waitForStart();
        intakeScrim.take.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeScrim.take.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                drive.driveBRR(gamepad1.right_stick_y,gamepad1.left_stick_y,gamepad1.left_bumper);
                intakeScrim.duck(gamepad2.left_trigger, gamepad2.right_trigger, gamepad2.right_bumper);
                intakeScrim.intakingStates(gamepad1.right_trigger, gamepad1.left_trigger);
                intakeScrim.sensorStuff(gamepad1.a, gamepad1.b);
                liftScrim.armState(gamepad2.y, gamepad1.left_trigger, gamepad1.right_trigger, gamepad2.x, gamepad2.dpad_left,
                        gamepad2.dpad_right, gamepad2.dpad_up, gamepad2.a, gamepad1.y, gamepad2.b, gamepad2.dpad_down);
                telemetry.addData("Lift", LiftScrim.winch.getCurrentPosition());
                telemetry.addData("Blue", blue);
                telemetry.addData("Override", intakeScrim.override);
                telemetry.addData("Prox", intakeScrim.prox());
                telemetry.update();
            }
        }
    }
}
