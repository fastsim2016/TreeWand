package core;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import core.VectorMatrix.DimVal;
import data.Graph;
import data.Node;
import util.Config;

public class GenerateSim {
	protected Graph g;
	private double[][] delta_x_l;
//	private static int KSI = Config.correctionLevel;
	private static int KSI = Config.depth;
	private static double sqrtC = Math.sqrt(Config.alpha);
	private static double C = Config.alpha;
	public Map<Integer, InvList> iLists;
	public List<Map<Integer,Double>> Sim;
	private static class MAXVComparator implements Comparator<NodeVal> { // NodeVal.val in descending order
		public int compare(NodeVal one, NodeVal two) {
			return Double.compare(two.val, one.val);
		}
	}
	public class InvList{
		public double maxv;
		public List<NodeVal> nodeValList;
		public InvList(){
			nodeValList = new ArrayList<NodeVal>();
		}
		public void insert(int idx, double val) {
			NodeVal new_pair = new NodeVal(idx, val);
			nodeValList.add(new_pair);
//			if (val >= maxv){
//				nodeValList.add(0, new_pair);
//			}
//			else{
//				int i;
//				for(i = 0; i < nodeValList.size(); i++){
//					if (nodeValList.get(i).val < val)
//						break;
//				}
//				nodeValList.add(i, new_pair);
//			}
		}
		public void sort(){
			Collections.sort(nodeValList, new MAXVComparator());
			maxv = nodeValList.get(0).val; // update the maximum value after 
		}
	}
	public static class NodeVal implements Comparable<NodeVal>{
		public int idx;
		public double val;
		public NodeVal(int node_idx, double value){
			idx = node_idx;
			val = value;
		}
		@Override
	    public int compareTo(NodeVal other){
	        return Double.compare(other.val, this.val);
	    }
	}
	public static class DimVal{
		public int dim;
		public double val;
		public DimVal(int dimension, double value){
			dim = dimension;
			val = value;
		}
	}
	public static int dim(Node x, int l){
		return x.index*(KSI + 1) + l;
	}
	
	public double dotProduct(int x, int y){
		Map<Integer,Double> x_vector = Sim.get(x);
		Map<Integer,Double> y_vector = Sim.get(y);
		Map<Integer,Double> smaller_map = x_vector.size() < y_vector.size() ? x_vector : y_vector;
		Map<Integer,Double> larger_map = x_vector.size() < y_vector.size() ? y_vector : x_vector;
		double product = 0;
		for (int dim : smaller_map.keySet()){
			Double score_lmap = larger_map.get(dim);
			if (score_lmap != null){
				product += score_lmap*smaller_map.get(dim);
			}
		}
		return product;
	}
	
	private int N(Node x, int d){
		if (d == 1){
			return x.in.size();
		}
		int sum = 0;
		for (Node y : x.in){
			sum += N(y, d-1);
		}
		return sum;
	}
	
	public GenerateSim(Graph G){
		g = G;
		delta_x_l = new double[G.nodes.size()][KSI];
		Sim = new ArrayList<Map<Integer,Double>>();
		for (int i = 0; i < G.nodes.size(); i++){
			Sim.add(null);
		}
		iLists = new HashMap<Integer, InvList>();
		for (Node x : G.nodes.values()) {
			delta_x(x.index);
		}
		for (Node a : G.nodes.values()) {
			Map<Integer,Double> row_a = new HashMap<Integer,Double>();
			Set<Node> R_l = new HashSet<Node>();
			Set<Node> R_next = new HashSet<Node>();
			R_l.add(a);
			row_a.put(dim(a,0),1.0);
			double delta = Config.delta;
			for (int l =0; l<KSI; l++){
				for (Node y : R_l){
					Double sim_a_y_l = row_a.get(dim(y, l));
					double upperBound = 0;
					for (int d = 1; d <= KSI - l; d++){
						upperBound += N(y,d)*sim_a_y_l/y.in.size()*Math.pow(C, (2*d+l)/2.0);
					}
					if (upperBound < delta){
						delta -= upperBound;
						continue;
					}
					for (Node x : y.in){
						Double val = row_a.get(dim(x,l+1));
						if (val != null){
							row_a.put(dim(x,l+1), val+sqrtC/y.in.size()*sim_a_y_l);
						}
						else{
							row_a.put(dim(x,l+1), sqrtC/y.in.size()*sim_a_y_l);
						}
						R_next.add(x);
					}
				}
				R_l = new HashSet<Node>(R_next);
				R_next = new HashSet<Node>();
				
			}
			for (Integer dimension : row_a.keySet()){
				int meetingnode = dimension/(KSI+1); // dimension = |x|*(KSI + 1) + l
				int l = dimension - meetingnode*(KSI+1);
				double corrected_val = row_a.get(dimension);
				if (l < KSI){
					corrected_val *= Math.sqrt(1-delta_x_l[meetingnode][l]);
					
				}
				row_a.put(dimension, corrected_val);
				InvList dim_list = iLists.get(dimension);
				if (dim_list == null){
					dim_list = new InvList();
					dim_list.insert(a.index, corrected_val);
				}
				else{
					dim_list.insert(a.index, row_a.get(dimension));
				}
				iLists.put(dimension, dim_list);
					
			}
			
			Sim.set(a.index, row_a);
			
		}
		Iterator<Entry<Integer, InvList>> it = iLists.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, InvList> pair = it.next();
	        pair.getValue().sort();
	    }
	}
	public void delta_x(int x) {
		HashMap<Integer,HashMap<Integer,Double>> P_prev = new HashMap<Integer,HashMap<Integer,Double>>();
		HashMap<Integer,Double> P0_x = new HashMap<Integer,Double>(); // content of the row x
		P0_x.put(x, 1.0);
		P_prev.put(x, P0_x);
		boolean stop_iteration = true;
		for (int k = 1; k <= Config.correctionLevel; k++) {
//		for (int k = 1; k <= KSI; k++) { // k = KSI - l where 0 =< l < KSI
			HashMap<Integer,HashMap<Integer,Double>> Pk = new HashMap<Integer,HashMap<Integer,Double>>();
			for (Map.Entry<Integer, HashMap<Integer,Double>> P_prev_row_map : P_prev.entrySet()) {
			    int i = P_prev_row_map.getKey();
			    HashMap<Integer,Double> P_prev_i = P_prev_row_map.getValue();
			    for (Map.Entry<Integer,Double> P_prev_ij : P_prev_i.entrySet()) {
			    	int j = P_prev_ij.getKey();
			    	if (i!=j || k == 1){ // k==1: the initial node x
			    	//if (i!=j){
			    		double P_prev_ij_val = P_prev_ij.getValue();
			    		if (P_prev_ij_val > Config.epsilon){
					    	boolean updated = g.TiTj(P_prev_ij_val, i, j, Pk, C);
					    	if (updated)
					    		stop_iteration = false;
			    		}
			    	}
			    }
			}
			double current_sim = 0;
			for (Map.Entry<Integer, HashMap<Integer,Double>> row : Pk.entrySet()) {
			    Integer i = row.getKey();
			    HashMap<Integer,Double> Pij = row.getValue();
			    if (Pij.containsKey(i)){
			    	current_sim += Pij.get(i);
			    }
			}
			for (int l = (KSI - k) ; l >= 0; l--){
				delta_x_l[x][l] += current_sim;
			}
//			Graph.printGraph(Pk);
			P_prev = Pk;
//			System.out.println("k="+k+" Sim_k="+current_sim);
//			System.out.println("k="+k+"Sim(a,b) = "+Sim_ab);
			if (stop_iteration)
				break;
		}
	}

}
