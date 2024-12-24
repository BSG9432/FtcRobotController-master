package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;

@TeleOp
public class MainWithFieldCentric extends LinearOpMode {
    DcMotor lift1, FR, FL, BL, BR,lift2;
    Servo Srotate, intake,slide1,slide2,claw,bucket;
    BNO055IMU imu;
    @Override
    public void runOpMode() {
        // Declare our motors
        // Make sure your ID's match your configuration
        BR = hardwareMap.dcMotor.get("BR");
        BL = hardwareMap.dcMotor.get("BL");
        FR = hardwareMap.dcMotor.get("FR");
        FL = hardwareMap.dcMotor.get("FL");
        lift2 = hardwareMap.dcMotor.get("lift2");
        lift1 = hardwareMap.dcMotor.get("lift1");
        intake = hardwareMap.servo.get("intake");
        Srotate = hardwareMap.servo.get("Srotate");
        slide1 = hardwareMap.servo.get("slide1");
        slide2 = hardwareMap.servo.get("slide2");
        claw = hardwareMap.servo.get("claw");
        bucket = hardwareMap.servo.get("bucket");


        FR.setDirection(DcMotor.Direction.REVERSE);
        FL.setDirection(DcMotor.Direction.FORWARD);
        BR.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.FORWARD);
        lift2.setDirection(DcMotor.Direction.FORWARD);
        lift1.setDirection(DcMotor.Direction.REVERSE);

        Deadline gamepadRateLimit = new Deadline(500, TimeUnit.MILLISECONDS);

        // Retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double lx = gamepad1.left_stick_x;
            double ly = gamepad1.left_stick_y;
            double rx = gamepad1.right_stick_x;

            double max = Math.max(Math.abs(lx) + Math.abs(ly) + Math.abs(rx), 1);

            double drivePower = 0.8 - (0.6 * gamepad1.right_trigger);

            if (gamepadRateLimit.hasExpired() && gamepad1.a) {
                imu.resetYaw();
                gamepadRateLimit.reset();
            }

            double heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            double adjustedLx = -ly * Math.sin(heading) + lx * Math.cos(heading);
            double adjustedLy = ly * Math.cos(heading) + lx * Math.sin(heading);

            BR.setPower(((adjustedLy - adjustedLx + rx) / max) * drivePower);
            BL.setPower(((adjustedLy + adjustedLx - rx) / max) * drivePower);
            FR.setPower(((adjustedLy + adjustedLx + rx) / max) * drivePower);
            FL.setPower(((adjustedLy - adjustedLx - rx) / max) * drivePower);
            //what the sigma - Joel

            // lifts
            if (Math.abs(gamepad2.left_stick_y) > .2) {
                lift1.setPower(gamepad2.left_stick_y *1);
                lift2.setPower(gamepad2.left_stick_y *1);
            } else {
                lift1.setPower(0);
                lift2.setPower(0);
            }
            if (gamepad2.dpad_up) {
                slide1.setPosition(.6);// down positions
                slide2.setPosition(.4); // opposite of 1 and .1 off
                Srotate.setPosition(.975);
            }
            if (gamepad2.dpad_down) {
                slide1.setPosition(0); // up positions
                slide2.setPosition(1);
                Srotate.setPosition(.4);
            }
            if (gamepad2.a) {
                claw.setPosition(.3); // grab
            }
            if (gamepad2.x) {
                claw.setPosition(0); //open
            }
            if (gamepad2.b) {
                intake.setPosition(.75 ); //close
            }
            if (gamepad2.y) {
                intake.setPosition(.5); //open
            }
            if (gamepad2.dpad_left) {
                bucket.setPosition(1);
            } else {
                bucket.setPosition(.6);
            }
            if (gamepad2.dpad_right) {
                Srotate.setPosition(.975);
            }
            if (gamepad2.right_bumper) {
                lift1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                lift2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
        }
    }
}