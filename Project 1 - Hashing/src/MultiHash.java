import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultiHash {
    int numEntries;
    int numHashes;
    TableEntry[] hashTable;
    List<Integer> hashFunctions;

    public MultiHash(int numEntries, int numHashes){
        this.numEntries = numEntries;
        this.numHashes = numHashes;
        hashTable = new TableEntry[numEntries];
        generateHashFunctions();
    }

    private static int getRandom(){
        Random random = new Random();
        return random.nextInt(Integer.MAX_VALUE - 1);
    }

    public static List<Integer> generateFlows(int numFlows){
        List<Integer> flows = new ArrayList<>();
        int flowId;
        for (int i=0; i<numFlows; i++){
            flowId = getRandom();
            flows.add(flowId);
        }
        return flows;
    }

    public void generateHashFunctions(){
        hashFunctions = new ArrayList<>();
        int hash = getRandom();
        for (int i=0; i<numHashes; i++){
            while (hashFunctions.contains(hash)){
                hash = getRandom();
            }
            hashFunctions.add(hash);
        }
    }

    public void captureFlows(List<Integer> flows) throws IOException {
        int hits = 0;
        int flowId;
        int hashFun;

        for (int flow : flows) {
            flowId = flow;
            int j;
            for (j = 0; j < hashFunctions.size(); j++) {
                hashFun = hashFunctions.get(j);
                int hashValue = flowId ^ hashFun;
                int hashCode = String.valueOf(hashValue).hashCode();
                if(hashCode<0) hashCode = hashCode*-1;
                int index = hashCode % numEntries;
                //if empty;
                if (null == hashTable[index]) {
                    hashTable[index] = new TableEntry(flowId);
                    hits++;
                    break;
                } else if (hashTable[index].flowID == flowId) {
                    //increment count
                    hashTable[index].count += 1;
                    break;
                }
            }
        }

        File fout = new File("OutputMultiHash.txt");
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write("Number of flows recorded = "+ hits);
        bw.newLine();
        bw.write("\n============ Hash Table ==============");
        bw.newLine();

        for (TableEntry entry: hashTable){
            int output = entry == null ? 0 : entry.flowID;
            bw.write(Integer.toString(output));
            bw.newLine();
        }
        bw.close();
    }

    public static void main(String[] args) throws IOException {
        List<Integer> flows = generateFlows(1000);
        MultiHash multiHash = new MultiHash(1000, 3);
        multiHash.captureFlows(flows);
        if(args.length == 3) {
            try {
                flows = generateFlows(Integer.parseInt(args[1]));
                multiHash = new MultiHash(Integer.parseInt(args[0]),Integer.parseInt(args[2]));
                multiHash.captureFlows(flows);
            } catch(NumberFormatException nfe) {
                System.out.println("Please provide a valid Input");
            }
        }
    }
}
