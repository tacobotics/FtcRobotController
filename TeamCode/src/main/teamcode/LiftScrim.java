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


public class LiftScrim {
    static DcMotor winch;
    public Servo rarm;
    public Servo larm;
    public Servo turret;
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
        CAP,
        SCRIM
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
    public final LinearOpMode liftScrim;

    public LiftScrim(LinearOpMode liftScrim){
        winch = liftScrim.hardwareMap.dcMotor.get("winch");
        winch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        winch.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        winch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rarm = liftScrim.hardwareMap.get(ServoImplEx.class, "rarm");
        larm = liftScrim.hardwareMap.get(ServoImplEx.class, "larm");
        turret = liftScrim.hardwareMap.get(Servo.class, "turret");
        upTake = liftScrim.hardwareMap.servo.get("upTake");
        finger = liftScrim.hardwareMap.servo.get("finger");

        ((PwmControl)rarm).setPwmRange(new PwmControl.PwmRange(500, 2500));
        ((PwmControl)larm).setPwmRange(new PwmControl.PwmRange(500, 2500));
        ((PwmControl)turret).setPwmRange(new PwmControl.PwmRange(800, 2380));

        this.liftScrim = liftScrim;
    }

    public void armRTP(double parm){
        larm.setPosition(parm);
        rarm.setPosition(1.01-parm);
    }

    public void liftRTP(int slides, double power){
        winch.setTargetPosition(slides);
        winch.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        winch.setPower(power);
    }

    public void armState(boolean y, double leftTrig, double rightTrig, boolean x, boolean left, boolean right, boolean up, boolean a,
                         boolean y1, boolean b, boolean down1){
        switch (liftState) {
            case TAKE:
                if (y) {
                    liftState = LiftState.UP;
                    liftLevel = LiftLevel.HIGH;
                } /*else if (b){
                    debounce = true;
                    liftState = LiftState.UP;
                    liftLevel = LiftLevel.CAP;
                }*/ else if (left) {
                    liftState = LiftState.UP;
                    liftLevel = LiftLevel.NEUTRAL;
                    tLeft = true;
                }   else if (right) {
                    liftState = LiftState.UP;
                    tRight = true;
                    liftLevel = LiftLevel.NEUTRAL;
                } else if (b){
                    liftState= LiftState.UP;
                    liftLevel = LiftLevel.SCRIM;
                } else if (y1){
                    finger.setPosition(.3);
                    armRTP(.075);
                    upTake.setPosition(takeDown);
                    liftRTP(50, 1);
                    blop = true;
                }  else if ((leftTrig > .3 || rightTrig > .3) && !Intake.full) {
                    finger.setPosition(.65);
                    turret.setPosition(.49);
                    upTake.setPosition(takeDown);
                    liftRTP(0, 1);
                    armRTP(0);
                } else if (leftTrig < .3 || rightTrig < .3) {
                    finger.setPosition(1);
                    turret.setPosition(.49);
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
                } else if (liftLevel == LiftLevel.SCRIM){
                    armRTP(.075);
                    upTake.setPosition(takeDown);
                    timerlift.reset();
                    positioned = true;
                    center = false;
                    liftRTP(300, 1);
                    liftState = LiftState.UP_WAIT;
                }
                break;

            case UP_WAIT:
                if (liftLevel == LiftLevel.HIGH){
                    if (timerlift.seconds() >= .25){
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

                } else if (liftLevel == LiftLevel.SCRIM){
                    if (timerlift.seconds() >= .3){
                        liftState = LiftState.UP2;
                    }
                }
                break;

            case UP2:
                if (liftLevel == LiftLevel.HIGH){
                    armRTP(.76);
                    timerlift.reset();
                    liftState = LiftState.UP3;
                } else if (liftLevel == LiftLevel.NEUTRAL){
                    if (tLeft || tRight) {
                        armRTP(.77);
                    } else {
                        armRTP(1);
                    }
                    timerlift.reset();
                    liftState = LiftState.UP3;
                } else if (liftLevel == LiftLevel.SCRIM){
                    liftRTP(0, .5);
                    armRTP(.63);
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
                    if (left){
                        armRTP(.75);
                        turret.setPosition(0);
                    } else if (right){
                        armRTP(.75);
                        turret.setPosition(1);
                    } else if (up){
                        armRTP(.77);
                        turret.setPosition(.5);
                    } else if (x){
                        finger.setPosition(.5);
                        timerlift.reset();
                        liftState = LiftState.DUMP_BACK;
                    }
                } else if (liftLevel == LiftLevel.SCRIM){
                    upTake.setPosition(takeUp);
                    if (left){
                        turret.setPosition(0);
                    } else if (right){
                        turret.setPosition(1);
                    } else if (up){
                        turret.setPosition(.5);
                    } else if (x){
                        finger.setPosition(.4);
                        timerlift.reset();
                        liftState = LiftState.DUMP_BACK;
                    }
                }  else if (liftLevel == LiftLevel.NEUTRAL){
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
                    liftRTP(400, .5);
                    if (debounce2) {
                        if (!b) {
                            debounce2 = false;
                        }
                    } else {
                        if (b) {
                            liftRTP(150, 1);
                            timerlift.reset();
                            liftState = LiftState.WAIT;
                        }
                    }
                }
                break;

            case WAIT:
                if (liftLevel == LiftLevel.CAP) {
                    if (timerlift.seconds() > .5) {
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
                        low = false;
                        liftState = LiftState.ARM_DOWN;
                    }
                } else {
                    if (timerlift.seconds() >= .25){
                        blip = true;
                        upTake.setPosition(takeDown);
                        liftState = LiftState.ARM_DOWN;
                    }
                }
                break;

            case ARM_DOWN:
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