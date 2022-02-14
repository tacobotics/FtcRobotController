package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

public abstract class teleinit extends New_Super_Class {
    public Drive drive;
    public Intake intake;
    public LiftClean liftClean;





    public double start_time;

    @Override
    public void inits() {
        drive = new Drive(this);
        intake = new Intake(this);
        liftClean = new LiftClean(this);
        while(!isStarted() && !isStopRequested() && !opModeIsActive()) {
            LiftClean.winch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
    }
}
