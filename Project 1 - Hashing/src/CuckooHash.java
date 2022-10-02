import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CuckooHash {

    int numEntries;
    int numHashes;
    TableEntry[] hashTable;
    List<Integer> hashFunctions;
    int cuckooSteps;

    public CuckooHash(int numEntries, int numHashes, int cuckooSteps){
        this.numEntries = numEntries;
        this.numHashes = numHashes;
        hashTable = new TableEntry[numEntries];
        generateHashFunctions();
        this.cuckooSteps = cuckooSteps;
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

    public void receiveFlows(List<Integer> flows) throws IOException {
        int hits = 0;
        for (int flowId: flows){
            if(receive(flowId)) hits++;
        }

        File fout = new File("OutputCuckooHash.txt");
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

        for (int i=0; i<numHashes; i++){
            hashFun = hashFunctions.get(i);
            int hashValue = flowId ^ hashFun;
            int hashCode = String.valueOf(hashValue).hashCode();
            if(hashCode<0) hashCode = hashCode*-1;
            int index = hashCode % numEntries;
            //increment counter
            if(null!=hashTable[index] && hashTable[index].flowID == flowId) {
                hashTable[index].count++;
                return true;
            }
        }
        return insert(flowId);
    }

    public boolean insert(int flowId){
        int hashFun;
        for (int i=0; i<numHashes; i++){
            hashFun = hashFunctions.get(i);
            int hashValue = flowId ^ hashFun;
            int hashCode = String.valueOf(hashValue).hashCode();
            if(hashCode<0) hashCode = hashCode*-1;
            int index = hashCode % numEntries;
            //insert if empty
            if(null == hashTable[index]) {
                hashTable[index] = new TableEntry(flowId);
                return true;
            }
        }

        for (int i=0; i<numHashes; i++){
            hashFun = hashFunctions.get(i);
            int hashValue = flowId ^ hashFun;
            int hashCode = String.valueOf(hashValue).hashCode();
            if(hashCode<0) hashCode = hashCode*-1;
            int index = hashCode % numEntries;
            //insert if empty
            if(move(index, cuckooSteps)) {
                hashTable[index] = new TableEntry(flowId);
                return true;
            }
        }

        return false;
    }

    public boolean move(int index, int steps){
        TableEntry currentFlow = hashTable[index];
        int hashFun;

        for (int i=0; i<numHashes; i++){
            hashFun = hashFunctions.get(i);
            int hashValue = currentFlow.flowID ^ hashFun;
            int hashCode = String.valueOf(hashValue).hashCode();
            if(hashCode<0) hashCode = hashCode*-1;
            int hashedIndex = hashCode % numEntries;
            if(hashedIndex!=index && hashTable[hashedIndex]==null){
                hashTable[hashedIndex] = currentFlow;
                return true;
            }
        }

        if(steps>1){
            for (int i=0; i<numHashes; i++) {
                hashFun = hashFunctions.get(i);
                int hashValue = currentFlow.flowID ^ hashFun;
                int hashCode = String.valueOf(hashValue).hashCode();
                if(hashCode<0) hashCode = hashCode*-1;
                int hashedIndex = hashCode % numEntries;
                if(hashedIndex!=index && move(hashedIndex, steps-1)){
                    hashTable[hashedIndex] = currentFlow;
                    return true;
                }
            }
        }

        return false;
    }

    public static void main(String[] args) throws IOException {
        List<Integer> flows = generateFlows(1000);
        CuckooHash cuckooHash = new CuckooHash(1000,3,2);
        cuckooHash.receiveFlows(flows);
    }
}

