package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.LiftAuto.left;
import static org.firstinspires.ftc.teamcode.LiftAuto.liftRTP;
import static org.firstinspires.ftc.teamcode.LiftAuto.moveOn;
import static org.firstinspires.ftc.teamcode.LiftAuto.right;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "ScrimAuto", preselectTeleOp = "ScrimTele")
public class ScrimAuto extends autoinit {

    public enum AutoState{
        DRIVE1,
        LIFT,
        DRIVE2
    }

    static AutoState autoState = AutoState.DRIVE1;

    @Override
    public void run() {
        waitForStart();
        if (opModeIsActive()) {
            moveOn = false;
            autoState = AutoState.DRIVE1;
            while (!isStopRequested()){
                telemetry.addData("left", VisionPipeline.avg1);
                telemetry.addData("right", VisionPipeline.avg2);
                telemetry.update();
                autoGo();
            }
        }
    }
    public void autoGo() {
        switch (autoState) {
            case DRIVE1:
                liftAuto.finger.setPosition(1);
                if (delay){
                    sleep(12000);
                }
                drive.go_same(815, .5);
                sleep(100);
                intake.take.setPower(-.5);
                liftAuto.upTake.setPosition(LiftAuto.takeDown);
                sleep(1400);
                intake.take.setPower(0);
                autoState = AutoState.LIFT;
                return;

            case LIFT:
                liftAuto.armState();
                if (moveOn){
                    autoState = AutoState.DRIVE2;
                }
                return;

            case DRIVE2: //left/right connotates which direction the turret goes
                if (blue && left) {
                    liftAuto.upTake.setPosition(LiftAuto.takeUp);
                    drive.right(-375, .5);
                    sleep(2000);
                    drive.go_same(-3500, .5);
                    liftRTP(0, 3000);
                    sleep(3000);
                    stop();
                } else if (blue && right) {
                    liftAuto.upTake.setPosition(LiftAuto.takeUp);
                    drive.right(-375, .5);
                    sleep(2000);
                    drive.go_same(-2100, .5);
                    liftRTP(0, 1);
                    sleep(2500);
                    stop();
                } else if (!blue && right){
                    liftAuto.upTake.setPosition(LiftAuto.takeUp);
                    drive.right(425, .5);
                    sleep(2000);
                    drive.go_same(-3500, .5);
                    liftRTP(0, 1);
                    sleep(3000);
                    stop();
                } else if (!blue && left){
                    liftAuto.upTake.setPosition(LiftAuto.takeUp);
                    drive.right(375, .5);
                    sleep(2000);
                    drive.go_same(-2100, .5);
                    liftRTP(0, 1);
                    sleep(2500);
                    stop();
                }
        }
    }
}