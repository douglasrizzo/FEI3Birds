package fei3.objects;

import java.awt.Rectangle;

/**
 * An obstacle in the Angry Birds environment
 * 
 * @author Douglas De Rizzo Meneghetti
 * 
 */
public class Obstacle implements Comparable<Obstacle>
{
	public enum Type
	{
		WOOD(0), ICE(1), STONE(2), HILLS(3);

		private int value;

		private Type(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}
	}

	private Rectangle rectangle;

	private Type type;

	public Obstacle(Rectangle rectangle, Type type)
	{
		this.rectangle = rectangle;
		this.type = type;
	}

	@Override
	public int compareTo(Obstacle arg0)
	{
		if (getRectangle().x > arg0.getRectangle().x)
		{
			return 1;
		}
		if (getRectangle().x < arg0.getRectangle().x)
		{
			return -1;
		}
		return 0;
	}

	public Rectangle getRectangle()
	{
		return rectangle;
	}

	public Type getType()
	{
		return type;
	}

	public void setRectangle(Rectangle rectangle)
	{
		this.rectangle = rectangle;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	@Override
	public String toString()
	{
		return type.toString() + " block [" + rectangle.x + ", " + rectangle.y
				+ "](" + rectangle.width + ", " + rectangle.height + ")";
	}
}