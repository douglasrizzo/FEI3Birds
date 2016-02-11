package fei3.vision;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ab.vision.Vision;

/**
 * Customized vision class
 * 
 * @author Caio Pavanelli
 * @author Douglas De Rizzo Meneghetti
 * @author Marcos Maresch
 * 
 */
public class FEIVision extends Vision
{
	public FEIVision(BufferedImage screenshot)
	{
		super(screenshot);
	}

	public List<Rectangle> findColour(int... colourCodes)
	{

		ArrayList<Rectangle> objects = new ArrayList<Rectangle>();

		for (int i : colourCodes)
		{
			objects.addAll(findColour(i));
		}
		return objects;
	}

	/**
	 * Does its best to get the bounding boxes of hills
	 * 
	 * @return a {@link Rectangle} list representing the hills
	 */
	public List<Rectangle> findHills()
	{
		ArrayList<Rectangle> hills = (ArrayList<Rectangle>) findColour(72, 136,
				137);

		// order hills by descending area
		Collections.sort(hills, new Comparator<Rectangle>() {

			@Override
			public int compare(Rectangle arg0, Rectangle arg1)
			{
				int area0 = arg0.width * arg0.height, area1 = arg1.width
						* arg1.height;
				if (area0 < area1)
				{
					return 1;
				}
				if (area0 > area1)
				{
					return -1;
				}
				return 0;
			}

		});

		// ignores small hills

		int i = hills.size() - 1;
		while (i > 0)
		{
			if (hills.get(i).width + hills.get(i).height <= 4)
			{
				hills.remove(i);
				i--;
			}
			else
			{
				break;
			}
		}

		// removes rectangles contained inside each other
		for (int j = 0; j < hills.size(); j++)
		{
			i = j + 1;
			while (i < hills.size())
			{
				if (hills.get(j).contains(hills.get(i)))
				{
					hills.remove(i);
				}
				else
				{
					i++;
				}
			}
		}

		return hills;
	}
}