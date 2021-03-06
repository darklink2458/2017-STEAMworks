package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Launcher extends SpartronicsSubsystem
{

    public static enum LauncherState
    {
        OFF,
        ON,
        SINGLE,
        UNJAM
    }

    //the "perfect" static speed that always makes goal
    public static final double DEFAULT_LAUNCHER_SPEED = 3000; //3000 rpm (CtreMagEncoder) Since it is CTRE, it is able to program its RPM itself
    public static final double DEFAULT_AGITATOR_SPEED = .4; //40 rpm (CtreMagEncoder)
    private CANTalon m_launcherMotor;
    private CANTalon m_agitatorMotor;
    private Logger m_logger;
    private LauncherState m_state;
    private double m_initialPos;
    private int m_startupCount;
    private int m_jamCount;
    private int m_allowableError = 4096 * 2 / (60 * 10); // 4096 nu/rev * 5 rpm and then convert to NU/100ms

    public Launcher()
    {
        m_logger = new Logger("Launcher", Logger.Level.DEBUG);
        try
        {
            m_state = LauncherState.OFF;
            m_startupCount = 0;
            m_launcherMotor = new CANTalon(RobotMap.LAUNCHER_MOTOR);
            m_launcherMotor.setAllowableClosedLoopErr(m_allowableError); //4096 Native Units per rev * 5 revs per min
            m_launcherMotor.changeControlMode(TalonControlMode.Speed);
            m_launcherMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
            m_launcherMotor.reverseSensor(false);
            m_launcherMotor.configNominalOutputVoltage(0.0f, -0.0f);
            m_launcherMotor.configPeakOutputVoltage(12.0f, -12.0f);
            //m_launcherMotor.setVoltageRampRate(48); // 120
            //m_launcherMotor.setCloseLoopRampRate(48); // 120

            // changeable fpid values in smartdashboard
            /*
             * m_launcherMotor.setF(.0305); // (1023)/Native Units Per 100ms.
             * See Talon Reference Manual pg 77
             * m_launcherMotor.setP(0); //(.09 currently) (Proportion off target
             * speed * 1023) / Worst Error //.03188 BASE
             * m_launcherMotor.setI(0); // (.0009 currently) start at 1 / 100th
             * of P gain
             * m_launcherMotor.setD(0);
             */

            m_agitatorMotor = new CANTalon(RobotMap.AGITATOR_MOTOR);
            m_agitatorMotor.changeControlMode(TalonControlMode.PercentVbus);
            m_agitatorMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
            m_agitatorMotor.configNominalOutputVoltage(0.0f, -0.0f);
            m_agitatorMotor.configPeakOutputVoltage(12.0f, -12.0f);
            m_agitatorMotor.reverseSensor(false);

            /*
             * m_agitatorMotor.setF(.00061);
             * m_agitatorMotor.setP(0.06);
             * m_agitatorMotor.setI(0.0);
             * m_agitatorMotor.setD(0.6);
             */

            m_logger.info("Launcher initialized");
        }
        catch (Exception e)
        {
            m_logger.exception(e, false);
            m_initialized = false;
        }
    }

    //Sets Launcher and Agitator to set speeds regarding state given from buttons
    public void setLauncher(LauncherState state)
    {
        if (initialized())
        {
            if (m_state != state && state == LauncherState.ON)
            {
                m_startupCount = 0;
                m_logger.debug("startupCount = 0");
            }
            m_state = state;
            double speedTarget = SmartDashboard.getNumber("Launcher_TGT", Launcher.DEFAULT_LAUNCHER_SPEED);
            double agitatorSpeedTarget = SmartDashboard.getNumber("Agitator_TGT", Launcher.DEFAULT_AGITATOR_SPEED);
            double speedActual = m_launcherMotor.getSpeed();
            SmartDashboard.putNumber("Launcher_ACT", speedActual);
            String msg = String.format("%.0f / %.0f", speedActual, speedTarget);
            SmartDashboard.putString("Launcher_MSG", msg);
            m_launcherMotor.set(getLauncherSpeed(speedTarget));
            m_agitatorMotor.set(getAgitatorSpeed(agitatorSpeedTarget));
        }
    }

    public double getLauncherSpeed(double speedTarget)
    {
        switch (m_state)
        {
            case ON:
                return speedTarget;
            case OFF:
                return 0;
            case SINGLE:
                return speedTarget;
            case UNJAM:
                return speedTarget;
        }
        return 0;
    }

    public double getAgitatorSpeed(double speedTarget)
    {
        switch (m_state)
        {
            case ON:
                if (isLauncherAtSpeed())
                {
                    if (isJammed())
                    {
                        return -speedTarget;
                    }
                    else
                    {
                        return speedTarget;
                    }
                }
                else
                {
                    return 0;
                }
            case OFF:
                return 0;
            case SINGLE:
                if (isSingleShotDone() || !isLauncherAtSpeed())
                {
                    return 0;
                }
                else
                {
                    return speedTarget; //returns a value of speedTarget - 1/10th of the maximum voltage
                }
            case UNJAM:
                if (!isJammed())
                {
                    m_state = LauncherState.ON;
                    return speedTarget;
                }
                else
                {
                    return -speedTarget;
                }
        }
        return 0;
    }

    private boolean isLauncherAtSpeed()
    {
        final double epsilon = 100; // allow 400 RPM of slop.
        double speedTarget = SmartDashboard.getNumber("Launcher_TGT", Launcher.DEFAULT_LAUNCHER_SPEED);
        double speedActual = m_launcherMotor.getSpeed();
        if (speedActual >= speedTarget - epsilon || speedActual <= speedTarget + epsilon)
        {
            m_startupCount++;
            if (m_startupCount < 15)
            {
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean isSingleShotDone()
    {
        double CurrentPosition = m_agitatorMotor.getPulseWidthPosition();
        m_logger.debug("isSingleShotDone: Current Position: " + CurrentPosition + " Initial Position: " + m_initialPos);
        if (CurrentPosition >= (m_initialPos + 1024)) /// 1024 native units, 1/4 rotation
        {
            return true;
        }
        return false;
    }

    private boolean isJammed() 
    {
        if (this.getRpm() < 10)
        {
            m_jamCount++;
            if (m_jamCount < 15)  // We are waiting for the reversed velocity to have an effect
            {
                return false;
            }
            else if (m_jamCount > 30) // If it doesn't work at this time, set speed to normal direction
            {
                m_jamCount = 0;
                return false;
            }
            else
            {
                return true;
            }
        }
        m_jamCount = 0;
        return false;
    }

    private double getRpm()  
    {
        double currentAgitatorSpeed = m_agitatorMotor.getEncVelocity(); // Assumed to be in ticks/100ms
        return currentAgitatorSpeed * 600.0 / 4096.0;
    }

    public void setAgitatorTarget()
    {
        m_initialPos = m_agitatorMotor.getPulseWidthPosition();
        m_logger.debug("setAgitatorTarget: Initial position is " + m_initialPos);
    }

    public double getInitialPos()
    {
        return m_initialPos;
    }

    public CANTalon getAgitator()
    {
        return m_agitatorMotor;
    }

    public boolean isEmpty()
    {
        double CurrentPosition = m_agitatorMotor.getPulseWidthPosition();
        if (CurrentPosition >= (m_initialPos + 4096 * 4))
        {
            return true;
        }
        return false;

    }

    public void initDefaultCommand()
    {
        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());

    }
}
