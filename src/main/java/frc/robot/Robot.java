package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {

  VictorSPX left1 = new VictorSPX(4);
  VictorSPX left2 = new VictorSPX(3);
  VictorSPX right1 = new VictorSPX(2);
  VictorSPX right2 = new VictorSPX(1);

  SparkMax Esteira = new SparkMax(5, MotorType.kBrushed);

  NetworkTable phone;

  @Override
  public void robotInit() {
    phone = NetworkTableInstance.getDefault().getTable("phone");
    right1.setInverted(true);
    right2.setInverted(true);
  }

  @Override
  public void teleopPeriodic() {

    double x = phone.getEntry("leftX").getDouble(0);
    double y = phone.getEntry("leftY").getDouble(0);
    double speed = phone.getEntry("speed").getDouble(0.6);

    double left = (y + x) * speed;
    double right = (y - x) * speed;

    VictorSPX esteira = new VictorSPX(5);   // canal que você usar

// dentro de teleopPeriodic()
double esteiraPower = phone.getEntry("esteira").getDouble(0);
MathUtil.clamp(esteiraPower, -0.5, 0.5); 
esteira.set(ControlMode.PercentOutput, esteiraPower);

    left1.set(ControlMode.PercentOutput, left);
    left2.set(ControlMode.PercentOutput, left);
    right1.set(ControlMode.PercentOutput, right);
    right2.set(ControlMode.PercentOutput, right);
  }
}
