main(String args[]) function is in /exec/online.java 

There are 4 arguments needed:


1. Config.depth - the same as in fastSim, natural length of graph expansion


2. k - the number of topK results to generate


3. Config.correctionLevel - the level of correction, the same as in fastSim


4. Config.delta - the upperbound of the total error of all topK node pairs' score caused by an approximation algorithm. The larger this value is, the faster the algorithm runs but less accurate.



There are additional parameters defined in /util/Config.java:


1. epsilon - threshold to discard a value in P matrix, an immediate step of calculating the 


2. alpha - damping factor, the same as in fastSim


3. numRepetitions - the number of repeating simRank calculation. The running time in the output file will be the total running time divided by numRepetitions


4. nodeFile - the file containing all the node id’s


5. edgeFile - the file containing all the edges


6. outputDir - the directory to store all the output files



All of the parameters can be initialized in Config.java file. They can be modified by a separate properties file, whose name is specified in line 23 as File f = new File("config.properties"); 

A separate file with the name “config_AP_baseline_dblp.properties” is provided as an example.

However, the arguments passed into main function will override the values defined in properties file.
