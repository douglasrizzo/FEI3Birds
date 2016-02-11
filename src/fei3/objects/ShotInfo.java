package fei3.objects;

import java.awt.Point;

/**
 * Information on a shot
 * 
 * @author Douglas De Rizo Meneghetti
 * 
 */
public class ShotInfo
{
	public enum AngleType
	{
		LOW, HIGH
	}

	private double angle;
	private Point releasePoint;
	private AngleType angleType;
	private double velocity;

	public ShotInfo(double angle, double velocity, Point releasePoint,
			AngleType angleType)
	{
		this.angle = angle;
		this.velocity = velocity;
		this.releasePoint = releasePoint;
		setAngleType(angleType);
	}

	public double getAngle()
	{
		return angle;
	}

	public AngleType getAngleType()
	{
		return angleType;
	}

	public Point getReleasePoint()
	{
		return releasePoint;
	}

	public double getVelocity()
	{
		return velocity;
	}

	public void setAngle(double angle)
	{
		this.angle = angle;
	}

	public void setAngleType(AngleType angleType)
	{
		this.angleType = angleType;
	}

	public void setReleasePoint(Point releasePoint)
	{
		this.releasePoint = releasePoint;
	}

	public void setVelocity(double velocity)
	{
		this.velocity = velocity;
	}
}
