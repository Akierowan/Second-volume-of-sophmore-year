package chapter1_b;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = scanner.nextInt();
        }
        Arrays.sort(arr);
        int ans = Integer.MAX_VALUE;
        for (int i = 1; i < n; i++) {
            int d = arr[i] - arr[i - 1];
            if (d < ans) {
                ans = d;
            }
        }
        System.out.println(ans);
    }
}
