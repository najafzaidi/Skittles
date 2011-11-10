package skittles.sim;

import java.io.IOException;

public class Skittles 
{
	public static void main( String[] args ) throws IOException
	{		
		Game gamNew = new Game( "GameConfig.xml" );
		for (int i=0;i<=1000;i++)
			gamNew.runGame();
	}
}
