

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class toyDemandSupply {

	private int lat;
	private int lon;
	private int simulationLength;
	private int totPax;
	private int totDriver;
	private ArrayList<Passenger> demandProfile;
	private ArrayList<Driver> supplyProfile;
	private int revenueUpp;
	private int patience;
	private int distanceTimeConversion;
	private int emptyCarMode;
	
	public toyDemandSupply(int lat, int lon, int simulationLength, int totPax, int totDriver, int distanceTimeConversion,
			int patience,int revenueUpp, int emptyCarMode) {
		super();
		this.lat = lat;
		this.lon = lon;
		this.simulationLength = simulationLength;
		this.totPax = totPax;
		this.totDriver = totDriver;
		this.demandProfile=new ArrayList<Passenger>();
		this.supplyProfile=new ArrayList<Driver>();
		this.distanceTimeConversion=distanceTimeConversion;
		this.patience=patience;
		this.revenueUpp=revenueUpp;
		this.emptyCarMode=emptyCarMode;
	}
	
	public void generateToy(){
		
		
		// Random r=new Random();
		// read passengers information
		readPassenger paxInfo = new readPassenger();
		String[][] pax_info = paxInfo.read_passenger();
		// 1:ID, 2:order_slot, 3:start_lon, 4:start_lat, 5:end_lon, 6:end_lat, 7:patience, 8:revenue, 9: nromalized revenue
		
		System.out.println(totPax);
		for (int i=0;i<totPax;i++){
			// System.out.println(i + ", " + Arrays.toString(pax_info[i]) );
			int originLon = Integer.parseInt(pax_info[i][3-1]); // (int)(lon*r.nextDouble());		
			int originLat = Integer.parseInt(pax_info[i][4-1]); //(int)(lat*r.nextDouble());
			int destLon = Integer.parseInt(pax_info[i][5-1]); //(int)(lon*r.nextDouble());
			int destLat = Integer.parseInt(pax_info[i][6-1]); //(int)(lat*r.nextDouble());
			int requestTime = Integer.parseInt(pax_info[i][2-1]); //(int)(simulationLength*r.nextDouble());
			int revenue = Integer.parseInt(pax_info[i][9-1]); //1+(int)(revenueUpp*r.nextDouble());
			int patience_data = Integer.parseInt(pax_info[i][7-1]);
			Passenger p = new Passenger (i, requestTime, originLat, originLon, destLat, destLon, distanceTimeConversion, patience_data,revenue);
			if (p.getTravelTime()>3){// to avoid situation of overlap origin and destination
			demandProfile.add(p);
			}	
		}
		
		// read drivers information
		readDriver driverInfo = new readDriver();
		String[][] driver_info = driverInfo.read_driver();
		// 1:ID, 2:nomalized_start_lon, 3:nomalized_start_lat, 4:shiftStartTime, 5:shiftEndTime, 6:service_score

		
		for (int j=0;j<totDriver;j++){
			int serviceScore = Integer.parseInt(driver_info[j][6-1]); //(int)(100*r.nextDouble());
			int shiftStartTime = Integer.parseInt(driver_info[j][4-1]); //1+(int)(simulationLength*r.nextDouble());
			int shiftEndTime = Integer.parseInt(driver_info[j][5-1]); //shiftStartTime+100+(int)(simulationLength*r.nextDouble());
			int currentLon = Integer.parseInt(driver_info[j][2-1]); //(int)(lon*r.nextDouble());
			int currentLat = Integer.parseInt(driver_info[j][3-1]); //(int)(lat*r.nextDouble());
			Driver d=new Driver(j,serviceScore, shiftStartTime,shiftEndTime,currentLat, currentLon,emptyCarMode);
			supplyProfile.add(d);
		}	
	}

	public ArrayList<Passenger> getDemandProfile() {
		return demandProfile;
	}

	public ArrayList<Driver> getSupplyProfile() {
		return supplyProfile;
	}
	
	
	
	
	
}
