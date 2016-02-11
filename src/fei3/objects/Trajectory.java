package fei3.objects;

import java.awt.Point;
import java.util.ArrayList;

import fei3.objects.Obstacle.Type;
import fei3.planner.FEITrajectoryPlanner;

/**
 * Trajectory from a {@link Bird} to a target {@link Point}
 * 
 * @author Douglas De Rizzo Meneghetti
 * 
 */
public class Trajectory implements Comparable<Trajectory>
{
	private int tapTime;
	private Bird bird;
	private ShotInfo shotInfo;
	private ArrayList<Point> points;
	private ArrayList<Obstacle> obstacles;
	private Point target;
	private Trajectory alternative;

	public Trajectory()
	{
		// TODO Auto-generated constructor stub
	}

	public Trajectory(Bird bird, Point target, int tapTime, ShotInfo shotInfo,
			ArrayList<Point> points, ArrayList<Obstacle> obstacles)
	{
		this.bird = bird;
		this.target = target;
		this.tapTime = tapTime;
		this.shotInfo = shotInfo;
		this.points = points;
		this.obstacles = obstacles;
	}

	@Override
	public int compareTo(Trajectory t)
	{
		return Double.compare(getWeight(), t.getWeight());
	}

	public boolean containsHills()
	{
		for (Obstacle ob : obstacles)
		{
			if (ob.getType() == Type.HILLS)
			{
				return true;
			}
		}
		return false;
	}

	public Trajectory getAlternative()
	{
		return alternative;
	}

	public Bird getBird()
	{
		return bird;
	}

	public ArrayList<Obstacle> getObstacles()
	{
		return obstacles;
	}

	public ArrayList<Point> getPoints()
	{
		return points;
	}

	public ShotInfo getShotInfo()
	{
		return shotInfo;
	}

	public int getTapTime()
	{
		return tapTime;
	}

	public Point getTarget()
	{
		return target;
	}

	public double getWeight()
	{
		return new FEITrajectoryPlanner().getTrajectoryWeight(this);
	}

	public void setAlternative(Trajectory alternative)
	{
		this.alternative = alternative;
	}

	public void setBird(Bird bird)
	{
		this.bird = bird;
	}

	public void setObstacles(ArrayList<Obstacle> obstacles)
	{
		this.obstacles = obstacles;
	}

	public void setPoints(ArrayList<Point> points)
	{
		this.points = points;
	}

	public void setShotInfo(ShotInfo shotInfo)
	{
		this.shotInfo = shotInfo;
	}

	public void setTapTime(int tapTime)
	{
		this.tapTime = tapTime;
	}

	public void setTarget(Point target)
	{
		this.target = target;
	}

}