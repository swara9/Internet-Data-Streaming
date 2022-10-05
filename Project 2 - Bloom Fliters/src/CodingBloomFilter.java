import java.io.*;
import java.util.*;

public class CodingBloomFilter {
    int numFilters;
    int numBits;
    int numHashes;
    List<Integer> hashFunctions;
    List<int[]> codingBloomFilters;

    public CodingBloomFilter(int numSets, int numBits, int numHashes){
        this.numFilters = log2(numSets+1);
        this.numBits = numBits;
        this.numHashes = numHashes;
        this.codingBloomFilters = new ArrayList<>();
        generateHashFunctions();
        initBloomFilters();
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

    public void initBloomFilters(){
        for(int i = 0; i<numFilters; i++){
            codingBloomFilters.add(new int[numBits]);
        }
    }

    public static List<FlowSet> generateSets(int numSets, int setSize) {
        List<FlowSet> flowSets = new ArrayList<>();
        Set<Integer> unique = new HashSet<>();
        List<String> codes = new ArrayList<>();
        int codeBits = log2(numSets+1);
        generateAllBinaryStrings(codeBits, new int[codeBits], 0, codes);
        FlowSet set;
        List<Integer> elements;
        for(int i=0; i<numSets; i++){
            elements = new ArrayList<>();
            int element = getRandom();
            for (int j=0; j<setSize; j++){
                while (unique.contains(element)){
                    element = getRandom();
                }
                elements.add(element);
                unique.add(element);
            }
            set = new FlowSet(codes.get(i+1), elements);
            flowSets.add(set);
        }

        return flowSets;
    }

    public void encode(List<FlowSet> sets){
        for(FlowSet set: sets){
            String code = set.code;
            //for each element
            for(int element: set.elements){
                for(int i=0; i<code.length(); i++){
                    if(code.charAt(i)=='1'){
                        //encode
                        for(int j=0; j<numHashes; j++){
                            //calculate hashcode
                            int hashFun = hashFunctions.get(j);
                            int hashValue = element ^ hashFun;
                            int hashCode = String.valueOf(hashValue).hashCode();
                            if(hashCode<0) hashCode = hashCode*-1;
                            int index = hashCode % numBits;
                            codingBloomFilters.get(i)[index] = 1;
                        }
                    }
                }
            }
        }
    }

    public int lookup(List<FlowSet> sets){
        int numCorrectLookups = 0;
        boolean correct = true;
        //for each set
        for(FlowSet set: sets){
            String code = set.code;
            //for each element in the set
            for(int element: set.elements){
                //for every bloom filter
                for(int i=0; i<code.length(); i++){
                    int j;
                    correct = true;
                    for(j=0; j<numHashes; j++){
                        //calculate hashcode
                        int hashFun = hashFunctions.get(j);
                        int hashValue = element ^ hashFun;
                        int hashCode = String.valueOf(hashValue).hashCode();
                        if(hashCode<0) hashCode = hashCode*-1;
                        int index = hashCode % numBits;
                        if(codingBloomFilters.get(i)[index] == 0){
                            break;
                        }
                    }

                    if((j==numHashes && code.charAt(i)=='0') || (j<numHashes && code.charAt(i)=='1')) {
                        correct = false;
                        break;
                    }
                }
                if(correct) numCorrectLookups++;
            }
        }
        return numCorrectLookups;
    }


    private static int getRandom(){
        Random random = new Random();
        return random.nextInt(Integer.MAX_VALUE - 1);
    }

    private static int log2(int n){
        return (int)Math.ceil((Math.log(n) / Math.log(2)));
    }

    // Function to generate all binary strings
    static void generateAllBinaryStrings(int n, int[] arr, int i, List<String> codes)
    {
        if (i == n)
        {
            StringBuilder sb = new StringBuilder();
            for(int num: arr){
                sb.append(num);
            }
            codes.add(sb.toString());
            return;
        }

        arr[i] = 0;
        generateAllBinaryStrings(n, arr, i + 1, codes);

        arr[i] = 1;
        generateAllBinaryStrings(n, arr, i + 1, codes);
    }

    public static void main(String[] args) throws IOException {
        CodingBloomFilter cbm = new CodingBloomFilter(7, 30000, 7);
        List<FlowSet> sets = generateSets(7, 1000);
        cbm.encode(sets);
        int lookup = cbm.lookup(sets);

        File fout = new File("CodingBloomFilterOutput.txt");
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write("Number of elements whose lookup results are correct = "+ lookup);
        bw.close();
        fos.close();
    }
}

class FlowSet{
    String code;
    List<Integer> elements;

    public FlowSet(String code, List<Integer> elements){
        this.code = code;
        this.elements = elements;
    }
}

