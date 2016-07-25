package data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import core.*;
import util.io.TextReader;
import util.io.TextWriter;

public class Graph {

    public Map<Integer, Node> nodes;
	public int[] nodesIndex;
	private HashMap<Integer,HashMap<Integer,Double>> T;
	private HashMap<Integer,Integer> sum_i;
	public static void printGraph(HashMap<Integer,HashMap<Integer,Double>> g){
		int max_idx = 0;
		for (Map.Entry<Integer, HashMap<Integer,Double>> entry_i : g.entrySet()) {
		    int i = entry_i.getKey();
		    if (i > max_idx)
		    	max_idx = i;
		    HashMap<Integer,Double> Tij = entry_i.getValue();
		    for (Map.Entry<Integer,Double> entry_ij : Tij.entrySet()) {
		    	int j = entry_ij.getKey();
		    	if (j > max_idx)
		    		max_idx = j;
		    }
		}
		for (int i = 0; i <= max_idx; i++){
			for (int j = 0; j <= max_idx; j++){
				if (g.containsKey(i)&&(g.get(i).containsKey(j))){
					System.out.format("\t%.3f",g.get(i).get(j));
				}
				else System.out.print("\t0");
			}
			System.out.println();
		}
	}
	public Graph() {
		T = new HashMap<Integer,HashMap<Integer,Double>>();
		sum_i = new HashMap<Integer,Integer> ();
		nodes = new HashMap<Integer,Node> ();
		
	}
	
	public boolean TiTj(double P_prev_ij, int i, int j, HashMap<Integer,HashMap<Integer,Double>> Pk_next, double C){
		if (T.containsKey(i) && T.containsKey(j)){
			HashMap<Integer,Double> TiT = T.get(i);
			HashMap<Integer,Double> Tj = T.get(j);
			for (Map.Entry<Integer,Double> TiT_row : TiT.entrySet()) {
				int row_num = TiT_row.getKey();
				double TiT_row_val = TiT_row.getValue();
		    	for (Map.Entry<Integer,Double> Tj_col : Tj.entrySet()) {
		    		int col_num = Tj_col.getKey();
		    		double Tj_col_val = Tj_col.getValue();
		    		HashMap<Integer,Double> Pk_next_row;
		    		if (Pk_next.containsKey(row_num)){
		    			Pk_next_row = Pk_next.get(row_num);
		    			if (Pk_next_row.containsKey(col_num)){
		    				double old_val = Pk_next_row.get(col_num);
		    				Pk_next_row.put(col_num, old_val+C*P_prev_ij*TiT_row_val*Tj_col_val);
		    			}
		    			else{
		    				double new_val = C*P_prev_ij*TiT_row_val*Tj_col_val;
//		    				if (new_val > SimRank.h)
		    					Pk_next_row.put(col_num, new_val);
		    			}
		    		}
		    		else{
		    			Pk_next_row = new HashMap<Integer,Double>();
		    			double new_val = C*P_prev_ij*TiT_row_val*Tj_col_val;
//	    				if (new_val > SimRank.h)
	    					Pk_next_row.put(col_num, new_val);
		    			Pk_next.put(row_num, Pk_next_row);
		    		}
		    	}
		    }
			return true;				
		}
		else return false;
	}


//    public void preprocess() {
//        Set<Node> remove = new HashSet<Node>();
//        for (Node n : nodes.values()) {
//            // if (n.in.size() < minNumEdges || n.out.size() < minNumEdges) {
//            if (n.in.isEmpty() || n.out.isEmpty())
//                remove.add(n);
//        }
//        if (remove.isEmpty())
//            return; // finished
//
//        System.out.println(remove.size() + " nodes to be removed...");
//
//        for (Node n : remove) {
//            // remove from out neighbors' in
//            for (Node m : n.out)
//                m.in.remove(n);
//
//            // remove from in neighbors' out
//            for (Node m : n.in)
//                m.out.remove(n);
//
//            nodes.remove(n.index);
//        }
//
//        preprocess();
//    }

