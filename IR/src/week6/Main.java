package week6;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        String folderPath = "IR/Documents";
        //开始创建倒排索引
        create(folderPath);
        //获取打印文档名称对应id
        Map<Integer, String> document_id = readDocumentId();
        System.out.println("The documents names correspond id as follow: ");
        for (Map.Entry<Integer, String> entry : document_id.entrySet()){
            System.out.println(entry.getValue() + " --- " + entry.getKey());
        }

        Scanner scanner = new Scanner(System.in);
        String line;
        while (true){
            System.out.println("Input the keywords(divided by space, \"0\" to exit): ");
            line = scanner.nextLine();
            if(line.equals("0")){
                break;
            }
            //读取文件中的倒排索引
            HashMap<String, Set<Integer>> invertedIndex = readFromFile();
            String[] words = line.split("\\s+");
            //记录需要匹配的单词数
            int amount = words.length;
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
    static void create(String folderPath){
        HashMap<String, Set<Integer>> invertedIndex = new HashMap<>();
        //选中该文件夹中的所有创建Inverted Index
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("IR\\File_dir\\document_id.txt"));
            writer.write(files.length + "\n");
            int documentId = 1;
            for(File file : files) {
                writer.write(documentId + " -- " + file.getName() + "\n");
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                while ((line = reader.readLine()) != null) {
                    //文本加工
                    List<String> words = process(line);
                    for (String word : words) {
                        invertedIndex.putIfAbsent(word, new HashSet<>());
                        invertedIndex.get(word).add(documentId);
                    }
                }
                reader.close();
                documentId++;
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeToFile(invertedIndex);
    }

    //处理文本
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

    //将倒排索引写入文件中
    static void writeToFile(HashMap<String, Set<Integer>> invertedIndex){
        try (PrintWriter writer = new PrintWriter("IR\\File_dir\\invertedIndex.txt")){
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

    static Map<Integer, String> readDocumentId() {
        Map<Integer, String> map = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("IR\\File_dir\\document_id.txt"));
            int size = Integer.parseInt(reader.readLine());
            for (int i = 0; i < size; i++) {
                String line = reader.readLine();
                String[] parts = line.split(" -- ");
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                map.put(id, name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    //读取文件中的倒排索引并返回
    static HashMap<String, Set<Integer>> readFromFile() {
        HashMap<String, Set<Integer>> invertedIndex = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("IR\\File_dir\\invertedIndex.txt"))){
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
