package frc.robot.subsystems;

import frc.robot.Constants;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkLimitSwitch;

public class Intake {
    Double inPosition = 0.0;
    SparkMax topIntake = new SparkMax(Constants.intakeTopMotorID, MotorType.kBrushless);
    SparkMax bottomIntake = new SparkMax(Constants.intakeBottomMotorID, MotorType.kBrushless);
    SparkMax intakePivot = new SparkMax(Constants.intakePivotMotorID, MotorType.kBrushless);
    RelativeEncoder intakeEncoder = intakePivot.getEncoder();
    SparkLimitSwitch insideSwitch = intakePivot.getForwardLimitSwitch();
    SparkLimitSwitch outsideSwitch = intakePivot.getForwardLimitSwitch();

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
            bottomIntake.set(0);
        }else{
            // using or not using the bottom intake motor depending on after algae or coral
            if (coralAlgae == 'A'){
                bottomIntake.set(0.1);
            }else if (coralAlgae == 'C'){
                bottomIntake.set(0);
            }
        } 
        
        // top roller shouldn't have power, setting pivot motor to the appropriate power
        topIntake.set(0);
        power = inPosition-position+intakeGravity();
        intakePivot.set(Intake.clamp(minPower, maxPower, power));
        return outside;
    }

    //function to intake the algae
    //constants prob not right
    void intakeAlgae(Boolean reversed){
        Double position, maxPower = 0.5, minPower = -0.5, power, rollerPower = -0.5, desiredPosition = inPosition+0.1;
        position = intakeEncoder.getPosition();
        
        // using limit switches
        if (insideSwitch.isPressed())
            maxPower = 0.0;
        if (outsideSwitch.isPressed())
            minPower = 0.0;

        // setting the bottom roller to intake algae or limiting its power if it is using a lot of power (holding algae)
        if (intakePivot.getOutputCurrent() > 2.5)
            rollerPower = 0.1;
        bottomIntake.set(rollerPower);
            
        // setting power of the pivot motor
        power = desiredPosition-position+intakeGravity();
        intakePivot.set(clamp(minPower, maxPower, power));
        return;
    }
}
