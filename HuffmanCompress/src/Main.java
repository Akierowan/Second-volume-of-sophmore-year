import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.PriorityQueue;

public class Main {
        public static void main(String[] args) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("C:\\Users\\luo\\Desktop\\File"));
            fileChooser.setDialogTitle("Choose file to compress");
            int userSelection = fileChooser.showOpenDialog(null);
            File file;
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
            } else {
                file = new File("HuffmanCompress/File_dir/Pic.bmp");
            }
            String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd_HH-mm-ss"));
            String folderPath = "HuffmanCompress/File_dir/" + timeStr;
            try {
                Files.createDirectories(Paths.get(folderPath));
                Files.copy(file.toPath(), new File(folderPath, file.getName()).toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int[] weight = new int[256];
            Arrays.fill(weight, 0);
            System.out.println("start to read");
//            try (FileInputStream fis = new FileInputStream(file);){
//                int b;
//                while ((b = fis.read()) != -1) {
//                    weight[b]++;
//                }
            //利用BufferedInputStream来加速读取
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    for (int i = 0; i < bytesRead; i++) {
                        weight[buffer[i] & 0xFF]++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("read finish");
            HuffmanTree huffmanTree = new HuffmanTree(weight);

        System.out.println("-----Huffman文件压缩-----");
        System.out.println("原文件每个字符的权值为：");
        System.out.println("Byte\tWeight");
        for(int i = 0; i < 256; i++) {
            if(weight[i] == 0) {
                continue;
            }
            System.out.println(String.format("0x%02X\t%d", i, weight[i]));
        }


        System.out.println("哈夫曼树的每个节点的信息为:");
        System.out.printf("Byte\tWeight\tParent\tLchild\tRchild\n");
        huffmanTree.printHfNode();

        System.out.println("先序遍历哈夫曼树的结果为:");
        huffmanTree.PreorderPrintHfWeight(510);
        System.out.println();

        System.out.println("先序遍历哈夫曼树输出编码信息:");
        huffmanTree.PreorderPrintHfCode(510);

       huffmanTree.printHfCodeInorder();

        new FileCompress(file, folderPath, huffmanTree.hfCode);
        FileDecompress.decompress(folderPath);
    }
}

class htNode{
    int parent, left, right;
    int weight;
    htNode(int weight) {
        this.weight = weight;
        parent = left = right = 0;
    }
}
class HuffmanTree{
    htNode[] HT = new htNode[511];
    String[] hfCode = new String[256];
    //构造函数
    HuffmanTree(int[] weight) {
        for(int i = 0; i < 256; i++) {
            HT[i] = new htNode(weight[i]);
        }
        creatHuffmanTree();
        creatHuffmanCode();
    }
    //构造哈夫曼树
        void creatHuffmanTree() {
            PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> HT[a].weight - HT[b].weight);
            for (int i = 0; i < 256; i++) {
                pq.offer(i);
            }
            for (int i = 256; i < 511; i++) {
                int node1 = pq.poll();
                int node2 = pq.poll();

                HT[i] = new htNode(HT[node1].weight + HT[node2].weight);
                HT[i].left = node1;
                HT[i].right = node2;
                HT[node1].parent = HT[node2].parent = i;
                pq.offer(i);
            }
        }
    //生成哈夫曼编码
    void creatHuffmanCode() {
        for (int i = 0; i < 256; i++) {
            StringBuilder code = new StringBuilder();
            int p = HT[i].parent;
            int t = i;
            while (p != 0) {
                if(HT[p].left == t) {
                    code.append(0);
                } else {
                    code.append(1);
                }
                t = p;
                p = HT[t].parent;
            }
            hfCode[i] = code.reverse().toString();
        }
    }
    //顺序遍历输出节点
    void printHfNode(){
        for (int i = 0; i < 511; i++) {
            System.out.printf("pHT[%d]\t%d\t%d\t%d\t%d\n", i + 1, HT[i].weight, HT[i].parent, HT[i].left, HT[i].right);
        }
    }
    int count = 0;
    //前序遍历输出权重
    void PreorderPrintHfWeight(int idx) {
        if (HT[idx].left == 0 && HT[idx].right == 0) {
            System.out.print(HT[idx].weight + " ");
            count++;
            if(count % 50 == 0) {
                System.out.println();
            }
            return;
        }
        PreorderPrintHfWeight(HT[idx].left);
        PreorderPrintHfWeight(HT[idx].right);
    }
    //前序遍历哈夫曼编码
    void PreorderPrintHfCode(int idx) {
        if (HT[idx].left == 0 && HT[idx].right == 0) {
            System.out.printf("0x%02X\t%s\n", idx, hfCode[idx]);
            return;
        }
        PreorderPrintHfCode(HT[idx].left);
        PreorderPrintHfCode(HT[idx].right);
    }
    //顺序遍历输出哈夫曼编码
    void printHfCodeInorder() {
        for (int i = 0; i < 256; i++) {
            System.out.printf("0x%02X\t%s\n", i, hfCode[i]);
        }
    }
}
