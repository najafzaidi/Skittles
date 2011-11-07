package skittles.g5;

import java.util.ArrayList;

import skittles.sim.*;

public class SkittleDiddle extends Player 
{
	boolean debugging=false;
	
	private int[] aintInHand;
	private int intColorNum;
	private int intPlayerNum;
	double dblHappiness;
	String strClassName;
	int intPlayerIndex;
	int round=0;
	int lastRoundWithTrading;
	// records the round in which some offer was executed
	
	int lastRoundWithOffer;
	// records the round in which some offer was made by teams other than us
	
	int totalInitialSkittles;
	int skittlesEaten;
	int colorsLeft;
	int colorsUnknownHave;
	int colorsUnknownTotal;
	int maxTransactionSize=Integer.MAX_VALUE;
	
	

	private double[] adblTastes;
	private int intLastEatIndex;
	private int intLastEatNum;
	private ArrayList<int[]> netTradesPerPlayer;	// executed trade accounting (credits, debits) stored here

	private ArrayList<offer_record> our_offers_list;
	
	@Override
	public String getClassName() 
	{
		return "SkittleDiddle";
	}

	@Override
	public int getPlayerIndex() 
	{
		return intPlayerIndex;
	}



	@Override
	public void initialize(int intPlayerNum,int intPlayerIndex, String strClassName,	int[] aintInHand) 
	{
		this.intPlayerIndex = intPlayerIndex;
		this.strClassName = strClassName;
		this.aintInHand = aintInHand;
		this.intPlayerNum = intPlayerNum;
		intColorNum = aintInHand.length;
		dblHappiness = 0;
		adblTastes = new double[ intColorNum ];
		
		our_offers_list = new ArrayList<offer_record>();
		
		for ( int intColorIndex = 0; intColorIndex < intColorNum; intColorIndex ++ )
		{
			totalInitialSkittles+=aintInHand[intColorIndex];
			adblTastes[ intColorIndex ] = -2;
			// may have to initialize it to -2 so that we know that we have not tasted it yet
		}
		maxTransactionSize=Integer.MAX_VALUE;
		/*maxTransactionSize=totalInitialSkittles/(intColorNum*3);
		if (maxTransactionSize<4)
			maxTransactionSize=4;
			*/
		// has to be updated 
		
		netTradesPerPlayer = new ArrayList<int[]>(intPlayerNum);
		for ( int i=0; i<intPlayerNum; i++ ) {
			netTradesPerPlayer.add(new int[intColorNum]);
		}
	}



	@Override
	public void eat( int[] aintTempEat )
	{
		round++;
		double minValue=Double.MAX_VALUE;
		int minValueIndex=Integer.MAX_VALUE;
		int skittlesToEat=0;
		double value=0;
		// the number of skittles in to eat 
		// to be added if skittle is just 1 and taste unknown then avoid eating it
		updateSkittlesInfo();
		for ( int intColorIndex = 0; intColorIndex < intColorNum; intColorIndex ++ )
		{
			if ( aintInHand[ intColorIndex ] > 0 )
			{ 
				value=adblTastes[intColorIndex]*Math.pow(aintInHand[ intColorIndex ],2);
				if(minValue>value ) {
					minValue=value;
					minValueIndex=intColorIndex;
				}
			}
		}
		skittlesToEat=1;
		if(minValue>0) {
			if ((round-lastRoundWithOffer) > 2)
				skittlesToEat=aintInHand[minValueIndex];
		}
		aintTempEat[ minValueIndex ] = skittlesToEat;
		aintInHand[ minValueIndex ] -= skittlesToEat;
		intLastEatIndex = minValueIndex;
		intLastEatNum = skittlesToEat;
		skittlesEaten+=skittlesToEat;
		if(debugging) {
			System.out.println("\n Eating by intPlayerIndex="+intPlayerIndex+" in round="+round+" intLastEatIndex="+intLastEatIndex+"  intLastEatNum="+intLastEatNum);
		}
	}

