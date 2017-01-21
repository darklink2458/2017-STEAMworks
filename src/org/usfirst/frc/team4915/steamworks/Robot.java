
package org.usfirst.frc.team4915.steamworks;

import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;
import org.usfirst.frc.team4915.steamworks.subsystems.Intake;

import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot
{
    public Logger m_logger;

    private Drivetrain m_drivetrain;
    private Intake m_intake;
    private OI m_oi;
    
    @Override
    public void robotInit()
    {
        m_logger = new Logger("Robot", Logger.Level.DEBUG);
        m_intake = new Intake();
        m_drivetrain = new Drivetrain();
        m_oi = new OI(this); // make sure OI is last
    }

    @Override
    public void robotPeriodic()
    {
        // This is invoked in all periodic cases in addition to the other active periodic mode.
        // We implement this method in order to quell the "unimplemented" message.
        // Currently we have no good reason to put code here...
    }

    public Intake getIntake()
    {
        return m_intake;
    }
    
    public Drivetrain getDrivetrain()
    {
        return m_drivetrain;
    }

    @Override
    public void autonomousInit()
    {
        Command acmd = m_oi.getAutoCommand();
        if (acmd != null)
        {
            acmd.start();
        }
    }
    
    @Override
    public void autonomousPeriodic()
    {
        Scheduler.getInstance().run();
    }

    @Override
    public void disabledInit()
    {

    }

    @Override
    public void disabledPeriodic()
    {
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit()
    {
        Command acmd = m_oi.getAutoCommand();
        if (acmd != null)
        {
            acmd.cancel();
        }
    }

    @Override
    public void teleopPeriodic()
    {
        Scheduler.getInstance().run();
    }

    @Override
    public void testPeriodic()
    {
        LiveWindow.run();
    }
}
