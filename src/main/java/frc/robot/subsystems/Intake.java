package frc.robot.subsystems;

import frc.robot.Constants;
import com.revrobotics.spark.SparkMax;
//import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkLimitSwitch;

public class Intake {
    public Double inPosition;
    public SparkMax topIntake = new SparkMax(Constants.intakeTopMotorID, MotorType.kBrushless);
    public SparkMax bottomIntake = new SparkMax(Constants.intakeBottomMotorID, MotorType.kBrushless);
    public SparkMax intakePivot = new SparkMax(Constants.intakePivotMotorID, MotorType.kBrushless);
    public RelativeEncoder intakeEncoder = intakePivot.getEncoder();
    
    public SparkLimitSwitch insideSwitch = intakePivot.getForwardLimitSwitch();
    public SparkLimitSwitch outsideSwitch = intakePivot.getReverseLimitSwitch();

    // clamp function (copy-pasted from elevator section)
    public static Double clamp(Double minimum, Double maximum, Double input){
        if (input < minimum)
            return minimum;
        if (input > maximum)
            return maximum;
        return input;
    }

    // calculate gravity comp for the intake
	// change the .1, define inPosition
    public Double intakeGravity(){
		Double encoderInput = intakeEncoder.getPosition();
        Double rotations = inPosition-encoderInput;
        return Math.sin(Math.toRadians(rotations)) / Math.pow(intakePivot.getOutputCurrent(), 2) * 0.1;
    }

    // function for retracting the intake for algae and coral
	// change gravityComp and power limits/scaling
	public Boolean retractIntake(char coralAlgae){
        Double maxPower = 0.5, minPower = -0.5, power, position = intakeEncoder.getPosition();
        Boolean outside = true;
            
        // using limit switches
        if (outsideSwitch.isPressed()){
            minPower = 0.0;
        }
        if (insideSwitch.isPressed()){
            minPower = 0.0;
            maxPower = 0.0;
            outside = false;
        
        
        // using or not using the bottom intake motor depending on after algae or coral
        spinRollers(0.0);
        if (coralAlgae == 'A' && !insideSwitch.isPressed())
            bottomIntake.set(0.1);
        }
        
        // top roller shouldn't have power, setting pivot motor to the appropriate power
        topIntake.set(0);
        power = inPosition-position+intakeGravity(); // pivot power based linearly on error + gravity comp
        intakePivot.set(Intake.clamp(minPower, maxPower, power));
        return outside;
    }

    //function to bring intake to a position
    //constants prob not right
    public void intakeTo(String destination){
        // processing the input string to find the correct destination
        Double desiredPosition = 0.0;
        if (destination == "intakeAlgae")
            desiredPosition = inPosition + 0.1;
            if (bottomIntake.getOutputCurrent() > 2.5)
                bottomIntake.set(0.1);
            else
                bottomIntake.set(0.5);
        if (destination == "stationIntakeCoral")
            desiredPosition = inPosition + 0.07;
        if (destination == "l1Outtake")
            desiredPosition = inPosition + 0.085;
        Double position, maxPower = 0.5, minPower = -0.5, power;
        position = intakeEncoder.getPosition();
        
        // using limit switches
        if (insideSwitch.isPressed())
            maxPower = 0.0;
        if (outsideSwitch.isPressed())
            minPower = 0.0;
            
        // setting power of the pivot motor
        power = desiredPosition-position+intakeGravity(); // pivot power based linearly on error + gravity comp
        intakePivot.set(clamp(minPower, maxPower, power));
    }

    // function for intaking coral
    // change constants!!!!
    public void intakeCoral(Boolean reversing){
        Double position = intakeEncoder.getPosition();
        Double maxPower = 0.5, minPower = -0.5, power, rollerPower = -0.5, desiredPosition = inPosition+0.4;

        // handle limit switches
        if (insideSwitch.isPressed())
            maxPower = 0.0;
        if (outsideSwitch.isPressed()){
            minPower = 0.0;
            maxPower = 0.0;
        }else
            // not using the rollers when not out to conserve power
            rollerPower = 0.0;

        // handling reverse intake button
        if (reversing)
            rollerPower *= -1;

        // set roller and pivot motor speeds
        spinRollers(rollerPower);
        power = desiredPosition-position+intakeGravity(); // pivot power based linearly on error + gravity comp
        intakePivot.set(clamp(minPower, maxPower, power)); 
    }

    // function for literally just spinning the rollers
    void spinRollers(Double speed){
        // limiting power if it is using a lot of power
        if (bottomIntake.getOutputCurrent() > 2.5)
            speed = 0.1;
        bottomIntake.set(speed);
        topIntake.set(speed);
    }
}
