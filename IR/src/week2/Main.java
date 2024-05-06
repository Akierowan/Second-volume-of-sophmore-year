package week2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //用于循环读取输入次数
        int n = 3;
        while (n-- > 0) {
            System.out.println("Please input the keyword to search:");
            String input = scanner.next();
            //去掉输入的单词两边的标点符号，避免用户误输入，保证是一个正确的单词
            input = input.replaceAll("^\\p{Punct}+|\\p{Punct}+$\n", "");
            //指定读取匹配文件的文件夹
            File folder = new File("IR\\Documents");
            File[] files = folder.listFiles();
            for (File file : files) {
                //若是成功匹配，输出其文件名
                if (matchInFile(input, file)) {
                    String name = file.getName();
                    System.out.println(name);
                }
            }
        }
    }
    static boolean matchInFile(String str, File file) {
        StringBuilder content = new StringBuilder();
        try {
            //利用scanner逐行读取文件内容保存到StringBuilder中
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return MatchUtil.kmp(content.toString(), str);
    }
}
