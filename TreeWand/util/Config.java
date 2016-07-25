package util;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Properties;

public final class Config {
    public static double epsilon                    = 1E-8; // threshold to discard value in P matrix
    public static double delta		                = 1E-4; // upper bound of error
    public static double alpha						= 0.75; // damping factor
    public static int	 numRepetitions				= 5;
    
    public static String nodeFile					= "/Users/Mao/Downloads/wiki/wiki-nodes";
    public static String edgeFile					= "/Users/Mao/Downloads/wiki/wiki-edges";
    public static String outputDir		            = ""; 
    public static int depth							=8; // maximum path length
	public static int correctionLevel				=0;
    
    static {
        String filePath = System.getProperty("config").trim();
        File f = new File(filePath);
//    	File f = new File("config_AP_baseline_dblp.properties");
//    	File f = new File("config_AP_baseline.properties");
        if (!f.exists()) {
            System.out.println("Please set the system properties first.");
            System.exit(0);
        }
        
        //System.out.println("*** config file used: " + f.getAbsolutePath() + " ***");
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(f));
            for (Field field : Config.class.getFields()) {
                if (field.getType().getName().equals("int"))
                    setInt(prop, field);
                else if (field.getType().getName().equals("double")) 
                    setDouble(prop, field);
                else if (field.getType().equals(String.class))
                    setString(prop, field);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static boolean hasValidProp(Properties prop, Field field) {
    	return prop.getProperty(field.getName()) != null
        	&& !prop.getProperty(field.getName()).trim().isEmpty();
    }
    
    private static String getProp(Properties prop, Field field) {
    	return prop.getProperty(field.getName()).trim();
    }

    private static void setInt(Properties prop, Field field) throws Exception {
        if (hasValidProp(prop, field))
            field.set(null, Integer.valueOf(getProp(prop, field)));
    }

    private static void setDouble(Properties prop, Field field) throws Exception {
        if (hasValidProp(prop, field)) {
            field.set(null, Double.valueOf(getProp(prop, field)));
        }
    }

    private static void setString(Properties prop, Field field) throws Exception {
        if (hasValidProp(prop, field)) {
            field.set(null, getProp(prop, field));
        }
    }
    
    public static void print() {
    	try {
    		for (Field field : Config.class.getFields())
    			System.out.println(field.getName() + " = " + field.get(null));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public static void main(String[] args) {
    	print();
    }

}
