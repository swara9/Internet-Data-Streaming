import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CountingBloomFilter {

    int numBits;
    int numHashes;
    int[] bloomFilter;
    List<Integer> hashFunctions;

    public CountingBloomFilter(int numBits, int numHashes){
        this.numBits = numBits;
        this.numHashes = numHashes;
        this.bloomFilter = new int[numBits];
        generateHashFunctions();
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

    public static List<Integer> generateSet(int setSize) {
        List<Integer> set = new ArrayList<>();
        int element = getRandom();
        for (int i = 0; i < setSize; i++) {
            while (set.contains(element)) {
                element = getRandom();
            }
            set.add(element);
        }
        return set;
    }

    public void encode(List<Integer> set){
        int hashFun;
        for (int element: set){
            for (int j = 0; j<numHashes; j++){
                hashFun = hashFunctions.get(j);
                int hashValue = element ^ hashFun;
                int hashCode = String.valueOf(hashValue).hashCode();
                if(hashCode<0) hashCode = hashCode*-1;
                int index = hashCode % numBits;
                bloomFilter[index] = bloomFilter[index]+1;
            }
        }
    }

    public int lookup(List<Integer> set){
        int count = 0;
        int hashFun;
        int j;
        for (int element: set){
            for (j = 0; j<numHashes; j++){
                hashFun = hashFunctions.get(j);
                int hashValue = element ^ hashFun;
                int hashCode = String.valueOf(hashValue).hashCode();
                if(hashCode<0) hashCode = hashCode*-1;
                int index = hashCode % numBits;
                if(bloomFilter[index] == 0){
                    break;
                }
            }
            if(j==numHashes) count++;
        }
        return count;
    }

    public void remove(int num, List<Integer> set){
        int hashFun;
        for(int i=0; i<num; i++){
            int element = set.get(i);
            for (int j = 0; j<numHashes; j++){
                hashFun = hashFunctions.get(j);
                int hashValue = element ^ hashFun;
                int hashCode = String.valueOf(hashValue).hashCode();
                if(hashCode<0) hashCode = hashCode*-1;
                int index = hashCode % numBits;
                bloomFilter[index] = bloomFilter[index]-1;
            }
        }
    }

    private static int getRandom(){
        Random random = new Random();
        return random.nextInt(Integer.MAX_VALUE - 1);
    }

    public static void main(String[] args) {
        CountingBloomFilter cbm = new CountingBloomFilter(10000, 7);
        List<Integer> setA = generateSet(1000);
        cbm.encode(setA);
        cbm.remove(500, setA);
        List<Integer> setB = generateSet(500);
        cbm.encode(setB);
        System.out.println(cbm.lookup(setA));
    }
}
