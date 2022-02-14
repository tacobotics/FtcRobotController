package org.firstinspires.ftc.teamcode;

//import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;


public class Drive {
    public DcMotor Left_Front;
    public DcMotor Left_Rear;
    public DcMotor Right_Front;
    public DcMotor Right_Rear;

    public int leftFTargPos;
    public int leftRTargPos;
    public int rightFTargPos;
    public int rightRTargPos;

    public boolean getYeeted = false;

    public enum DriveState {
        REGULAR,
        DEFENSE
    }

    DriveState driveState = DriveState.REGULAR;

    private final ElapsedTime timerdrive = new ElapsedTime();
    public final LinearOpMode drive;

    public Drive(LinearOpMode drive){
        Left_Front = drive.hardwareMap.dcMotor.get("Left_Front");
        Left_Front.setDirection(DcMotorSimple.Direction.REVERSE);
        Left_Front.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Left_Front.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        Left_Rear = drive.hardwareMap.dcMotor.get("Left_Rear");
        Left_Rear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Left_Rear.setDirection(DcMotorSimple.Direction.REVERSE);
        Left_Rear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        Right_Front = drive.hardwareMap.dcMotor.get("Right_Front");
        Right_Front.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Right_Front.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        Right_Rear = drive.hardwareMap.dcMotor.get("Right_Rear");
        Right_Rear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Right_Rear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        Right_Front.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Right_Rear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Left_Front.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Left_Rear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        Right_Front.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Right_Rear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Left_Front.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Left_Rear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

      //  Blinkyboi = drive.hardwareMap.get((RevBlinkinLedDriver.class), "BlinkyBoi");

        this.drive=drive;
    }

    /*
    public void time(){
            if (timecolor < 80) {
                Blinkyboi.setPattern(RevBlinkinLedDriver.BlinkinPattern.fromNumber(88));
            } else if (timecolor  > 120) {
                Blinkyboi.setPattern(RevBlinkinLedDriver.BlinkinPattern.fromNumber(44));
            } else if ((getRuntime() - start_time)  > 115) {
                Blinkyboi.setPattern(RevBlinkinLedDriver.BlinkinPattern.fromNumber(80));
            } else if ((getRuntime() - start_time)  > 90) {
                Blinkyboi.setPattern(RevBlinkinLedDriver.BlinkinPattern.fromNumber(82));
            } else if (getRuntime() - start_time)  > 80) {
                Blinkyboi.setPattern(RevBlinkinLedDriver.BlinkinPattern.fromNumber(85));
            }
    }
    */
    public void go_same(int all, double power) {
            Right_Front.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            Right_Rear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            Left_Front.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            Left_Rear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            Right_Front.setTargetPosition(-all);
            Right_Rear.setTargetPosition(-all);
            Left_Rear.setTargetPosition(-all);
            Left_Front.setTargetPosition(-all);
            Right_Front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Right_Rear.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Left_Front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Left_Rear.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Right_Front.setPower(power);
            Right_Rear.setPower(power);
            Left_Front.setPower(power);
            Left_Rear.setPower(power);
            timerdrive.reset();
            drive.idle();
    }

    public void goSave(int all, double power) {
        Right_Front.setTargetPosition(-all);
        Right_Rear.setTargetPosition(-all);
        Left_Rear.setTargetPosition(-all);
        Left_Front.setTargetPosition(-all);
        Right_Front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Right_Rear.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Left_Front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Left_Rear.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Right_Front.setPower(power);
        Right_Rear.setPower(power);
        Left_Front.setPower(power);
        Left_Rear.setPower(power);
        timerdrive.reset();
        drive.idle();
    }

    public void dumDrive(double power){
        Right_Front.setPower(power);
        Right_Rear.setPower(power);
        Left_Front.setPower(power);
        Left_Rear.setPower(power);
    }

    public void right(int turn, double power) {
            Right_Front.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            Right_Rear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            Left_Front.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            Left_Rear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            Right_Front.setTargetPosition(turn);
            Right_Rear.setTargetPosition(turn);
            Left_Rear.setTargetPosition(-turn);
            Left_Front.setTargetPosition(-turn);
            Right_Front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Right_Rear.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Left_Front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Left_Rear.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Right_Front.setPower(power);
            Right_Rear.setPower(power);
            Left_Front.setPower(power);
            Left_Rear.setPower(power);
            drive.idle();
    }

    public void driveBRR(double right, double left, boolean slow){
        switch (driveState) {
            case REGULAR:
                if (left == 0 && right == 0) {
                    timerdrive.reset();
                    driveState = DriveState.DEFENSE;
                } else if (slow) {
                    getYeeted = false;
                    Right_Front.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    Right_Rear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    Left_Front.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    Left_Rear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    leftFTargPos = Left_Front.getCurrentPosition();
                    leftRTargPos = Left_Rear.getCurrentPosition();
                    rightFTargPos = Right_Front.getCurrentPosition();
                    rightRTargPos = Right_Rear.getCurrentPosition();
                    Right_Front.setPower(left / 2);
                    Right_Rear.setPower(left / 2);
                    Left_Front.setPower(right / 2);
                    Left_Rear.setPower(right / 2);
                } else {
                    getYeeted = false;
                    Right_Front.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    Right_Rear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    Left_Front.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    Left_Rear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    leftFTargPos = Left_Front.getCurrentPosition();
                    leftRTargPos = Left_Rear.getCurrentPosition();
                    rightFTargPos = Right_Front.getCurrentPosition();
                    rightRTargPos = Right_Rear.getCurrentPosition();
                    Right_Front.setPower(left);
                    Right_Rear.setPower(left);
                    Left_Front.setPower(right);
                    Left_Rear.setPower(right);
                }
                break;

            case DEFENSE:
                if (timerdrive.seconds() < .5){
                    Right_Front.setPower(0);
                    Right_Rear.setPower(0);
                    Left_Front.setPower(0);
                    Left_Rear.setPower(0);
                    leftFTargPos = Left_Front.getCurrentPosition();
                    leftRTargPos = Left_Rear.getCurrentPosition();
                    rightFTargPos = Right_Front.getCurrentPosition();
                    rightRTargPos = Right_Rear.getCurrentPosition();
                } else {
                    Right_Front.setTargetPosition(rightFTargPos);
                    Right_Rear.setTargetPosition(rightRTargPos);
                    Left_Rear.setTargetPosition(leftRTargPos);
                    Left_Front.setTargetPosition(leftFTargPos);
                    Right_Front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    Right_Rear.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    Left_Front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    Left_Rear.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    Right_Front.setPower(1);
                    Right_Rear.setPower(1);
                    Left_Front.setPower(1);
                    Left_Rear.setPower(1);
                }
                if ((left != 0 || right != 0)){
                    driveState = DriveState.REGULAR;
                }
        }
    }
}