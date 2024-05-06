import java.io.*;
import java.util.HashMap;

public class FileDecompress {
    //解压文件
    static void decompress(String folderPath) {
//        try {
//            DataInputStream dataIs = new DataInputStream(new FileInputStream(folderPath + "\\compress.huf"));
//            String type = dataIs.readUTF();
//            int padding = dataIs.readInt();
//            HashMap<String, Integer> hfCodeMap = new HashMap<>();
//            for (int i = 0; i < 256; i++) {
//                String str = dataIs.readUTF();
//                hfCodeMap.put(str, i);
//            }
//            int byteRead;
//            StringBuilder content = new StringBuilder();
//            while ((byteRead = dataIs.read()) != -1) {
//                String str = Integer.toBinaryString(byteRead );
//                int len = str.length();
//                if (len < 8) {
//                    str = "0".repeat(8 - len) + str;
//                }
//                content.append(str);
//            }
//
//            if (padding != 0) {
//                content.setLength(content.length() - padding);
//            }
//
//            FileOutputStream fos = new FileOutputStream(folderPath + "/decompress." + type);
//            StringBuilder str = new StringBuilder();
//            for (int i = 0; i < content.length(); i++) {
//                str.append(content.charAt(i));
//                if (hfCodeMap.containsKey(str.toString())) {
//                    fos.write(hfCodeMap.get(str.toString()));
//                    str.setLength(0);
//                }
//            }
//            dataIs.close();
//            fos.close();
        System.out.println("decompress start");
        try {
            DataInputStream dataIs = new DataInputStream(new BufferedInputStream(new FileInputStream(folderPath + "\\compress.huf")));
            String type = dataIs.readUTF();
            FileOutputStream fos = new FileOutputStream(folderPath + "/decompress." + type);
            int padding = dataIs.readInt();
            HashMap<String, Integer> hfCodeMap = new HashMap<>();
            for (int i = 0; i < 256; i++) {
                String str = dataIs.readUTF();
                hfCodeMap.put(str, i);
            }

            StringBuilder content = new StringBuilder();
            int byteRead;
            while ((byteRead = dataIs.read()) != -1) {
                String str = Integer.toBinaryString(byteRead);
                int len = str.length();
                if (len < 8) {
                    str = "0".repeat(8 - len) + str;
                }
                content.append(str);
            }
            //截断填充序列
            if (padding != 0) {
                content.setLength(content.length() - padding);
            }
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < content.length(); i++) {
                str.append(content.charAt(i));
                //若存在相应的哈夫曼编码则写入文件
                if (hfCodeMap.containsKey(str.toString())) {
                    fos.write(hfCodeMap.get(str.toString()));
                    str.setLength(0);
                }
            }
            dataIs.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("decompress finish");
    }
}
