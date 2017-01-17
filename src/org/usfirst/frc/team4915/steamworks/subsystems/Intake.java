package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Robot;
import org.usfirst.frc.team4915.steamworks.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Intake extends Subsystem
{

    private static final double INTAKE_SPEED = 0.75;

    private final CANTalon m_intakeMotor = new CANTalon(RobotMap.INTAKE_MOTOR);

    public Intake(Robot robot)
    {
        m_intakeMotor.changeControlMode(TalonControlMode.Speed);
    }

    @Override
    protected void initDefaultCommand()
    {

    }

    public void setIntake(boolean onOff)
    {
        if (onOff)
        {
            m_intakeMotor.set(INTAKE_SPEED);
        }
        else
        {
            m_intakeMotor.set(0);
        }

    }

}