Setup: Jave coding environment + Gurobi Solver

mainSim: set the basic simulation environment;

Simulator: ride-matching environment;

Driver: driver class;
Passenger: passenger class;
readDriver: driver online/offline time and location information; Data from driver_15_16.txt
readPassenger: passenger request time and location information; Data from driver_15_16.txt

toyDemandSupply: generate the demand and supply from readDriver and readPassenger

Different Matching Policies:

(matchingMode == 0) randomMatching policy: match driver and passenger in a randomized way;
(matchingMode == 1) weightedMatching policy: match driver and passenger by setting a set of deterministic weight;
(matchingMode == 2) revenueMaximization policy: single objective -- revenue maximization -- policy;
(matchingMode == 3) pickuptimeMinimization policy: single objective -- pickup distance/time minimization -- policy;
(matchingMode == 4) serviceMaximization policy: single objective -- service quality maximization -- policy;
(matchingMode == 5) stablerevenueMaximization policy: stable matching with revenuemaximization;
(matchingMode == 6) stablepickuptimeMimization policy: stable matching with pickup time minimization;
(matchingMode == 7) stableserviceMaximization policy: stable matchng with service quality maximization;
(matchingMode == 8) compromiseMatching policy: the policy in the paper with 3 objective targets;
(matchingMode == 12) compromiseMatching2 policy: the policy in the paper with 2 objective targets;