package skittles.g5;

import skittles.sim.*;

public class FatKid extends Player 
{
	private int[] aintInHand;
	private int intColorNum;
	double dblHappiness;
	String strClassName;
	int intPlayerIndex;
	int round=0;
	boolean debugging=true;
	int totalInitialSkittles;
	int skittlesEaten;

	private double[] adblTastes;
	private int intLastEatIndex;
	private int intLastEatNum;

	@Override
	public void eat( int[] aintTempEat )
	{
		round++;
		double minValueTasteValue=2;
		int minValueTasteIndex=Integer.MAX_VALUE;
		int skittlesToEat=0;
		int colorsLeft=0;
		// the number of skittles in to eat 
		// to be added if skittle is just 1 and taste unknown then avoid eating it

		for ( int intColorIndex = 0; intColorIndex < intColorNum; intColorIndex ++ )
		{
			if ( aintInHand[ intColorIndex ] > 0 )
			{ 
				colorsLeft++;
				if(minValueTasteValue>adblTastes[intColorIndex]) {
					minValueTasteValue=adblTastes[intColorIndex];
					minValueTasteIndex=intColorIndex;
				}
				if(minValueTasteValue==-2 && adblTastes[intColorIndex]== -2 && minValueTasteIndex!=Integer.MAX_VALUE) {
					if(aintInHand[minValueTasteIndex]<aintInHand[intColorIndex]) {
						minValueTasteIndex=intColorIndex;
					}
				}
			}
		}
		skittlesToEat=1;
		if(adblTastes[minValueTasteIndex]>0) {
			/*if(colorsLeft==1)
				skittlesToEat=aintInHand[minValueTasteIndex];
			if( minValueTasteValue>0.5)*/
			skittlesToEat=aintInHand[minValueTasteIndex];
			if(colorsLeft>intColorNum/3 && minValueTasteValue<0.5 && aintInHand[minValueTasteIndex]<(2*totalInitialSkittles/intColorNum))
				skittlesToEat=1;
			/* this should be the case if no other player is left or no active trading being done
			 if(noActiveTrading())
				skittlesToEat=aintInHand[minValueTasteIndex];
			 */
		}
		aintTempEat[ minValueTasteIndex ] = skittlesToEat;
		aintInHand[ minValueTasteIndex ] -= skittlesToEat;
		intLastEatIndex = minValueTasteIndex;
		intLastEatNum = skittlesToEat;
		skittlesEaten+=skittlesToEat;
		if(debugging) {
			System.out.println("\n Eating by intPlayerIndex="+intPlayerIndex+" in round="+round+" intLastEatIndex="+intLastEatIndex+"  intLastEatNum="+intLastEatNum);
		}
	}

	@Override
	public void offer( Offer offTemp )
	{
		double maxValueTasteValue=-2.0;
		double minValueTasteValue=2;
		int maxValueTasteIndex=0;
		int minValueTasteIndex=0;
		int transactionSize=0;
		// the number of skittles in offer 

		for ( int intColorIndex = 0; intColorIndex < intColorNum; intColorIndex ++ )
		{
			if(maxValueTasteValue<adblTastes[intColorIndex]) {
				maxValueTasteValue=adblTastes[intColorIndex];
				maxValueTasteIndex=intColorIndex;
			}
			if ( aintInHand[ intColorIndex ] > 0 )
			{ 
				if(minValueTasteValue>adblTastes[intColorIndex]) {
					minValueTasteValue=adblTastes[intColorIndex];
					minValueTasteIndex=intColorIndex;
				}
			}

		}
		if(3<aintInHand[minValueTasteIndex]) 
			transactionSize=3;
		else 
			transactionSize=aintInHand[minValueTasteIndex];

		int[] aintOffer = new int[ intColorNum ];
		int[] aintDesire = new int[ intColorNum ];
		if (maxValueTasteValue>0) {
			aintOffer[ minValueTasteIndex ] = transactionSize;
			aintDesire[ maxValueTasteIndex ] = transactionSize;
		}
		offTemp.setOffer( aintOffer, aintDesire );
		if(debugging) {
			System.out.println("\nstrClassName="+this.strClassName+"  intPlayerIndex="+intPlayerIndex+" Offer="+minValueTasteIndex+
					"  Desire="+maxValueTasteIndex);
			printArray("aintInHand",aintInHand);
			printArray("adblTastes",adblTastes);
		}
	}
	public void printArray(String message,double[] tempArray) {
		String printingString="";
		for (int i=0;i<tempArray.length;i++) {
			printingString=printingString+" , "+String.format("%+1.5f",tempArray[i]);
		}
		System.out.println(message+"  "+printingString);
	}
	public void printArray(String message,int[] tempArray) {
		String printingString="";
		for (int i=0;i<tempArray.length;i++) {
			printingString=printingString+" , "+String.format("%8d",tempArray[i]);
		}
		System.out.println(message+"  "+printingString);
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
		if ( adblTastes[ intLastEatIndex ] == -2 )
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
		double maxGain=-1;
		for ( Offer offTemp : aoffCurrentOffers )
		{
			if ( offTemp.getOfferedByIndex() == intPlayerIndex || offTemp.getOfferLive() == false )
				continue;
			int[] aintDesire = offTemp.getDesire();
			if ( checkEnoughInHand( aintDesire ) )
			{
				double gainByAccepting = evaluateOffer(offTemp);
				if(debugging) {
					System.out.println("\n for intPlayerIndex="+intPlayerIndex+" gainByAccepting="+gainByAccepting+"  maxGain="+maxGain);
					printArray("we give",offTemp.getDesire());
					printArray("we get",offTemp.getOffer());
				}
				if(gainByAccepting>maxGain && gainByAccepting>0) {
					if(debugging) {
						System.out.println("Above offer is current max\n");
					}
					offReturn = offTemp;
					maxGain=gainByAccepting;

				}
			}
		}
		if( maxGain>0) {
			int[] aintDesire = offReturn.getDesire();
			aintDesire = offReturn.getDesire();
			int[] aintOffer = offReturn.getOffer();
			for ( int intColorIndex = 0; intColorIndex < intColorNum; intColorIndex ++ )
			{
				aintInHand[ intColorIndex ] += aintOffer[ intColorIndex ] - aintDesire[ intColorIndex ];
			}
		}

		return offReturn;
	}

	public double evaluateOffer( Offer o ) {
		double changeInScore = 0;	
		int[] offer = o.getOffer();
		int[] desire = o.getDesire();
		for (int i=0; i < aintInHand.length; i++) {
			if (adblTastes[i]>0) {
				changeInScore += (adblTastes[i] * Math.pow((offer[i] + aintInHand[i]), 2)) - (adblTastes[i] * Math.pow(aintInHand[i], 2));
				changeInScore -= (adblTastes[i] * Math.pow(aintInHand[i], 2)) - (adblTastes[i] * Math.pow((aintInHand[i]-desire[i]), 2));
			} else {
				changeInScore += (adblTastes[i] * offer[i]);
				changeInScore -= (adblTastes[i] * desire[i]);
				// as adblTastes is negative as well as positive so the final value will correct itself
			}
		}
		return changeInScore;
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
			totalInitialSkittles+=aintInHand[intColorIndex];
			adblTastes[ intColorIndex ] = -2;
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



}
