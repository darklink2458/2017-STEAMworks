package org.usfirst.frc.team4915.steamworks.commands;

import org.usfirst.frc.team4915.steamworks.subsystems.Intake;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class IntakeThrottleCommand extends Command
{

    private final Intake m_intake;
    private final Joystick m_joystick;
    
    public IntakeThrottleCommand(Intake intake, Joystick joystick)
    {
        m_intake = intake;
        m_joystick = joystick;
        requires(m_intake);
    }

    @Override
    public void end()
    {
        m_intake.setIntake(false, 0);
    }

    @Override
    public void execute()
    {
        m_intake.setIntake(true, m_joystick.getAxis(AxisType.kThrottle));
    }

    @Override
    public void initialize()
    {
        
    }

    @Override
    public void interrupted()
    {
        end();
    }

    @Override
    public boolean isFinished()
    {
        return false;
    }
}
