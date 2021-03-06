package fei3.agent;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ab.demo.other.ActionRobot;
import ab.demo.other.Env;
import ab.demo.other.Shot;
import ab.demo.util.StateUtil;
import ab.vision.GameStateExtractor.GameState;
import fei3.objects.Bird;
import fei3.objects.Bird.Color;
import fei3.objects.Obstacle;
import fei3.objects.Obstacle.Type;
import fei3.objects.ShotInfo;
import fei3.objects.ShotInfo.AngleType;
import fei3.objects.Trajectory;
import fei3.planner.FEITrajectoryPlanner;
import fei3.vision.FEIVision;

/**
 * Angry Birds agent based on the NaiveAgent available at www.aibirds.org
 * 
 * @author Douglas De Rizzo Meneghetti
 * 
 */
public class FEI3Agent implements Runnable
{
	public static void main(String args[])
	{
		FEI3Agent fei3Agent = new FEI3Agent();
		fei3Agent.run();
	}

	private int currentLevel = 1, currentBird = 1;
	private ActionRobot ar;
	private FEITrajectoryPlanner tp;
	private List<Bird> birds;
	private Point previousTarget;

	public FEI3Agent()
	{
		ar = new ActionRobot();
		tp = new FEITrajectoryPlanner();
		previousTarget = null;
		currentBird = 1;
		// currentLevel = 18;
		// --- go to the Poached Eggs episode level selection page ---
		ActionRobot.GoFromMainMenuToLevelSelection();
	}

	/**
	 * Builds a {@link Trajectory} object given all the objects that compose it
	 * 
	 * @param bird
	 * @param pig
	 * @param sling
	 * @param target
	 * @param shotInfo
	 * @param obstacles
	 * @param points
	 * @return
	 */
	private Trajectory buildTrajectory(Bird bird, Rectangle pig,
			Rectangle sling, Point target, ShotInfo shotInfo,
			List<Obstacle> obstacles, ArrayList<Point> points)
	{
		Trajectory t;
		boolean hasHills = false, gotFirstObstacle = false;
		ArrayList<Obstacle> obcsInTrajectory = new ArrayList<Obstacle>();
		int tapTime = 0;

		Collections.sort(obstacles);

		bigFor: for (Point point : points)
		{
			for (Obstacle obstacle : obstacles)
			{
				// does not analyze trajectory points further than the
				// target
				if (point.x > pig.getMaxX())
				{
					break bigFor;
				}
				// if the obstacle already has already been proven to be in the
				// trajectory, skips it
				if (obcsInTrajectory.contains(obstacle))
				{
					continue;
				}

				// if the obstacle is in the trajectory, adds it to the
				// list
				if (obstacle.getRectangle().contains(point))
				{
					if (obstacle.getType() != Type.HILLS)
					{
						obcsInTrajectory.add(obstacle);

						if (!gotFirstObstacle)
						{
							gotFirstObstacle = true;
							tapTime = tp
									.getTapTime(obstacle.getRectangle().x
											- sling.getCenterX(),
											shotInfo.getVelocity(),
											shotInfo.getAngle());
						}
					}
					else if (obstacle.getType() == Type.HILLS && !hasHills)
					{
						hasHills = true;
						obcsInTrajectory.add(obstacle);
					}
				}
			}
		}

		t = new Trajectory(bird, target, tapTime, shotInfo, points,
				obcsInTrajectory);
		return t;
	}

