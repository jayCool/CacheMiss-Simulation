package caches;


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



    @Option(name = "-config", usage = "configuration file")
    private String config = "";

   
    
    @Argument
    private List<String> arguments = new ArrayList<String>();

    public static void main(String[] args) throws CmdLineException, FileNotFoundException {
        ArgumentParser argParser = new ArgumentParser();
        CmdLineParser parser = new CmdLineParser(argParser);
        parser.parseArgument(args);
        
        argParser.start();
     }

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
