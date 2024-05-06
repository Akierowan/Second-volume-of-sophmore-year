package chapter1_a;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ArrayList<Integer> nums = new ArrayList<Integer>();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int input = scanner.nextInt();
            if (input == 0) {
                break;
            }
            nums.add(input);
        }
        for (int num : nums) {
            for (int i = num + 1; ; i++) {
                if(judge(i)) {
                    System.out.println(i);
                    break;
                }
            }
        }
    }

    static boolean judge(int num) {
        ArrayList<Integer> factor = new ArrayList<Integer>(100);
        //Arrays.fill(factor,0);
        int t = num;
        int i = 2;
        while (i <= t) {
            if(t % i == 0) {
                factor.add(i);
                t /= i;
                i = 2;
            } else {
                i++;
            }
        }
        if(factor.isEmpty()) {
            return false;
        }
        int sum = 0;
        for(i = 0; i < factor.size(); i++) {
            sum += sumOfNum(factor.get(i));
        }
        return sum == sumOfNum(num);
    }

    static int sumOfNum(int num) {
        String str = Integer.toString(num);
        int sum = 0;
        for (int i = 0; i < str.length(); i++) {
            sum += str.charAt(i) - '0';
        }
        return sum;
    }
}
