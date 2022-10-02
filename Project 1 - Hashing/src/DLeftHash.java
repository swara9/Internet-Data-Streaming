import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DLeftHash {

    int numEntries;
    int numSegments;
    int[] hashTable;
    List<Integer> hashFunctions;
    int segmentSize;

    public DLeftHash(int numEntries, int numSegments){
        this.numEntries = numEntries;
        this.numSegments = numSegments;
        hashTable = new int[numEntries];
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
//        int flowId = getRandom();
        for (int i=0; i<numFlows; i++){
//            while (flows.contains(flowId)){
            flowId = getRandom();
//            }
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
            System.out.println(entry);
        }
    }

    public boolean receive(int flowId){
        int hashFun;
        for (int i=0; i<numSegments; i++){
            hashFun = hashFunctions.get(i);
            int hashValue = flowId ^ hashFun;
            // int hashCode = String.valueOf(hashValue).hashCode();
            int index = (hashValue % segmentSize)+(i*segmentSize);
            if(hashTable[index]==flowId){
                //increment counter
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
            // int hashCode = String.valueOf(hashValue).hashCode();
            int index = (hashValue % segmentSize)+(i*segmentSize);
            if(hashTable[index]==0){
                //increment counter
                hashTable[index] = flowId;
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        List<Integer> flows = generateFlows(1000);
        DLeftHash dLeftHash = new DLeftHash(1000, 4);
        dLeftHash.receiveFlows(flows);
    }
}
