import java.util.ArrayList;
import gurobi.*;

public class serviceMaximization {

	private ArrayList<Passenger> paxList;
	private ArrayList<Driver> driverList;
	private ArrayList<int[]> matchedPairs;
	private int distanceTimeConversion;
	private int maxAllowedPickupTime;
	private double weightPickupTime;// weight pickup time
	private double weightServiceScore;// weight service score
	private double weightRevenue;// weight revenue
	private double reverseParameter;

	public serviceMaximization(ArrayList<Passenger> paxList, ArrayList<Driver> driverList, int distanceTimeConversion, int maxAllowedPickupTime, double weightRevenue, double weightPickupTime, double weightServiceScore, double reverseParameter) {
		super();
		this.paxList = paxList;
		this.driverList = driverList;
		this.matchedPairs = new ArrayList<int[]>();
		this.distanceTimeConversion=distanceTimeConversion;
		this.maxAllowedPickupTime=maxAllowedPickupTime;
		this.weightPickupTime = weightPickupTime;
		this.weightRevenue = weightRevenue;
		this.weightServiceScore = weightServiceScore;
		this.reverseParameter = reverseParameter;
	}

	public void doMatching(){


		// Define the pickup time matrix
		int[][] pickupTime = new int[paxList.size()][driverList.size()];

		ArrayList<Driver> driverListPotential = new ArrayList<Driver>();
		ArrayList<Integer> driverPositionMap = new ArrayList<Integer>();
		
		double maxservice = 0.0;
		
		for (int j = 0; j < driverList.size(); j++) {
			Driver d=driverList.get(j);
			for (int i = 0; i < paxList.size(); i++){
				Passenger p = paxList.get(i);
				pickupTime[i][j] = ((Math.abs(d.getCurrentLat()-p.getOriginLat())+Math.abs(d.getCurrentLon()-p.getOriginLon()))*distanceTimeConversion);
				if (pickupTime[i][j] < maxAllowedPickupTime){
					driverListPotential.add(d);
					driverPositionMap.add(j);// j is the position of driver in driverList
					break;
				}
			}
		}
			


		// Use gurobi to solve the service maximization problem
		try {
			GRBEnv env = new GRBEnv();
			GRBModel servmaxModel = new GRBModel(env);
			servmaxModel.set(GRB.StringAttr.ModelName, "Service Maximization Matching");
			
			int[][] reducedPickupTime=new int[paxList.size()][driverListPotential.size()];
			// Introduce decision variables
			GRBVar[][] x = new GRBVar[paxList.size()][driverListPotential.size()];
			for (int j = 0; j < driverListPotential.size(); j++) {
				Driver d = driverListPotential.get(j);
				for (int i = 0; i < paxList.size(); i++){
					Passenger p = paxList.get(i);
					reducedPickupTime[i][j] = ((Math.abs(d.getCurrentLat()-p.getOriginLat())+Math.abs(d.getCurrentLon()-p.getOriginLon()))*distanceTimeConversion);
					if (reducedPickupTime[i][j] < maxAllowedPickupTime){
						x[i][j] = servmaxModel.addVar(0, 1.0, d.getServiceScore(), GRB.BINARY, "x" + i + j);
					}
					else {
						x[i][j] = servmaxModel.addVar(0, 0.0, 1.0, GRB.BINARY, "x" + i + j);
					}
				}
			}

			//System.out.println("Wait Pax="+paxList.size()+", Idle Driver="+driverList.size()+", Potential Driver="+driverListPotential.size());


			// The objective is to maximize the total integrated revenue
			servmaxModel.set(GRB.IntAttr.ModelSense, -1);
			// Update model to integrate new variables
			servmaxModel.update();		       

			for (int i = 0; i < paxList.size(); ++i){
				GRBLinExpr expr_link = new GRBLinExpr();
				for (int j = 0; j < driverListPotential.size(); ++j){
					expr_link.addTerm(1.0, x[i][j]);
				}
				servmaxModel.addConstr(expr_link, GRB.LESS_EQUAL, 1.0, "expr_link"+i);
			}

			for (int j = 0; j < driverListPotential.size(); ++j){
				GRBLinExpr expr_link_2 = new GRBLinExpr();
				for (int i = 0; i < paxList.size(); ++i){
					expr_link_2.addTerm(1.0, x[i][j]);
				}
				servmaxModel.addConstr(expr_link_2, GRB.LESS_EQUAL, 1.0, "expr_link_2"+j);
			}

			// Solve
			servmaxModel.optimize();
			
			maxservice = servmaxModel.get(GRB.DoubleAttr.ObjVal);

			servmaxModel.dispose();
			env.dispose();
			

			GRBEnv env2 = new GRBEnv();
			GRBModel servmaxModel2 = new GRBModel(env2);
			servmaxModel2.set(GRB.StringAttr.ModelName, "Service Maximization Matching & Distance Minimization");
			
			// Introduce decision variables
			GRBVar[][] y = new GRBVar[paxList.size()][driverListPotential.size()];
			for (int j = 0; j < driverListPotential.size(); j++) {
				for (int i = 0; i < paxList.size(); i++){
					if (reducedPickupTime[i][j] < maxAllowedPickupTime){
						y[i][j] = servmaxModel2.addVar(0, 1.0, (reverseParameter - reducedPickupTime[i][j])/2, GRB.BINARY, "y" + i + j);
					}
					else {
						y[i][j] = servmaxModel2.addVar(0, 0.0, 1.0, GRB.BINARY, "y" + i + j);
					}
				}
			}

			System.out.println("Wait Pax="+paxList.size()+", Idle Driver="+driverList.size()+", Potential Driver="+driverListPotential.size());


			// The objective is to maximize the total integrated revenue
			servmaxModel2.set(GRB.IntAttr.ModelSense, -1);
			// Update model to integrate new variables
			servmaxModel2.update();		       

			for (int i = 0; i < paxList.size(); ++i){
				GRBLinExpr expr_link = new GRBLinExpr();
				for (int j = 0; j < driverListPotential.size(); ++j){
					expr_link.addTerm(1.0, y[i][j]);
				}
				servmaxModel2.addConstr(expr_link, GRB.LESS_EQUAL, 1.0, "expr_link"+i);
			}

			for (int j = 0; j < driverListPotential.size(); ++j){
				GRBLinExpr expr_link_2 = new GRBLinExpr();
				for (int i = 0; i < paxList.size(); ++i){
					expr_link_2.addTerm(1.0, y[i][j]);
				}
				servmaxModel2.addConstr(expr_link_2, GRB.LESS_EQUAL, 1.0, "expr_link_2"+j);
			}
			
			// service constraints
			GRBLinExpr expr_service = new GRBLinExpr();
			for (int j = 0; j < driverListPotential.size(); ++j){
				Driver d = driverListPotential.get(j);
				for (int i = 0; i < paxList.size(); ++i){
					expr_service.addTerm(d.getServiceScore(), y[i][j]);
				}
			}
			servmaxModel2.addConstr(expr_service, GRB.GREATER_EQUAL, maxservice, "expr_service");


			// Solve
			servmaxModel2.optimize();


			// Double[][] compSolution = new Double[paxList.size()][driverListPotential.size()];
			for (int i = 0; i < paxList.size(); i++){
				// Passenger p = paxList.get(i);
				for (int j = 0; j < driverListPotential.size(); j++){
					// Driver d = driverListPotential.get(j);
					if (y[i][j].get(GRB.DoubleAttr.X) > 0.5) {
						int[] newPair = new int[]{i,driverPositionMap.get(j),reducedPickupTime[i][j]};
						// d.setAvailable(false);
						matchedPairs.add(newPair);
						break;
					}
				}
			}

			System.out.println("matched pair size="+matchedPairs.size()+", tot wait pax="+paxList.size());
			

			servmaxModel2.dispose();
			env2.dispose();


		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}

	} // end the matching function

	public ArrayList<int[]> getMatchedPairs() {
		return matchedPairs;
	}

}
