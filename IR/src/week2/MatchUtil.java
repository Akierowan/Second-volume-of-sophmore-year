package week2;

// 利用KMP算法进行子串匹配
public  class MatchUtil {
    private static int[] getNext(String str) {
        char[] arr = str.toCharArray();
        int len = arr.length;
        int[] next = new int[len];
        next[0] = -1;
        int j = 0, k = -1;
        while (j < len - 1) {
            if (k == -1 || charEqual(arr[j], arr[k])) {
                j++;
                k++;
                next[j] = k;
            } else {
                k = next[k];
            }
        }
        return next;
    }
    //自定义比较函数，忽略大小写进行匹配
    private static boolean charEqual(char c1, char c2) {
        return Character.toLowerCase(c1) == Character.toLowerCase(c2);
    }
    static boolean kmp(String s1, String s2) {
        char[] arr1 = s1.toCharArray();
        char[] arr2 = s2.toCharArray();
        int i = 0, j = 0;
        int[] next = getNext(s2);
        while(i < arr1.length && j < arr2.length) {
            if (j == -1 || charEqual(arr1[i], arr2[j])) {
                i++;
                j++;
            } else {
                j = next[j];
            }
        }
        if (j == arr2.length) {
            return true;
        } else {
            return false;
        }
    }
}
