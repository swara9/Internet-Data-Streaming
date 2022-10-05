import java.util.*;

public class CodingBloomFilter {
    int numFilters; //3
    int numBits; //30000
    int numHashes; //7
    List<Integer> hashFunctions;
    List<int[]> codingBloomFilters;

    public CodingBloomFilter(int numFilters, int numBits, int numHashes){
        this.numFilters = numFilters;
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

    public static List<FlowSet> generateSets(int numSets, int setSize, String[] codes) {
        List<FlowSet> flowSets = new ArrayList<>();
        Set<Integer> unique = new HashSet<>();
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
            set = new FlowSet(codes[i], elements);
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

    public static void main(String[] args) {
        int numSets = 7;
        String[] codes = {"001","010","011","100","101","110","111"};
        List<FlowSet> sets = generateSets(numSets, 1000, codes);

        CodingBloomFilter cbm = new CodingBloomFilter(3, 30000, 7);
        cbm.encode(sets);
        System.out.println(cbm.lookup(sets));
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

//    Coded Bloom filter
//        Input: number of sets, number of elements in each set, number of filters, number of bits
//        in each filter, number of hashes – for demo, they are 7, 1000, 3, 30,000, and 7 respectively
//        Function:  generate  7  sets  of  1000  elements  each,  their  codes  are  001  through  111
//        respectively, encode all sets in 3 filters according to the algorithm, perform lookup on all
//        elements in the 7 sets. All 7000 elements should be distinct.
//        Output: number of elements whose lookup results are correct
