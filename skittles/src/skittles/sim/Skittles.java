package skittles.sim;

import java.io.IOException;

public class Skittles 
{
	public static void main( String[] args ) throws IOException
	{		
		for (int i=0;i<=500;i++) {
			Game gamNew = new Game( "GameConfig.xml" );
			gamNew.runGame();
		}
	}
}
