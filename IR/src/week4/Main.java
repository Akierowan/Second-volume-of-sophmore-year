package week4;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static HashMap<String, Set<Integer>> invertedIndex = new HashMap<>();
    //存储文档名称和对应id
    static Map<Integer, String> documents = new HashMap<>();

    public static void main(String[] args) {
        String folderPath = "IR/Documents";
        create(folderPath);
        //文档名称对应id
        System.out.println("The documents names correspond id as follow: ");
        for (Map.Entry<Integer, String> entry : documents.entrySet()){
            System.out.println(entry.getValue() + " --- " + entry.getKey());
        }

        Scanner scanner = new Scanner(System.in);
        String word;
        while (true){
            System.out.println("Input the keyword(\"0\" to exit): ");
            word = scanner.next();
            if(word.equals("0")){
                break;
            }
            //将输入的单词转化为小写再进行比对
            word = word.toLowerCase();
            if(invertedIndex.containsKey(word)) {
                List<Integer> ids = new ArrayList<>(invertedIndex.get(word));
                Collections.sort(ids);
                System.out.println(ids);
            } else {
                System.out.println("none");
            }
        }
    }

    static void create(String folderPath){
        //选中该文件夹中的所有创建Inverted Index
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        int documentId = 1;
        for(File file : files) {
            documents.put(documentId, file.getName());
            try(FileInputStream fileInputStream = new FileInputStream(file);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream)); ){
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    //文本加工
                    List<String> words = process(line);
                    for (String word : words) {
                        invertedIndex.putIfAbsent(word, new HashSet<>());
                        invertedIndex.get(word).add(documentId);
                    }
                }
                documentId++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static List<String> process(String line) {
        //单词拆分
        Pattern pattern = Pattern.compile("\\b\\w+(?:\\.\\w+)*\\b");
        Matcher matcher = pattern.matcher(line);
        List<String> words = new ArrayList<>();
        while (matcher.find()) {
            String word = matcher.group();
            //normalization
            word.replaceAll("\\.", "");
            //统一转化为小写字母存储
            word = word.toLowerCase();
            words.add(word);
        }
        return words;
    }
}
