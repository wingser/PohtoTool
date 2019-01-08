package com.wingser.exec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import net.coobird.thumbnailator.Thumbnails;

public class FileMgr {

	private static FileMgr instance;

	private boolean bWechatPic = true;

	private boolean bWechatMv = true;

	private int iSimilarPercent = 80;// 图片相似比例
	
	private Map<String, int[]> fileFingerMap = new ConcurrentHashMap<String, int[]>();// 图片特征指纹
	
	private List<SimilarFileBean> lstSFB = new ArrayList<SimilarFileBean>();

	public List<SimilarFileBean> getLstSFB() {
		return lstSFB;
	}

	public void setLstSFB(List<SimilarFileBean> lstSFB) {
		this.lstSFB = lstSFB;
	}

	public int getSimilarPercent() {
		return iSimilarPercent;
	}

	public void setSimilarPercent(int iSimilarPercent) {
		this.iSimilarPercent = iSimilarPercent;
	}

	public boolean isbWechatPic() {
		return bWechatPic;
	}

	public void setbWechatPic(boolean bWechatPic) {
		this.bWechatPic = bWechatPic;
	}

	public boolean isbWechatMv() {
		return bWechatMv;
	}

	public void setbWechatMv(boolean bWechatMv) {
		this.bWechatMv = bWechatMv;
	}

	private FileMgr() {

	}

	public static FileMgr getInstance() {
		if (instance == null) {
			instance = new FileMgr();
		}
		return instance;
	}

	public List<File> getFileList(String sFilePath, boolean bCheckSubFolder) {
		// 判断是文件还是路径。
		File f = new File(sFilePath);
		List<File> lstFile = new ArrayList<File>();

		// 文件存在
		if (f.exists()) {
			if (f.isDirectory()) {
				// 如果是目录，获取列表
				if (bCheckSubFolder) {
					// 递归找文件。
					FileUtil.traverseFolder(f, lstFile);
				} else {
					// 当前文件夹
					File[] allFile = f.listFiles();
					for (File file : allFile) {
						lstFile.add(file);
					}
				}
			} else if (f.isFile()) {
				// 如果是文件，直接填装列表返回。
				lstFile.add(f);
			}
		}
		return lstFile;
	}

	/**
	 * 获取新照片时间
	 * @param oldFile
	 * @param iShiftingTime
	 * @param sdf
	 * @return
	 */
	public String getNewName(File oldFile, int iShiftingTime, SimpleDateFormat sdf) {

		String newName = "";
		Date date = null;
		// 读取拍摄时间
		try {
			Metadata metadata = null;
			if (oldFile.getName().toLowerCase().contains("mp4")) {
				// 如果是MP4视频
				// 从文件名解析。从修改时间解析，从创建时间解析。解析不到拍摄时间，会走异常处理逻辑。
				// metadata = Mp4MetadataReader.readMetadata(oldFile);
				date = getGuessTime(oldFile);
			} else {
				// 图片
				metadata = ImageMetadataReader.readMetadata(oldFile);
				ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
				if (directory == null) {
					date = getGuessTime(oldFile);
				} else {
					date = directory.getDateOriginal(TimeZone.getDefault());
				}
				if (date == null) {
					date = getGuessTime(oldFile);
				}
			}
			
			//处理时间偏移
			date = new Date(date.getTime()+iShiftingTime);
							
			newName = sdf.format(date);
			newName += oldFile.getName().substring(oldFile.getName().indexOf("."));

		} catch (Exception e) {
			System.err.println(e);
			// 无法解析拍摄时间，mp4，或者是没有拍摄时间属性，通过文件名,修改及创建时间猜测
			try {
				date = getGuessTime(oldFile);
				newName = sdf.format(date);
				newName += oldFile.getName().substring(oldFile.getName().indexOf("."));
				System.err.println(oldFile.getName() + "  to  " + newName);
			} catch (Exception e1) {
			}
		}
		return newName;
	}

	private Date getGuessTime(File oldFile) throws Exception {

		Date date = null;
		/*
		 * 1. 文件名时间 支持如下日期格式 VID_20170909_134921.mp4 IMG_20171008_122021.jpg
		 * 1498410520375.mp4 mmexport1488815760605.jpg 微信图片_201710111235022.jpg
		 */
		Date fileNameTime = null;
		if (oldFile.getName().length() == 23
				&& (oldFile.getName().startsWith("VID") || oldFile.getName().startsWith("VID"))) {
			// VID_20170909_134921.mp4
			// IMG_20171008_122021.jpg
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_kkmmss");
			fileNameTime = dateFormat.parse(oldFile.getName().substring(4));
		} else if (oldFile.getName().startsWith("微信图片_") && oldFile.getName().length() >= 5) {
			// 微信图片_201710111235022.jpg
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddkkmmss");
			fileNameTime = dateFormat.parse(oldFile.getName().substring(5));
		} else {
			// 微信文件名处理
			String fileNo = oldFile.getName().toLowerCase().replaceAll(".mp4", "").replaceAll("[^0-9]", "");
			if (fileNo.length() >= 13) {
				// 1498410520375.mp4
				// mmexport1488815760605.jpg
				Long ltime = Long.parseLong(fileNo.substring(0, 13));
				fileNameTime = new Date(ltime);
			}
		}
		date = fileNameTime;

		/*
		 * 2. 获取文件创建时间及文件修改时间。
		 */
		Date modTime = new Date(oldFile.lastModified());
		Date createTime = new Date(0L);

		Path path = Paths.get(oldFile.getAbsolutePath());
		BasicFileAttributeView basicview = Files.getFileAttributeView(path, BasicFileAttributeView.class,
				LinkOption.NOFOLLOW_LINKS);
		BasicFileAttributes attr;
		attr = basicview.readAttributes();
		createTime = new Date(attr.creationTime().toMillis());

		if (date.after(modTime)) {
			date = modTime;
		}
		if (date.after(createTime)) {
			date = createTime;
		}
		return date;
	}



