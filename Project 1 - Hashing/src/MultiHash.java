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
        return random.nextInt(Integer.MAX_VALUE - 1)+1;
    }

    public static List<Integer> generateFlows(int numFlows){
        List<Integer> flows = new ArrayList<>();
        int flowId = getRandom();
        for (int i=0; i<numFlows; i++){
            while (flows.contains(flowId)){
                flowId = getRandom();
            }
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

    public void captureFlows(List<Integer> flows){
        int hits = 0;
        int misses = 0;
        int flowId;
        int hashFun;

        for (int flow : flows) {
            flowId = flow;
            int j;
            for (j = 0; j < hashFunctions.size(); j++) {
                hashFun = hashFunctions.get(j);
                int hashValue = flowId ^ hashFun;
//                int hashCode = String.valueOf(hashValue).hashCode();
                int index = hashValue % numEntries;
                //if empty;
                if (hashTable[index] == null) {
                    hashTable[index] = new TableEntry(flowId);
                    hits++;
                    break;
                } else if (hashTable[index].flowID == flowId) {
                    hashTable[index].count++;
                    break;
                }
            }
            if (j == numHashes) {
                misses++;
            }
        }
        System.out.println("Number of flows recorded = "+ hits);
        System.out.println("Number of flows missed = "+ misses);

    }

    public static void main(String[] args) {
        List<Integer> flows = generateFlows(1000);
        MultiHash multiHash = new MultiHash(1000, 3);
        multiHash.captureFlows(flows);
    }
}

class TableEntry{
    int flowID;
    int count;

    public TableEntry(int flowID){
        this.flowID = flowID;
        count = 1;
    }
}

//Input: number of table entries, number of flows, number of hashes – for demo, they are
//        1000, 1000 and 3, respectively
//        Function: generate flow IDs randomly, assume each flow has one packet, record one flow
//        at a time into the hash table, and ignore the flows that cannot be placed into the hash
//        table.
//        Output: number of flows in the hash table, and the list of table entries (print out the flow
//        ID if an entry has a flow or zero otherwise)