package exec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import core.*;
import core.GenerateSim.NodeVal;
import core.Genertate_Candidate.NPComparator;
import core.Genertate_Candidate.NodesPair;
import data.*;
import util.*;
import util.io.TextReader;
import util.io.TextWriter;

public class Online {
    public static void main(String args[]) throws Exception {
        // init parameters
        Config.depth = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);
        Config.correctionLevel = Integer.parseInt(args[2]);
        // Config.epsilon = Double.parseDouble(args[3]);
        Config.delta = Double.parseDouble(args[3]);
        /*
         * move filename construction here in case the program changes
         * Config.delta
         */
        String outfile = Config.outputDir + "/" + "base-AP_BT_" + "D"
        + Config.depth + "_CR" + Config.correctionLevel + "_k" + k// String.format("%1.0e",Config.epsilon)
        + "_DLT" + String.format("%1.0e", Config.delta);
        TextWriter out = new TextWriter(outfile);
        System.out.println(outfile);
        Graph graph = new Graph();
        graph.loadGraphFromFile(Config.nodeFile, Config.edgeFile);
        // graph.loadGraphFromFile("node", "edge_copy");
        // load queries
        PriorityQueue<NodesPair> H = null;
        long start = System.currentTimeMillis();
        for (int rep = 0; rep < Config.numRepetitions; rep ++){
            GenerateSim gs = new GenerateSim(graph);
            Genertate_Candidate gc = new Genertate_Candidate(gs);
            // PriorityQueue<NodesPair> pq = gc.generate_candidate(6);
            // for (NodesPair np : pq){
            // System.out.println("Sim("+np.n1+","+np.n2+")="+np.val);
            // }
            
            Set<Integer> candidates = gc.generate_candidate(k);
            // String candidatesSet = "/Users/Mao/Desktop/baseline_candidate";
            // TextWriter nodesOut = new TextWriter(candidatesSet);
            // for (Integer n : candidates){
            // nodesOut.writeln(graph.nodesIndex[n]);
            // }
            // nodesOut.close();
            NPComparator npc = new NPComparator();
            H = new PriorityQueue<NodesPair>(k, npc);
            //		for (Integer x_idx = 0; x_idx < graph.numNodes(); x_idx++) {
            //			for (Integer y_idx = x_idx + 1; y_idx < graph.numNodes(); y_idx++) {
            //				double current_score = gs.dotProduct(x_idx, y_idx);
            //				if (H.size() == k) {
            //					NodesPair min_pair = H.peek();
            //					if (current_score > min_pair.val) {
            //						H.add(new NodesPair(x_idx, y_idx, current_score));
            //						H.poll();
            //					}
            //				} else {
            //					H.add(new NodesPair(x_idx, y_idx, current_score));
            //				}
            //
            //			}
            //		}
            double theta = 0;
            Map<Integer, Map<Integer, Boolean>> smallIdxMap = new HashMap<Integer, Map<Integer, Boolean>>();
            for (Integer x_idx : candidates) {
                TreeeWand tw_x = new TreeeWand(gs, x_idx, theta);
                while (true) {
                    theta = H.size() < k ? 0 : H.peek().val;
                    int y_idx = tw_x.treewand();
                    if (tw_x.ATr.root == null) {
                        break;
                    }
                    if (y_idx < 0)
                        continue;
                    double current_score = gs.dotProduct(x_idx, y_idx);
                    if (current_score > theta) {
                        int larger_idx = x_idx > y_idx ? x_idx : y_idx;
                        int smaller_idx = x_idx < y_idx ? x_idx : y_idx;
                        Map<Integer, Boolean> bigIdxMap = smallIdxMap
                        .get(smaller_idx);
                        if (bigIdxMap != null) { // smaller_idx has its own map
                            if (bigIdxMap.get(larger_idx) != null)
                                continue;
                            else {
                                bigIdxMap.put(larger_idx, true);
                            }
                        } else {
                            bigIdxMap = new HashMap<Integer, Boolean>();
                            bigIdxMap.put(larger_idx, true);
                            smallIdxMap.put(smaller_idx, bigIdxMap);
                        }
                        if (H.size() == k) {
                            H.poll();
                        }
                        H.add(new NodesPair(smaller_idx, larger_idx, current_score));
                    }
                }
            }
        }
        long elapsed = (System.currentTimeMillis() - start);
        
        out.writeln(elapsed + "ms ");
        List<NodesPair> result = new ArrayList<NodesPair>();
        while (H.size() > 0) {
            result.add(H.poll());
        }
        for (int i = result.size() - 1; i >= 0; i--) {
            NodesPair npv = result.get(i);
            out.writeln(graph.nodesIndex[npv.n1] + "\t"
                        + graph.nodesIndex[npv.n2] + "\t" + npv.val);
            // System.out.println(graph.nodesIndex[npv.n1]+"\t"+graph.nodesIndex[npv.n2]+"\t"+npv.val);
        }
        out.close();
        
    }
    
    public void printSimMat(GenerateSim gs) {
        for (Map<Integer, Double> x_vector : gs.Sim) {
            
            List<Entry<Integer, Double>> dim_v = new ArrayList<Entry<Integer, Double>>();
            Iterator<Entry<Integer, Double>> it = x_vector.entrySet()
            .iterator();
            int idx = 0;
            while (it.hasNext()) {
                Entry<Integer, Double> x_Di = it.next();
                dim_v.add(x_Di);
            }
            Collections.sort(dim_v, new Comparator<Entry<Integer, Double>>() {
                @Override
                public int compare(Entry<Integer, Double> o1,
                                   Entry<Integer, Double> o2) {
                    return Double.compare(o1.getKey(), o2.getKey());
                }
            });
            for (Entry<Integer, Double> x_Di : dim_v) {
                System.out.print(x_Di.getKey() + ":");
                System.out.format("%.2f", x_Di.getValue());
                System.out.print("\t");
                
            }
            System.out.println();
        }
    }
}
