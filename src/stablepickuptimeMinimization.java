import java.util.ArrayList;
import gurobi.*;

public class stablepickuptimeMinimization {

	private ArrayList<Passenger> paxList;
	private ArrayList<Driver> driverList;
	private ArrayList<int[]> matchedPairs;
	private int distanceTimeConversion;
	private int maxAllowedPickupTime;
	private double weightPickupTime;// weight pickup time
	private double weightServiceScore;// weight service score
	private double weightRevenue;// weight revenue
	private double reverseParameter;

	public stablepickuptimeMinimization(ArrayList<Passenger> paxList, ArrayList<Driver> driverList, int distanceTimeConversion, int maxAllowedPickupTime, double weightRevenue, double weightPickupTime, double weightServiceScore, double reverseParameter) {
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
			


		// Use gurobi to solve the pick up time minimization problem
		try {
			GRBEnv env = new GRBEnv();
			GRBModel stablepickminModel = new GRBModel(env);
			stablepickminModel.set(GRB.StringAttr.ModelName, "Pickup Time Minimization Matching with Stability Constraints");
			
			int[][] reducedPickupTime = new int[paxList.size()][driverListPotential.size()];
			int[] revenueTerm = new int[paxList.size()];
			int[] serviceTerm = new int[driverListPotential.size()];

			// Introduce decision variables
			GRBVar[][] x = new GRBVar[paxList.size()][driverListPotential.size()];
			for (int j = 0; j < driverListPotential.size(); j++) {
				Driver d = driverListPotential.get(j);
				serviceTerm[j] = d.getServiceScore();
				for (int i = 0; i < paxList.size(); i++){
					Passenger p = paxList.get(i);
					revenueTerm[i] = p.getRevenue();
					reducedPickupTime[i][j] = ((Math.abs(d.getCurrentLat()-p.getOriginLat())+Math.abs(d.getCurrentLon()-p.getOriginLon()))*distanceTimeConversion);
					if (reducedPickupTime[i][j] < maxAllowedPickupTime){
						x[i][j] = stablepickminModel.addVar(0, 1.0, (reverseParameter - reducedPickupTime[i][j])/2, GRB.BINARY, "x" + i + j);
					}
					else {
						x[i][j] = stablepickminModel.addVar(0, 0.0, 1.0, GRB.BINARY, "x" + i + j);
					}
				}
			}

			System.out.println("Wait Pax="+paxList.size()+", Idle Driver="+driverList.size()+", Potential Driver="+driverListPotential.size());


			// The objective is to minimize the pick up time
			stablepickminModel.set(GRB.IntAttr.ModelSense, -1);
			// Update model to integrate new variables
			stablepickminModel.update();		       

			for (int i = 0; i < paxList.size(); ++i){
				GRBLinExpr expr_link = new GRBLinExpr();
				for (int j = 0; j < driverListPotential.size(); ++j){
					expr_link.addTerm(1.0, x[i][j]);
				}
				stablepickminModel.addConstr(expr_link, GRB.LESS_EQUAL, 1.0, "expr_link"+i);
			}

			for (int j = 0; j < driverListPotential.size(); ++j){
				GRBLinExpr expr_link_2 = new GRBLinExpr();
				for (int i = 0; i < paxList.size(); ++i){
					expr_link_2.addTerm(1.0, x[i][j]);
				}
				stablepickminModel.addConstr(expr_link_2, GRB.LESS_EQUAL, 1.0, "expr_link_2"+j);
			}
			
		     for (int i = 0; i < paxList.size(); ++i){		    	  
		          for (int j = 0; j < driverListPotential.size(); ++j){
		        	  if (reducedPickupTime[i][j] < maxAllowedPickupTime){
		        	      GRBLinExpr expr_preference = new GRBLinExpr();
	                      for (int ii = 0; ii < paxList.size(); ++ ii){
		        		      if (revenueTerm[ii]>= revenueTerm[i]){
		        			      expr_preference.addTerm(1.0, x[ii][j]);
		        		      }
		        	      }
		        	  
		        	      for (int jj = 0; jj < driverListPotential.size(); ++ jj){
		        		      if (serviceTerm[jj] >= serviceTerm[j]){
		        			      expr_preference.addTerm(1.0, x[i][jj]);
		        		      }
		        	      }
		        	      expr_preference.addTerm(1.0, x[i][j]);  
		        	      stablepickminModel.addConstr(expr_preference, GRB.GREATER_EQUAL, 1.0, "expr_preference"+i+j);
                       }
		          }
              }			
						

			// Solve
			stablepickminModel.optimize();

			// Double[][] compSolution = new Double[paxList.size()][driverListPotential.size()];
			for (int i = 0; i < paxList.size(); i++){
				// Passenger p = paxList.get(i);
				for (int j = 0; j < driverListPotential.size(); j++){
					// Driver d = driverListPotential.get(j);
					if (x[i][j].get(GRB.DoubleAttr.X) > 0.5) {
						int[] newPair = new int[]{i,driverPositionMap.get(j),reducedPickupTime[i][j]};
						// d.setAvailable(false);
						matchedPairs.add(newPair);
						break;
					}
				}
			}

			System.out.println("matched pair size="+matchedPairs.size()+", tot wait pax="+paxList.size());
			

			stablepickminModel.dispose();
			env.dispose();



		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}

	} // end the matching function

	public ArrayList<int[]> getMatchedPairs() {
		return matchedPairs;
	}

}
