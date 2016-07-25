package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Map.Entry;

import core.GenerateSim.InvList;
import core.GenerateSim.NodeVal;
import core.Genertate_Candidate.NPComparator;
import core.Genertate_Candidate.NodesPair;
import util.AvlNode;
import util.AvlTest;
import util.AvlTree;
import util.BTreePrinter;


public class TreeeWand {
	int curNode;
	int x_idx; // the index of the node on which treewand is performed
	double theta;
	// Map<Integer, List<NodeVal>> iLists;
	// PriorityQueue<NodesPair> nodesPairHeap;
	GenerateSim Gs;
	public AvlTree ATr;

	public TreeeWand(GenerateSim Gs, int x_idx, double theta) {
		this.Gs = Gs;
		ATr = new AvlTree();
		ATr.x_idx = x_idx;
		this.x_idx = x_idx;
		this.theta = theta;
		init();
	}

	private static class MIN_NODE_Comparator implements Comparator<NodeVal> {
		public int compare(NodeVal one, NodeVal two) {
			// the value 0 if x == y; a value less than 0 if x < y; and a value
			// greater than 0 if x > y
			int x = one.idx;
			int y = two.idx;
			if (x == y) {
				return 0;
			} else if (x < y) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	private void init() {
		curNode = 0;
		Map<Integer, Double> x_vector = Gs.Sim.get(x_idx);
		List<Map.Entry<Integer, Double>> entry_list = new ArrayList<Map.Entry<Integer, Double>>(
				x_vector.entrySet());
		// while (it.hasNext()) {// line 3
		// Entry<Integer, Double> x_Di = it.next();
		for (Entry<Integer, Double> x_Di : entry_list) {
			int dim = x_Di.getKey(); // get current dimension of the x_vector
										// component
			InvList Ii = Gs.iLists.get(dim);
			// System.out.println(Ii.nodeValList.size());
			List<NodeVal> inv_list = new ArrayList<NodeVal>(Ii.nodeValList);
			// System.out.println(inv_list.size());
//			for (int i = 0; i < inv_list.size(); i++) {
//				NodeVal y_Di = inv_list.get(i);
//				if (y_Di.idx == x_idx) { // if the current idex == x_idx, delete
//											// it from inv_list and break
//					inv_list.remove(i);
//					break;
//				}
//			}
			if (inv_list.size() == 0) {
				continue;
			}
			double ub_i = x_Di.getValue() * inv_list.get(0).val; // the first
																	// element
																	// has max
																	// value
			Collections.sort(inv_list, new MIN_NODE_Comparator()); // sort the
																	// list by
																	// node's id
																	// at
																	// ascending
																	// order
			ATr.insert(inv_list, dim, ub_i);
//			BTreePrinter.printNode(ATr.root);
			// iLists.put(dim, inv_list);
		}

	}

	public int treewand() {
		int v = -1;
		while (true) {
			AvlNode pivotNode = ATr.locatePivot(theta);
//			BTreePrinter.printNode(ATr.root);
			if (pivotNode == null) { // return -1 when root is null or cannot
										// find a pivot
				ATr.root = null; // if pivot is null, current node can't form a
									// pair exceeding theta
				return -1; // line 7, line 9
			}
//			int pivotDim = pivotNode.dim;
			if (pivotNode.topNode <= curNode) { // line 10
				AvlNode node_next = ATr.removeToSkip(); // line 12
				if (node_next == null) {
					System.out.println("Empty tree");
//					break; // stop after tree is empty
				}
				if (node_next.next(curNode + 1) > 0) { // line 13
					ATr.insert(node_next.inv_list, node_next.dim, node_next.ub); // advance
																				// posting[aterm]
				}
			}

			else { // pivot > curDoc
				if (ATr.leftMost().topNode == pivotNode.topNode) {
					curNode = pivotNode.topNode;
					return curNode;
				} else {
					AvlNode node_next = ATr.removeToSkip(); // line 21
					if (node_next == null) {
//						break; // stop after tree is empty
					}
					if (node_next.next(pivotNode.topNode) > 0) { // line 22
						ATr.insert(node_next.inv_list, node_next.dim,
								node_next.ub); // advance posting[aterm]
//						break; // stop when the inv_list has next element
					}
				}
			}

		}

//		return v;
	}

}
