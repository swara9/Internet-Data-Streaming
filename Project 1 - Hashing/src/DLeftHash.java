import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DLeftHash {

    int numEntries;
    int numSegments;
    TableEntry[] hashTable;
    List<Integer> hashFunctions;
    int segmentSize;

    public DLeftHash(int numEntries, int numSegments){
        this.numEntries = numEntries;
        this.numSegments = numSegments;
        hashTable = new TableEntry[numEntries];
        this.segmentSize = numEntries/ this.numSegments;
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
        for (int i = 0; i< numSegments; i++){
            while (hashFunctions.contains(hash)){
                hash = getRandom();
            }
            hashFunctions.add(hash);
        }
    }

    public void receiveFlows(List<Integer> flows) throws IOException {
        int hits = 0;
        for (int flowId: flows){
            if(receive(flowId)) hits++;
        }

        File fout = new File("OutputDLeftHash.txt");
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

    public boolean receive(int flowId){
        int hashFun;
        for (int i=0; i<numSegments; i++){
            hashFun = hashFunctions.get(i);
            int hashValue = flowId ^ hashFun;
            int hashCode = String.valueOf(hashValue).hashCode();
            if(hashCode<0) hashCode = hashCode*-1;
            int index = (hashCode % segmentSize)+(i*segmentSize);
            if(null != hashTable[index] && hashTable[index].flowID==flowId){
                //increment counter
                hashTable[index].count++;
                return true;
            }
        }

        return insert(flowId);
    }

    public boolean insert(int flowId){
        int hashFun;
        for (int i=0; i<numSegments; i++){
            hashFun = hashFunctions.get(i);
            int hashValue = flowId ^ hashFun;
            int hashCode = String.valueOf(hashValue).hashCode();
            if(hashCode<0) hashCode = hashCode*-1;
            int index = (hashCode % segmentSize)+(i*segmentSize);
            if(null==hashTable[index]){
                //increment counter
                hashTable[index] = new TableEntry(flowId);
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) throws IOException {
        List<Integer> flows = generateFlows(1000);
        DLeftHash dLeftHash = new DLeftHash(1000, 4);
        dLeftHash.receiveFlows(flows);
        if(args.length == 3) {
            try {
                flows = generateFlows(Integer.parseInt(args[1]));
                dLeftHash = new DLeftHash(Integer.parseInt(args[0]),Integer.parseInt(args[2]));
                dLeftHash.receiveFlows(flows);
            } catch(NumberFormatException nfe) {
                System.out.println("Please provide a valid Input");
            }
        }
    }
}
