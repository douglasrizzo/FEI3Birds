package fei3.planner;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import ab.planner.TrajectoryPlanner;
import fei3.objects.Bird;
import fei3.objects.Bird.Color;
import fei3.objects.Obstacle;
import fei3.objects.Obstacle.Type;
import fei3.objects.ShotInfo;
import fei3.objects.Trajectory;

public class FEITrajectoryPlanner extends TrajectoryPlanner
{
	/**
	 * Array containing the relation between birds and obstacles. Lines are
	 * {@link Bird} {@link Color}s (red, blue, yellow), columns are
	 * {@link Obstacle} {@link Type}s (wood, ice, stone, hills, high, low).
	 */
	private static final double[][] weights = { { 1, 6, 2, 100 },
												{ 3, 1, 6, 100 },
												{ 1, 6, 3, 100 } };

	// create a trajectory planner object
	public FEITrajectoryPlanner()
	{}

	/**
	 * Estimate launch points given a desired target point using maximum
	 * velocity. If there are two launch point for the target, they are both
	 * returned in the list (lower point, higher point). Note - angles greater
	 * than 75 are not considered.
	 * 
	 * @param slingshot
	 *            bounding rectangle of the slingshot
	 * @param targetPoint
	 *            coordinates of the target to hit
	 * 
	 * @return A list containing 2 possible release points
	 */
	public ArrayList<ShotInfo> estimateLaunchPointFEI(Rectangle slingshot,
			Point targetPoint)
	{
		// calculate relative position of the target (normalized)
		double scale = getSceneScale(slingshot);
		Point ref = getReferencePoint(slingshot);

		double x = (targetPoint.x - ref.x) / scale;
		double y = -(targetPoint.y - ref.y) / scale;

		double bestError = 1000;
		double theta1 = 0;
		double theta2 = 0;

		// first estimate launch angle using the projectile equation (constant
		// velocity)
		double v = _scaleFactor * _launchVelocity[6];
		double v2 = v * v;
		double v4 = v2 * v2;
		double tangent1 = (v2 - Math.sqrt(v4 - (x * x + 2 * y * v2))) / x;
		double tangent2 = (v2 + Math.sqrt(v4 - (x * x + 2 * y * v2))) / x;
		double t1 = actualToLaunch(Math.atan(tangent1));
		double t2 = actualToLaunch(Math.atan(tangent2));
		double velocityTheta1 = 0, velocityTheta2 = 0;

		// search angles in range [t1 - BOUND, t1 + BOUND]
		for (double theta = t1 - BOUND; theta <= t1 + BOUND; theta += 0.001)
		{
			velocityTheta1 = getVelocity(theta);
			// initial velocities
			double u_x = velocityTheta1 * Math.cos(theta);
			double u_y = velocityTheta1 * Math.sin(theta);

			// the normalised coefficients
			double a = -0.5 / (u_x * u_x);
			double b = u_y / u_x;

			// the error in y-coordinate
			double error = Math.abs(a * x * x + b * x - y);
			if (error < bestError)
			{
				theta1 = theta;
				bestError = error;
			}
		}

		bestError = 1000;

		// search angles in range [t2 - BOUND, t2 + BOUND]
		for (double theta = t2 - BOUND; theta <= t2 + BOUND; theta += 0.001)
		{
			velocityTheta2 = getVelocity(theta);

			// initial velocities
			double u_x = velocityTheta2 * Math.cos(theta);
			double u_y = velocityTheta2 * Math.sin(theta);

			// the normalised coefficients
			double a = -0.5 / (u_x * u_x);
			double b = u_y / u_x;

			// the error in y-coordinate
			double error = Math.abs(a * x * x + b * x - y);
			if (error < bestError)
			{
				theta2 = theta;
				bestError = error;
			}
		}

		theta1 = actualToLaunch(theta1);
		theta2 = actualToLaunch(theta2);

		System.out.println("Two angles: " + Math.toDegrees(theta1) + ", "
				+ Math.toDegrees(theta2));

		// add launch points to the list
		ArrayList<ShotInfo> pts = new ArrayList<ShotInfo>();
		pts.add(new ShotInfo(theta1, velocityTheta1, findReleasePoint(
				slingshot, theta1), ShotInfo.AngleType.LOW));

		// add the higher point if it is below 90 degrees and not same as first
		if (theta2 < Math.toRadians(90) && theta2 != theta1)
		{
			pts.add(new ShotInfo(theta2, velocityTheta2, findReleasePoint(
					slingshot, theta2), ShotInfo.AngleType.HIGH));
		}

		return pts;
	}

	public int getTapTime(double x, double v0, double theta)
	{
		double haha = Math.cos(theta);
		return (int) (x / (v0 * Math.cos(theta)));
	}

	public double getTrajectoryWeight(Trajectory t)
	{
		int aux = 0;
		for (Obstacle obstacle : t.getObstacles())
		{
			aux += getWeight(t.getBird().getColor(), obstacle.getType());
		}

		return aux;
	}

	/**
	 * Returns the relation between a {@link Bird}'s {@link Color} and an
	 * {@link Obstacle}'s {@link Type}. The smaller the number, the more
	 * effective is the bird against that type of obstacle.
	 * 
	 * @param bird
	 *            A bird
	 * @param obstacle
	 *            An obstacle
	 * @return A nonnegative integer representing the relation between a bird's
	 *         color and an obstacle's type
	 * 
	 */
	public double getWeight(Bird bird, Obstacle obstacle)
	{
		return getWeight(bird.getColor(), obstacle.getType());
	}

	/**
	 * Returns the relation between a {@link Bird}'s {@link Color} and an
	 * {@link Obstacle}'s {@link Type}. The smaller the number, the more
	 * effective is the bird against that type of obstacle.
	 * 
	 * @param color
	 *            The color of a bird
	 * @param type
	 *            The type of an obstacle
	 * @return A nonnegative integer representing the relation between a bird's
	 *         color and an obstacle's type
	 * 
	 */
	public double getWeight(Bird.Color color, Obstacle.Type type)
	{
		return weights[color.getValue()][type.getValue()];
	}

	/**
	 * Predicts a trajectory
	 * 
	 * @param slingshot
	 *            bounding rectangle of the slingshot
	 * @param releasePoint
	 * @return
	 */
	public List<Point> predictTrajectory(Rectangle slingshot, Point releasePoint)
	{
		setTrajectory(slingshot, releasePoint);
		return _trajectory;
	}
}