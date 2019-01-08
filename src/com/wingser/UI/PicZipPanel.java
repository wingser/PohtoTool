package com.wingser.UI;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.wingser.exec.FileMgr;
import com.wingser.exec.FileUtil;
import com.wingser.exec.PicThreadPool;

public class PicZipPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7198530902102859346L;
	private JTextField picToTxt;
	private JTextField maxFileSizeTxt;
	private JTextField prefixTXT;
	private JProgressBar progressBar;
	private FileMgr mgr = FileMgr.getInstance();
	
	public PicZipPanel() {
		setLayout(null);
		
		JLabel lbl_title = new JLabel("��Ƭ����ѹ��", SwingConstants.CENTER);
		lbl_title.setBounds(318, 10, 240, 33);
		lbl_title.setFont(new Font("΢���ź�", Font.PLAIN, 24));
		add(lbl_title);

		JLabel label = new JLabel("ѡ���ļ����ļ��У�");
		label.setBounds(10, 48, 125, 30);
		add(label);

		// file path show
		final JTextField picFromTxt = new JTextField();
		picFromTxt.setBounds(145, 50, 481, 28);
		picFromTxt.setColumns(10);
		picFromTxt.setEditable(false);
		add(picFromTxt);

		// click chose file button
		final JFileChooser jfcWingserPhotoRenamer = new JFileChooser();
		jfcWingserPhotoRenamer.setCurrentDirectory(new File("D://"));
		JButton button = new JButton("ѡ���ļ�");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jfcWingserPhotoRenamer.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);// �趨ֻ��ѡ���ļ���
				int state = jfcWingserPhotoRenamer.showOpenDialog(null);// �˾��Ǵ��ļ�ѡ��������Ĵ������
				if (state == 1) {
				} else {
					File f = jfcWingserPhotoRenamer.getSelectedFile();// fΪѡ�񵽵�Ŀ¼
					picFromTxt.setText(f.getAbsolutePath());
					picToTxt.setText(f.getAbsolutePath()+File.separator+"mini");
				}
			}
		});
		button.setBounds(636, 52, 93, 23);
		add(button);

		// sub folder include?
		final JCheckBox checkSubFolder = new JCheckBox("�������ļ���");
		checkSubFolder.setSelected(true);
		checkSubFolder.setBounds(749, 52, 103, 23);
		add(checkSubFolder);
		
		JLabel label_1 = new JLabel("ѹ��ͼƬ����ļ��У�");
		label_1.setBounds(10, 88, 125, 30);
		add(label_1);
		
		picToTxt = new JTextField();
		picToTxt.setEditable(false);
		picToTxt.setColumns(10);
		picToTxt.setBounds(145, 90, 481, 28);
		add(picToTxt);
		
		final JFileChooser jfc = new JFileChooser();
		jfc.setCurrentDirectory(new File("D://"));
		JButton button_1 = new JButton("ѡ���ļ���");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// �趨ֻ��ѡ���ļ���
				int state = jfc.showOpenDialog(null);// �˾��Ǵ��ļ�ѡ��������Ĵ������
				if (state == 1) {
				} else {
					File f = jfc.getSelectedFile();// fΪѡ�񵽵�Ŀ¼
					picToTxt.setText(f.getAbsolutePath());
				}
			}
		});
		button_1.setBounds(636, 92, 93, 23);
		add(button_1);
		
		JLabel label_2 = new JLabel("ѹ��ͼƬС�ڣ�");
		label_2.setBounds(102, 132, 108, 30);
		add(label_2);
		
		maxFileSizeTxt = new JTextField("500");
		maxFileSizeTxt.setColumns(10);
		maxFileSizeTxt.setBounds(188, 134, 32, 28);
		maxFileSizeTxt.setHorizontalAlignment(JTextField.RIGHT);
		add(maxFileSizeTxt);
		
		JLabel label_3 = new JLabel("KB");
		label_3.setBounds(222, 132, 108, 30);
		add(label_3);
		
		JLabel label_4 = new JLabel("�����ļ�ǰ׺��");
		label_4.setBounds(340, 132, 108, 30);
		add(label_4);
		
		prefixTXT = new JTextField("mini_");
		prefixTXT.setHorizontalAlignment(SwingConstants.CENTER);
		prefixTXT.setColumns(10);
		prefixTXT.setBounds(426, 134, 66, 28);
		add(prefixTXT);
		
		final JCheckBox coverSrcFile = new JCheckBox("ֱ�Ӹ���ԭͼ");
		coverSrcFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (coverSrcFile.isSelected()) {
					prefixTXT.setEditable(false);
				} else {
					prefixTXT.setEditable(true);
				}
			}
		});
		coverSrcFile.setSelected(false);
		coverSrcFile.setBounds(599, 136, 103, 23);
		coverSrcFile.setToolTipText("ֱ�Ӹ���ԭͼ���������ԭ�ļ��У��ļ����Ʊ��ֲ��䡣");
		add(coverSrcFile);
		
		JButton button_2 = new JButton("ѹ��ͼƬ");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// ѹ���ļ�.
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							picZiping(picFromTxt.getText(), picToTxt.getText(), coverSrcFile.isSelected(),
									checkSubFolder.isSelected());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
		button_2.setBounds(367, 172, 111, 48);
		add(button_2);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(10, 242, 863, 14);
		add(progressBar);

	}

	protected void picZiping(final String srcPath, final String tagPath, final boolean isCoverSrcFile, boolean bSubFolder) throws InterruptedException {

		//����Ŀ��ѹ���ļ���
		if (!isCoverSrcFile) {
			File tagFilePath = new File(tagPath);
			if(!tagFilePath.exists())
			{
				tagFilePath.mkdirs();
			}
		}
		
		final List<File> lstFilePic = mgr.getFileList(srcPath, bSubFolder);
		progressBar.setMinimum(1);
		progressBar.setMaximum(lstFilePic.size());
		final int iFileMaxLength = Integer.parseInt(maxFileSizeTxt.getText()) * 1024; //�ļ�����С����
		
		final PicThreadPool pool = new PicThreadPool();
		for (int i = 0; i < lstFilePic.size(); i++) {
			if (FileUtil.isPic(lstFilePic.get(i))) {
				final int index = i;
				// ���߳�����ͼƬѹ��
				pool.addTaskAndRun(new Runnable() {
					@Override
					public void run() {
						try {
							if (isCoverSrcFile) {
								//����ԭ�ļ����ļ���һ��
								mgr.doPicZipping(lstFilePic.get(index), lstFilePic.get(index), iFileMaxLength);
							}
							else
							{
								//�������ļ�
								String sNewFile = lstFilePic.get(index).getAbsolutePath().replace(srcPath, tagPath);
								File zipFile = new File(sNewFile);
								if (!zipFile.getParentFile().exists()) {
									//�ļ�·�������ڣ������ļ���
									zipFile.getParentFile().mkdirs();
								}
								mgr.doPicZipping(lstFilePic.get(index), zipFile, iFileMaxLength);
							}
							pool.getCnt().incrementAndGet();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
			else
			{
				pool.getCnt().incrementAndGet();
			}
		}
		
		// ���������ύ�ֹ꣬ͣ�����ύ�����½�����
		pool.getPool().shutdown();
		while (true) {
			if (pool.getPool().isTerminated()) {
				//ȫִ����
				//���ý�����
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						progressBar.setValue(lstFilePic.size());
					}
				});
				break;
			}
			//���ý�����
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					progressBar.setValue(pool.getCnt().get());
				}
			});
			Thread.sleep(300);
		}
	}
}
