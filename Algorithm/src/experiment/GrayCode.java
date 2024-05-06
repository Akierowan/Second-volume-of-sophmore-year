package experiment;

import java.util.Scanner;

public class GrayCode {
    static int[][] gray;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
         int n = scanner.nextInt();
         int m = (int)Math.pow(2, n);
         gray = new int[m][n];
         gray[0][0] = 0;
         gray[1][0] = 1;
         fun(n, m);
        for (int i = 0; i < m; i++) {
            for (int j = n - 1 ; j >= 0; j--) {
                System.out.print(gray[i][j]);
            }
            System.out.println();
        }
    }
    static void fun(int n, int m){
        if(n == 1) {
            return;
        }
        fun(n - 1, m / 2);
        for (int i = 0; i < m / 2; i++) {
            gray[i][n - 1] = 0;
        }
        for (int i = m / 2; i < m; i++) {
            gray[i][n - 1] = 1;
            for (int j = 0; j < n - 1; j++) {
                gray[i][j] = gray[m - i - 1][j];
            }
        }
    }
}
