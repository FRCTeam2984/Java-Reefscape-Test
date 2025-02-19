import edu.wpi.first.wpilibj.DigitalInput;
import com.ctre.phoenix6.hardware.TalonFX; 
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonFXConfiguration;

public class Climb {
public static void Climb(TalonFX(device ID), DigitalInput(number), Encoder(number))		// not right
TalonFX climbMotor = new TalonFX(device id);
DigitalInput limitUnwindedForward = new DigitalInput(number);
DigitalInput limitUnwindedBackward = new DigitalInput(number);
// extendButton creation
// retractButton creation
Encoder climbEncoder = new Encoder(number);

public void letsClimb(){
if(isButtonPressed(extendButton) && System.currentTimeMillis() > 1){
	servo.releaseRatchet;
	Thread.sleep(500);
	if(climbEncoder < NUMBER)
climbMotor.set(ControlMode.PercentOutput, 0.01);		 // climb motor unwind, idk what direction is wind and unwind (invert?)
	if(limitUnwindedBackward.get() || limitUnwindedForward.get())		// limit switches, use kyle’s limits
		climbMotor.set(ControlMode.PercentOutput, 0);  // not sure what will happen, put in as a guess
	// find out led code -> LED light flashes or solid a specific color
}
if(isButtonPressed(retractButton)){
	climbMotor.set(ControlMode.PercentOutput, 0.01);		 // climb motor wind to climb, idk what direction is wind and unwind (invert?)
if(limitUnwindedBackward.get() || limitUnwindedForward.get()) 		 // limit switches, use kyle’s limits
		climbMotor.set(ControlMode.PercentOutput, 0);  // not sure what will happen, put in as a guess
}
if(isButtonPressed(extendButton))
	servo.releaseRachet;
else
	servo.engageRatchet;
	}
}