    public void saveToFile(String nodeFile, String edgeFile) throws Exception {
        TextWriter out = new TextWriter(nodeFile);
        for (Node n : nodes.values())
            out.writeln(n.index);
        out.close();

        out = new TextWriter(edgeFile);
        int count = 0;
        for (Node n : nodes.values())
            for (Node m : n.out) {
                out.writeln(n.index + "\t" + m.id);
                count++;
            }
        out.close();

        System.out.println("# Nodes: " + nodes.size());
        System.out.println("# Edges: " + count);
    }

//    public void clear() {
//        nodes.clear();
//    }


    public void loadGraphFromFile(String nodeFile, String edgeFile) throws Exception {
//        clear();

        TextReader inN = new TextReader(nodeFile);
        TextReader inE = new TextReader(edgeFile);
        String line;

        System.out.print("Loading graph");
        int count = 0;
        while ((line = inN.readln()) != null) {
            count++;
        }
        inN.close();
        inN = new TextReader(nodeFile);
        this.nodesIndex = new int[count];
        count = 0;
        while ((line = inN.readln()) != null) {
            int id = Integer.parseInt(line);
            this.addNode(new Node(id,count));
            this.nodesIndex[count]=id;
            count++;
        }

//        while ((line = inE.readln()) != null) {
//            String[] split = line.split("\t");
//            int from = Integer.parseInt(split[0]);
//            int to = Integer.parseInt(split[1]);
//            this.addEdge(from, to);
//            
//        }
		while ( (line = inE.readln()) != null )
		{
			String[] parts = line.split("\t");
			int startIdx = nodes.get(Integer.parseInt(parts[1])).index; // Transition matrix is based on the reverse graph
			int endIdx = nodes.get(Integer.parseInt(parts[0])).index;
			this.addEdgeByIdx(endIdx, startIdx);
			if (T.containsKey(startIdx)){
				HashMap<Integer,Double> Tj = T.get(startIdx);
				Tj.put(endIdx, 1.0);
				sum_i.put(startIdx, sum_i.get(startIdx)+1);
			}
			else{
				HashMap<Integer,Double> Tj = new HashMap<Integer,Double>();
				Tj.put(endIdx, 1.0);
				sum_i.put(startIdx, 1);
				T.put(startIdx, Tj);
			}
		}
				
		for (Map.Entry<Integer, HashMap<Integer,Double>> entry_i : T.entrySet()) {
		    Integer startId = entry_i.getKey();
		    Integer sum = sum_i.get(startId);
		    HashMap<Integer,Double> Tij = entry_i.getValue();
		    for (Map.Entry<Integer,Double> entry_ij : Tij.entrySet()) {
		    	entry_ij.setValue(1.0/sum);
		    }
		}
//		printGraph(T);
//		System.out.println();
        System.out.println();

        inN.close();
        inE.close();

        
        
    }


    public void addNode(Node n) {
        nodes.put(n.id, n);
    }

    public void addEdgeByIdx(int from, int to) {
        Node nFrom = getNodeByIndex(from);
        Node nTo = getNodeByIndex(to);
        nFrom.out.add(nTo);
        nTo.in.add(nFrom);
    }
    
   
   

    public void resetPrimeG(){
    	for (Node n : nodes.values()) {
			n.isVisited = false;
			
		}
    	
    }


    public int numNodes() {
        return nodes.size();
    }

    public Node getNode(int id) {
        return nodes.get(id);
    }

    public Collection<Node> getNodes() {
        return nodes.values();
    }

    public boolean containsNode(int id) {
        return nodes.containsKey(id);
    }


	public Node getNodeByIndex(int i) {
		// TODO Auto-generated method stub
		int nodeId = nodesIndex[i];
		return nodes.get(nodeId);
		
	}
	
	
	
}
