package com.wingser.exec;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.List;

public class FileUtil {

	/**
	 * �����ļ�����
	 * @param f
	 * @param name
	 * @param value
	 * @throws IOException
	 */
	public static void setFileAttribute(File f, String name, String value) throws IOException {
		Path path = Paths.get(f.getAbsolutePath());
        UserDefinedFileAttributeView userDefinedFileAttributeView = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);  
        userDefinedFileAttributeView.write(name, Charset.defaultCharset().encode(value));  
	}
	
	/**
	 * ��ȡ�ļ�����
	 * @param f
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public static String getFileAttribute(File f, String name) throws IOException {
		Path path = Paths.get(f.getAbsolutePath());
	    UserDefinedFileAttributeView userDefinedFileAttributeView = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);  
	    
	    ByteBuffer bb = ByteBuffer.allocate(userDefinedFileAttributeView.size(name)); // ׼��һ����ڴ���ȡ  
	    userDefinedFileAttributeView.read(name, bb);  
	    bb.flip();
	    return Charset.defaultCharset().decode(bb).toString();
	}
	
	/**
	 * �ж��Ƿ���ͼƬ
	 * 
	 * @param f
	 * @return
	 */
	public static boolean isPic(File f) {

		String sFileName = f.getName().toLowerCase();
		if (sFileName.contains("jpg") || sFileName.contains("jpeg") || sFileName.contains("jpe")
				|| sFileName.contains("tif") || sFileName.contains("png") || sFileName.contains("bmp")
				|| sFileName.contains("psd") || sFileName.contains("gif") || sFileName.contains("ico")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * ��ȡ���ļ����ļ��б�
	 * @param file
	 * @param lstFile
	 */
	public static void traverseFolder(File file, List<File> lstFile) {

		if (file.exists()) {
			File[] files = file.listFiles();
			if (files.length == 0) {
				return;
			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						traverseFolder(file2, lstFile);
					} else if (file2.isFile()) {
						lstFile.add(file2);
					}
				}
			}
		}
	}
}
