package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;
import org.usfirst.frc.team4915.steamworks.commands.IntakeThrottleCommand;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake extends SpartronicsSubsystem
{

    private static final double INTAKE_SPEED = 0.75;
    private Joystick m_intakeStick;
    private CANTalon m_intakeMotor;
    private Logger m_logger;

    public Intake()
    {
        m_logger = new Logger("Intake", Logger.Level.DEBUG);
        try
        {
            
            m_intakeMotor = new CANTalon(RobotMap.INTAKE_MOTOR);
            m_intakeMotor.changeControlMode(TalonControlMode.Speed);
            m_logger.info("Intake initialized");
            
            m_intakeMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
            m_intakeMotor.reverseSensor(false);
            m_intakeMotor.configNominalOutputVoltage(+0.0f, -0.0f);
            m_intakeMotor.configPeakOutputVoltage(+12.0f, -12.0f);
            m_intakeMotor.setProfile(0);
            m_intakeMotor.setF(0);
            m_intakeMotor.setP(0);
            m_intakeMotor.setI(0);
            m_intakeMotor.setD(0);
            
        }
        catch (Exception e)
        {
            m_logger.exception(e, false);
            m_initialized = false;
        }
    }
    
    public void setIntakeStick(Joystick s)
    {
        m_intakeStick = s;
    }
    
    @Override
    public void initDefaultCommand()
    {
        setDefaultCommand(new IntakeThrottleCommand(this, m_intakeStick));
    }
    
    public double getMotorVoltage()
    {
        return m_intakeMotor.getOutputVoltage() / m_intakeMotor.getBusVoltage();
    }

    public void setIntake(boolean onOff, double joyStickAmount)
    {
        if (initialized())
        {
            if (onOff)
            {
                //m_logger.info("Turn the motor on");
                m_logger.info("\tout: " + m_intakeMotor.getOutputVoltage() / m_intakeMotor.getBusVoltage() + " " + m_intakeMotor.getSpeed() + " \tspd:");
                m_logger.info(" Terr: " + m_intakeMotor.getClosedLoopError() + " \ttrg: " + joyStickAmount );
                m_intakeMotor.set(joyStickAmount * 1500);
            }
            else
            {
                m_logger.info("Turn the motor off");
                m_intakeMotor.set(0);
            }
        }

    }
}