	public String renameFile(File file, String newFilePath) throws Exception {
		File newFile = new File(newFilePath);
		if (!file.exists()) {
			throw new Exception("源文件不存在！");
		}

		// 文件名已经符合标准，不用修改了。
		if (file.getAbsolutePath().equalsIgnoreCase(newFilePath)) {
			return newFile.getName();
		}
		if (newFile.getName().contains(".")
				&& file.getName().startsWith(newFile.getName().substring(0, newFile.getName().lastIndexOf(".")))) {
			// 20170101_121212(1).jpg是合法名称
			return file.getName();
		}

		// 重名文件自动累加重命名
		int i = 0;
		while (newFile.exists() && i++ < 100) {
			if (newFile.getName().contains(".")) {
				String newName = newFile.getName().substring(0, newFile.getName().lastIndexOf("."))
						.replace("(" + (i - 1) + ")", "") // 截取文件名，不带扩展名，去掉之间旧（n）
						+ "(" + i + ")" + newFile.getName().substring(newFile.getName().lastIndexOf("."));
				newFile = new File(newFile.getAbsolutePath().replace(newFile.getName(), newName));
				;
			}
		}

		if (!newFile.exists()) {
			// 执行改名
			file.renameTo(newFile);
		} else {
			// 累加了10个文件后，还是重名
			throw new Exception("同名文件太多（超过100个）：" + newFile.getName());
		}
		return newFile.getName();
	}

	/**
	 * 添加图片指纹
	 * @param f
	 * @throws IOException
	 */
	public void addFileFinger(File f) throws IOException {
		fileFingerMap.put(f.getAbsolutePath(), ImgSimilarity.getImgFinger(f));
	}
	
	/**
	 * 添加图片指纹
	 * @param f
	 * @throws IOException
	 */
	public void clearFinger() {
		fileFingerMap.clear();
	}
	
	/**
	 * 寻找重复的文件对
	 * 
	 * @param absolutePath
	 * @param progressBar 
	 * @return
	 */
	public List<SimilarFileBean> getDuplicatePic(List<File> lstFile) {

		// 获取文件列表
		lstSFB.clear();
		// 计算相似比例
		int iSimilar = 0;
		for (int i = 0; i < lstFile.size(); i++) {
			for (int j = i + 1; j < lstFile.size(); j++) {
				try {
					// 已经有图片指纹
					if (fileFingerMap.containsKey(lstFile.get(i).getAbsolutePath())
							&& fileFingerMap.containsKey(lstFile.get(j).getAbsolutePath())) {
						// 是图片，检查相似度
						iSimilar = ImgSimilarity.getSimilarity(fileFingerMap.get(lstFile.get(i).getAbsolutePath()),
								fileFingerMap.get(lstFile.get(j).getAbsolutePath()));
						if (iSimilar >= iSimilarPercent) {
							// 找到相似图片，继续找下一张相似。继续循环
							SimilarFileBean tmp = new SimilarFileBean();
							tmp.setFileSrc(lstFile.get(i));
							tmp.setFileSimilar(lstFile.get(j));
							tmp.setiSimilarPercentage(iSimilar);
							lstSFB.add(tmp);
						}
					}
				} catch (Exception e) {
					System.out.println(
							"compare file failed!" + lstFile.get(i).getName() + ":" + lstFile.get(j).getName());
				}
			}
		}
		return lstSFB;
	}



	/**
	 * 删除并备份文件
	 * @param sFile
	 */
	public void delPicAndBackup(String sFile) {
		File f = new File(sFile);
		
		//查找根目录
		File tmpF = f.getParentFile() ;	
		while (tmpF.getParent() != null) {
			System.out.println(f.getAbsolutePath());
			tmpF = tmpF.getParentFile();
		}
		
		//文件存在 创建目录
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String date = sdf.format(new Date());
		String path = tmpF.getAbsolutePath()+ CONST.PicBackupPath +File.separator+date + File.separator + f.getName();
		File rennameFile = new File(path);
		if (!rennameFile.getParentFile().exists()) {
			rennameFile.getParentFile().mkdirs();
		}
		
		//文件已经存在，修改要重命名的文件名(前面加操作时间)
		if (rennameFile.exists()) {
			rennameFile = new File(System.currentTimeMillis() + "." + rennameFile.getAbsolutePath());
		}
		
		//文件迁移
		f.renameTo(rennameFile);
		
		// 文件属性备份文件的原目录
		try {
			if (rennameFile.exists()) {
				FileUtil.setFileAttribute(rennameFile, CONST.PicFrom, sFile);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 图片压缩
	 * @param src
	 * @param tag
	 * @param size 最大文件大小限制
	 * @throws IOException
	 */
	public void doPicZipping(File src, File tag, int size) throws IOException {
		float f = 0.5f;//压缩效果0.5
		int tagSize = size;
		while (f>0.1) {
			Thumbnails.of(src).scale(1).outputQuality(f).toFile(tag);
			if (tag.length() < tagSize) {
				System.out.println("zipping OK " + tag.getAbsolutePath() + " compress as:" + f);
				break;
			}else {
				f -= 0.2f; //没达到大小，降低压缩效果，继续压缩。
				src = tag;	//针对压缩好的目标文件，再次压缩，效果更好。
			}
		}
	}
}
