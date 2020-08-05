
import java.util.ArrayList;

public class Driver {

	private int id;
	private int serviceScore;
	private int shiftStartTime; // time to get online and start working shift
	private int shiftEndTime;// time to get offline and end working shift
	private int shiftDuration;// time duration from shift start to shift end
	private int currentLat;// latitude of current location
	private int currentLon;// longitude of current location
	private int headingLat;// latitude of heading location (passenger destination)
	private int headingLon;// longitude of heading location (passenger destination)
	private boolean available;// indicator, if available
	private int emptyCarMode;// empty car behavior. 
	private int currentServePaxId;// passenger id of current onboard passenger
	private int timeUntilAvailable;// remaining time until available, i.e., remaining travel time of current onboard passenger
	private ArrayList<Integer> servedPaxIdList;// id list of served passengers
	private boolean online;// indicator, if online or not
	private int totIncome;
	private int remainingCruiseTime;// remaining time until current cruise
	private int cruiseLat;// cruise destination latitude
	private int cruiseLon;// cruise destination longitude
	
	public Driver (int id, int serviceScore, int shiftStartTime, int shiftEndTime, int currentLat, int currentLon, int emptyCarMode){
		this.id=id;
		this.serviceScore=serviceScore;
		this.shiftStartTime=shiftStartTime;
		this.shiftEndTime=shiftEndTime;
		this.shiftDuration=shiftEndTime-shiftStartTime;
		this.currentLat=currentLat;
		this.currentLon=currentLon;
		this.headingLat=-1;
		this.headingLon=-1;
		this.available=false;
		this.emptyCarMode=emptyCarMode;
		this.currentServePaxId=-1;
		this.timeUntilAvailable=-100;
		this.servedPaxIdList=new ArrayList<Integer>();
		this.online=false;
		this.cruiseLat=-1;
		this.cruiseLon=-1;
		this.remainingCruiseTime=0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getShiftStartTime() {
		return shiftStartTime;
	}

	public void setShiftStartTime(int shiftStartTime) {
		this.shiftStartTime = shiftStartTime;
	}

	public int getShiftEndTime() {
		return shiftEndTime;
	}

	public void setShiftEndTime(int shiftEndTime) {
		this.shiftEndTime = shiftEndTime;
	}

	public int getShiftDuration() {
		return shiftDuration;
	}

	public void setShiftDuration(int shiftDuration) {
		this.shiftDuration = shiftDuration;
	}

	public int getCurrentLat() {
		return currentLat;
	}

	public void setCurrentLat(int currentLat) {
		this.currentLat = currentLat;
	}

	public int getCurrentLon() {
		return currentLon;
	}

	public void setCurrentLon(int currentLon) {
		this.currentLon = currentLon;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public int getEmptyCarMode() {
		return emptyCarMode;
	}

	public void setEmptyCarMode(int emptyCarMode) {
		this.emptyCarMode = emptyCarMode;
	}

	public int getCurrentServePaxId() {
		return currentServePaxId;
	}

	public void setCurrentServePaxId(int currentServePaxId) {
		this.currentServePaxId = currentServePaxId;
	}

	public int getTimeUntilAvailable() {
		return timeUntilAvailable;
	}

	public void setTimeUntilAvailable(int timeUntilAvailable) {
		this.timeUntilAvailable = timeUntilAvailable;
	}

	public ArrayList<Integer> getServedPaxIdList() {
		return servedPaxIdList;
	}

	public void setServedPaxIdList(ArrayList<Integer> servedPaxIdList) {
		this.servedPaxIdList = servedPaxIdList;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public int getServiceScore() {
		return serviceScore;
	}

	public void setServiceScore(int serviceScore) {
		this.serviceScore = serviceScore;
	}

	public int getHeadingLat() {
		return headingLat;
	}

	public void setHeadingLat(int headingLat) {
		this.headingLat = headingLat;
	}

	public int getHeadingLon() {
		return headingLon;
	}

	public void setHeadingLon(int headingLon) {
		this.headingLon = headingLon;
	}

	public int getTotIncome() {
		return totIncome;
	}

	public void setTotIncome(int totIncome) {
		this.totIncome = totIncome;
	}

	public int getRemainingCruiseTime() {
		return remainingCruiseTime;
	}

	public void setRemainingCruiseTime(int remainingCruiseTime) {
		this.remainingCruiseTime = remainingCruiseTime;
	}

	public int getCruiseLat() {
		return cruiseLat;
	}

	public void setCruiseLat(int cruiseLat) {
		this.cruiseLat = cruiseLat;
	}

	public int getCruiseLon() {
		return cruiseLon;
	}

	public void setCruiseLon(int cruiseLon) {
		this.cruiseLon = cruiseLon;
	}
	
	
	
	
}
