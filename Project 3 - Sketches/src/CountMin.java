import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class CountMin {
    int k;
    int w;
    List<int[]> countMin;
    List<Integer> hashFunctions;

    public CountMin(int k, int w){
        this.k = k;
        this.w = w;
        countMin = new ArrayList<>();
        for(int i=0; i<k; i++){
            int[] counter = new int[w];
            countMin.add(counter);
        }
        generateHashFunctions();
    }

    private static int getRandom(){
        Random random = new Random();
        return random.nextInt(Integer.MAX_VALUE - 1);
    }

    public void generateHashFunctions(){
        hashFunctions = new ArrayList<>();
        int hash = getRandom();
        for (int i=0; i<k; i++){
            while (hashFunctions.contains(hash)){
                hash = getRandom();
            }
            hashFunctions.add(hash);
        }
    }


    public void record(List<String[]> flows){
        int hashFun;
        //for each flow
        for(String[] flow: flows){
            String flowId = flow[0];
            int count = Integer.parseInt(flow[1]);
            for(int i=0; i<k; i++){
                hashFun = hashFunctions.get(i);
                int hashCode = flowId.hashCode();
                int hashValue = hashCode^hashFun;
                if(hashValue<0) hashValue = hashValue*-1;
                int index = hashValue % w;
                countMin.get(i)[index]+=count;
            }
        }
    }

    public int query(String flowId){
        int min = Integer.MAX_VALUE;
        int hashFun;
        for(int i=0; i<k; i++){
            hashFun = hashFunctions.get(i);
            int hashCode = flowId.hashCode();
            int hashValue = hashCode^hashFun;
            if(hashValue<0) hashValue = hashValue*-1;
            int index = hashValue % w;
            int curr = countMin.get(i)[index];
            if(curr < min){
                min = curr;
            }
        }
        return min;
    }

    public float computeAvgError(List<String[]> flows){
        float avgError = 0;
        int numFlows = flows.size();
        int estimated;
        int actual;
        for(String[] flow: flows){
            estimated = query(flow[0]);
            actual = Integer.parseInt(flow[1]);
            avgError += Math.abs(estimated-actual);
        }
        avgError = avgError/numFlows;
        return avgError;
    }

    public PriorityQueue<String[]> top100Estimated(List<String[]> flows) {
        PriorityQueue<String[]> priorityQueue = new PriorityQueue<>((a, b) -> (Integer.parseInt(b[1]) - Integer.parseInt(a[1])));
        int estimated;
        int actual;
        for(String[] flow: flows){
            estimated = query(flow[0]);
            actual = Integer.parseInt(flow[1]);
            String[] pqObject = new String[3];
            pqObject[0] = flow[0];
            pqObject[1] = String.valueOf(estimated);
            pqObject[2] = String.valueOf(actual);
            priorityQueue.add(pqObject);
        }
        return priorityQueue;
    }

    public void generateOutput(List<String[]> flows) throws IOException {
        File fout = new File("CountMinOutput.txt");
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write("Average error among all flows = "+ computeAvgError(flows));
        bw.write("\n\n100 flows with largest estimated size: ");
        bw.write("\n\nFlow ID\t\t\t Estimated size \t\t True size");

        PriorityQueue<String[]> priorityQueue = top100Estimated(flows);
        for(int i=0; i<100; i++){
            String[] flow = priorityQueue.remove();
            bw.write("\n"+flow[0]+"\t "+flow[1]+"\t\t\t\t"+flow[2]);
        }
        bw.close();
        fos.close();

    }

    public static void main(String[] args) throws IOException {
        String file ="project3input.txt";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        reader.readLine() ;
        List<String[]> flows = new ArrayList<>();
        String currentLine = reader.readLine();
        while(currentLine!=null){
            String[] flow = currentLine.split("\t");
            flow[1] = flow[1].trim();
            flows.add(flow);
            currentLine = reader.readLine();
        }
        reader.close();

        CountMin countMin = new CountMin(3, 3000);
        if(args.length == 2) {
            try {
                int k = Integer.parseInt(args[0]);
                int w = Integer.parseInt(args[1]);
                countMin = new CountMin(k,w);
            } catch(NumberFormatException nfe) {
                System.out.println("Please provide a valid Input");
            }
        }
        countMin.record(flows);
        countMin.generateOutput(flows);
    }
}