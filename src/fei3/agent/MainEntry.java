package fei3.agent;

import ab.vision.TestVision;

public class MainEntry
{
	public static void main(String args[])
	{
		if (args.length == 0)
		{
			Thread nathre = new Thread(new FEI3Agent());
			nathre.start();
			Thread thre = new Thread(new TestVision());
			thre.start();
		}
	}
}
