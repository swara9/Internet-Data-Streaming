import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CuckooHash {

    int numEntries;
    int numHashes;
    int[] hashTable;
    List<Integer> hashFunctions;
    int cuckooSteps;

    public CuckooHash(int numEntries, int numHashes, int cuckooSteps){
        this.numEntries = numEntries;
        this.numHashes = numHashes;
        hashTable = new int[numEntries];
        generateHashFunctions();
        this.cuckooSteps = cuckooSteps;
    }

    private static int getRandom(){
        Random random = new Random();
        return random.nextInt(Integer.MAX_VALUE - 1);
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

    public void receiveFlows(List<Integer> flows){
        int hits = 0;
        int misses = 0;
        for (int flowId: flows){
            if(receive(flowId)) hits++;
            else misses++;
        }
        System.out.println("Number of flows recorded = "+ hits);
        System.out.println("Number of flows missed = "+ misses);
        System.out.println("\n==========================");
        for (int entry: hashTable){
//            System.out.println(entry);
        }
    }

    public boolean receive(int flowId){
        int hashFun;

        for (int i=0; i<numHashes; i++){
            hashFun = hashFunctions.get(i);
            int hashValue = flowId ^ hashFun;
            int index = hashValue % numEntries;
            //increment counter
            if(hashTable[index]== flowId) return true;
        }
        return insert(flowId);
    }

    public boolean insert(int flowId){
        int hashFun;
        for (int i=0; i<numHashes; i++){
            hashFun = hashFunctions.get(i);
            int hashValue = flowId ^ hashFun;
            int index = hashValue % numEntries;
            //insert if empty
            if(hashTable[index] == 0) {
                hashTable[index] = flowId;
                return true;
            }
        }

        for (int i=0; i<numHashes; i++){
            hashFun = hashFunctions.get(i);
            int hashValue = flowId ^ hashFun;
            int index = hashValue % numEntries;
            //insert if empty
            if(move(index, cuckooSteps)) {
                hashTable[index] = flowId;
                return true;
            }
        }

        return false;
    }

    public boolean move(int index, int steps){
        int currentFlow = hashTable[index];
        int hashFun;

        for (int i=0; i<numHashes; i++){
            hashFun = hashFunctions.get(i);
            int hashValue = currentFlow ^ hashFun;
            int hashedIndex = hashValue % numEntries;
            if(hashedIndex!=index && hashTable[hashedIndex]==0){
                hashTable[hashedIndex] = currentFlow;
                return true;
            }
        }

        if(steps>1){
            for (int i=0; i<numHashes; i++) {
                hashFun = hashFunctions.get(i);
                int hashValue = currentFlow ^ hashFun;
                int hashedIndex = hashValue % numEntries;
                if(hashedIndex!=index && move(hashedIndex, steps-1)){
                    hashTable[hashedIndex] = currentFlow;
                    return true;
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {
        List<Integer> flows = generateFlows(1000);
        CuckooHash cuckooHash = new CuckooHash(1000,3,2);
        cuckooHash.receiveFlows(flows);
    }
}

//    Cuckoo hash table
//        Input: number of table entries, number of flows, number of hashes, number of Cuckoo
//        steps – for demo, they are 1000, 1000, 3, and 2, respectively
//        Function: generate flow IDs randomly, assume each flow has one packet, record one flow
//        at a time into the hash table, and ignore the flows that cannot be placed into the hash
//        table.
//        Output: number of flows in the hash table, and the list of table entries (print out the flow
//        ID if an entry has a flow or zero otherwise)
