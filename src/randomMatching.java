import java.util.ArrayList;

public class randomMatching {

	private ArrayList<Passenger> paxList;
	private ArrayList<Driver> driverList;
	private ArrayList<int[]> matchedPairs;
	private int distanceTimeConversion;
	private int maxAllowedPickupTime;
	
	public randomMatching(ArrayList<Passenger> paxList, ArrayList<Driver> driverList, int distanceTimeConversion, int maxAllowedPickupTime) {
		super();
		this.paxList = paxList;
		this.driverList = driverList;
		this.matchedPairs = new ArrayList<int[]>();
		this.distanceTimeConversion=distanceTimeConversion;
		this.maxAllowedPickupTime=maxAllowedPickupTime;
	}
	
	public void doMatching(){
		
		for (int i=0;i<paxList.size();i++){
			Passenger p=paxList.get(i);
			for (int j=0;j<driverList.size();j++){
				Driver d=driverList.get(j);
				if (d.isAvailable()==true){
					int pickupTime=(Math.abs(d.getCurrentLat()-p.getOriginLat())+Math.abs(d.getCurrentLon()-p.getOriginLon()))*distanceTimeConversion;
					if (pickupTime<maxAllowedPickupTime){
						if (pickupTime==0){
							pickupTime++;
						}
						int[] newPair=new int[]{i,j,pickupTime};
						d.setAvailable(false);
						matchedPairs.add(newPair);
						break;
					}
				}
			}
		}	
	}

	public ArrayList<int[]> getMatchedPairs() {
		return matchedPairs;
	}

}
