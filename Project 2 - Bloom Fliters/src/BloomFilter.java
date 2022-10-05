import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BloomFilter {
    int numBits;
    int numHashes;
    int[] bloomFilter;
    List<Integer> hashFunctions;

    public BloomFilter(int numBits, int numHashes){
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
                bloomFilter[index] = 1;
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
                if(bloomFilter[index] != 1){
                    break;
                }
            }
            if(j==numHashes) count++;
        }
        return count;
    }

    private static int getRandom(){
        Random random = new Random();
        return random.nextInt(Integer.MAX_VALUE - 1);
    }

    public static void main(String[] args) throws IOException {
        BloomFilter bloomFilter = new BloomFilter(10000, 7);
        List<Integer> setA = generateSet(1000);
        bloomFilter.encode(setA);
        int lookupA = bloomFilter.lookup(setA);
        List<Integer> setB = generateSet(1000);
        int lookupB = bloomFilter.lookup(setB);

        File fout = new File("BloomFilterOutput.txt");
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write("Number of elements after lookup of set A = "+ lookupA);
        bw.write("\nNumber of elements after lookup of set B = "+ lookupB);
        bw.close();
        fos.close();
    }

}