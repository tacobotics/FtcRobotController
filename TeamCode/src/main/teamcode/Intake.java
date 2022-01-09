package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Lift.winchPos;

import static java.lang.Math.abs;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Intake {
    public ColorSensor color_REV_ColorRangeSensor;

    public DcMotor take;
    public DcMotor lduck;
    public DcMotor rduck;

    public double going = .27;
    public double threshold = 3;

    public boolean override = false;

    static boolean full;
    static boolean blue = true;



    private final ElapsedTime timerIntake = new ElapsedTime();
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

    int intakePos(){
        return (take.getCurrentPosition());
    }

    double prox(){
        return ((DistanceSensor) color_REV_ColorRangeSensor).getDistance(DistanceUnit.CM);
    }

    public void intaking(double voomin, double voomout){
        if(((voomin > .3 && winchPos < 10) || Lift.blip) && !full){
            take.setPower(-1);
        } else if((voomout > .3 && voomin < .3) || Lift.blop) {
            take.setPower(1);
        } else {
            if (((abs(intakePos()) % 75) > 5) || ((abs(intakePos()) % 75) > (75 -5))){
                take.setPower(.025);
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
            full = !(((DistanceSensor) color_REV_ColorRangeSensor).getDistance(DistanceUnit.CM) > threshold);
        }

    }

    public void duck (double slow, double fast) {
            if (blue) {
                if (fast > .3) {
                    lduck.setPower(1);
                    rduck.setPower(1);
                } else if (slow > .3) {
                    lduck.setPower(going);
                    rduck.setPower(going);
                } else {
                    lduck.setPower(0);
                    rduck.setPower(0);
                }
            } else {
                if (fast > .3) {
                    lduck.setPower(1);
                    rduck.setPower(1);
                } else if (slow > .3) {
                    lduck.setPower(-going);
                    rduck.setPower(-going);
                } else {
                    lduck.setPower(0);
                    rduck.setPower(0);
                }
            }
        }
}