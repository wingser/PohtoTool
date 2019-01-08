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

	private int iSimilarPercent = 80;// ͼƬ���Ʊ���
	
	private Map<String, int[]> fileFingerMap = new ConcurrentHashMap<String, int[]>();// ͼƬ����ָ��
	
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
		// �ж����ļ�����·����
		File f = new File(sFilePath);
		List<File> lstFile = new ArrayList<File>();

		// �ļ�����
		if (f.exists()) {
			if (f.isDirectory()) {
				// �����Ŀ¼����ȡ�б�
				if (bCheckSubFolder) {
					// �ݹ����ļ���
					FileUtil.traverseFolder(f, lstFile);
				} else {
					// ��ǰ�ļ���
					File[] allFile = f.listFiles();
					for (File file : allFile) {
						lstFile.add(file);
					}
				}
			} else if (f.isFile()) {
				// ������ļ���ֱ����װ�б��ء�
				lstFile.add(f);
			}
		}
		return lstFile;
	}

	/**
	 * ��ȡ����Ƭʱ��
	 * @param oldFile
	 * @param iShiftingTime
	 * @param sdf
	 * @return
	 */
	public String getNewName(File oldFile, int iShiftingTime, SimpleDateFormat sdf) {

		String newName = "";
		Date date = null;
		// ��ȡ����ʱ��
		try {
			Metadata metadata = null;
			if (oldFile.getName().toLowerCase().contains("mp4")) {
				// �����MP4��Ƶ
				// ���ļ������������޸�ʱ��������Ӵ���ʱ�������������������ʱ�䣬�����쳣�����߼���
				// metadata = Mp4MetadataReader.readMetadata(oldFile);
				date = getGuessTime(oldFile);
			} else {
				// ͼƬ
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
			
			//����ʱ��ƫ��
			date = new Date(date.getTime()+iShiftingTime);
							
			newName = sdf.format(date);
			newName += oldFile.getName().substring(oldFile.getName().indexOf("."));

		} catch (Exception e) {
			System.err.println(e);
			// �޷���������ʱ�䣬mp4��������û������ʱ�����ԣ�ͨ���ļ���,�޸ļ�����ʱ��²�
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
		 * 1. �ļ���ʱ�� ֧���������ڸ�ʽ VID_20170909_134921.mp4 IMG_20171008_122021.jpg
		 * 1498410520375.mp4 mmexport1488815760605.jpg ΢��ͼƬ_201710111235022.jpg
		 */
		Date fileNameTime = null;
		if (oldFile.getName().length() == 23
				&& (oldFile.getName().startsWith("VID") || oldFile.getName().startsWith("VID"))) {
			// VID_20170909_134921.mp4
			// IMG_20171008_122021.jpg
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_kkmmss");
			fileNameTime = dateFormat.parse(oldFile.getName().substring(4));
		} else if (oldFile.getName().startsWith("΢��ͼƬ_") && oldFile.getName().length() >= 5) {
			// ΢��ͼƬ_201710111235022.jpg
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddkkmmss");
			fileNameTime = dateFormat.parse(oldFile.getName().substring(5));
		} else {
			// ΢���ļ�������
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
		 * 2. ��ȡ�ļ�����ʱ�估�ļ��޸�ʱ�䡣
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
			throw new Exception("Դ�ļ������ڣ�");
		}

		// �ļ����Ѿ����ϱ�׼�������޸��ˡ�
		if (file.getAbsolutePath().equalsIgnoreCase(newFilePath)) {
			return newFile.getName();
		}
		if (newFile.getName().contains(".")
				&& file.getName().startsWith(newFile.getName().substring(0, newFile.getName().lastIndexOf(".")))) {
			// 20170101_121212(1).jpg�ǺϷ�����
			return file.getName();
		}

		// �����ļ��Զ��ۼ�������
		int i = 0;
		while (newFile.exists() && i++ < 100) {
			if (newFile.getName().contains(".")) {
				String newName = newFile.getName().substring(0, newFile.getName().lastIndexOf("."))
						.replace("(" + (i - 1) + ")", "") // ��ȡ�ļ�����������չ����ȥ��֮��ɣ�n��
						+ "(" + i + ")" + newFile.getName().substring(newFile.getName().lastIndexOf("."));
				newFile = new File(newFile.getAbsolutePath().replace(newFile.getName(), newName));
				;
			}
		}

		if (!newFile.exists()) {
			// ִ�и���
			file.renameTo(newFile);
		} else {
			// �ۼ���10���ļ��󣬻�������
			throw new Exception("ͬ���ļ�̫�ࣨ����100������" + newFile.getName());
		}
		return newFile.getName();
	}

	/**
	 * ���ͼƬָ��
	 * @param f
	 * @throws IOException
	 */
	public void addFileFinger(File f) throws IOException {
		fileFingerMap.put(f.getAbsolutePath(), ImgSimilarity.getImgFinger(f));
	}
	
	/**
	 * ���ͼƬָ��
	 * @param f
	 * @throws IOException
	 */
	public void clearFinger() {
		fileFingerMap.clear();
	}
	
	/**
	 * Ѱ���ظ����ļ���
	 * 
	 * @param absolutePath
	 * @param progressBar 
	 * @return
	 */
	public List<SimilarFileBean> getDuplicatePic(List<File> lstFile) {

		// ��ȡ�ļ��б�
		lstSFB.clear();
		// �������Ʊ���
		int iSimilar = 0;
		for (int i = 0; i < lstFile.size(); i++) {
			for (int j = i + 1; j < lstFile.size(); j++) {
				try {
					// �Ѿ���ͼƬָ��
					if (fileFingerMap.containsKey(lstFile.get(i).getAbsolutePath())
							&& fileFingerMap.containsKey(lstFile.get(j).getAbsolutePath())) {
						// ��ͼƬ��������ƶ�
						iSimilar = ImgSimilarity.getSimilarity(fileFingerMap.get(lstFile.get(i).getAbsolutePath()),
								fileFingerMap.get(lstFile.get(j).getAbsolutePath()));
						if (iSimilar >= iSimilarPercent) {
							// �ҵ�����ͼƬ����������һ�����ơ�����ѭ��
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
	 * ɾ���������ļ�
	 * @param sFile
	 */
	public void delPicAndBackup(String sFile) {
		File f = new File(sFile);
		
		//���Ҹ�Ŀ¼
		File tmpF = f.getParentFile() ;	
		while (tmpF.getParent() != null) {
			System.out.println(f.getAbsolutePath());
			tmpF = tmpF.getParentFile();
		}
		
		//�ļ����� ����Ŀ¼
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String date = sdf.format(new Date());
		String path = tmpF.getAbsolutePath()+ CONST.PicBackupPath +File.separator+date + File.separator + f.getName();
		File rennameFile = new File(path);
		if (!rennameFile.getParentFile().exists()) {
			rennameFile.getParentFile().mkdirs();
		}
		
		//�ļ��Ѿ����ڣ��޸�Ҫ���������ļ���(ǰ��Ӳ���ʱ��)
		if (rennameFile.exists()) {
			rennameFile = new File(System.currentTimeMillis() + "." + rennameFile.getAbsolutePath());
		}
		
		//�ļ�Ǩ��
		f.renameTo(rennameFile);
		
		// �ļ����Ա����ļ���ԭĿ¼
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
	 * ͼƬѹ��
	 * @param src
	 * @param tag
	 * @param size ����ļ���С����
	 * @throws IOException
	 */
	public void doPicZipping(File src, File tag, int size) throws IOException {
		float f = 0.5f;//ѹ��Ч��0.5
		int tagSize = size;
		while (f>0.1) {
			Thumbnails.of(src).scale(1).outputQuality(f).toFile(tag);
			if (tag.length() < tagSize) {
				System.out.println("zipping OK " + tag.getAbsolutePath() + " compress as:" + f);
				break;
			}else {
				f -= 0.2f; //û�ﵽ��С������ѹ��Ч��������ѹ����
				src = tag;	//���ѹ���õ�Ŀ���ļ����ٴ�ѹ����Ч�����á�
			}
		}
	}
}
