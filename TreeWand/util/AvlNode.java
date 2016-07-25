package util;

import java.util.List;

import core.GenerateSim.NodeVal;


/** Here is the AVL-Node class for Completenesse **/
public class AvlNode {
	public AvlNode left;
	public AvlNode right;
	public AvlNode parent;
	public int topNode;
	public int height;
	public double leftSum;
	public double ub;
	public List<NodeVal> inv_list;
	public int dim;
	public int nextNode;
	public AvlNode(int k) {
		left = right = parent = null;
		//balance = 0;
		height = 0;
		topNode = k;
		leftSum = 0;
		height = 0;
	}

	public String toString() {
		return "" + topNode;
	}

	/**
	 * Calculating the "height" of a node.
	 * 
	 * @param cur
	 * @return The height of a node (-1, if node is not existent eg. NULL).
	 */
	public int setHeight() {
		if (left == null && right == null) {
			height =  0;
		} else if (left == null) {
			height = 1 + right.height;
		} else if (right == null) {
			height = 1 + left.height;
		} else {
			height = 1 + maximum(left.height, right.height);
		}
		return height;
	}
	
	public int next(int nodeId){
		while(inv_list.size() > 0){
			if (inv_list.get(0).idx < nodeId) // next(pivot) line 22
				inv_list.remove(0);
			else
				break;
		}
		return inv_list.size();
	}

	public void updateLeftSum(){
		leftSum = 0;
		if (left != null){
			leftSum += leftSubtreeSum(left);
		}
	}
	private double leftSubtreeSum(AvlNode n){
		double current_sum = n.leftSum + n.ub;
		if (n.right != null){
			current_sum += leftSubtreeSum(n.right);
		}
		return current_sum;
	}
	/**
	 * Return the maximum of two integers.
	 */
	private int maximum(int a, int b) {
		if (a >= b) {
			return a;
		} else {
			return b;
		}
	}
	


}
