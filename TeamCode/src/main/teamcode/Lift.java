/*
 Todo
 *Clock intake
 *Speed up extension/retraction -- done for now
 *Clean up states
 *Fix yeeting -- hardware
 *Auto outake -- done
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;


public class Lift {
    static DcMotor winch;
    public Servo rarm;
    public Servo larm;
    public Servo turret;
    public Servo dump;
    public Servo upTake;
    public Servo finger;

    public enum LiftState {
        ARM_DOWN,
        LIFT_DOWN,
        ARM_CLEAR,
        LIFT_TAKE,
        TAKE,
        UP,
        UP2,
        UP3,
        DUMP_BACK,
        UPM,
        UP2M,
        UP3M,
        WAIT,
        UP_WAIT,
        UPM_WAIT
    }

    LiftState liftState = LiftState.TAKE;

    public double takeDown = .7;
    public double takeUp = 0;

    static double winchPos;
    static boolean blip = false;
    static boolean blop = false;
    public boolean low = false;
    public boolean positioned = false;
    public boolean center = true;
    public boolean isCentered = true;
    public boolean tLeft = false;
    public boolean tRight = false;

    private final ElapsedTime timerlift = new ElapsedTime();
    public final LinearOpMode lift;

    public Lift(LinearOpMode lift){
        winch = lift.hardwareMap.dcMotor.get("winch");
        winch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        winch.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        winch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rarm = lift.hardwareMap.get(ServoImplEx.class, "rarm");
        larm = lift.hardwareMap.get(ServoImplEx.class, "larm");
        turret = lift.hardwareMap.get(Servo.class, "turret");
        dump = lift.hardwareMap.get(Servo.class, "dump");
        upTake = lift.hardwareMap.servo.get("upTake");
        finger = lift.hardwareMap.servo.get("finger");

        ((PwmControl)rarm).setPwmRange(new PwmControl.PwmRange(500, 2500));
        ((PwmControl)larm).setPwmRange(new PwmControl.PwmRange(500, 2500));

        this.lift=lift;
    }

    public void armRTP(double parm){
        larm.setPosition(parm);
        rarm.setPosition(1-parm);
    }

    public void liftRTP(int slides, double power){
        winch.setTargetPosition(slides);
        winch.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        winch.setPower(power);
    }

    static double winchPos(){
        return (winch.getCurrentPosition());
    }

    public void armState(boolean y, double leftTrig, double rightTrig, boolean x, boolean left, boolean up, boolean right, boolean down){
        switch (liftState) {
            case ARM_DOWN:
                    dump.setPosition(.35);
                    positioned = false;
                    center = true;
                    armRTP(.6);
                    timerlift.reset();
                    liftState = LiftState.LIFT_DOWN;
                break;

            case LIFT_DOWN:
                if (timerlift.seconds() >= .2) {
                    blip = false;
                    turret.setPosition(.49);
                    timerlift.reset();
                    liftState = LiftState.ARM_CLEAR;
                }
                break;

            case ARM_CLEAR:
                    if (low) {
                        if (timerlift.seconds() >= 1) {
                            isCentered = true;
                            liftRTP(200, .5);
                            armRTP(.05);
                            timerlift.reset();
                            liftState = LiftState.LIFT_TAKE;
                        }
                    } else {
                        if (timerlift.seconds() >= .1) {
                            liftRTP(200, .5);
                            isCentered = true;
                            armRTP(.05);
                            timerlift.reset();
                            liftState = LiftState.LIFT_TAKE;
                        }
                }
                break;

            case LIFT_TAKE:
                if (timerlift.seconds() >= .75) {
                    liftRTP(50, 1);
                    upTake.setPosition(takeUp);
                    timerlift.reset();
                    liftState = LiftState.TAKE;
                }
                break;

            case TAKE:
                if (y) {
                    lift.telemetry.addLine("Read if y");
                    liftState = LiftState.UP;
                } else if (left) {
                    liftState = LiftState.UPM;
                    tLeft = true;
                }   else if (right) {
                    liftState = LiftState.UPM;
                    tRight = true;
                } else if (up){
                    liftState = LiftState.UPM;
                } else if ((leftTrig > .3 || rightTrig > .3) && !Intake.full) {
                    dump.setPosition(.4);
                    liftRTP(0, 1);
                    upTake.setPosition(takeDown);
                    armRTP(0);
                    finger.setPosition(.75);
                } else if (leftTrig < .3 || rightTrig < .3) {
                    dump.setPosition(.35);
                    armRTP(.02);
                    upTake.setPosition(takeDown);
                    liftRTP(50, 1);
                    if (winch.getCurrentPosition() < 25){
                        blop = true;
                    } else {
                        finger.setPosition(1);
                        blop = false;
                    }
                }
                break;

            case UP:
                lift.telemetry.addLine("Changed state");
                armRTP(.05);
                dump.setPosition(.2);
                upTake.setPosition(takeDown);
                timerlift.reset();
                positioned = true;
                center = false;
                liftRTP(400, 1);
                liftState = LiftState.UP_WAIT;
                break;

            case UP2:
                    armRTP(.75);
                    timerlift.reset();
                    liftState = LiftState.UP3;
                break;

            case UP_WAIT:
                if (timerlift.seconds() >= .25){
                    dump.setPosition(.8);
                    liftState = LiftState.UP2;
                }

            case UP3:
                upTake.setPosition(takeUp);
                    if (x) {
                        dump.setPosition(.4);
                        finger.setPosition(.5);
                        timerlift.reset();
                        liftState = LiftState.DUMP_BACK;
                    } else if (left){
                        armRTP(.7);
                        turret.setPosition(.19);
                        isCentered = false;
                    } else if (right){
                        armRTP(.7);
                        turret.setPosition(.81);
                        isCentered = false;
                        low = true;
                    } else if (up){
                        armRTP(.75);
                        turret.setPosition(.49);
                        isCentered = true;
                    }
                break;

            case DUMP_BACK:
                if (low){
                    if (timerlift.seconds() >= .5){
                        blip = true;
                        upTake.setPosition(takeDown);
                        dump.setPosition(.35);
                        low = false;
                        liftState = LiftState.ARM_DOWN;
                    }
                } else {
                    if (timerlift.seconds() >= .5){
                        blip = true;
                        upTake.setPosition(takeDown);
                        dump.setPosition(.35);
                        liftState = LiftState.ARM_DOWN;
                    }
                }
                break;

            case WAIT:
                if (down){
                    liftState = LiftState.DUMP_BACK;
                }
                break;

            case UPM:
                lift.telemetry.addLine("Changed states");
                armRTP(.2);
                dump.setPosition(.3);
                upTake.setPosition(takeDown);
                timerlift.reset();
                positioned = true;
                center = false;
                liftRTP(50, 1);
                liftState = LiftState.UPM_WAIT;
                break;

            case UP2M:
                    dump.setPosition(1);
                    if (tLeft || tRight) {
                        armRTP(.82);
                    } else {
                        armRTP(1);
                    }
                    timerlift.reset();
                    liftState = LiftState.UP3M;
                break;

            case UPM_WAIT:
                if (timerlift.seconds() >= .25){
                    if (!tLeft && !tRight){
                        liftRTP(250, 1);
                    }
                    dump.setPosition(.8);
                    liftState = LiftState.UP2M;
                }

            case UP3M:
                if (timerlift.seconds() >= .25){
                    upTake.setPosition(takeUp);
                    if (tLeft){
                        turret.setPosition(.25);
                        liftRTP(0, 1);
                        isCentered = false;
                    } else if (tRight){
                        turret.setPosition(.75);
                        liftRTP(0, 1);
                        isCentered = false;
                    }
                    if (x) {
                        dump.setPosition(.6);
                        finger.setPosition(.5);
                        timerlift.reset();
                        if (tRight || tLeft){
                            tLeft = false;
                            tRight = false;
                            liftState = LiftState.DUMP_BACK;
                        } else {
                            liftState = LiftState.WAIT;
                        }
                    }
                }
                break;
        }
    }
}