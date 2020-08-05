
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Simulator {

	// input
	private int horizontalRange;
	private int verticalRange;
	private ArrayList<Passenger> demandProfile; // list to contain all passenger
	private ArrayList<Driver> supplyProfile;// list to contain all drivers
	private int emptyCarMode; // 1 means stay unmoved; 2 means random walk; 3 means hot area
	private int matchingMode;
	// 0 means random matching, 1 means weighted average
	// 2 means revenue maximization, 3 means distance minimization, 4 means service maximization 
	// 5 means revenue + stable, 6 means distance + stable, 7 means service maximization
	// 8 means compromise 
	private int matchingInterval;// matching interval, do matching every matchingInterval second
	private int maxAllowedPickupTime;// maximum allowed pickup time 
	private int distanceTimeConversion;// parameter to convert distance to travel time
	private int simulationLength;// duration of simulation 
	private int totNumPax;// total number of passengers in the demand profile
	private int randomWalkRange;// range of random walk for empty car
	
	private double[] targetPickupTime;// target pickup time 
	private double reverseParameter; // transfer the pickup minimization to reverse pickup time maximization
	private double reverseParameter2; // pickup time normalization

	private double[] targetServiceScore;// target service score 
	private double[] targetRevenue;// target revenue 

	private double realtimePickupTime;// realtime pickup time 
	private double realtimeServiceScore;// realtime service score 
	private double realtimeRevenue;// realtime revenue
	
	private double weightPickupTime;// weight pickup time
	private double weightServiceScore;// weight service score
	private double weightRevenue;// weight revenue

	// intermediate
	private ArrayList<Passenger> waitingPaxList; // list of passengers that are still waiting
	private ArrayList<Passenger> cancelPaxList;// list of passengers that canceled order
	private ArrayList<Passenger> inServicePaxList;// list of passengers that on board
	private ArrayList<Passenger> finishPaxList;// list of passengers that already arrived at destination and finished service
	private ArrayList<Driver> onlineAvailableDriverList;// list of drivers that are online and available (idle)
	private ArrayList<Driver> onlineBusyDriverList;// list of drivers that are online and busy (serving passenger)
	private ArrayList<Driver> finishShiftDriverList;// list of drivers that finished working shift and get offline


	// output
	private double servedProb; // ratio of served passengers
	private int totNumServedPax;// total number of served passengers
	private int totNumCancelledPax;// total number of passengers that canceled orders
	private int totNumFinishedPax;// total number of passengers that finish service
	private double avgPickupTime;// average pickup time of served order
	private double avgTravPickupTime; // average tranversed pickup time of served order
	private double avgServiceScore;// average service score of served order
	private double avgRevenue;// average revenue of served order

	// ending State Demand and Supply
	private ArrayList<Passenger> demandEnding;// list to contain all passengers after simulation
	private ArrayList<Driver> supplyEnding;// list to contain all drivers after simulation

	public Simulator(int horizontalRange, int verticalRange, ArrayList<Passenger> demandProfile, ArrayList<Driver> supplyProfile, int emptyCarMode,
			int matchingMode, int matchingInterval, int maxAllowedPickupTime, int distanceTimeConversion, int randomWalkRange, int simulationLength, double[] targetRevenue, double[] targetPickupTime, double[] targetServiceScore, double reverseParameter) {
		super();
		this.horizontalRange=horizontalRange;
		this.verticalRange=verticalRange;
		this.demandProfile = demandProfile;
		this.totNumPax=demandProfile.size();
		this.supplyProfile = supplyProfile;
		this.emptyCarMode = emptyCarMode;
		this.matchingMode = matchingMode;
		this.matchingInterval = matchingInterval;
		this.maxAllowedPickupTime = maxAllowedPickupTime;
		this.distanceTimeConversion = distanceTimeConversion;
		this.randomWalkRange=randomWalkRange;
		this.targetRevenue = targetRevenue;
		this.targetPickupTime = targetPickupTime;
		this.targetServiceScore = targetServiceScore;
		this.reverseParameter = reverseParameter;
		this.reverseParameter2 = 2.5;
		this.servedProb=-1;
		this.totNumServedPax=-1;
		this.totNumFinishedPax=-1;
		this.totNumCancelledPax=-1;
		this.avgPickupTime=-1;
		this.avgRevenue=-1;
		this.avgServiceScore=-1;
		this.avgTravPickupTime=-1;
		this.weightPickupTime=1;
		this.weightRevenue=1;
		this.weightServiceScore=1;
		this.realtimePickupTime=0;
		this.realtimeRevenue=0;
		this.realtimeServiceScore=0;
		this.demandEnding=new ArrayList<Passenger>();
		this.supplyEnding=new ArrayList<Driver>();
		this.simulationLength=simulationLength;
		this.waitingPaxList=new ArrayList<Passenger>();
		this.cancelPaxList=new ArrayList<Passenger>();
		this.inServicePaxList=new ArrayList<Passenger>();
		this.finishPaxList=new ArrayList<Passenger>();
		this.onlineAvailableDriverList=new ArrayList<Driver>();
		this.onlineBusyDriverList=new ArrayList<Driver>();
		this.finishShiftDriverList=new ArrayList<Driver>();
		
	}

	public void sim(){

		for (int t=0;t<simulationLength;t++){

			int hour=t/3600;
			int min=(t-hour*3600)/60;
			int sec=t-hour*3600-min*60;



			//			System.out.println("waitingPaxSize="+waitingPaxList.size());
			//			System.out.println("cancelPaxSize="+cancelPaxList.size());
			//			System.out.println("inServicePax="+inServicePaxList.size());
			//			System.out.println("finishServicePax="+finishPaxList.size());
			//			System.out.println("busyDriver="+onlineBusyDriverList.size());
			//			System.out.println("availableDriver="+onlineAvailableDriverList.size());
			//			System.out.println("offlineDriver="+finishShiftDriverList.size());



			// Add new passengers to waitingPaxList
			for (int i=demandProfile.size()-1;i>=0;i--){
				Passenger p=demandProfile.get(i);
				if (p.getRequestTime()==t){
					waitingPaxList.add(p);
					demandProfile.remove(i);
				}
			}

			// Check if passengers in waitingPaxList cancel at t
			for (int i=waitingPaxList.size()-1;i>=0;i--){
				Passenger p=waitingPaxList.get(i);
				if (p.getCancelTime()==t){
					p.setCancel(true);
					cancelPaxList.add(p);
					waitingPaxList.remove(i);				
				}
			}

			// check if passengers in inServicePaxList finish service at t
			for (int i=inServicePaxList.size()-1;i>=0;i--){
				Passenger p=inServicePaxList.get(i);
				if (p.getTripEndTime()==t){
					p.setFinish(true);
					finishPaxList.add(p);
					inServicePaxList.remove(i);
				}
			}

			// Add new drivers to onlineDriverList;
			for (int j=supplyProfile.size()-1;j>=0;j--){
				Driver d=supplyProfile.get(j);
				//	System.out.println("shiftstarttime="+d.getShiftStartTime());
				if (d.getShiftStartTime()==t){
					d.setOnline(true);
					d.setAvailable(true);
					onlineAvailableDriverList.add(d);
					supplyProfile.remove(j);
				}
			}

			// Update online available (idle) driver status			 
			for (int j=onlineAvailableDriverList.size()-1;j>=0;j--){
				Driver d=onlineAvailableDriverList.get(j);

				//	a. Remove drivers from onlineAvailableDriverList to finishShiftDriverList if shiftEndingTime = t
				if (d.getShiftEndTime()==t){
					d.setAvailable(false);
					d.setOnline(false);
					finishShiftDriverList.add(d);
					onlineAvailableDriverList.remove(j);
				}

				//   b. empty car routing.  1 means stay in previous area; 2 means random walk; 3 means hot area
				if (emptyCarMode==1){

				}
				else if (emptyCarMode==2){
					// driver has not arrived cruise destination
					if (d.getRemainingCruiseTime()>0) {
						
						if (d.getRemainingCruiseTime()!=(Math.abs(d.getCruiseLat()-d.getCurrentLat()+Math.abs(d.getCruiseLon()-d.getCurrentLon())))) {
							System.out.println("Lat="+d.getCruiseLat()+","+d.getCurrentLat());
							System.out.println("Lon="+d.getCruiseLon()+","+d.getCurrentLon());
							System.out.println("RemainingTime Wrong="+d.getRemainingCruiseTime());
							System.exit(0);
						}
						
						
						if (d.getCruiseLat()>d.getCurrentLat()) {
							d.setCurrentLat(d.getCurrentLat()+1);
						}
						else if (d.getCruiseLat()<d.getCurrentLat()) {
							d.setCurrentLat(d.getCurrentLat()-1);
						}
						else if (d.getCruiseLon()>d.getCurrentLon()) {
							d.setCurrentLon(d.getCurrentLon()+1);
						}
						else if (d.getCruiseLon()<d.getCurrentLon()) {
							d.setCurrentLon(d.getCurrentLon()-1);
						}
						// check if remainingCruiseTime>0, but current location = cruise location
						else {
							System.out.println("Lat="+d.getCruiseLat()+","+d.getCurrentLat());
							System.out.println("Lon="+d.getCruiseLon()+","+d.getCurrentLon());
							System.out.println("RemainingTime="+d.getRemainingCruiseTime());
							System.out.println("Error in Cruise");
							System.exit(0);
						}
						// cruise time minus 1
						d.setRemainingCruiseTime(d.getRemainingCruiseTime()-1);
					}
					else if (d.getRemainingCruiseTime()==0) {
						Random r=new Random();
						// random walk to four direction;
						boolean validCruise=false;
						do{
							double rn=r.nextDouble();
							// east
							if (rn<0.25) {
								int cruiseLat=Math.min(d.getCurrentLat()+randomWalkRange, horizontalRange);
								d.setRemainingCruiseTime(cruiseLat-d.getCurrentLat());
								d.setCruiseLat(cruiseLat);
								d.setCruiseLon(d.getCurrentLon());
							}
							// west
							else if (rn<0.5) {
								int cruiseLat=Math.max(d.getCurrentLat()-randomWalkRange, 0);
								d.setRemainingCruiseTime(d.getCurrentLat()-cruiseLat);
								d.setCruiseLat(cruiseLat);
								d.setCruiseLon(d.getCurrentLon());
							}
							//north
							else if (rn<0.75) {
								int cruiseLon=Math.min(d.getCurrentLon()+randomWalkRange, verticalRange);
								d.setRemainingCruiseTime(cruiseLon-d.getCurrentLon());
								d.setCruiseLat(d.getCurrentLat());
								d.setCruiseLon(cruiseLon);
							}
							//south
							else {
								int cruiseLon=Math.max(d.getCurrentLon()-randomWalkRange, 0);
								d.setRemainingCruiseTime(d.getCurrentLon()-cruiseLon);
								d.setCruiseLat(d.getCurrentLat());
								d.setCruiseLon(cruiseLon);
							}
							// make sure the cruise location is not current location
							if (d.getRemainingCruiseTime()>0) {
								validCruise=true;
							}					
						}while(validCruise==false);
						// check if unsuccessful in generate random walk
						
						if (d.getCruiseLat()==d.getCurrentLat()&&d.getCruiseLon()==d.getCurrentLon()) {
							System.out.println("Lat="+d.getCruiseLat()+","+d.getCurrentLat()+", Lon="+d.getCruiseLon()+","+d.getCurrentLon());
							System.out.println("RemainingTime="+d.getRemainingCruiseTime());
							System.exit(0);
						}
						// check if cruise time is wrong
						if (d.getRemainingCruiseTime()!=(Math.abs(d.getCruiseLat()-d.getCurrentLat()+Math.abs(d.getCruiseLon()-d.getCurrentLon())))) {
							System.out.println("Lat="+d.getCruiseLat()+","+d.getCurrentLat());
							System.out.println("Lon="+d.getCruiseLon()+","+d.getCurrentLon());
							System.out.println("RemainingTime="+d.getRemainingCruiseTime());
							System.out.println("Error in cruise time calculation");
							System.exit(0);
						}
						
						if (d.getRemainingCruiseTime()==0) {
							System.out.println("Error to generate random walk");
							System.exit(0);
						}					
					}
					else {
						System.out.println("Error get remaining cruise time");
						System.exit(0);
					}
				}
				// going to hot area
				else{

				}

			}
			

			// Update online busy drivers status
			for (int j=onlineBusyDriverList.size()-1;j>=0;j--){
				Driver d=onlineBusyDriverList.get(j);
				d.setTimeUntilAvailable(d.getTimeUntilAvailable()-1);

				// if current service finishes
				if (d.getTimeUntilAvailable()==0){
					d.setAvailable(true);
					d.getServedPaxIdList().add(d.getCurrentServePaxId());
					d.setCurrentServePaxId(-1);
					d.setCurrentLat(d.getHeadingLat());
					d.setCurrentLon(d.getHeadingLon());
					d.setHeadingLat(-1);
					d.setHeadingLon(-1);

					// if shiftEndTime<=t, driver becomes offline right after finishing current trip
					if (d.getShiftEndTime()<=t){
						d.setOnline(false);
						d.setAvailable(false);
						finishShiftDriverList.add(d);
						onlineBusyDriverList.remove(j);
					}
					// if shiftEndTime>t, driver becomes online available after finishing current trip
					else{
						onlineAvailableDriverList.add(d);
						onlineBusyDriverList.remove(j);
					}
				}
			}

			//  Matching every matching interval
			if ((t % matchingInterval==0) || (t == simulationLength -1)){

				System.out.println("--------Time: "+hour+":"+min+":"+sec);
				ArrayList<int[]> matchedPair=new ArrayList<int[]>();
				// 0 means random matching, 1 means weighted average
				// 2 means revenue maximization, 3 means distance minimization, 4 means service maximization 
				// 5 means revenue + stable, 6 means distance + stable, 7 means service maximization
				// 8 means compromise 
				if (matchingMode==0){
					randomMatching rm=new randomMatching(waitingPaxList,onlineAvailableDriverList,distanceTimeConversion,maxAllowedPickupTime);
					rm.doMatching();
					matchedPair=rm.getMatchedPairs();
				}
				else if(matchingMode==1) {
					weightedMatching weightedpro=new weightedMatching(waitingPaxList,onlineAvailableDriverList,distanceTimeConversion,maxAllowedPickupTime,weightRevenue,weightPickupTime,weightServiceScore,reverseParameter);
					weightedpro.doMatching();
					matchedPair=weightedpro.getMatchedPairs();
				}
				else if(matchingMode==2) {
					revenueMaximization revenuemax=new revenueMaximization(waitingPaxList,onlineAvailableDriverList,distanceTimeConversion,maxAllowedPickupTime,weightRevenue,weightPickupTime,weightServiceScore,reverseParameter);
					revenuemax.doMatching();
					matchedPair=revenuemax.getMatchedPairs();
				}
				else if(matchingMode==3) {
					pickuptimeMinimization pickupmin=new pickuptimeMinimization(waitingPaxList,onlineAvailableDriverList,distanceTimeConversion,maxAllowedPickupTime,weightRevenue,weightPickupTime,weightServiceScore,reverseParameter);
					pickupmin.doMatching();
					matchedPair=pickupmin.getMatchedPairs();
				}
				else if(matchingMode==4) {
					serviceMaximization servicemax=new serviceMaximization(waitingPaxList,onlineAvailableDriverList,distanceTimeConversion,maxAllowedPickupTime,weightRevenue,weightPickupTime,weightServiceScore,reverseParameter);
					servicemax.doMatching();
					matchedPair=servicemax.getMatchedPairs();
				}
				else if(matchingMode==5) {
					stablerevenueMaximization stablerevenuemax=new stablerevenueMaximization(waitingPaxList,onlineAvailableDriverList,distanceTimeConversion,maxAllowedPickupTime,weightRevenue,weightPickupTime,weightServiceScore,reverseParameter);
					stablerevenuemax.doMatching();
					matchedPair=stablerevenuemax.getMatchedPairs();
				}
				else if(matchingMode==6) {
					stablepickuptimeMinimization stablepickupmin=new stablepickuptimeMinimization(waitingPaxList,onlineAvailableDriverList,distanceTimeConversion,maxAllowedPickupTime,weightRevenue,weightPickupTime,weightServiceScore,reverseParameter);
					stablepickupmin.doMatching();
					matchedPair=stablepickupmin.getMatchedPairs();
				}
				else if(matchingMode==7) {
					stableserviceMaximization stableservicemax=new stableserviceMaximization(waitingPaxList,onlineAvailableDriverList,distanceTimeConversion,maxAllowedPickupTime,weightRevenue,weightPickupTime,weightServiceScore,reverseParameter);
					stableservicemax.doMatching();
					matchedPair=stableservicemax.getMatchedPairs();
				}
				else if(matchingMode==8) {
					compromiseMatching compro=new compromiseMatching(waitingPaxList,onlineAvailableDriverList,distanceTimeConversion,maxAllowedPickupTime,weightRevenue,weightPickupTime,weightServiceScore,reverseParameter);
					compro.doMatching();
					matchedPair=compro.getMatchedPairs();
				}
				else if(matchingMode==9) {
					compromisestablerevenueMaximization comprostable=new compromisestablerevenueMaximization(waitingPaxList,onlineAvailableDriverList,distanceTimeConversion,maxAllowedPickupTime,weightRevenue,weightPickupTime,weightServiceScore,reverseParameter);
					comprostable.doMatching();
					matchedPair=comprostable.getMatchedPairs();
				}
				else if(matchingMode==10) {
					compromisestablepickuptimeMinimization comprostable=new compromisestablepickuptimeMinimization(waitingPaxList,onlineAvailableDriverList,distanceTimeConversion,maxAllowedPickupTime,weightRevenue,weightPickupTime,weightServiceScore,reverseParameter);
					comprostable.doMatching();
					matchedPair=comprostable.getMatchedPairs();
				}
				else if(matchingMode==11) {
					compromisestableserviceMaximization comprostable=new compromisestableserviceMaximization(waitingPaxList,onlineAvailableDriverList,distanceTimeConversion,maxAllowedPickupTime,weightRevenue,weightPickupTime,weightServiceScore,reverseParameter);
					comprostable.doMatching();
					matchedPair=comprostable.getMatchedPairs();
				}
				else if(matchingMode==12) {
					compromiseMatching2 compro=new compromiseMatching2(waitingPaxList,onlineAvailableDriverList,distanceTimeConversion,maxAllowedPickupTime,weightRevenue,weightPickupTime,weightServiceScore,reverseParameter);
					compro.doMatching();
					matchedPair=compro.getMatchedPairs();
				}
				

				// matched pair, int[3] 
				// first value: position of matched passenger in waitingPaxList
				// second value: position of matched driver in onlineAvailableDriverList
				// third value: pickup time between matched passegner and driver

				
				if (matchedPair.size()!=0) {
					for (int[] pair:matchedPair){
						//						System.out.println("matched pair:"+pair[0]+","+pair[1]+","+pair[2]);
						int matchedPaxPosition=pair[0];
						int matchedDriverPosition=pair[1];
						int matchedPickupTime=pair[2];
						Passenger matchedPax=waitingPaxList.get(matchedPaxPosition); // get the passenger 
						Driver matchedDriver=onlineAvailableDriverList.get(matchedDriverPosition); // get the driver
						
						
						// Define the nomalized tranversed pickup time = (Dm - dij)/3
						realtimePickupTime =  realtimePickupTime + (reverseParameter - matchedPickupTime)/reverseParameter2;
						
						// update status of matched passengers 
						matchedPax.setAssign(true);
						matchedPax.setAssignTime(t);
						matchedPax.setPickupTime(matchedPickupTime);
						matchedPax.setServedDriverId(matchedDriver.getId());
						matchedPax.setWaitTimeUntilAssignment(t-matchedPax.getRequestTime());
						matchedPax.setWaitTimeUntilTripStart(matchedPax.getWaitTimeUntilAssignment()+matchedPickupTime);
						matchedPax.setTripStartTime(t+matchedPickupTime);
						matchedPax.setTripEndTime(matchedPax.getTripStartTime()+matchedPax.getTravelTime());
						matchedPax.setServedServiceScore(matchedDriver.getServiceScore());
						
						realtimeRevenue = realtimeRevenue + matchedPax.getRevenue();
						
						
						// update status of matched drivers
						matchedDriver.setAvailable(false);
						matchedDriver.setCurrentServePaxId(matchedPax.getId());
						matchedDriver.setTimeUntilAvailable(matchedPickupTime+matchedPax.getTravelTime());				
						matchedDriver.setHeadingLat(matchedPax.getDestLat());
						matchedDriver.setHeadingLon(matchedPax.getDestLon());
						matchedDriver.setTotIncome(matchedDriver.getTotIncome()+matchedPax.getRevenue());
						matchedDriver.setRemainingCruiseTime(0);
						matchedDriver.setCruiseLat(-1);
						matchedDriver.setCruiseLon(-1);
						
						realtimeServiceScore = realtimeServiceScore + matchedDriver.getServiceScore();
					}
					
					//update the adaptive weights for matching
					
					if (t <= simulationLength/2) {
					    weightPickupTime = Math.max((t/matchingInterval + 1)*targetPickupTime[0] - realtimePickupTime, 1.0)/(t/matchingInterval + 1);
					    weightRevenue = Math.max((t/matchingInterval + 1)*targetRevenue[0] - realtimeRevenue, 1.0)/(t/matchingInterval + 1);
					    weightServiceScore = Math.max((t/matchingInterval + 1)*targetServiceScore[0] - realtimeServiceScore, 1.0)/(t/matchingInterval + 1);
					}
					else {
						weightPickupTime = Math.max((t/matchingInterval + 1)*targetPickupTime[1] - realtimePickupTime, 1.0)/(t/matchingInterval + 1);
						weightRevenue = Math.max((t/matchingInterval + 1)*targetRevenue[1] - realtimeRevenue, 1.0)/(t/matchingInterval + 1);
						weightServiceScore = Math.max((t/matchingInterval + 1)*targetServiceScore[1] - realtimeServiceScore, 1.0)/(t/matchingInterval + 1);	
					}
					

					// move matched passengers from waitingPaxList to inServicePaxList, 
					for (int i=waitingPaxList.size()-1;i>=0;i--){
						Passenger p=waitingPaxList.get(i);
						if (p.isAssign()==true){
							inServicePaxList.add(p);
							waitingPaxList.remove(i);
						}
					}

					// move matched drivers from onlineAvailableDriverList to onlineBusyDriverList
					for (int j=onlineAvailableDriverList.size()-1;j>=0;j--){
						Driver d=onlineAvailableDriverList.get(j);
						if (d.isAvailable()==false){
							onlineBusyDriverList.add(d);
							onlineAvailableDriverList.remove(j);
						}
					}
				}			
			}

			//				System.out.println("----Ending of t="+t);
			//				
			//				System.out.println("waitingPaxSize="+waitingPaxList.size());
			//				System.out.println("cancelPaxSize="+cancelPaxList.size());
			//				System.out.println("inServicePax="+inServicePaxList.size());
			//				System.out.println("finishServicePax="+finishPaxList.size());
			//				System.out.println("busyDriver="+onlineBusyDriverList.size());
			//				System.out.println("availableDriver="+onlineAvailableDriverList.size());
			//				System.out.println("offlineDriver="+finishShiftDriverList.size());

		}

		// generate demand profile after simulation
		for (Passenger p:waitingPaxList){
			demandEnding.add(p);
		}
		for (Passenger p:inServicePaxList){
			demandEnding.add(p);
		}
		for (Passenger p:finishPaxList){
			demandEnding.add(p);
		}
		for (Passenger p:cancelPaxList){
			demandEnding.add(p);
		}

		// generate supply profile after simulation
		for (Driver d:onlineAvailableDriverList){
			supplyEnding.add(d);
		}
		for (Driver d:onlineBusyDriverList){
			supplyEnding.add(d);
		}
		for (Driver d:finishShiftDriverList){
			supplyEnding.add(d);
		}
		
		// add the mathcing visualization tool


	}

	public void computePerformance(){

		totNumServedPax=inServicePaxList.size()+finishPaxList.size();
		totNumCancelledPax=cancelPaxList.size();
		totNumFinishedPax=finishPaxList.size();
		servedProb=(double)(totNumServedPax)/totNumPax;

		int totPickupTime=0;
		double totTravPickupTime=0;
		int totServiceScore=0;
		int totRevenue=0;
		
		for (Passenger p: inServicePaxList){
			totPickupTime+=p.getPickupTime();
			totTravPickupTime+=(double)(reverseParameter - p.getPickupTime())/reverseParameter2;
			totServiceScore+=p.getServedServiceScore();
			totRevenue+=p.getRevenue();
		}
		for (Passenger p: finishPaxList){
			totPickupTime+=p.getPickupTime();
			totTravPickupTime+=(double)(reverseParameter - p.getPickupTime())/reverseParameter2;
			totServiceScore+=p.getServedServiceScore();
			totRevenue+=p.getRevenue();
		}
		avgPickupTime=(double)(totPickupTime)/(simulationLength/matchingInterval + 1); //totNumServedPax;
		avgTravPickupTime = (double)(totTravPickupTime)/(simulationLength/matchingInterval + 1); //totNumServedPax;
		avgServiceScore=(double)(totServiceScore)/(simulationLength/matchingInterval + 1); //totNumServedPax;
		avgRevenue=(double)(totRevenue)/(simulationLength/matchingInterval + 1); //totNumServedPax;	
		System.out.println("totDemand="+totNumPax);
		System.out.println("totServed="+totNumServedPax);
		System.out.println("Served Ratio="+servedProb);
		System.out.println("Avg Pickup Time="+avgPickupTime);
		System.out.println("Avg Tranversed Pickup Time="+avgTravPickupTime);
		System.out.println("Avg Service Score="+avgServiceScore);
		System.out.println("Avg Revenue="+avgRevenue);
	}


	public void writeOutput() throws IOException {

		// FileWriter fwPax = new FileWriter("C:\\Users\\User\\Dropbox\\Data Backup for Windows CP\\Documents\\JavaCoding\\MSOM_compromise_matching\\Passenger_MM_02.csv");
		FileWriter fwPax = new FileWriter(System.getProperty("user.dir") + "\\RideMatching_2020\\Results\\Passenger_MM_02.csv");
		fwPax.write("id,requestTime, originLat, originLon,destLat,destLon,distanceTimeConversion,patience,travelTime,"
				+ "cancelTime,assignTime,pickupTime,waitTimeUntilAssignment,waitTimeUntilTripStart,tripStartTime,"
				+ "tripEndTime,cancel,assign,finish,revenue,servedDriverId,servedServiceScore\n");
		for (Passenger p:demandEnding){
			fwPax.write(p.getId()+","+p.getRequestTime()+","+p.getOriginLat()+","+p.getOriginLon()+","+p.getDestLat()+","+p.getDestLon()+
					","+p.getDistanceTimeConversion()+","+p.getPatience()+","+p.getTravelTime()+","+p.getCancelTime()+","+p.getAssignTime()+
					","+p.getPickupTime()+","+p.getWaitTimeUntilAssignment()+","+p.getWaitTimeUntilTripStart()+","+p.getTripStartTime()+
					","+p.getTripEndTime()+","+p.isCancel()+","+p.isAssign()+","+p.isFinish()+","+p.getRevenue()+","+p.getServedDriverId()+
					","+p.getServedServiceScore()+"\n");
		}
		fwPax.close();	

		// FileWriter fwDriver = new FileWriter("C:\\Users\\User\\Dropbox\\Data Backup for Windows CP\\Documents\\JavaCoding\\MSOM_compromise_matching\\Driver_MM_02.csv");
		FileWriter fwDriver = new FileWriter(System.getProperty("user.dir") + "\\RideMatching_2020\\Results\\Driver_MM_02.csv");

		fwDriver.write("id,serviceScore, shiftStartTime, shiftEndTime,shiftDuration,currentLat,currentLon,headingLat,headingLon,"
				+ "available,emptyCarMode,currentServePaxId,timeUntilAvailable,online, number of served pax,"
				+ "totIncome,avg income per pax\n");
		for (Driver d:supplyEnding){
			int noOfServedPax=d.getServedPaxIdList().size();
			if (d.getCurrentServePaxId()!=-1) {
				noOfServedPax++;
			}
			fwDriver.write(d.getId()+","+d.getServiceScore()+","+d.getShiftStartTime()+","+d.getShiftEndTime()+","+d.getShiftDuration()+
					","+d.getCurrentLat()+","+d.getCurrentLon()+","+d.getHeadingLat()+","+d.getHeadingLon()+","+d.isAvailable()+
					","+d.getEmptyCarMode()+","+d.getCurrentServePaxId()+","+d.getTimeUntilAvailable()+","+d.isOnline()+
					","+noOfServedPax+","+d.getTotIncome()+","+(double)(d.getTotIncome())/noOfServedPax+"\n");
		}
		fwDriver.close();	


	}

	public double getServedProb() {
		return servedProb;
	}

	public double getAvgPickupTime() {
		return avgPickupTime;
	}

	public double getAvgServiceScore() {
		return avgServiceScore;
	}

	public double getAvgRevenue() {
		return avgRevenue;
	}

	public int getTotNumPax() {
		return totNumPax;
	}

	public int getTotNumCancelledPax() {
		return totNumCancelledPax;
	}

	public int getTotNumFinishedPax() {
		return totNumFinishedPax;
	}

	public ArrayList<Passenger> getDemandEnding() {
		return demandEnding;
	}

	public ArrayList<Driver> getSupplyEnding() {
		return supplyEnding;
	}




}
