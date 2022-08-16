package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

public abstract class scriminit extends New_Super_Class {
    public Drive drive;
    public IntakeScrim intakeScrim;
    public LiftScrim liftScrim;





    public double start_time;

    @Override
    public void inits() {
        drive = new Drive(this);
        intakeScrim = new IntakeScrim(this);
        liftScrim = new LiftScrim(this);
        while(!isStarted() && !isStopRequested() && !opModeIsActive()) {
            LiftScrim.winch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
    }
}
