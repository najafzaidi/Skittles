package skittles.g5;

import skittles.sim.*;

public class FatKid extends Player 
{
	private int[] aintInHand;
	private int intColorNum;
	double dblHappiness;
	String strClassName;
	int intPlayerIndex;

	private double[] adblTastes;
	private int intLastEatIndex;
	private int intLastEatNum;

	@Override
	public void eat( int[] aintTempEat )
	{
		int intMaxColorIndex = -1;
		int intMaxColorNum = 0;
		for ( int intColorIndex = 0; intColorIndex < intColorNum; intColorIndex ++ )
		{
			if ( aintInHand[ intColorIndex ] > intMaxColorNum )
			{
				intMaxColorNum = aintInHand[ intColorIndex ];
				intMaxColorIndex = intColorIndex;
			}
		}
		aintTempEat[ intMaxColorIndex ] = intMaxColorNum;
		aintInHand[ intMaxColorIndex ] = 0;
		intLastEatIndex = intMaxColorIndex;
		intLastEatNum = intMaxColorNum;
	}

	@Override
	public void offer( Offer offTemp )
	{
		double maxValueTasteValue=-2.0;
		double minValueTasteValue=+2;
		int maxValueTasteIndex=Integer.MIN_VALUE;
		int minValueTasteIndex=Integer.MAX_VALUE;
		int transactionSize=0;
		// the number of skittles in offer 

		for ( int intColorIndex = 0; intColorIndex < intColorNum; intColorIndex ++ )
		{
			if ( aintInHand[ intColorIndex ] > 0 )
			{ 
				if(maxValueTasteValue<adblTastes[intColorIndex]) {
					maxValueTasteValue=adblTastes[intColorIndex];
					maxValueTasteIndex=intColorIndex;
				}
				if(minValueTasteValue>adblTastes[intColorIndex]) {
					minValueTasteValue=adblTastes[intColorIndex];
					minValueTasteIndex=intColorIndex;
				}
			}

		}

		if(aintInHand[maxValueTasteIndex]<aintInHand[minValueTasteIndex]) 
			transactionSize=maxValueTasteIndex;
		else 
			transactionSize=minValueTasteIndex;

		int[] aintOffer = new int[ intColorNum ];
		int[] aintDesire = new int[ intColorNum ];
		aintOffer[ minValueTasteIndex ] = transactionSize;
		aintDesire[ maxValueTasteIndex ] = transactionSize;
		offTemp.setOffer( aintOffer, aintDesire );
	}

	@Override
	public void syncInHand(int[] aintInHand) 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void happier(double dblHappinessUp) 
	{
		double dblHappinessPerCandy = dblHappinessUp / Math.pow( intLastEatNum, 2 );
		if ( adblTastes[ intLastEatIndex ] == -1 )
		{
			adblTastes[ intLastEatIndex ] = dblHappinessPerCandy;
		}
		else
		{
			if ( adblTastes[ intLastEatIndex ] != dblHappinessPerCandy )
			{
				System.out.println( "Error: Inconsistent color happiness!" );
			}
		}
	}

	@Override
	public Offer pickOffer(Offer[] aoffCurrentOffers) 
	{
		Offer offReturn = null;
		for ( Offer offTemp : aoffCurrentOffers )
		{
			if ( offTemp.getOfferedByIndex() == intPlayerIndex || offTemp.getOfferLive() == false )
				continue;
			int[] aintDesire = offTemp.getDesire();
			if ( checkEnoughInHand( aintDesire ) )
			{
				offReturn = offTemp;
				aintDesire = offReturn.getDesire();
				int[] aintOffer = offReturn.getOffer();
				for ( int intColorIndex = 0; intColorIndex < intColorNum; intColorIndex ++ )
				{
					aintInHand[ intColorIndex ] += aintOffer[ intColorIndex ] - aintDesire[ intColorIndex ];
				}
				break;
			}
		}

		return offReturn;
	}

	@Override
	public void offerExecuted(Offer offPicked) 
	{
		int[] aintOffer = offPicked.getOffer();
		int[] aintDesire = offPicked.getDesire();
		for ( int intColorIndex = 0; intColorIndex < intColorNum; intColorIndex ++ )
		{
			aintInHand[ intColorIndex ] += aintDesire[ intColorIndex ] - aintOffer[ intColorIndex ];
		}
	}

	@Override
	public void updateOfferExe(Offer[] aoffCurrentOffers) 
	{
		// dumpplayer doesn't care
	}

	@Override
	public void initialize(int intPlayerIndex, String strClassName,	int[] aintInHand) 
	{
		this.intPlayerIndex = intPlayerIndex;
		this.strClassName = strClassName;
		this.aintInHand = aintInHand;
		intColorNum = aintInHand.length;
		dblHappiness = 0;
		adblTastes = new double[ intColorNum ];
		for ( int intColorIndex = 0; intColorIndex < intColorNum; intColorIndex ++ )
		{
			adblTastes[ intColorIndex ] = -1;
			// may have to initialize it to -2 so that we know that we have not tasted it yet
		}
	}

	private boolean checkEnoughInHand( int[] aintTryToUse )
	{
		for ( int intColorIndex = 0; intColorIndex < intColorNum; intColorIndex ++ )
		{
			if ( aintTryToUse[ intColorIndex ] > aintInHand[ intColorIndex ] )
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public String getClassName() 
	{
		return "DumpPlayer";
	}

	@Override
	public int getPlayerIndex() 
	{
		return intPlayerIndex;
	}

	public int evaluateOffer( Offer o ) {
		int sum = 0;	
		int[] offer = o.getOffer();
		for (int i=0; i < aintInHand.length; i++) {
			sum += (int) (adblTastes[i] * Math.pow(offer[i] + aintInHand[i], 2));
		}
		return sum;
	}


}
