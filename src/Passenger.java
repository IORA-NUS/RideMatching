

public class Passenger {

	private int id;
	private int requestTime; //booking time
	private int originLat;// latitude of origin
	private int originLon;// longitude of origin
	private int destLat;// latitude of destination
	private int destLon;// longitude of destination
	private int distanceTimeConversion;// parameter to converte distance to travel time
	private int patience;// maximum waiting time of passengers before assignment
	private int travelTime;// travel time from trip origin to destination
	private int cancelTime;// time to cancel order if not assigned a driver
	private int assignTime;// time to get assignment
	private int pickupTime;// pickup time from assigned driver to passenger origin
	private int waitTimeUntilAssignment;// waiting time from request to assignment
	private int waitTimeUntilTripStart;// waiting time from request to trip start
	private int tripStartTime;// time be picked up and start the trip
	private int tripEndTime;// time to reach destination and end the trip
	private boolean cancel;// indicator, if cancel trip
	private boolean assign;// indicator, if be assigned a driver
	private boolean finish;// indicator, if finish trip
	private int revenue;// order revenue
	private int servedDriverId;// assigned driver id
	private int servedServiceScore;// assigned driver service score
	
	public Passenger (int id, int requestTime, int originLat, int originLon, int destLat, int destLon, int distanceTimeConversion, int patience, int revenue){
		this.id=id;
		this.requestTime=requestTime;
		this.originLat=originLat;
		this.originLon=originLon;
		this.destLat=destLat;
		this.destLon=destLon;
		this.distanceTimeConversion=distanceTimeConversion;
		this.patience=patience;
		this.cancelTime=requestTime+patience;  // cancel before being matched
		this.travelTime=(Math.abs(destLat-originLat)+Math.abs(destLon-originLon))*distanceTimeConversion;
		this.assignTime=-1;
		this.pickupTime=-1;
		this.waitTimeUntilAssignment=-1;
		this.waitTimeUntilTripStart=-1;
		this.tripStartTime=-1;
		this.tripEndTime=-1;
		this.cancel=false;
		this.assign=false;
		this.finish=false;
		this.revenue=revenue;
		this.servedDriverId=-1;
		this.servedServiceScore=-1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(int requestTime) {
		this.requestTime = requestTime;
	}

	public int getOriginLat() {
		return originLat;
	}

	public void setOriginLat(int originLat) {
		this.originLat = originLat;
	}

	public int getOriginLon() {
		return originLon;
	}

	public void setOriginLon(int originLon) {
		this.originLon = originLon;
	}

	public int getDestLat() {
		return destLat;
	}

	public void setDestLat(int destLat) {
		this.destLat = destLat;
	}

	public int getDestLon() {
		return destLon;
	}

	public void setDestLon(int destLon) {
		this.destLon = destLon;
	}

	public int getDistanceTimeConversion() {
		return distanceTimeConversion;
	}

	public void setDistanceTimeConversion(int distanceTimeConversion) {
		this.distanceTimeConversion = distanceTimeConversion;
	}

	public int getPatience() {
		return patience;
	}

	public void setPatience(int patience) {
		this.patience = patience;
	}

	public int getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(int travelTime) {
		this.travelTime = travelTime;
	}

	public int getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(int cancelTime) {
		this.cancelTime = cancelTime;
	}

	public int getAssignTime() {
		return assignTime;
	}

	public void setAssignTime(int assignTime) {
		this.assignTime = assignTime;
	}

	public int getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(int pickupTime) {
		this.pickupTime = pickupTime;
	}

	public int getWaitTimeUntilAssignment() {
		return waitTimeUntilAssignment;
	}

	public void setWaitTimeUntilAssignment(int waitTimeUntilAssignment) {
		this.waitTimeUntilAssignment = waitTimeUntilAssignment;
	}

	public int getWaitTimeUntilTripStart() {
		return waitTimeUntilTripStart;
	}

	public void setWaitTimeUntilTripStart(int waitTimeUntilTripStart) {
		this.waitTimeUntilTripStart = waitTimeUntilTripStart;
	}

	public int getTripStartTime() {
		return tripStartTime;
	}

	public void setTripStartTime(int tripStartTime) {
		this.tripStartTime = tripStartTime;
	}

	public int getTripEndTime() {
		return tripEndTime;
	}

	public void setTripEndTime(int tripEndTime) {
		this.tripEndTime = tripEndTime;
	}

	public boolean isAssign() {
		return assign;
	}

	public void setAssign(boolean assign) {
		this.assign = assign;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	public boolean isCancel() {
		return cancel;
	}

	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	public int getRevenue() {
		return revenue;
	}

	public void setRevenue(int revenue) {
		this.revenue = revenue;
	}

	public int getServedDriverId() {
		return servedDriverId;
	}

	public void setServedDriverId(int servedDriverId) {
		this.servedDriverId = servedDriverId;
	}

	public int getServedServiceScore() {
		return servedServiceScore;
	}

	public void setServedServiceScore(int servedServiceScore) {
		this.servedServiceScore = servedServiceScore;
	}

	
	
}
