package tvfan.tv.lib;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * @version 1.00
 * @description: 文件处理util
 * @author tao
 */
public class FileUtils {
    private String SDPATH;

    public String getSDPATH() {
        return SDPATH;
    }
    public FileUtils() {
        //得到当前外部存储设备的目录
        // /SDCARD
        SDPATH = Environment.getExternalStorageDirectory() + "/";
    }
    
    public FileUtils(String path) {
        //得到当前外部存储设备的目录
        SDPATH = path +"/";
    }
    
    /**
     * 在SD卡上创建文件
     * 
     * @throws IOException
     */
    public File creatSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        file.createNewFile();
        return file;
    }
    
    /**
     * 在SD卡上创建目录
     * 
     * @param dirName
     */
    public File creatSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        dir.mkdirs();
        return dir;
    }

    
    /**
     * 创建目录
     * 
     * @param dirName
     */
    public static File creatDir(String dirName) {
        File dir = new File(dirName);
        dir.mkdirs();
        return dir;
    }
    
    
    /**
     * 判断SD卡上的文件夹是否存在
     */
    public static boolean isFileExist(String fileName){
        File file = new File(fileName);
        return file.exists();
    }
    
    
    /**
     * 判断包内的文件夹是否存在
     */
    public static boolean isFileDateExist(String fileName){
        File file = new File(fileName);
        return file.exists();
    }
    
    
    
    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    public File write2SDFromInput(String path,String fileName,InputStream input){
        File file = null;
        OutputStream output = null;
        try{
            creatSDDir(path);
            file = creatSDFile(path + fileName);
            output = new FileOutputStream(file);
            byte buffer [] = new byte[4 * 1024];
            while((input.read(buffer)) != -1){
                output.write(buffer);
            }
            output.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                output.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return file;
    }
    
    
    //删除指定文件夹下所有文件
    //param path 文件夹完整绝对路径
     public static boolean delAllFile(String path) {
         boolean flag = false;
         String allPath = Environment.getExternalStorageDirectory() + "/" + path;
         File file = new File(allPath);
         if (!file.exists()) {
           return flag;
         }
         if (!file.isDirectory()) {
           return flag;
         }
         String[] tempList = file.list();
         File temp = null;
         for (int i = 0; i < tempList.length; i++) {
            if (allPath.endsWith(File.separator)) {
               temp = new File(allPath + tempList[i]);
            } else {
                temp = new File(allPath + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
               temp.delete();
            }
            if (temp.isDirectory()) {
               delAllFile(allPath + "/" + tempList[i]);//先删除文件夹里面的文件
               flag = true;
            }
         }
         return flag;
       }
     
     
   //删除指定文件夹下所有文件
     //param path 文件夹完整绝对路径
      public static boolean delAllDateFile(String path) {
          boolean flag = false;
          String allPath = path;
          File file = new File(allPath);
          if (!file.exists()) {
            return flag;
          }
          if (!file.isDirectory()) {
        	file.delete();  
            return flag;
          }
          String[] tempList = file.list();
          if (tempList!=null){
              File temp = null;
              for (int i = 0; i < tempList.length; i++) {
                  if (allPath.endsWith(File.separator)) {
                      temp = new File(allPath + tempList[i]);
                  } else {
                      temp = new File(allPath + File.separator + tempList[i]);
                  }
                  if (temp.isFile()) {
                      temp.delete();
                  }
                  if (temp.isDirectory()) {
                      delAllDateFile(allPath + "/" + tempList[i]);//先删除文件夹里面的文件
                      flag = true;
                  }
              }
          }
          return flag;
        }
     
     
     
     
     	//删除文件夹
     	//param folderPath 文件夹完整绝对路径
        public static void delFolder(String folderPath) {
        try {
           delAllFile(folderPath); //删除完里面所有内容
           String filePath = folderPath;
           filePath = filePath.toString();
           //java.io.File myFilePath = new java.io.File(filePath);
           //myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
          e.printStackTrace(); 
        }
   }

}