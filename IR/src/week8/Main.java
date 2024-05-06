package week8;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    //文档存储路径
    private static String DOCUMENT_PATH =  "IR/Document";
    //新增文档的存储路径
    private static String ADDITION_PATH = "IR/Addition";
    //倒排索引、字典存储路径
    private static String File_PATH = "IR/File_dir";
    //文档id序号
    private static  int documentId = 1;

    public static void main(String[] args) {
        //开始创建倒排索引
        create();

        //在原有的倒排索引上新增内容
        add();

        //读取文件中的倒排索引
        HashMap<String, Set<Integer>> invertedIndex = readFromFile();
        Scanner scanner = new Scanner(System.in);
        String line;
        while (true){
//            System.out.println("Input the keywords(divided by space, \"0\" to exit): ");
//            line = scanner.nextLine();
//            if(line.equals("0")){
//                break;
//            }
//            String[] words = line.split("\\s+");
            System.out.println("input the sentence(\"0\" to exit): ");
            line = scanner.nextLine();
            if (line.equals("0")) {
                break;
            }
            List<String> words = process(line);
            //记录需要匹配的单词数
            int amount = words.size();
            List<Integer>[] lists = new List[amount];
            int i = 0;
            for (String word : words) {
                word = word.toLowerCase();
                if (invertedIndex.containsKey(word)) {
                    List<Integer> ids = new ArrayList<>(invertedIndex.get(word));
                    Collections.sort(ids);
                    lists[i++] = ids;
                } else {
                    break;
                }
            }
            //读取到的lists的长度不等于单词数则说明必有单词缺失
            if( i != amount) {
                System.out.println("none");
                continue;
            }
            if(amount == 1) {
                System.out.println(lists[0]);
                continue;
            }
            List<Integer> list = lists[0];
            //用双指针进行遍历，已适配两个及两个以上输入单词的情况
            for (i = 1; i < amount ; i++) {
                List<Integer> temp = List.copyOf(list);
                list.clear();
                int p1 = 0, p2 = 0;
                while (p1 < lists[i].size() && p2 < temp.size()) {
                    int id1 = lists[i].get(p1), id2 = temp.get(p2);
                    if(id1 == id2) {
                        list.add(id1);
                        p1++;
                        p2++;
                    } else if(id1 < id2) {
                        p1++;
                    } else {
                        p2++;
                    }
                }
            }
            System.out.println(list);
        }
    }

    //创建倒排索引
    static void create(){
        HashMap<String, Set<Integer>> invertedIndex = new HashMap<>();
        //选中该文件夹中的所有创建Inverted Index
        handleFile(DOCUMENT_PATH, invertedIndex);
    }
    //新增倒排索引
    static void add(){
        HashMap<String, Set<Integer>> invertedIndex = readFromFile();  //读取原有的倒排索引
        handleFile(ADDITION_PATH, invertedIndex);
    }

    //给定文件夹路径和倒排索引来处理文件
    private static void handleFile(String folderPath, HashMap<String, Set<Integer>> invertedIndex) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        for (File file : files) {
            try(BufferedReader reader = new BufferedReader(new FileReader(file))){
                String line;
                while ((line = reader.readLine()) != null) {
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
        writeToFile(invertedIndex);
    }

    //处理文本
    static List<String> process(String line) {
        List<String> words = new ArrayList<>();

        Pattern CN_pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher CN_matcher = CN_pattern.matcher(line);
        //判断该行是中文还是英文
        if(CN_matcher.find()) {
            //读取词典
            HashSet<String> dictionary = new HashSet<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(File_PATH + "/dictionary.txt"))){
                String phase;
                while ((phase = reader.readLine()) != null) {
                    dictionary.add(phase);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //将该行按照逗号和句号进行拆分
            String[] sentence = line.split("[，。\\s]+");
            for(String str : sentence) {
                StringBuilder word = new StringBuilder();
                int maxLen = 0;        //最大匹配长度
                int i = 0;
                while (i < str.length()){
                    word.append(str.charAt(i));
                    if(dictionary.contains(word.toString())) {   //匹配成功则更新最大长度
                        maxLen = word.length();
                    }
                    i++;
                    if(word.length() == 5 || i == str.length()) { //达到最大匹配数为5 或者到达句子末尾 结束匹配
                        words.add(word.substring(0, maxLen).toString());
                        i = i  - (word.length() - maxLen);      //恢复至匹配成功后的第一位
                        maxLen = 0;
                        word.setLength(0);
                    }
                }
            }
        } else {
            //英文处理
            Pattern pattern = Pattern.compile("\\b\\w+(?:\\.\\w+)*\\b");
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                String word = matcher.group();
                //normalization
                word.replaceAll("\\.", "");
                //统一转化为小写字母存储
                word = word.toLowerCase();
                words.add(word);
            }
        }

        return words;
    }

    //将倒排索引写入文件中
    static void writeToFile(HashMap<String, Set<Integer>> invertedIndex){
        try (PrintWriter writer = new PrintWriter(File_PATH + "/invertedIndex.txt")){
            for(Map.Entry<String, Set<Integer>> entry : invertedIndex.entrySet()){
                writer.print(entry.getKey() + ":");
                for(Integer value : entry.getValue()) {
                    writer.print(value + ",");
                }
                writer.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取文件中的倒排索引并返回
    static HashMap<String, Set<Integer>> readFromFile() {
        HashMap<String, Set<Integer>> invertedIndex = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(File_PATH + "/invertedIndex.txt"))){
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String key = parts[0];
                String[] values = parts[1].split(",");
                Set<Integer> valueSet = new HashSet<>();
                for (String value : values) {
                    valueSet.add(Integer.parseInt(value));
                }
                invertedIndex.put(key, valueSet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return invertedIndex;
    }
}
