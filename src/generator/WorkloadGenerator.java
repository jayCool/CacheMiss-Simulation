package generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jw
 */
public class WorkloadGenerator {

    String config = "";

    /**
     * Initialize the workload generator
     *
     * @param config
     */
    public WorkloadGenerator(String config) {
        this.config = config;
    }

    public void run() {
        loadConfigurations();
    }

    private void loadConfigurations() {
        try {
            HashMap<Integer, Integer> parentMap = new HashMap<>();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(this.config));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray clients = (JSONArray) jsonObject.get("clients");
            String outputDir = (String) jsonObject.get("outputDir");

            for (Object object : clients) {

                JSONObject client = (JSONObject) object;
                int id = Integer.parseInt((String) client.get("id"));
                int workloadType = Integer.parseInt((String) client.get("workloadType"));
                int workloadLength = Integer.parseInt((String) client.get("workloadLength"));
                int distinctObject = Integer.parseInt((String) client.get("distinctObject"));

                ArrayList<Integer> workload = new ArrayList<>();
                switch (workloadType) {
                    case WorkloadType.UNIFORM:
                        workload = generateUniformWorkload(workloadLength, distinctObject, 0);
                        break;
                    case WorkloadType.BC:
                        double b = Double.parseDouble((String) client.get("b"));
                        double c = Double.parseDouble((String) client.get("c"));
                        workload = generateBCWorkload(workloadLength, distinctObject, b, c);
                        break;
                    case WorkloadType.ZIPF:
                        double a = Double.parseDouble((String) client.get("a"));
                        workload = generateZipfWorkload(workloadLength, distinctObject, a);
                        break;
                }

                outputWorkload(outputDir, id, workload);

            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkloadGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(WorkloadGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkloadGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param workloadLength
     * @param distinctObject
     * @return uniformly distributed workload
     */
    private ArrayList<Integer> generateUniformWorkload(int workloadLength, int distinctObject, int leading) {
        int lower = workloadLength / distinctObject;
        int remainder = workloadLength % distinctObject;

        ArrayList<Integer> workload = new ArrayList<>();

        int k = lower;
        for (int i = 0; i < distinctObject; i++) {
            if (i >= distinctObject - remainder) {
                k = lower + 1;
            }
            for (int j = 0; j < k; j++) {
                workload.add(i + leading);
            }
        }

        Collections.shuffle(workload);
        return workload;
    }

    /**
     *
     * @param workloadLength
     * @param distinctObject
     * @param b
     * @param c
     * @return b-c access workload
     */
    private ArrayList<Integer> generateBCWorkload(int workloadLength, int distinctObject, double b, double c) {
        int cSize = (int) (workloadLength * c);

        ArrayList<Integer> bUniform = generateUniformWorkload(cSize, (int) (distinctObject * b), 0);
        ArrayList<Integer> bpUniform = generateUniformWorkload(workloadLength - cSize, distinctObject - (int) (distinctObject * b), (int) (distinctObject * b));

        ArrayList<Integer> result = new ArrayList<>();
        result.addAll(bUniform);
        result.addAll(bpUniform);

        Collections.shuffle(result);

        return result;
    }

    /**
     *
     * @param workloadLength
     * @param distinctObject
     * @param a
     * @return Zipf workload
     */
    private ArrayList<Integer> generateZipfWorkload(int workloadLength, int distinctObject, double a) {

        ArrayList<Integer> result = new ArrayList<Integer>();

        double h = 0;
        for (int i = 1; i <= distinctObject; i++) {
            h += Math.pow(1.0 / i, a);
        }

        int popularity;
        for (int i = 1; i < distinctObject; i++) {
            double fx = 1.0 / (Math.pow(i, a) * h);
            double diff = fx * workloadLength - Math.floor(fx * workloadLength);
            popularity = (int) (Math.floor(fx * workloadLength));
            if (diff > Math.random()) {
                popularity++;
            }
            for (int j = 0; j < popularity; j++) {
                result.add(i);
            }
        }

        Collections.shuffle(result);
        return result;
    }

    /**
     * Outputs the workload to the outputDir.
     *
     * @param outputDir
     * @param id
     * @param workload
     */
    private void outputWorkload(String outputDir, int id, ArrayList<Integer> workload) {
        File directory = new File(String.valueOf(outputDir));
        
        if (!directory.exists()) {
            directory.mkdir();
        }
        
        PrintWriter pw;
        try {
            pw = new PrintWriter(new File(outputDir + "/" + id + ".txt"));
            for (Integer request : workload) {
                pw.println(request);
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkloadGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
