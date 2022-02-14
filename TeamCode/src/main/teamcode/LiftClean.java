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
    static Servo upTake;
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
        HIGH
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

    public void armState(boolean y, double leftTrig, double rightTrig, boolean x, boolean left, boolean up, boolean right, boolean a, boolean y1){
        switch (liftState) {

            case TAKE:
                if (y) {
                    tRight = false;
                    tLeft = false;
                    liftState = LiftState.UP;
                    liftLevel = LiftLevel.HIGH;
                } else if (left) {
                    tLeft = true;
                    tRight = false;
                    liftState = LiftState.UP;
                    liftLevel = LiftLevel.NEUTRAL;
                }   else if (right) {
                    tRight = true;
                    tLeft = false;
                    liftState = LiftState.UP;
                    liftLevel = LiftLevel.NEUTRAL;
                } else if (y1){
                    finger.setPosition(.5);
                    dump.setPosition(.6);
                    armRTP(.2);
                    liftRTP(100, 1);
                    blop = true;
                } else if ((leftTrig > .3 || rightTrig > .3) && !Intake.full) {
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
                    LiftClean.upTake.setPosition(.45);
                    armRTP(.075);
                    liftRTP(50, 1);
                    blop = winch.getCurrentPosition() < 25;
                }
                break;

            case UP:
                slow = true;
                finger.setPosition(1);
                if (liftLevel == LiftLevel.HIGH){
                    armRTP(.05);
                    dump.setPosition(.2);
                    upTake.setPosition(takeDown);
                    timerlift.reset();
                    positioned = true;
                    center = false;
                    liftRTP(400, 1);
                    liftState = LiftState.UP_WAIT;
                } else if(liftLevel == LiftLevel.NEUTRAL) {
                    liftRTP(200, 1);
                    armRTP(.05);
                    upTake.setPosition(takeDown);
                    timerlift.reset();
                    positioned = true;
                    center = false;
                    liftRTP(50, 1);
                    liftState = LiftState.UP_WAIT;
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
                    armRTP(.7);
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
                    } else if (left){
                        armRTP(.55);
                        turret.setPosition(.15);
                        isCentered = false;
                    } else if (right){
                        armRTP(.55);
                        turret.setPosition(.85);
                        isCentered = false;
                        low = true;
                    } else if (up){
                        armRTP(.55);
                        turret.setPosition(.5);
                        isCentered = true;
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
                }
                break;

            case WAIT:
                if (a){
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