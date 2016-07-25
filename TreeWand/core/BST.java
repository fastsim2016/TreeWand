package core;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

import core.GenerateSim.NodeVal;

/* Class BSTNode */
class BSTNode
{    
	BSTNode left, right;
	int topNode;
	double ub;
	double leftSum;
	List<NodeVal> inv_list;
	int nextNode;
	int dim;
	int height;

	/* Constructor */
	public BSTNode()
	{
		left = null;
		right = null;
		topNode = 0;
		height = 0;
	}
	/* Constructor */
	public BSTNode(int n)
	{
		left = null;
		right = null;
		topNode = n;
		leftSum = 0;
		height = 0;
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
}

/* Class SelfBalancingBinarySearchTree */
public class BST
{
	public BSTNode root;     

	/* Constructor */
	public BST()
	{
		root = null;
	}
	public BSTNode locatePivot(double theta){
		BSTNode curNode = root;
		BSTNode pivotNode = null;
//		int pivotDim = -1;
		while (curNode != null){
			if (curNode.leftSum + curNode.ub > theta){
//				pivotDim = curNode.dim;
				pivotNode = curNode;
				curNode = curNode.left;
			}
			else{
				theta = theta - curNode.leftSum - curNode.ub;
				curNode = curNode.right;
			}
		}
		return pivotNode;
	}
	/* Function to insert topNode */
	public void insert(List<NodeVal> inv_list, int dim, double ub_i)
	{
		root = insert(inv_list, dim, ub_i, root);
	}
	/* Function to get height of topNode */
	private int height(BSTNode t )
	{
		return t == null ? -1 : t.height;
	}
	/* Function to max of left/right topNode */
	private int max(int lhs, int rhs)
	{
		return lhs > rhs ? lhs : rhs;
	}
	/* Function to insert topNode recursively */
	private BSTNode insert(List<NodeVal> inv_list, int dim, double ub_i, BSTNode t)
	{
		int x = inv_list.get(0).idx; // x = top element's index
		int next_element = -1;
		if (inv_list.size() > 1){
			next_element = inv_list.get(1).idx;
		}
		
		if (t == null){
			t = new BSTNode(x); // TODO
			t.inv_list = inv_list;
			t.dim = dim;
			t.ub = ub_i;
			t.nextNode = -1;
			if (inv_list.size() > 1){
				t.nextNode = inv_list.get(1).idx;
			}
		}
		else if (x < t.topNode)
		{
			t.left = insert( inv_list, dim, ub_i, t.left );
		}
		else if (x > t.topNode)
		{
			t.right = insert( inv_list, dim, ub_i, t.right );
		}
		else{ // x == t.topNode
			if (next_element > t.nextNode) // the invList with largest second element goes to left
			{
				t.left = insert( inv_list, dim, ub_i, t.left );
		}
			else // otherwise, it goes right
			{
				t.right = insert( inv_list, dim, ub_i, t.right );
			}
		}
		t.height = max( height( t.left ), height( t.right ) ) + 1;
		if (t.left != null){
			t.leftSum = t.left.leftSum + t.left.ub;
		}
		return t;
	}


	public int counttopNodes()
	{
		return counttopNodes(root);
	}
	private int counttopNodes(BSTNode r)
	{
		if (r == null)
			return 0;
		else
		{
			int l = 1;
			l += counttopNodes(r.left);
			l += counttopNodes(r.right);
			return l;
		}
	}
//	/* Functions to search for an element */
//	public boolean search(int val)
//	{
//		return search(root, val);
//	}
	public BSTNode removeToSkip(){
		if (root == null){
			return null;
		}
		List<BSTNode> nList = new ArrayList<BSTNode>();
		BSTNode cur_node = root;
		while (cur_node.left != null){
			nList.add(cur_node); // nList includes root to the parent of the leftmost leaf node
			cur_node = cur_node.left;
		}
		if (cur_node == root ){
			root = root.right;
			if (root != null){
				root.height = height( root.right ) + 1;
			}
			return cur_node;
		}
		BSTNode leafParent = nList.get(nList.size() - 1); 
		leafParent.left = cur_node.right; // delete the leaf node
		leafParent.leftSum = 0;
		leafParent.height = height( leafParent.right ) + 1;
		for (int i = nList.size() - 1; i > 0; i--){ // from the leaf parent to the left child of root
			BSTNode current_parent = nList.get(i-1);
			current_parent.leftSum = current_parent.left.leftSum + current_parent.left.ub;
			current_parent.height = max( height( current_parent.left ), height( current_parent.right ) ) + 1;
			
		}

		return cur_node; // return the deleted leaf node
	}
	public BSTNode leftMost(){
		BSTNode cur_node = root;
		while (cur_node.left != null){
			cur_node = cur_node.left;
		}
		return cur_node;
	}
}