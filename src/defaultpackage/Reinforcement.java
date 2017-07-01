package defaultpackage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import objects.markov.State;
import objects.markov.StateAction;
import objects.markov.StateTransition;



public class Reinforcement {
	public static void learning() throws ClassNotFoundException, SQLException{
		//ResultSet rs = Dbhandler.getStateActionNext();
		ResultSet transRS = Dbhandler.getDatabaseStateTrans();
		ResultSet stateActionRS = Dbhandler.getDatabaseStateAction();
		ResultSet stateRS = Dbhandler.getDatabaseStates();
		
		Hashtable<String, ArrayList<StateTransition>> transList = new Hashtable<String, ArrayList<StateTransition>>(); //startID+action peker på en liste med stateTransitions. StateTransition har occurrence
		Hashtable<String, StateAction> saList = new Hashtable<String, StateAction>(); //"State+action peker på et stateAction-objekt. Dette objektet har occurrence og value
		Hashtable<Integer, State> stateList = new Hashtable<Integer, State>(); //stateID peker på et state-objekt. Dette objektet har occurrence og value
		
		while (transRS.next()){ //konverterer resultset til hashtable med objekter
			String key = transRS.getString("StartID")+transRS.getString("Action");
			StateTransition trans = new StateTransition(new State(transRS.getInt("StartID")), new State (transRS.getInt("EndID")), transRS.getString("Action"));
			trans.setOccurrence(transRS.getInt("Occurrence"));
			if (transList.containsKey(key)){
				ArrayList<StateTransition> tlist = transList.get(key);
				tlist.add(trans);
				transList.put(key, tlist);
			}
			else{
				ArrayList<StateTransition> tlist = new ArrayList<StateTransition>();
				tlist.add(trans);
				transList.put(key, tlist);
			}
		}
		
		while (stateActionRS.next()){		//konverterer resultset til hashtable med objekter
			String key = stateActionRS.getString("StateID")+stateActionRS.getString("Action");
			StateAction sa = new StateAction(stateActionRS.getInt("StateID"), stateActionRS.getString("Action"),stateActionRS.getInt("Occurrence"));
			saList.put(key, sa);
		}
		
		while (stateRS.next()){ //konverterer resultset til hashtable med objekter
			State state = new State(stateRS.getInt("StateID"), stateRS.getInt("Reward"), 0.0, stateRS.getInt("Occurrence"));
			state.setTeam(stateRS.getString("Team"));
			stateList.put(stateRS.getInt("StateID"), state);
		}

		int maxI = 3000;
		double currentValue = 0;
		double lastValue = 0;
		boolean converged = false;
		double c = 0.0001;
		
		Set<String> saKeys = saList.keySet();
		Set<Integer> stateIds = stateList.keySet();

		for (int i = 0; i < 2000; i++){
			if (!converged){
				for (String key: saKeys){
					StateAction sa = saList.get(key);
					if(!transList.containsKey(key)) continue;
					ArrayList<StateTransition> tlist = transList.get(key);
					int saOcc = sa.getOccurrence();
					double newQ = 0.0;
					for (StateTransition st:tlist){
						int stOcc = st.getOccurrence();
						State nextState = stateList.get(st.getEndState().getStateID());
						double nextReward = (Double) (nextState.getReward()+nextState.getValue());
						newQ += ((double) stOcc/(double) saOcc)*nextReward;
						currentValue += Math.abs(newQ);
					}
					sa.setValue(newQ);	
				}
				if (!converged){	
					for (int stateID: stateIds){
						State s = stateList.get(stateID);
						s.setValue(0);
					}
					for (String key: saKeys){
						int stateID = Integer.parseInt(key.replaceAll("[^\\d.]", ""));
						State s = stateList.get(stateID);
						double stateOcc = (double) s.getOccurrence();
						double saOcc = (double) saList.get(key).getOccurrence();
						s.setValue(s.getValue()+saList.get(key).getValue()*saOcc/stateOcc);
					}
				}
				if ((currentValue-lastValue)/currentValue < c) converged = true;
				lastValue = currentValue;
				currentValue = 0;
				if(converged){
					for (int stateID: stateIds){
						System.out.println("Team: " + stateList.get(stateID).getTeam() + " Zone: " + stateList.get(stateID).getZone() + " Value: " + stateList.get(stateID).getValue());
						if (stateList.get(stateID).getValue()>0.10) System.err.println("Value>0.10");
					}
					System.err.println("CONVERGED " + i);
					Dbhandler.updateStateActionQ(saList);
					Dbhandler.updateStateValues(stateList);
				}
			}
		}
		


	}
}
