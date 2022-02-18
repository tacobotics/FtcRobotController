/*
 Todo:
  Clock intake
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;


public class LiftClean {
    static DcMotor winch;
    public Servo rarm;
    public Servo larm;
    public Servo turret;
    public Servo dump;
    public Servo upTake;
    static Servo finger;

    public enum LiftState {
        TAKE,
        UP,
        UP_WAIT,
        UP2,
        UP3,
        WAIT,
        DUMP_BACK,
        ARM_DOWN,
        LIFT_DOWN,
        ARM_CLEAR,
        LIFT_TAKE
    }

    public enum LiftLevel{
        NEUTRAL,
        HIGH,
        CAP
    }

    LiftState liftState = LiftState.TAKE;

    LiftLevel liftLevel = LiftLevel.HIGH;

    public double takeDown = .63;
    public double takeUp = .15;

    static boolean slow = false;
    static boolean blip = false;
    static boolean blop = false;
    public boolean low = false;
    public boolean positioned = false;
    public boolean center = true;
    public boolean isCentered = true;
    public boolean tLeft = false;
    public boolean tRight = false;
    public boolean debounce = false;
    public boolean debounce2 = false;

    private final ElapsedTime timerlift = new ElapsedTime();
    public final LinearOpMode liftClean;

    public LiftClean(LinearOpMode liftClean){
        winch = liftClean.hardwareMap.dcMotor.get("winch");
        winch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        winch.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        winch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rarm = liftClean.hardwareMap.get(ServoImplEx.class, "rarm");
        larm = liftClean.hardwareMap.get(ServoImplEx.class, "larm");
        turret = liftClean.hardwareMap.get(Servo.class, "turret");
        dump = liftClean.hardwareMap.get(Servo.class, "dump");
        upTake = liftClean.hardwareMap.servo.get("upTake");
        finger = liftClean.hardwareMap.servo.get("finger");

        ((PwmControl)rarm).setPwmRange(new PwmControl.PwmRange(500, 2500));
        ((PwmControl)larm).setPwmRange(new PwmControl.PwmRange(500, 2500));
        ((PwmControl)turret).setPwmRange(new PwmControl.PwmRange(800, 2380));

        this.liftClean = liftClean;
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

    public void armState(boolean y, double leftTrig, double rightTrig, boolean x, boolean left, boolean up, boolean right, boolean a, boolean y1, boolean b, boolean down1){
        switch (liftState) {

            case TAKE:
                if (y) {
                    liftState = LiftState.UP;
                    liftLevel = LiftLevel.HIGH;
                } else if (b){
                    debounce = true;
                    liftState = LiftState.UP;
                    liftLevel = LiftLevel.CAP;
                } else if (left) {
                    liftState = LiftState.UP;
                    liftLevel = LiftLevel.NEUTRAL;
                    tLeft = true;
                }   else if (right) {
                    liftState = LiftState.UP;
                    tRight = true;
                    liftLevel = LiftLevel.NEUTRAL;
                } else if (y1){
                    finger.setPosition(.5);
                    dump.setPosition(.6);
                    armRTP(.05);
                    upTake.setPosition(takeDown);
                    liftRTP(100, 1);
                    blop = true;
                }  else if ((leftTrig > .3 || rightTrig > .3) && !Intake.full) {
                    finger.setPosition(.65);
                    turret.setPosition(.49);
                    dump.setPosition(.4);
                    upTake.setPosition(takeDown);
                    liftRTP(0, 1);
                    armRTP(.05);
                } else if (leftTrig < .3 || rightTrig < .3) {
                    finger.setPosition(1);
                    turret.setPosition(.49);
                    dump.setPosition(.4);
                    upTake.setPosition(.45);
                    armRTP(.075);
                    liftRTP(50, 1);
                    blop = winch.getCurrentPosition() < 25;
                }
                break;

            case UP:
                slow = true;
                finger.setPosition(1);
                if (liftLevel == LiftLevel.HIGH){
                    armRTP(.075);
                    dump.setPosition(.2);
                    upTake.setPosition(takeDown);
                    timerlift.reset();
                    positioned = true;
                    center = false;
                    liftRTP(400, 1);
                    liftState = LiftState.UP_WAIT;
                } else if(liftLevel == LiftLevel.NEUTRAL) {
                    liftRTP(200, 1);
                    armRTP(.075);
                    upTake.setPosition(takeDown);
                    timerlift.reset();
                    positioned = true;
                    center = false;
                    liftRTP(50, 1);
                    liftState = LiftState.UP_WAIT;
                } else if (liftLevel == LiftLevel.CAP){
                    slow = false;
                    armRTP(.075);
                    upTake.setPosition(takeDown);
                    timerlift.reset();
                    positioned = true;
                    center = false;
                    armRTP(1);
                    dump.setPosition(.8);
                    if (debounce){
                        if(!b){
                            debounce = false;
                        }
                    } else {
                        if (b){
                            debounce2 = true;
                            liftState = LiftState.UP3;
                        }
                    }
                    if (down1){
                        liftRTP(175,1);
                    } else {
                        liftRTP(325,1);
                    }
                }
                break;

            case UP_WAIT:
                if (liftLevel == LiftLevel.HIGH){
                    if (timerlift.seconds() >= .25){
                        dump.setPosition(.6);
                        liftState = LiftState.UP2;
                    }
                } else if(liftLevel == LiftLevel.NEUTRAL) {
                    liftRTP(250, 1);
                    if (timerlift.seconds() >= .25){
                        if (!tLeft && !tRight){
                            liftRTP(250, 1);
                        }
                        liftState = LiftState.UP2;
                    }

                }
                break;

            case UP2:
                if (liftLevel == LiftLevel.HIGH){
                    dump.setPosition(.4);
                    armRTP(.76);
                    timerlift.reset();
                    liftState = LiftState.UP3;
                } else if (liftLevel == LiftLevel.NEUTRAL){
                    dump.setPosition(.3);
                    if (tLeft || tRight) {
                        armRTP(.75);
                    } else {
                        armRTP(1);
                    }
                    timerlift.reset();
                    liftState = LiftState.UP3;
                }
                break;

            case UP3:
                slow = false;
                if (a){
                    liftState = LiftState.DUMP_BACK;
                } else if (liftLevel == LiftLevel.HIGH){
                    upTake.setPosition(takeUp);
                    if (x) {
                        finger.setPosition(.5);
                        timerlift.reset();
                        liftState = LiftState.DUMP_BACK;
                    }
                } else if (liftLevel == LiftLevel.NEUTRAL){
                    if (timerlift.seconds() >= .5){
                        liftRTP(0, 1);
                        if (tLeft){
                            low = true;
                            turret.setPosition(.05);
                            liftRTP(0, 1);
                            isCentered = false;
                            if (timerlift.seconds() >= .75){
                                upTake.setPosition(takeUp);
                            }
                        } else if (tRight){
                            low = true;
                            turret.setPosition(.95);
                            liftRTP(0, 1);
                            isCentered = false;
                            if (timerlift.seconds() >= .75){
                                upTake.setPosition(takeUp);
                            }
                        }
                        if (x) {
                            low = true;
                            finger.setPosition(.5);
                            timerlift.reset();
                            if (tRight || tLeft){
                                tLeft = false;
                                tRight = false;
                            }
                            liftState = LiftState.WAIT;
                        }
                    }
                } else if (liftLevel == LiftLevel.CAP) {
                    armRTP(.65);
                    dump.setPosition(.4);
                    liftRTP(400, .5);
                    if (debounce2) {
                        if (!b) {
                            debounce2 = false;
                        }
                    } else {
                        if (b) {
                            liftRTP(150, 1);
                            dump.setPosition(.6);
                            timerlift.reset();
                            liftState = LiftState.WAIT;
                        }
                    }
                }
                break;

            case WAIT:
                if (liftLevel == LiftLevel.CAP) {
                    if (timerlift.seconds() > .5) {
                        dump.setPosition(.35);
                        liftState = LiftState.DUMP_BACK;
                    }
                }
                if (a) {
                    liftState = LiftState.DUMP_BACK;
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
                    if (timerlift.seconds() >= .25){
                        blip = true;
                        upTake.setPosition(takeDown);
                        dump.setPosition(.35);
                        liftState = LiftState.ARM_DOWN;
                    }
                }
                break;

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
                    turret.setPosition(.5);
                    timerlift.reset();
                    liftState = LiftState.ARM_CLEAR;
                }
                break;

            case ARM_CLEAR:
                if (low) {
                    if (timerlift.seconds() >= 1) {
                        isCentered = true;
                        liftRTP(200, .5);
                        armRTP(.075);
                        timerlift.reset();
                        liftState = LiftState.LIFT_TAKE;
                    }
                } else {
                    if (timerlift.seconds() >= .1) {
                        liftRTP(200, .5);
                        isCentered = true;
                        armRTP(.1);
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
        }
    }
}