package experiment;

import java.util.Scanner;

public class Median {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] arr1 = new int[n];
        int[] arr2 = new int[n];
        for (int i = 0; i < n; i++) {
            arr1[i] = scanner.nextInt();
        }
        for (int i = 0; i < n; i++){
            arr2[i] = scanner.nextInt();
        }
        int left1 = 0, left2 = 0;
        int right1 = n - 1, right2 = n - 1;

        while(true) {
            if((right1 - left1 + 1) == 2 && (right2 - left2 + 1) == 2) {
                int num1 = Math.max(arr1[left1], arr2[left2]);
                int num2 = Math.min(arr1[right1], arr2[right2]);
                System.out.println((num1 + num2) * 0.5);
                return;
            }

            int m1 = (left1 + right1) / 2;
            int m2 = (left2 + right2) / 2;
//            if(arr1[m1] < arr2[m2]) {
//                left1 = m1;
//                right2 = m2;
//                if((right2 + left2 + 1) % 2 == 0) {
//                    right2++;
//                }
//            } else{
//                right1 = m1;
//                left2 = m2;
//                if((right1 + left1 + 1 ) % 2 == 0){
//                    right1++;
//                }
//            }
        }
    }
}
