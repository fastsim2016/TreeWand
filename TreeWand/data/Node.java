package data;

import java.util.ArrayList;
import java.util.List;

/**
 * node structure.
 * 
 * @author zfw
 * 
 */
public class Node {

	public int id;
	public boolean isHub = false;
	public boolean isVisited = false;
	public int index = -1; // the automatically generated incremental id
	//public boolean isRead = false;
	//public boolean isDangling = false;
	//public int clusterId = -1;
	
	public List<Node> out;
	public List<Node> in;
	
	//public List<Node> inInSub;
	
	public List<Integer> outId; 
	public List<Integer> inId;
	
	public double vOld = 0;
	public double vNew = 0;

	public Node(int id) {
		this.id = id;
		out = new ArrayList<Node>();
		in = new ArrayList<Node>();
	}
	public Node(int id,int index) {
		this.id = id;
		this.index = index;
		out = new ArrayList<Node>();
		in = new ArrayList<Node>();
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}
	
	@Override
	public boolean equals(Object o) {
		Node n = (Node)o;
		return n.id == this.id;
	}

	
	
}
