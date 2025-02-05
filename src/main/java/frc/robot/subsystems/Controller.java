package frc.robot.subsystems;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Constants;

public class Controller {
    public static final XboxController m_controller = new XboxController(Constants.kOperatorControllerPort);
    public Boolean lowerElevator(){
        return m_controller.getBButton();
    }  
    public Boolean raiseElevator(){
        return m_controller.getYButton();
    }
    public Boolean algaeSwitch(){
        return m_controller.getRightBumperButton();
    }
    public Boolean coralButton(){
        return m_controller.getLeftBumperButton();
    }
}
