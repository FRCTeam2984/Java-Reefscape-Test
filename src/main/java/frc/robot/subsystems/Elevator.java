// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Elevator extends SubsystemBase {
  public TalonFX elevatorMotor = new TalonFX(Constants.elevatorMotorID);
  Double bottomPosition = 0.0;

	// function for keeping a variable between a lower and upper limit
  private static Double clamp(Double minimum, Double maximum, Double input){
    if (input < minimum)
      return minimum;
    if (input > maximum)
      return maximum;
    return input;
  }

  // function for moving elevator (you can change function name, i don’t care)
  // change the math for the units of distance, power at different positions, gravity compensation
	public Boolean elevatorTo(Double destination){
    String rawInput = elevatorMotor.getRotorPosition().toString();
    Double position = Double.parseDouble(rawInput.substring(0, 10)) - bottomPosition;
     
		// convert destination from input units to encoder counts
		Double encoderDestination = destination * 0.1;//CHANGE THIS
    Double minPower = -0.9, maxPower = 0.9, gravityComp = 0.0, error = encoderDestination - position, power;
		Integer maxError = 5; // change gravityComp
		Boolean closeEnough = false;
			
		// set motor power based on error or set it to keep position
		if (Math.abs(error) > maxError)
			power = error/100 + gravityComp;
    else {
	    power = gravityComp;
	    closeEnough = true;
    }

    // Clamp power and use limit switches
    if (elevatorMotor.getReverseLimit().getValue().toString() == "ClosedToGround")
      power = Elevator.clamp(0.0, maxPower, power);
    else if (elevatorMotor.getForwardLimit().getValue().toString() == "ClosedToGround")
      power = Elevator.clamp(minPower, 0.0, power);
    else
      power = Elevator.clamp(minPower, maxPower, power);

    // set motor power and return whether it is close enough
    elevatorMotor.set(power);
    return closeEnough;
  }
}
