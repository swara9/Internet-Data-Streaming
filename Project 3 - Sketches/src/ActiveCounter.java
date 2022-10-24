import java.util.Random;

public class ActiveCounter {
    int number;
    int exponent;

    public ActiveCounter(){
        number = 0;
        exponent = 0;
    }

    public boolean doesIncrement(int exponent){
        int prob = (int) Math.pow(2,exponent);
        Random random = new Random();
        return random.nextInt(0, prob) == 0;
    }

    public long increment(int times){
        int range = (int)Math.pow(2,16)-1;
        for(int i=0; i<times; i++){
            if(doesIncrement(exponent)){
                number++;
            }
            if(number>range){
                number = 32768;
                exponent++;
            }
        }
        return (long) (number * Math.pow(2, exponent));
    }

    public static void main(String[] args) {
        ActiveCounter activeCounter = new ActiveCounter();
        System.out.println(activeCounter.increment(1000000));
    }
}
