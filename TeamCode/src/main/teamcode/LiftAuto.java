/*
 Todo:
  Clock intake
  Speed up extension/retraction -- done for now
  Clean up states -- done
  Fix yeeting -- hardware
  Auto outake -- done
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;


public class LiftAuto {
    static DcMotor winch;
    public Servo rarm;
    public Servo larm;
    public Servo turret;
    public Servo upTake;
    public Servo finger;

    public enum LiftState {
        TAKE,
        UP,
        UP_WAIT,
        UP2,
        UP3,
        WAIT,
        DUMP_BACK,
        ARM_DOWN,
        ARM_CLEAR,
        LIFT_TAKE,
        TAKEPOS

    }

    public enum LiftLevel{
        LOW,
        MID,
        HIGH
    }

    LiftState liftState = LiftState.TAKE;

    static LiftLevel liftLevel = LiftLevel.HIGH;

    static double takeDown = .6;
    static double takeUp = 0;

    public boolean low = false;
    public boolean positioned = false;
    public boolean center = true;
    public boolean isCentered = true;

    static boolean tLeft = false;
    static boolean tRight = false;
    static boolean up = false;
    static boolean right = false;
    static boolean left = true;
    static boolean moveOn = false;

    private final ElapsedTime timerlift = new ElapsedTime();
    public final LinearOpMode liftAuto;

    public LiftAuto(LinearOpMode liftAuto){
        winch = liftAuto.hardwareMap.dcMotor.get("winch");
        winch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        winch.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        winch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rarm = liftAuto.hardwareMap.get(ServoImplEx.class, "rarm");
        larm = liftAuto.hardwareMap.get(ServoImplEx.class, "larm");
        turret = liftAuto.hardwareMap.get(Servo.class, "turret");
        upTake = liftAuto.hardwareMap.servo.get("upTake");
        finger = liftAuto.hardwareMap.servo.get("finger");

        ((PwmControl)rarm).setPwmRange(new PwmControl.PwmRange(500, 2500));
        ((PwmControl)larm).setPwmRange(new PwmControl.PwmRange(500, 2500));

        this.liftAuto = liftAuto;
    }

    public void armRTP(double parm){
        larm.setPosition(parm);
        rarm.setPosition(1-parm);
    }

    static void liftRTP(int slides, double power){
        winch.setTargetPosition(slides);
        winch.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        winch.setPower(power);
    }

    public void armState(){
        switch (liftState) {

            case TAKE:
                if (up) {
                    liftState = LiftState.UP;
                } else if (left) {
                    liftState = LiftState.UP;
                    tLeft = true;
                    tRight = false;
                }   else if (right) {
                    liftState = LiftState.UP;
                    tRight = true;
                    tLeft = false;
                } else {
                    armRTP(.075);
                    upTake.setPosition(takeDown);
                    liftRTP(50, 1);
                }
                break;

            case UP:
                if (liftLevel == LiftLevel.HIGH){
                    finger.setPosition(1);
                    armRTP(.05);
                    upTake.setPosition(takeDown);
                    timerlift.reset();
                    positioned = true;
                    center = false;
                    liftRTP(375, 1);
                    liftState = LiftState.UP_WAIT;
                } else if(liftLevel == LiftLevel.MID) {
                    armRTP(.05);
                    upTake.setPosition(takeDown);
                    timerlift.reset();
                    positioned = true;
                    center = false;
                    liftRTP(100, 1);
                    liftState = LiftState.UP_WAIT;
                } else if(liftLevel == LiftLevel.LOW) {
                    upTake.setPosition(takeDown);
                    armRTP(.05);
                    timerlift.reset();
                    positioned = true;
                    center = false;
                    liftRTP(100, 1);
                    liftState = LiftState.UP_WAIT;
                }
                break;

                case UP_WAIT:
                if (liftLevel == LiftLevel.HIGH){
                    if (timerlift.seconds() >= .5){
                        liftState = LiftState.UP2;
                    }
                } else if(liftLevel == LiftLevel.MID) {
                    if (timerlift.seconds() >= .5){
                        if (!tLeft && !tRight){
                            liftRTP(250, 1);
                        }
                        liftState = LiftState.UP2;
                    }
                } else if(liftLevel == LiftLevel.LOW) {
                    if (timerlift.seconds() >= .5){
                        if (!tLeft && !tRight){
                            liftRTP(0, 1);
                        }
                        liftState = LiftState.UP2;
                    }
                }
                break;

            case UP2:
                if (liftLevel == LiftLevel.HIGH){
                    armRTP(.71);
                    timerlift.reset();
                    liftState = LiftState.UP3;
                } else if (liftLevel == LiftLevel.MID){
                    armRTP(.82);
                    timerlift.reset();
                    liftState = LiftState.UP3;
                } else if (liftLevel == LiftLevel.LOW){
                    armRTP(.86);
                    timerlift.reset();
                    liftRTP(50, 1);
                    liftState = LiftState.UP3;
                }
                break;

            case UP3:
                if (timerlift.seconds() > 1.5) {
                    if (left) {
                        if (liftLevel == LiftLevel.LOW){
                            turret.setPosition(.28);
                        } else {
                            turret.setPosition(.25);
                        }
                        isCentered = false;
                        timerlift.reset();
                        liftState = LiftState.WAIT;
                    } else if (right) {
                        if (liftLevel == LiftLevel.LOW){
                            turret.setPosition(.72);
                        } else {
                            turret.setPosition(.78);
                        }
                        isCentered = false;
                        low = true;
                        timerlift.reset();
                        liftState = LiftState.WAIT;
                    }
                }
                break;

            case WAIT:
                if (timerlift.seconds() > 1){
                    finger.setPosition(.85);
                    timerlift.reset();
                    liftState = LiftState.DUMP_BACK;
                    timerlift.reset();
                }
                break;

            case DUMP_BACK:
                if (timerlift.seconds() >= 1){
                    upTake.setPosition(takeDown);
                    low = false;
                    liftState = LiftState.ARM_DOWN;
                }
                break;

            case ARM_DOWN:
                turret.setPosition(.55);
                finger.setPosition(1);
                positioned = false;
                center = true;
                armRTP(.6);
                timerlift.reset();
                liftState = LiftState.ARM_CLEAR;
                break;


            case ARM_CLEAR:
                if (timerlift.seconds() >= 1.5) {
                    liftRTP(200, .5);
                    isCentered = true;
                    armRTP(.075);
                    timerlift.reset();
                    liftState = LiftState.LIFT_TAKE;
                }
                break;

            case LIFT_TAKE:
                if (timerlift.seconds() >= .75) {
                    liftRTP(50, 1);
                    upTake.setPosition(takeUp);
                    timerlift.reset();
                    liftState = LiftState.TAKEPOS;
                }
                break;

            case TAKEPOS:
                    armRTP(.075);
                    upTake.setPosition(takeDown);
                    liftRTP(50, 1);
                    TacAuto.autoState = TacAuto.AutoState.DRIVE2;
                    moveOn = true;
                break;
        }
    }
}