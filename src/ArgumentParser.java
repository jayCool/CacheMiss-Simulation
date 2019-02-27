
import generator.WorkloadGenerator;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author workshop
 */
public class ArgumentParser {

    /**
     * @param args the command line arguments
     */
    
    @Option(name = "-usage", usage = "1:generate workload, 2: simulate cache miss")
    private int usage = 1;

    
    @Option(name = "-breath", usage = "branch factor")
    private int breath = 1;

    @Option(name = "-config", usage = "configuration file")
    private String config = "";

    
    @Option(name = "-depth", usage = "tree depth ")
    private int depth = 1;

    @Option(name = "-cacheType", usage = "cache type 1 FIFO, 2, LRU, 3,Random, 4.ARC, 5.LRFU, 6.LRU different object size")
    private int cacheType = 2;

    @Option(name = "-requestType", usage = "workload type 1 Uniform, 2 B-C, 3 Zipf, 4. Empirical Trace") //4 for manual
    private int requestType = 1;

    @Option(name = "-requestOutPutAddress", usage = "Save the synthetic workload for debugging") //4 for manual
    private String requestOutPutAddress = "requestOut.txt";

    @Option(name = "-requestAddress", usage = "real trace file address") //4 for manual
    private String requestAddress = "";

    @Option(name = "-totalDistinct", usage = "total number of distinct pages")
    private int totalDistinct = 10000;

    @Option(name = "-singleSize", usage = "single work load size")
    private int singleSize = 100000;

    @Option(name = "-singleDistinct", usage = "single work load distinct pages")
    private int singleDistinct = 1000;

    @Option(name = "-cacheSize", usage = "single cache size")
    private int cacheSize = 100;

    @Option(name = "-b", usage = "b For B-C Acces")
    private double b = 0.8;

    @Option(name = "-c", usage = "c For B-C Access")
    private double c = 0.2;

    @Option(name = "-a", usage = "a For Zipf Distribution")
    private double a = 1.2;

    @Option(name = "-lamda", usage = "for LRFU")
    private double v = 0.5;

    @Option(name = "-cacheStrategy", usage = "cache strategy 1 LCE, 2 LCD, 3 MCD, 4 IQIYI")
    private int cacheStrategy = 1;

    @Option(name = "-iter", usage = "iteration")
    private int iteration = 10;

    @Option(name = "-debug", usage = "debug mode")
    private int debug = 0;
    
    @Option(name = "-cacheConfigFile", usage = "read the cache definition from file")
    private String cacheConfigFile = "";
    
    @Option(name = "-missRatioOption", usage = "Option of miss ratio calculation: 0-by request, 1-by object size")
    private int missRatioOption=0;
    
    @Option(name = "-history", usage = "outputing the cache history")
    private boolean history;
    
    @Option(name = "-windowSize", usage = "batch updating window size --- IQIYI")
    private int windowSize=10;
    
    @Option(name = "-batchFrequency", usage = "batch updating frequency --- IQIYI")
    private int batchFrequency=1;
    
    @Option(name = "-outputDir", usage = "output file directory")
    private String outputDir = "";
    
    @Option(name = "-p", usage = "p Value of LRUp Cache")
    private double p = 0.0;
     
    @Option(name = "-cacheContent", usage = "check the cache content")
    private boolean cacheContent=false;
     
    @Option(name = "-randomWarming", usage = "Randomize the cache warming")
    private boolean randomWarming=false;
     
    
    
    
    
    
    
    
    @Argument
    private List<String> arguments = new ArrayList<String>();

    public static void main(String[] args) throws CmdLineException, FileNotFoundException {
        ArgumentParser argParser = new ArgumentParser();
        CmdLineParser parser = new CmdLineParser(argParser);
        parser.parseArgument(args);
        
        argParser.start();
           
            /*
            
            double totalCacheMiss = 0.0;
            int iteration = argParser.iteration;
            double[] misses = new double[1000];
            int numOfClients = 0;
            long largest = 0;
            
            for (int i = 0; i < iteration; i++) {
                Simulator simulator = new Simulator();
                argParser.assignValues(simulator);
                simulator.run();
                
                //cache miss statistics summary
                totalCacheMiss += 1.0 * simulator.missed / simulator.serial_id;
                for (int j = 0; j < simulator.misses.length; j++) {
                    misses[j] += simulator.misses[j];
                }
                largest = (long) Math.max(largest, simulator.largest);
                numOfClients = simulator.misses.length;
                
                ///System.err.println("cache: " + i +  );
            }
            for (int i = 0; i < numOfClients; i++) {
                System.out.println("cache " + i + " :  " + misses[i] / iteration);
            }
            System.out.println("By request:     " + totalCacheMiss / iteration);
            
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java cacheMissSimulator [options...] arguments...");
            parser.printUsage(System.err);
            System.err.println();
            return;
        }

*/
    }

    
    /*
    private void assignValues(Simulator simulator) {
        simulator.a = this.a;
        simulator.b = this.b;
        simulator.c = this.c;
        simulator.v = this.v;
        simulator.branchFactor = this.breath;

        simulator.cacheSize = this.cacheSize;
        simulator.cacheType = this.cacheType;

        simulator.client_no = (int) Math.pow(this.breath, this.depth - 1);
        if (!requestAddress.isEmpty()){
            requestType = 4;
        }
        simulator.requestType = this.requestType;
        simulator.singleWorkLoadSize = this.singleSize;
        simulator.totalDistinctFiles = this.totalDistinct;
        simulator.treeDepth = this.depth;
        simulator.requestAddress = this.requestAddress;
        simulator.requestOutPutAddress = this.requestOutPutAddress;
        simulator.cacheConfigFile = this.cacheConfigFile;
        simulator.batchFrequency = this.batchFrequency;
        simulator.windowSize = this.windowSize;
        simulator.outputDir = this.outputDir;
        simulator.p = this.p;
        simulator.cacheContent = this.cacheContent;
        if (this.debug == 1) {
            simulator.debug = true;
        }
        
        simulator.history = history;
        simulator.randomWarming = randomWarming;
    }*/

    private void start() {
        if (this.config.isEmpty())
            System.err.println("Configuration File Adress Is Empty!");
        
        if (this.usage == 1){
            WorkloadGenerator wg = new WorkloadGenerator(config);
            wg.run();
        }
        
        if (this.usage == 2){
            Simulator sim = new Simulator(config);
            sim.run();
        }
    }
}