	@Override
	public void offer( Offer offTemp )
	{

		double maxValue=Double.MIN_VALUE;
		double minValue=Double.MAX_VALUE;
		double value; 
		int maxValueTasteIndex=0;
		int minValueTasteIndex=0;
		int transactionSize=0;

		if(debugging) {
			System.out.println("\n In round "+round);
			for ( int i=0; i<intPlayerNum; i++ ) {
				printArray("Player "+i,netTradesPerPlayer.get(i));
			}
		}
		
		int[] aintOffer = new int[ intColorNum ];
		int[] aintDesire = new int[ intColorNum ];		
		
		if(our_offers_list.isEmpty())
		{
			for ( int intColorIndex = 0; intColorIndex < intColorNum; intColorIndex ++ )
			{
				if(adblTastes[intColorIndex]!=-2) {
					value=adblTastes[intColorIndex]*Math.pow(aintInHand[ intColorIndex ],2);
					if(maxValue<value) {
						maxValue=value;
						maxValueTasteIndex=intColorIndex;
					}
					if ( aintInHand[ intColorIndex ] > 0 )
					{ 
						if(minValue>value) {
							minValue=value;
							minValueTasteIndex=intColorIndex;
						}
					}
				}
			}
			
			transactionSize = 2;
			
			if(minValueTasteIndex!=maxValueTasteIndex)
			{
				if (maxValue>0)
				{
					aintOffer[ minValueTasteIndex ] = transactionSize;
					aintDesire[ maxValueTasteIndex ] = transactionSize;
				}
				else
				{
					aintOffer[ minValueTasteIndex ] = transactionSize;
					aintDesire[ minValueTasteIndex ] = transactionSize;
				}
			} 
			else
			{
				if(minValue<0)
				{
					aintOffer[ minValueTasteIndex ] = transactionSize;
					aintDesire[ minValueTasteIndex ] = transactionSize;
				}
			}
		}
		else
		{
			offer_record temp = our_offers_list.get(our_offers_list.size()-1); // last offer
			int i;
			int j;
			int oldTransactionSize = 0;
			for (i=0; i< intColorNum; i++)
			{
				if(temp.Offer[i]>0)
				{
					oldTransactionSize = temp.Offer[i];
					break;
				}
			}
			for (j=0; j< intColorNum; j++)
			{
				if(temp.Desire[j]>0)
					break;
			}
			if(temp.executed == true) // last offer accepted
			{
				aintOffer[i] = oldTransactionSize * 2;
				aintDesire[j] = oldTransactionSize * 2;
			}
			else
			{
				for ( int intColorIndex = 0; intColorIndex < intColorNum; intColorIndex ++ )
				{
					if(adblTastes[intColorIndex]!=-2)
					{
						value=adblTastes[intColorIndex]*Math.pow(aintInHand[ intColorIndex ],2);
						if(maxValue<value && intColorIndex!=j)
						{
							maxValue=value;
							maxValueTasteIndex=intColorIndex;
						}
						if ( aintInHand[ intColorIndex ] > 0 )
						{ 
							if(minValue>value && intColorIndex!=i)
							{
								minValue=value;
								minValueTasteIndex=intColorIndex;
							}
						}
					}
				}
				transactionSize = 2;
				
				if(minValueTasteIndex!=maxValueTasteIndex)
				{
					if (maxValue>0)
					{
						aintOffer[ minValueTasteIndex ] = transactionSize;
						aintDesire[ maxValueTasteIndex ] = transactionSize;
					}
					else
					{
						aintOffer[ minValueTasteIndex ] = transactionSize;
						aintDesire[ minValueTasteIndex ] = transactionSize;
					}
				} 
				else
				{
					if(minValue<0)
					{
						aintOffer[ minValueTasteIndex ] = transactionSize;
						aintDesire[ minValueTasteIndex ] = transactionSize;
					}
				}
			}
			
		}
		
		offTemp.setOffer( aintOffer, aintDesire );
		
		offer_record temp = new offer_record();
		temp.Offer = aintOffer;
		temp.Desire = aintDesire;
		temp.executed = false;
		
		our_offers_list.add(temp);
		
		if(debugging) {
			System.out.println("\nstrClassName="+this.strClassName+"  intPlayerIndex="+intPlayerIndex+" Offer="+minValueTasteIndex+
					"  Desire="+maxValueTasteIndex);
			printArray("aintInHand",aintInHand);
			printArray("adblTastes",adblTastes);
		}
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
			
			if ( offTemp.getOfferedByIndex() != intPlayerIndex && lastRoundWithOffer<round) {
				if(!isOfferEmpty(offTemp))
					lastRoundWithOffer=round;
			}
			
			if ( offTemp.getOfferedByIndex() == intPlayerIndex || offTemp.getOfferLive() == false )
				continue;
			int[] aintDesire = offTemp.getDesire();
			if ( checkEnoughInHand( aintDesire ) )
			{
				double gainByAccepting = evaluateOffer(offTemp);
				if(debugging) {
					System.out.println("\n for intPlayerIndex="+intPlayerIndex+" gainByAccepting="+gainByAccepting+"  maxGain="+maxGain);
					printArray("we give",offTemp.getDesire());
					printArray("we  get",offTemp.getOffer());
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

	@Override
	public void offerExecuted(Offer offPicked) 
	{
		int[] aintOffer = offPicked.getOffer();
		int[] aintDesire = offPicked.getDesire();
		for ( int intColorIndex = 0; intColorIndex < intColorNum; intColorIndex ++ )
		{
			aintInHand[ intColorIndex ] += aintDesire[ intColorIndex ] - aintOffer[ intColorIndex ];
		}
		
		if(intPlayerIndex==offPicked.getOfferedByIndex())
		{
			offer_record temp = new offer_record();
			temp.Offer = aintOffer;
			temp.Desire = aintDesire;
			temp.executed = true;
			
			our_offers_list.remove(our_offers_list.size()-1);
			our_offers_list.add(temp);
		}
		
	}

	@Override
	public void updateOfferExe(Offer[] aoffCurrentOffers) {
		int maker;
		int taker;

		for( Offer o : aoffCurrentOffers ) {
			
			maker = o.getOfferedByIndex();
			taker = o.getPickedByIndex();
						
			if (taker<0) { // the offer wasn't taken	
				continue;
			}
			
			lastRoundWithTrading=round;
			// an offer was executed in this round
			
			int[] offer = o.getOffer();
			int[] desire = o.getDesire();
			
			// Offer maker
			for (int i=0; i < offer.length; i++) {  // subtract skittles given away in offer
				if (offer[i] > 0) {
					int currentCount = netTradesPerPlayer.get(maker)[i];
					netTradesPerPlayer.get(maker)[i] = currentCount - offer[i];
				}
			}
			for (int i=0; i < desire.length; i++) {  // add skittles taken in desired
				if (desire[i] > 0) {
					int currentCount = netTradesPerPlayer.get(maker)[i];
					netTradesPerPlayer.get(maker)[i] = currentCount + desire[i];
				}
			}
			
			// Offer taker
			for (int i=0; i < offer.length; i++) {  // add skittles given away in offer
				if (offer[i] > 0) {
					int currentCount = netTradesPerPlayer.get(taker)[i];
					netTradesPerPlayer.get(taker)[i] = currentCount + offer[i];
				}
			}
			for (int i=0; i < desire.length; i++) {  // subtract skittles taken in desired
				if (desire[i] > 0) {
					int currentCount = netTradesPerPlayer.get(taker)[i];
					netTradesPerPlayer.get(taker)[i] = currentCount - desire[i];
				}
			}
		}
	}

	@Override
	public void syncInHand(int[] aintInHand) 
	{
		// TODO Auto-generated method stub

	}


	public void updateSkittlesInfo() {
		colorsLeft=0;
		colorsUnknownHave=0;
		colorsUnknownTotal=0;

		for ( int intColorIndex = 0; intColorIndex < intColorNum; intColorIndex ++ )
		{
			if ( aintInHand[ intColorIndex ] > 0 )
			{ 
				colorsLeft++;
				if(adblTastes[intColorIndex]==-2) {
					colorsUnknownHave++;
					colorsUnknownTotal++;
				}
			} else 
				if(adblTastes[intColorIndex]==-2) 
					colorsUnknownTotal++;
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


	
	public boolean isOfferEmpty( Offer o ) {
		int[] offer = o.getOffer();
		int[] desire = o.getDesire();
		for (int i=0; i < aintInHand.length; i++) {
			if (offer[i]>0 ||desire[i]>0 ) {
				return false;
			}
		}
		return true;
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

	class offer_record
	{
		int[] Offer = new int[ intColorNum ];
		int[] Desire = new int[ intColorNum ];
		boolean executed;
	}

}
/*
 *   Things to do (maybe, some ideas might be repetitive)->
 *       1- should we try to trade something which we have never tasted (depending on current info about all which we have )
 *       2- know when the trading is not happening, done with
 *       3- store preferences of other players 
 *       4- some idea of who is having what by noticing the offer execution
 *       5- does it help to be lazy-smart and trade with active-smart ?
 *       6- find matching players ( they need what we don't and we need what they don't) 
 *       7- not to eat if itsthe only skittle and we dont know its taste
 *       8- how often should the offer be repeated
 */

