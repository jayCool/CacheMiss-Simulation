package generator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ZhangJiangwei
 */
class RequestGenerator {

    String requestOutPutAddress;
    String requestConfigFile;
   
    /**
     * 
     * @param requestOutPutAddress 
     */
    RequestGenerator(String requestOutPutAddress) {
        this.requestOutPutAddress = requestOutPutAddress;
     }



    public ArrayList<String> generateManual(String file) {
           ArrayList<String> result = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(file));
         
            while (scanner.hasNext()) {
                result.add(scanner.nextLine().trim());
            }
            scanner.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public ArrayList<String> generateUniform(int totalSize, ArrayList<String> elementID) {
        int lower = totalSize / elementID.size();
        int remainder = totalSize % elementID.size();
        ArrayList<String> files = new ArrayList<>();
        int k = lower;
        for (int i = 0; i < elementID.size(); i++) {
            if (i >= elementID.size() - remainder) {
                k = lower + 1;
            }
            for (int j = 0; j < k; j++) {
                files.add(elementID.get(i));
            }
        }

        Collections.shuffle(files);
        printFiles(files);
        return files;
    }

    public ArrayList<String> generateZipf(int totalSize, ArrayList<String> elementID, double a) {
        Collections.shuffle(elementID);
        ArrayList<String> result = new ArrayList<String>();
        double h = 0;
        for (int i = 1; i <= elementID.size(); i++) {
            h += Math.pow(1.0 / i, a);
        }
        int popularity;
        int total = 0;
        for (int i = 1; i < elementID.size(); i++) {
            double fx = 1.0 / (Math.pow(i, a) * h);
            double diff = fx * totalSize - Math.floor(fx * totalSize);
            popularity = (int) (Math.floor(fx * totalSize));
            if (diff > Math.random()) {
                popularity++;
            }
            if (popularity > 0) {
                total++;
            }
            //    System.out.println(popularity);
            for (int j = 0; j < popularity; j++) {
                result.add(elementID.get(i));
            }

        }
        //     System.out.println(total  + "  "+ result.size());
        Collections.shuffle(result);
        printFiles(result);

        return result;
    }

    public ArrayList<String> generateBC(int totalSize, ArrayList<String> elementID, double b, double c) {
        int cSize = (int) (totalSize * c);
        int cpSize = totalSize - cSize;
        ArrayList<String> bElement = new ArrayList<>();
        ArrayList<String> bpElement = new ArrayList<>();
        Collections.shuffle(elementID);
        int bElementSize = (int) (elementID.size() * b);

        for (int i = 0; i < elementID.size(); i++) {
            if (i < bElementSize) {
                bElement.add(elementID.get(i));
            } else {
                bpElement.add(elementID.get(i));
            }
        }

        ArrayList<String> bUniform = this.generateUniform(cSize, bElement);

        ArrayList<String> bpUniform = this.generateUniform(cpSize, bpElement);

        ArrayList<String> result = new ArrayList<>();
        result.addAll(bUniform);
        result.addAll(bpUniform);
        Collections.shuffle(result);
        printFiles(result);

        return result;
    }

    private void printFiles(ArrayList<String> files) {
        PrintWriter pw;
        try {
            pw = new PrintWriter(new File(this.requestOutPutAddress));
            for (String temp : files) {
                pw.println(temp);
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RequestGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    ArrayList<Queue<String>> generateRequests(String requestAddress, int client_no, double a, double b, double c, int requestType, int singleWorkLoadSize, int totalDistinctFiles) {
        ArrayList<RequestType> requestTypes = new ArrayList<>();

        for (int i = 0; i < client_no; i++) {

            RequestType rt = new RequestType(requestType, singleWorkLoadSize, b, c, a, totalDistinctFiles);
            requestTypes.add(rt);
        }
        
        ArrayList<String> traceAddresses = new ArrayList<>();
        if (requestType==4){
            traceAddresses = generateManual(requestAddress);
        }
        ArrayList<Queue<String>> result = new ArrayList<>();
        for (RequestType rt : requestTypes) {
            Queue<String> queue = new LinkedList<>();
            switch (rt.type) {
                case 1:
                    queue.addAll(generateUniform(rt.singleWorkLoadSize, rt.distinctElements));
                    break;
                case 2:
                    queue.addAll(generateBC(rt.singleWorkLoadSize, rt.distinctElements, rt.b, rt.c));
                    break;
                case 3:
                    queue.addAll(generateZipf(rt.singleWorkLoadSize, rt.distinctElements, rt.a));
                    break;
                case 4:
                    queue.addAll(generateManual(traceAddresses.remove(0)));
                    break;
            }
            result.add(queue);
        }
        return result;
    }
    
    
   

}
