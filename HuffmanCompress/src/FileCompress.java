import java.io.*;

public class FileCompress {
    //文件头类
    class FileHead{
        String type;   //文件类型
        int padding;  //填充位数
    }
    FileHead fileHead = new FileHead();
    //初始化文件头
    void initHead(File file){
        String fileName = file.getName();
        fileHead.type = fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    //生成压缩文件
    FileCompress(File file, String foldePath, String[] hfCode) {
        initHead(file);
        StringBuilder content = new StringBuilder();
        //System.out.println("compress start");
//        try(FileInputStream fis = new FileInputStream(file);
//            DataOutputStream dataOs = new DataOutputStream(new FileOutputStream(foldePath + "\\compress.huf"));) {
//            int b;
//            while ((b = fis.read()) != -1) {
//                content.append(hfCode[b]);
//            }
//
//            fileHead.padding = 8 - content.length() % 8;
//
//            content.append("0".repeat(fileHead.padding));
//            // start to write
//            dataOs.writeUTF(fileHead.type);
//            dataOs.writeInt(fileHead.padding);
//
//            for(int i = 0; i < 256; i++) {
//                dataOs.writeUTF(hfCode[i]);
//            }
//
//            for(int i = 0; i < content.length(); i += 8) {
//                String str = content.substring(i, i + 8);
//                dataOs.writeByte(Integer.parseInt(str, 2));
//            }
        try (FileInputStream fis = new FileInputStream(file);
             DataOutputStream dataOs = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(foldePath + "\\compress.huf")));) {
            int b;
            while ((b = fis.read()) != -1) {
                content.append(hfCode[b]);
            }
            //填充
            fileHead.padding = 8 - content.length() % 8;
            content.append("0".repeat(fileHead.padding));
            //写入文件头
            dataOs.writeUTF(fileHead.type);
            dataOs.writeInt(fileHead.padding);
            //写入哈夫曼编码
            for (int i = 0; i < 256; i++) {
                dataOs.writeUTF(hfCode[i]);
            }
            //写字节内容
            for (int i = 0; i < content.length(); i += 8) {
                String str = content.substring(i, Math.min(i + 8, content.length()));
                dataOs.writeByte(Integer.parseInt(str, 2));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("compress finish");
    }
}
