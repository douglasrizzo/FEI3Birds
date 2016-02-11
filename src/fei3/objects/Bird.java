package fei3.objects;

import java.awt.Rectangle;

/**
 * An Angry Birds bird
 * 
 * @author Douglas De Rizzo Meneghetti
 */
public class Bird
{
	public enum Color
	{
		RED(0), BLUE(1), YELLOW(2);

		private int value;

		private Color(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}
	}

	private Color color;
	private Rectangle rectangle;

	public Bird()
	{}

	public Bird(Color color, Rectangle rectangle)
	{
		this.color = color;
		this.rectangle = rectangle;
	}

	public Color getColor()
	{
		return color;
	}

	public Rectangle getRectangle()
	{
		return rectangle;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public void setRectangle(Rectangle rectangle)
	{
		this.rectangle = rectangle;
	}

}