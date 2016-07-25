package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AvlTest {
	int x_idx; // the index of the node on which treewand is performed
	public static AvlNode rndNumber() {
		int size = 20;
		AvlTree tr = new AvlTree();
		ArrayList<Integer> list = new ArrayList<Integer>(size);
		for (int i = 1; i <= size; i++) {
			list.add(i);
		}

		Random rand = new Random();
//		while (list.size() > 0) {
//			int index = rand.nextInt(list.size());
//			tr.insert(list.remove(index));
//		}
//		int rmsize = 20;
//		ArrayList<Integer> rmlist = new ArrayList<Integer>(rmsize);
//		for (int i = 1; i <= rmsize; i++) {
//			rmlist.add(i);
//		}
//
//		while (rmlist.size() > 0) {
//			int index = rand.nextInt(rmlist.size());
//			tr.remove(rmlist.remove(index));
//		}
//		
//		list = new ArrayList<Integer>(size);
//		for (int i = 1; i <= size; i++) {
//			list.add(i);
//		}
//
//		while (list.size() > 0) {
//			int index = rand.nextInt(list.size());
//			tr.insert(list.remove(index));
//		}
//		
		return tr.root;
	}


	public static void main(String[] args) {

		BTreePrinter.printNode(rndNumber());
		BTreePrinter.printNode(rndNumber());

	}
}

class Node<T extends Comparable<?>> {
	AvlNode left, right;
	T data;

	public Node(T data) {
		this.data = data;
	}
}