	private double distance(Point p1, Point p2)
	{
		return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
				* (p1.y - p2.y));
	}

	/**
	 * Generates all trajectories from the slingshot to the pigs
	 * 
	 * @param bird
	 *            The bird that is going to be shot
	 * @param slingshot
	 *            The bounding box of the slingshot
	 * @param pigs
	 *            A list with all the pigs
	 * @param obstacles
	 *            A list with the obstacles
	 * @return An {@link ArrayList} with all trajectories from the slingshot to
	 *         the pigs
	 */
	public ArrayList<Trajectory> getAllTrajectories(Bird bird,
			Rectangle slingshot, List<Rectangle> pigs, List<Obstacle> obstacles)
	{
		ArrayList<Trajectory> trajectories = new ArrayList<Trajectory>();

		// generates trajectories for every pig
		for (Rectangle pig : pigs)
		{
			Point target = new Point((int) pig.getCenterX(),
					(int) pig.getCenterY());

			// estimate the trajectory. if there are two points in the
			// arraylist, 0 will be low shot and 1 will be high shot
			ArrayList<ShotInfo> pts = tp.estimateLaunchPointFEI(slingshot,
					target);

			ShotInfo shotInfo = pts.get(0);
			ArrayList<Point> trajPoints = (ArrayList<Point>) tp
					.predictTrajectory(slingshot, shotInfo.getReleasePoint());

			Trajectory t1 = buildTrajectory(bird, pig, slingshot, target,
					shotInfo, obstacles, trajPoints);

			trajectories.add(t1);

			// if there is a corresponding high shot for the low shot processed
			// before, builds its trajectory and sets one as the alternative for
			// the other
			if (pts.size() > 1)
			{
				shotInfo = pts.get(1);

				Trajectory t2 = buildTrajectory(bird, pig, slingshot, target,
						shotInfo, obstacles, trajPoints);

				t1.setAlternative(t2);
				t2.setAlternative(t1);

				trajectories.add(t2);
			}
		}

		// orders trajectories by weight
		Collections.sort(trajectories);
		return trajectories;
	}

	public int getCurrentLevel()
	{
		return currentLevel;
	}

	/**
	 * Inserts all birds in a single {@link List}
	 * 
	 * @param slingshot
	 *            The bounding box of the slingshot
	 * @param redBirds
	 *            {@link List} containing the Rectangles representing red birds
	 * @param blueBirds
	 *            {@link List} containing the Rectangles representing blue birds
	 * @param yellowBirds
	 *            {@link List} containing the Rectangles representing yellow
	 *            birds
	 * @return A {@link List} of {@link Bird} trying to return them in the order
	 *         they are going to be used
	 */
	private List<Bird> processBirds(Rectangle slingshot,
			List<Rectangle> redBirds, List<Rectangle> blueBirds,
			List<Rectangle> yellowBirds)
	{
		ArrayList<Bird> birds = new ArrayList<Bird>();

		for (Rectangle red : redBirds)
		{
			birds.add(new Bird(Color.RED, red));
		}
		for (Rectangle blue : blueBirds)
		{
			birds.add(new Bird(Color.BLUE, blue));
		}
		for (Rectangle yellow : yellowBirds)
		{
			birds.add(new Bird(Color.YELLOW, yellow));
		}

		Point slingCenter = new Point((int) slingshot.getCenterX(),
				(int) slingshot.getCenterY());

		for (int i = birds.size() - 1; i > 0; i--)
		{
			for (int j = i; j > 0; j--)
			{
				Point bjCenter = new Point((int) birds.get(j).getRectangle()
						.getCenterX(), (int) birds.get(j).getRectangle()
						.getCenterY()), bjMinusOneCenter = new Point(
						(int) birds.get(j - 1).getRectangle().getCenterX(),
						(int) birds.get(j - 1).getRectangle().getCenterY());

				if (distance(slingCenter, bjCenter) < distance(slingCenter,
						bjMinusOneCenter))
				{
					Bird aux = birds.get(j);
					birds.set(j, birds.get(j - 1));
					birds.set(j - 1, aux);
				}
			}
		}

		return birds;
	}

	/**
	 * Gets the {@link Rectangle} lists that represent obstacles and groups them
	 * in a single list of type {@link Obstacle}
	 * 
	 * @param woods
	 *            List with rectangles representing wood blocks
	 * @param ices
	 *            List with rectangles representing ice blocks
	 * @param stones
	 *            List with rectangles representing stone blocks
	 * @return A single list consisting of Obstacle objects
	 * 
	 * @author Douglas De Rizzo Meneghetti
	 * @param hills
	 */
	private List<Obstacle> processObstacles(List<Rectangle> woods,
			List<Rectangle> ices, List<Rectangle> stones, List<Rectangle> hills)
	{
		ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();

		for (Rectangle rectangle : woods)
		{
			obstacles.add(new Obstacle(rectangle, Type.WOOD));
		}
		for (Rectangle rectangle : ices)
		{
			obstacles.add(new Obstacle(rectangle, Type.ICE));
		}
		for (Rectangle rectangle : stones)
		{
			obstacles.add(new Obstacle(rectangle, Type.STONE));
		}
		for (Rectangle rectangle : hills)
		{
			obstacles.add(new Obstacle(rectangle, Type.HILLS));
		}

		return obstacles;
	}

	// run the client
	@Override
	public void run()
	{
		ar.loadLevel(currentLevel);
		while (true)
		{
			GameState state = solve();
			if (state == GameState.WON)
			{
				try
				{
					Thread.sleep(3000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				int score = -2;
				while (score != StateUtil.checkCurrentScore(ActionRobot.proxy))
				{
					try
					{
						Thread.sleep(500);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					score = StateUtil.checkCurrentScore(ActionRobot.proxy);
				}
				System.out.println("###### The game score is " + score
						+ "########");
				ar.loadLevel(++currentLevel);
				// make a new trajectory planner whenever a new level is entered
				tp = new FEITrajectoryPlanner();

				// returns the counter to the first bird
				currentBird = 1;
				birds = null;
			}
			else if (state == GameState.LOST)
			{
				System.out.println("restart");
				ar.restartLevel();
			}
			else if (state == GameState.LEVEL_SELECTION)
			{
				System.out
						.println("Unexpected level selection page. Go to the last current level: "
								+ currentLevel);
				ar.loadLevel(currentLevel);
			}
			else if (state == GameState.MAIN_MENU)
			{
				System.out
						.println("Unexpected main menu page. Go to the last current level: "
								+ currentLevel);
				ActionRobot.GoFromMainMenuToLevelSelection();
				ar.loadLevel(currentLevel);
			}
			else if (state == GameState.EPISODE_MENU)
			{
				System.out
						.println("Unexpected episode menu page. Go to the last current level: "
								+ currentLevel);
				ActionRobot.GoFromMainMenuToLevelSelection();
				ar.loadLevel(currentLevel);
			}
		}
	}

	public void setCurrentLevel(int currentLevel)
	{
		this.currentLevel = currentLevel;
	}

	public GameState solve()
	{
		// capture Image
		BufferedImage screenshot = ActionRobot.doScreenShot();

		// process image
		FEIVision vision = new FEIVision(screenshot);

		Rectangle sling = vision.findSlingshot();

		while (sling == null && ar.checkState() == GameState.PLAYING)
		{
			System.out
					.println("No slingshot detected. Please remove pop up or zoom out.");
			ar.fullyZoom();
			screenshot = ActionRobot.doScreenShot();
			vision = new FEIVision(screenshot);
			sling = vision.findSlingshot();
		}

		GameState state = ar.checkState();
		if (state == GameState.WON)
			return state;

		// grabs all pigs and obstacles
		List<Rectangle> redBirds = vision.findRedBirds(), blueBirds = vision
				.findBlueBirds(), yellowBirds = vision.findYellowBirds(), pigs = vision
				.findPigs(), woods = vision.findWood(), ices = vision.findIce(), stones = vision
				.findStones(), hills = vision.findHills();

		if (birds == null)
		{
			birds = processBirds(sling, redBirds, blueBirds, yellowBirds);
		}

		List<Obstacle> obstacles = processObstacles(woods, ices, stones, hills);

		// if there is a sling, then play, otherwise just skip.
		if (sling != null)
		{
			ar.fullyZoom();
			if (!pigs.isEmpty())
			{
				// Initialize a shot list
				ArrayList<Shot> shots = new ArrayList<Shot>();

				// if, for some wicked reason, the system tries to use more
				// birds than the level has, repeats the strategy for the last
				// bird of the level
				if (currentBird > birds.size())
				{
					currentBird = birds.size();
				}

				ArrayList<Trajectory> trajectories = getAllTrajectories(
						birds.get(currentBird - 1), sling, pigs, obstacles);

				Trajectory t = trajectories.get(0);

				// if the trajectory contains hills AND the angle type is low
				// AND there is an alternative trajectory with high angle,
				// change if to high
				if (t.containsHills()
						&& t.getShotInfo().getAngleType() == AngleType.LOW
						&& t.getAlternative() != null)
				{
					t = t.getAlternative();
				}

				// if the current target is too close to the previous target and
				// there are more trajectories available, chooses the next best
				// trajectory
				if (previousTarget != null
						&& distance(previousTarget, t.getTarget()) < 10
						&& trajectories.size() > 1)
				{
					t = trajectories.get(1);
				}

				System.out.println("the target point is " + t.getTarget());

				Point releasePoint = t.getShotInfo().getReleasePoint(), refPoint = tp
						.getReferencePoint(sling);

				/* Get the center of the active bird */
				int focus_x = (int) (Env.getFocuslist().containsKey(
						currentLevel) ? Env.getFocuslist().get(currentLevel)
						.getX() : refPoint.x);
				int focus_y = (int) (Env.getFocuslist().containsKey(
						currentLevel) ? Env.getFocuslist().get(currentLevel)
						.getY() : refPoint.y);
				System.out.println("the release point is: " + releasePoint);
				/*
				 * =========== Get the release point from the trajectory
				 * prediction module====
				 */
				System.out.println("Shoot!!");
				if (releasePoint != null)
				{
					double releaseAngle = tp.getReleaseAngle(sling,
							releasePoint);
					System.out.println(" The release angle is : "
							+ Math.toDegrees(releaseAngle));
					int delay = 0;
					if (releaseAngle > Math.PI / 4)
					{
						delay = 1700;
					}
					else
					{
						delay = 500;
					}

					// int tap_time = (int) (delay + Math.random() * 1500);
					int tap_time = (int) (delay + 1500);

					shots.add(new Shot(focus_x, focus_y, (int) releasePoint
							.getX() - focus_x, (int) releasePoint.getY()
							- focus_y, 0, tap_time));
				}
				else
				{
					System.err.println("Out of Knowledge");
				}

				// check whether the slingshot is changed. the change of the
				// slingshot indicates a change in the scale.
				ar.fullyZoom();
				screenshot = ActionRobot.doScreenShot();
				vision = new FEIVision(screenshot);
				Rectangle _sling = vision.findSlingshot();
				if (sling.equals(_sling))
				{
					state = ar.shootWithStateInfoReturned(shots);
					// update parameters after a shot is made
					if (state == GameState.PLAYING)
					{
						screenshot = ActionRobot.doScreenShot();
						vision = new FEIVision(screenshot);
						List<Point> traj = vision.findTrajPoints();
						tp.adjustTrajectory(traj, sling, releasePoint);
						previousTarget = new Point(t.getTarget());
						currentBird++;
					}
				}
				else
				{
					System.out
							.println("Scale is changed, can not execute the shot, will re-segment the image.");
				}
			}
		}

		return state;
	}
}