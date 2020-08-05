
import java.io.IOException;
import java.util.ArrayList;

public class mainSim {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		int matchingInterval = 6; // matching is performed for every 6 time units
		int maxAllowedPickupTime = 240; // time unit --> 360s = 3km; 1h = 30km
		int distanceTimeConversion = 1; // 1 distance unit to x time unit; here x = 1
		int emptyCarMode = 2; // 1 means stay in previous area, 2 means random walk
		int matchingMode = 8; // choosing different matching policies
		// 0 means random matching, 1 means weighted average
		// 2 means revenue maximization, 3 means distance minimization, 4 means service maximization 
		// 5 means revenue + stable, 6 means distance + stable, 7 means service maximization
		// 8 means compromise with full (3) targets
		// 12 means compromise matching with 2 targets
		int simulationLength = 1*10*720;// length of simulation in time units, here we use two hours = 7200 time units
		int patience = 120;// uniform [6-120]
		int randomWalkRange = 120;// time unit --> random walk in 3km/6
		int horizontalRange = 5244; // simulation area horizontal length in distance unit (map zone)
		int verticalRange = 3873; // simulation area vertical length in distance unit (map zone)
		int numberOfRequest = 47141; // 53916; // for toy demand/supply generator, total number of passengers
		int numberOfDriver = 12018; // for toy demand/supply generator, total number of drivers
		int revenueBound = 3000;// for toy demand/supply generator, uniform distributed revenue from 0 to x
		
		// reformulate the distance gap
		double reverseParameter = 480; //480;
		double gamma = 1.5; // the target below is estimated from historical data
		// double targetPickupTime = 1500;// (in the reversed way) target pickup time 
		// double targetReversePickupTime = 4951 * gamma; // 758; //reverseParameter - targetPickupTime;
		// double targetServiceScore = 5440 * gamma; // 756;// target service score 
		// double targetRevenue = 4239 * gamma; // 701;// target revenue 
		
		/*
		double[] targetReversePickupTime = {4523, 5347}; 
		double[] targetServiceScore = {5069,5831};
		double[] targetRevenue = {3867,4526}; 
		*/
		
		/*
		double[] targetReversePickupTime = {4951, 4951}; 
		double[] targetServiceScore = {5440,5440};
		double[] targetRevenue = {4239,4239}; 
		*/
		
		/* offpeak
		double[] targetReversePickupTime = {4513*gamma, 4513*gamma}; 
		double[] targetServiceScore = {5051*gamma,5051*gamma};
		double[] targetRevenue = {3880*gamma,3880*gamma}; 
		*/		
		
		double[] targetReversePickupTime = {4915*gamma, 4915*gamma}; 
		double[] targetServiceScore = {5439*gamma,5439*gamma};
		double[] targetRevenue = {4185*gamma,4185*gamma}; 
		
		// Generate the demand & supply
		toyDemandSupply ds = new toyDemandSupply(horizontalRange,verticalRange,simulationLength,
				numberOfRequest,numberOfDriver,distanceTimeConversion,patience,revenueBound,emptyCarMode);
		ds.generateToy();
		
		// Get the demand and supply information
		ArrayList<Passenger> demandProfile=ds.getDemandProfile();
		ArrayList<Driver> supplyProfile=ds.getSupplyProfile();
		System.out.println("demand="+demandProfile.size());
		System.out.println("supply="+supplyProfile.size());
		
		// Start the simulation
		Simulator sim=new Simulator(horizontalRange,verticalRange,demandProfile, supplyProfile, emptyCarMode, matchingMode,  matchingInterval,  
				maxAllowedPickupTime, distanceTimeConversion, randomWalkRange,simulationLength, targetRevenue, targetReversePickupTime, targetServiceScore, reverseParameter);
		
		sim.sim();
		sim.computePerformance();	
		sim.writeOutput();
		
	}

}
