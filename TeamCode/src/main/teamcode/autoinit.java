package org.firstinspires.ftc.teamcode;

//import android.opengl.Visibility;

import static org.firstinspires.ftc.teamcode.VisionPipeline.avg1;
import static org.firstinspires.ftc.teamcode.VisionPipeline.avg2;
import static org.firstinspires.ftc.teamcode.VisionPipeline.visionThresh;

import android.webkit.WebStorage;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

public abstract class autoinit extends New_Super_Class {
    public Drive drive;
    public Intake intake;
    public LiftAuto liftAuto;

    public boolean pressPause;

    static boolean blueDuck;

    public VisionPipeline vision;
    OpenCvCamera webcam;

    @Override
    public void inits() {
        drive = new Drive(this);
        intake = new Intake(this);
        liftAuto = new LiftAuto(this);

        vision = new VisionPipeline();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "webcam"), cameraMonitorViewId);

        webcam.openCameraDevice();
        webcam.setPipeline(vision);
        webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);

        while (!isStarted() && !isStopRequested() && !opModeIsActive()) {
            Spot();
        }
        while  (isStopRequested()){
            webcam.closeCameraDevice();
        }
    }

    public void Spot() {
        while (!isStarted()) {
            if (gamepad1.x) {
                blue = true;
                blueDuck = true;
            } else if (gamepad1.b) {
                blue = false;
                blueDuck = false;
            } else if (gamepad1.dpad_left) {
                LiftAuto.right = true;
                LiftAuto.left = false;
                LiftAuto.up = true;
            } else if (gamepad1.left_trigger > 0.3) {
                delay = false;
            } else if (gamepad1.right_trigger > 0.3) {
                delay = true;
            } else if (gamepad1.dpad_right) {
                LiftAuto.right = false;
                LiftAuto.left = true;
                LiftAuto.up = true;
            }
            telemetry.addData(">", "Please set up auto...");
            if (blue) {
                telemetry.addLine("Blue auto");
            } else {
                telemetry.addLine("Red auto");
            }
            if (LiftAuto.right) {
                telemetry.addLine("Left side");
            } else {
                telemetry.addLine("Right side");
            }
            if (avg1 < visionThresh){
                if(blue && LiftAuto.left){
                    LiftAuto.liftLevel = LiftAuto.LiftLevel.LOW;
                } else if (blue) {
                    LiftAuto.liftLevel = LiftAuto.LiftLevel.MID;
                } else if (LiftAuto.right){
                    LiftAuto.liftLevel = LiftAuto.LiftLevel.MID;
                } else if (LiftAuto.left){
                    LiftAuto.liftLevel = LiftAuto.LiftLevel.LOW;
                }
            } else if (avg2 < visionThresh){
                    if(blue && LiftAuto.left){
                        LiftAuto.liftLevel = LiftAuto.LiftLevel.MID;
                    } else if (blue) {
                        LiftAuto.liftLevel = LiftAuto.LiftLevel.HIGH;
                    } else if (LiftAuto.right){
                        LiftAuto.liftLevel = LiftAuto.LiftLevel.HIGH;
                    } else if (LiftAuto.left){
                        LiftAuto.liftLevel = LiftAuto.LiftLevel.MID;
                    }
            } else {
                if(blue && LiftAuto.left){
                    LiftAuto.liftLevel = LiftAuto.LiftLevel.HIGH;
                } else if (blue) {
                    LiftAuto.liftLevel = LiftAuto.LiftLevel.LOW;
                } else if (LiftAuto.right){
                    LiftAuto.liftLevel = LiftAuto.LiftLevel.LOW;
                } else if (LiftAuto.left){
                    LiftAuto.liftLevel = LiftAuto.LiftLevel.HIGH;
                }
            }
            if (gamepad1.dpad_up){
                if (!pressPause){
                    visionThresh++;
                    pressPause = true;
                }
            } else if (gamepad1.dpad_down){
                if (!pressPause){
                    visionThresh--;
                    pressPause = true;
                }
            } else {
                pressPause = false;
            }
            telemetry.addData("Delay", delay);
            telemetry.addData("left", avg1);
            telemetry.addData("right", avg2);
            telemetry.addData("visionThresh", visionThresh);
            telemetry.addData("lift level", LiftAuto.liftLevel);
            telemetry.update();
        }
    }
}