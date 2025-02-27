package frc.robot.subsystems;

import frc.robot.Constants;
import pabeles.concurrency.ConcurrencyOps.Reset;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.units.measure.Current;
//import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkLimitSwitch;

public class Intake {
    public Double inPosition;
    private SparkMax topIntake = new SparkMax(Constants.intakeTopMotorID, MotorType.kBrushless);
    private SparkMax bottomIntake = new SparkMax(Constants.intakeBottomMotorID, MotorType.kBrushless);
    private SparkMax transportPivot = new SparkMax(Constants.intakePivotMotorID, MotorType.kBrushless);
    private SparkMax intakePivot = new SparkMax(Constants.intakePivotMotorID, MotorType.kBrushless);
    private RelativeEncoder transportEncoder = intakePivot.getEncoder();
    private RelativeEncoder intakeEncoder = intakePivot.getEncoder();
    private char intakeLastUsed;
    private String currentState = "none"; 
    private Integer timer = 0;
    public Boolean retractNeeded = false, movingCoral = false;
    
    private SparkLimitSwitch insideSwitch = intakePivot.getForwardLimitSwitch();
    private SparkLimitSwitch outsideSwitch = intakePivot.getReverseLimitSwitch();

    // clamp function (copy-pasted from elevator section)
    private static Double clamp(Double minimum, Double maximum, Double input){
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
	public Boolean retractIntake(){
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
            movingCoral = true;
        
        // using or not using the bottom intake motor depending on after algae or coral
        spinRollers(0.0);
        if (intakeLastUsed == 'A' && !insideSwitch.isPressed())
            bottomIntake.set(0.1);
        }

        power = inPosition-position+intakeGravity(); // pivot power based linearly on error + gravity comp
        intakePivot.set(Intake.clamp(minPower, maxPower, power));
        if (outside = false && intakeLastUsed == 'C')
            currentState = "none";
        return outside;
    }

    //function to bring intake to a position
    //constants prob not right
    public void intakeTo(String destination){
        // processing the input string to find the correct destination
        Double desiredPosition = 0.0;
        movingCoral = true;
        if (destination == "intakeAlgae")
            intakeLastUsed = 'A';
            desiredPosition = inPosition + 0.1;
            if (bottomIntake.getOutputCurrent() > 2.5)
                bottomIntake.set(0.1);
            else
                bottomIntake.set(0.5);
        if (destination == "stationIntakeCoral")
            intakeLastUsed = 'C';
            desiredPosition = inPosition + 0.07;
        if (destination == "l1Outtake")
            intakeLastUsed = 'C';
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

        intakeLastUsed = 'C';
		movingCoral = true;

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

    // function for putting the coral in the elevator
    Boolean moveCoral(){
	Double minPower = -0.7, maxPower = 0.7;
	if (elevator.elevatorto(elevator.bottomPosition) && transportEncoder.getPosition() <= 0.05){
        switch (currentState){
		    case ("start"): // start
			    timer = 0;
		    	currentState = "run belt";
	    		break;
	        case ("run belt"): // run belt
                ++timer;
	    		transportPivot.set(0.5);
	    		if (timer >= 50*3 || coral detected in arm) // 3 seconds
	    			Current state = use interior arm;
			break;
		case ('T'): // use transport arm
			If (belt-side limit switch activated){
				minPower = 0;
			}
			If (elevator-side limit switch pressed){
				Interior arm.set(0);
				Current state = return interior arm;
break;
			}
			Calculate gravity comp;
			Interior arm.set(clamp(minPower, maxPower, gravitycomp+error);
			Break;
		case ('R'): // return transport arm
			If (belt-side limit switch activated){
				Interior arm.set(0);
				Current state = none;
break;
			}
			If (elevator-side limit switch pressed){
				maxPower = 0;
			}
			Calculate gravity comp;
			Interior arm.set(clamp(minPower, maxPower, gravitycomp-error);
			Break;
		    }
	    }
    }
}
