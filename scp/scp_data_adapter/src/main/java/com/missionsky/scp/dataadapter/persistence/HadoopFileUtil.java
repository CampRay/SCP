package com.missionsky.scp.dataadapter.persistence;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HadoopFileUtil {
	private static final Logger logger = LoggerFactory.getLogger(HadoopFileUtil.class);
	
	private HadoopFileUtil(){}
	
	/**
	 * 创建HDFS文件
	 * @param fileName 创建文件的文件名
	 * @param buff 写入的字节数据
	 */
	public static void createFile(String fileName,byte[] buff){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path dfs = new Path(fileName);
			FSDataOutputStream outputStream = hdfs.create(dfs);
			outputStream.write(buff,0,buff.length);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	/**
	 * 上传本地文件
	 * @param srcFile 本地文件路径
	 * @param dstFile 上传后文件路径
	 */
	public static void uploadFile(String srcFile,String dstFile){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path src = new Path(srcFile);
			Path dst = new Path(dstFile);
			hdfs.copyFromLocalFile(src, dst);
			FileStatus status[] = hdfs.listStatus(dst);
			for(FileStatus statu:status){
				System.out.println(statu.getPath());
			}
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	/**
	 * 下载服务器文件
	 * @param srcFile 本地文件路径
	 * @param dstFile 服务端文件路径
	 */
	public static void downFile(String srcFile,String dstFile){
		Configuration conf = new Configuration();
		try {
			FileSystem fs = FileSystem.get(URI.create(dstFile), conf);
			FSDataInputStream in = fs.open(new Path(dstFile));
			OutputStream out = new FileOutputStream(srcFile);
			IOUtils.copyBytes(in, out, 1024, true);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	/**
	 * 创建HDFS目录
	 * @param dir 完整目录路径
	 */
	public static void createDir(String dir) {
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path dfs = new Path("/" + dir);
			hdfs.mkdirs(dfs);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	/**
	 * 重命名HDFS文件
	 * @param oldFile 旧的文件名
	 * @param newFile 新的文件名
	 */
	public static boolean rename(String oldFile,String newFile){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path frpath = new Path(oldFile);
			Path topath = new Path(newFile);
			return hdfs.rename(frpath, topath);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
		return false;
	}
	
	/**
	 * 删除HDFS上的文件
	 * @param fileName 需要删除的文件
	 */
	public static boolean deleteFile(String fileName){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path delef = new Path(fileName);
			return hdfs.delete(delef, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return false;
	}
	
	/**
	 * 查看某个HDFS文件是否存在
	 * @param fileName 检测文件的路径
	 */
	public static boolean checkFile(String fileName){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path findf = new Path(fileName);
			return hdfs.exists(findf);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return false;
	}
	
	/**
	 * 查看HDFS文件的最后修改时间
	 * @param fileName 查看的文件路径
	 * @return
	 */
	public static long getModifyTime(String fileName){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path fpath = new Path(fileName);
			FileStatus fileStatus = hdfs.getFileStatus(fpath);
			return fileStatus.getModificationTime();
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
		return 0;
	}
	
	/**
	 * 读取HDFS某个目录下的所有文件并返回最小的文件
	 * @param dir
	 */
	public static Map<String, String> listAllFile(String dir){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path listf = new Path("/"+dir);
			if(hdfs.exists(listf)){
				FileStatus stats[] = hdfs.listStatus(listf);
				int j = 0;
				for(int i=0;i<stats.length;i++){
					System.out.println(stats[i].getPath().toString());
					if(stats[i].getLen()<stats[j].getLen()){
						j = i;
					}
				}
				hdfs.close();
				if(stats.length>0){
					Map<String, String> map = new HashMap<String, String>();
					map.put("path", stats[j].getPath().toString());
					map.put("size", String.valueOf(stats[j].getLen()));
					return map;
				}
			}else {
				return null;
			}
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * 查找某个文件在HDFS集群的位置
	 * @param fileName
	 */
	public static void getFileLoc(String fileName){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path fpath = new Path(fileName);
			FileStatus status = hdfs.getFileStatus(fpath);
			BlockLocation[] blkLocations = hdfs.getFileBlockLocations(status, 0, status.getLen());
			for(int i=0;i<blkLocations.length;i++){
				String[] hosts = blkLocations[i].getHosts();
				System.out.println("block_"+i+"_location:"+hosts[0]); 
			}
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	/**
	 * 获取HDFS集群上所有节点名称信息
	 */
	public void getList(){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			DistributedFileSystem df = (DistributedFileSystem) hdfs;
			DatanodeInfo[] dataNodeStats = df.getDataNodeStats();
			for (int i = 0; i < dataNodeStats.length; i++) {
				logger.info("DataNode_{}_Name:{}",i,dataNodeStats[i].getHostName());
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	/**
	 * 追加数据
	 * @param localFile
	 * @param hdfsPath
	 */
	public static void appendFile(String localFile,String hdfsPath){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			InputStream in = new FileInputStream(localFile);
			OutputStream out = hdfs.append(new Path(hdfsPath));
			IOUtils.copyBytes(in, out, conf);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void appendFile(byte[] bytes,String hdfsPath){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(URI.create(hdfsPath),conf);
			FSDataOutputStream out = hdfs.append(new Path(hdfsPath));
			out.write(bytes);
			out.flush();
			out.close();
			hdfs.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
