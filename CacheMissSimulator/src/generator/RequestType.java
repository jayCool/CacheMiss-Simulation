package generator;


import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Zhang Jiangwei
 */
public class RequestType {

    int type;   // 1 for uniform, 2 for b-c, 3 Zipf
    double b, c;
    double a;
    int singleWorkLoadSize;
    ArrayList<String> distinctElements = new ArrayList<>();

    RequestType(int requestType, int singleWorkLoadSize, double b, double c, double a, int totalDistinctFiles) {
        this.type = requestType;
        this.singleWorkLoadSize = singleWorkLoadSize;
        this.b = b;
        this.c = c;
        this.a = a;

        //generate distinct elements for non-empirical inputs
        if (this.type != 4) {
            ArrayList<String> distinctElements = new ArrayList<String>();
            for (int k = 0; k < totalDistinctFiles; k++) {
                distinctElements.add("" + k);
            }
            this.distinctElements = distinctElements;
        }
    }

    @Override
    public String toString() {
        return "RequestType{" + "type=" + type + ", b=" + b + ", c=" + c + ", a=" + a + ", totalSize=" + singleWorkLoadSize + ", elements=" + distinctElements.toString() + '}';
    }
}
