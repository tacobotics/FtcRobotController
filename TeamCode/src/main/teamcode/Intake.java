package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.LiftAuto.takeDown;
import static org.firstinspires.ftc.teamcode.LiftAuto.takeUp;
import static org.firstinspires.ftc.teamcode.LiftClean.slow;
import static org.firstinspires.ftc.teamcode.autoinit.blueDuck;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.lang.Math;


import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Intake {
    static ColorSensor color_REV_ColorRangeSensor;

    public DcMotor take;
    public DcMotor lduck;
    public DcMotor rduck;

    public double going = .25;
    public double threshold = 3.0;

    public int takeStop = 1451;

    public boolean override = false;

    static boolean full;

    private final ElapsedTime timerTake = new ElapsedTime();
    public final LinearOpMode intake;

    public Intake(LinearOpMode intake){
        color_REV_ColorRangeSensor = intake.hardwareMap.get(ColorSensor.class, "color");

        take = intake.hardwareMap.dcMotor.get("take");
        take.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        take.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        take.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rduck = intake.hardwareMap.dcMotor.get("rduck");
        rduck.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lduck = intake.hardwareMap.dcMotor.get("lduck");
        lduck.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        this.intake=intake;
    }

    static double prox(){
        return ((DistanceSensor) color_REV_ColorRangeSensor).getDistance(DistanceUnit.CM);
    }

    int currentPos(){
        return (take.getCurrentPosition());
    }

    public void intakingStates(double voomin, double voomout) {
            if (slow) {
                take.setPower(-.5);
            } else if (((voomin > .3 && LiftClean.winch.getCurrentPosition() < 20) || LiftClean.blip) && !full) {
                take.setPower(-1);
                timerTake.reset();
            } else if (voomout > .3) {
                take.setPower(.5);
            } else {
                if (timerTake.seconds() < .1) {
                    take.setPower(0);
                } else if (timerTake.seconds() < .75) {
                    take.setPower(.25);
                } else {
                    take.setPower(0);
                }
            }
        }



    public void sensorStuff(boolean a, boolean b){
        if (a){
            override = false;
        } else if (b){
            override = true;
        }
        if (override){
            full = false;
        } else {
            full = !(prox() > threshold);
        }
    }

    public void duck (double slow, double fast, boolean superSlow) {
        if (!blueDuck) {
            if (slow > .3) {
                lduck.setPower(-going);
                rduck.setPower(-going);
            } else if (fast > .3) {
                lduck.setPower(-1);
                rduck.setPower(-1);
            } else if (superSlow) {
                lduck.setPower(-.1);
                rduck.setPower(-.1);
            } else {
                lduck.setPower(0);
                rduck.setPower(0);
            }
        } else if (slow > .3) {
            lduck.setPower(going);
            rduck.setPower(going);
        } else if (fast > .3) {
            lduck.setPower(1);
            rduck.setPower(1);
        } else if (superSlow){
            lduck.setPower(.1);
            rduck.setPower(.1);
        } else {
            lduck.setPower(0);
            rduck.setPower(0);
            }
        }
    }