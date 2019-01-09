package com.wingser.UI;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.wingser.exec.FileMgr;
import com.wingser.exec.FileUtil;
import com.wingser.exec.PicThreadPool;
import com.wingser.exec.SimilarFileBean;

public class PhotoToolUI {

	private JFrame frame;
	private JTextField renameTextField;
	private JTextField duplicateTextField;
	private FileMgr mgr = FileMgr.getInstance();
	private JCheckBox checkBox;
	private JRadioButton radioButton;
	private JRadioButton radioButton_1;
	private JRadioButton radioButton_2;
	private JLabel label_3;

	private JTable renameTable;	//�������ı��
	private PhotoTableModel renameTableModel; //�������������

	private JTabbedPane tabbedPane = new JTabbedPane();
	private JPanel renamePanel = new JPanel();
	private JPanel duplicatePhotoPanel = new JPanel();
	
	SimpleDateFormat formatter;
	List<File> lstFile = null ;//�����ļ�
	private JTextField textField;
	
	private JTable duplicateTable;	//�ظ�ͼƬ���
	private PhotoTableModel duplicateTableModel;//�ظ�ͼƬ�������model
	private JProgressBar progressBarDuplicate; //�ظ�ͼƬ����
	private JButton btnCheckDuplicate;
	private JPanel panel_src;
	private JLabel lbl_srcPic;
	private JPanel panel_similar;
	private JLabel lbl_similarPic;
	private JTextField textField_1;
	
	/**
	 * Create the application.
	 */
	public PhotoToolUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("������Ƭ����");

		frame.setBounds(100, 100, 900, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		renamePanel.setLayout(null);
		
		//��ʼ�����������
		initRenamePanel();
		tabbedPane.add(renamePanel, "��Ƭ������");

		
		//��ʼ���ظ��ļ������塣
		initDuplicatePhotoPanel();
		tabbedPane.add(duplicatePhotoPanel, "�ظ���Ƭ����");

		//����ͼƬѹ������
		tabbedPane.add(new PicZipPanel(), "��Ƭѹ��");
		
		frame.getContentPane().add(tabbedPane);

		//����ͼ�꣨���Ͻǣ�
		try {
			Image img = ImageIO.read(this.getClass().getResource("/pic/Zoey.JPG"));
			frame.setIconImage(img);
		} catch (Exception e2) {
		}
	}

	private void initDuplicatePhotoPanel(){
		duplicatePhotoPanel.setLayout(null);
		JLabel lblDuplicatePhotoTitle = new JLabel("�������ظ���Ƭ������", JLabel.CENTER);
		lblDuplicatePhotoTitle.setFont(new Font("΢���ź�", Font.PLAIN, 24));
		lblDuplicatePhotoTitle.setBounds(318, 10, 240, 33);
		duplicatePhotoPanel.add(lblDuplicatePhotoTitle);
		
		/*********************************************** ���Ʊ��� start  ****************************************/
		JLabel lblNewLabel = new JLabel("���Ʊ�����");
		lblNewLabel.setBounds(48, 45, 67, 30);
		duplicatePhotoPanel.add(lblNewLabel);
		
		textField = new JTextField();
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					//�޸����ưٷֱ�
					mgr.setSimilarPercent(Integer.parseInt(textField.getText()));
				} catch (Exception e) {
				}
			}
		});
		textField.setText("80");
		textField.setBounds(107, 50, 25, 21);
		textField.setHorizontalAlignment(JTextField.CENTER);
		textField.setColumns(10);
		duplicatePhotoPanel.add(textField);

		JLabel labelPercent = new JLabel("%");
		labelPercent.setBounds(135, 53, 12, 15);
		duplicatePhotoPanel.add(labelPercent);
		/*********************************************** ���Ʊ��� end  ****************************************/
		/*********************************************** ѡ���ļ��� start  ****************************************/
		JLabel label = new JLabel("ѡ���ļ��У�");
		label.setBounds(173, 45, 125, 30);
		duplicatePhotoPanel.add(label);
		
		final JFileChooser jfcDuplcatePhoto = new JFileChooser();
		jfcDuplcatePhoto.setCurrentDirectory(new File("D://"));

		// file path show
		duplicateTextField = new JTextField();
		duplicateTextField.setBounds(246, 47, 481, 28);
		duplicateTextField.setColumns(10);
		duplicateTextField.setEditable(false);
		duplicatePhotoPanel.add(duplicateTextField);

		// click chose file button
		JButton button = new JButton("ѡ���ļ�");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jfcDuplcatePhoto.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);// �趨ֻ��ѡ���ļ���
				int state = jfcDuplcatePhoto.showOpenDialog(null);// �˾��Ǵ��ļ�ѡ��������Ĵ������
				if (state == 1) {
				} else {
					File f = jfcDuplcatePhoto.getSelectedFile();// fΪѡ�񵽵�Ŀ¼
					duplicateTextField.setText(f.getAbsolutePath());
				}
			}
		});
		button.setBounds(737, 49, 93, 23);
		duplicatePhotoPanel.add(button);
		/*********************************************** ѡ���ļ��� start  ****************************************/
		
		btnCheckDuplicate = new JButton("�����ظ�ͼƬ");
		btnCheckDuplicate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnCheckDuplicate.setEnabled(false);
				// ����table����Ϣ
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							setDuplicateTabelData(duplicateTextField.getText());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
		btnCheckDuplicate.setBounds(381, 84, 125, 33);
		duplicatePhotoPanel.add(btnCheckDuplicate);
		
		//************************************ͼƬչʾ��************************************//
		panel_src = new JPanel();
		panel_src.setBounds(10, 119, 330, 330);
		duplicatePhotoPanel.add(panel_src);
		panel_src.setLayout(null);
		
		lbl_srcPic = new JLabel("ԭͼƬ");
		lbl_srcPic.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount() == 2)
				{
					//˫����ͼƬ
					try {
						Desktop.getDesktop().open(new File(lbl_srcPic.getToolTipText()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		lbl_srcPic.setBounds(0, 0, 330, 330);
		panel_src.add(lbl_srcPic);
		lbl_srcPic.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_srcPic.setBorder(BorderFactory.createLoweredBevelBorder());
		
		panel_similar = new JPanel();
		panel_similar.setBounds(539, 119, 330, 330);
		duplicatePhotoPanel.add(panel_similar);
		panel_similar.setLayout(null);
		
		lbl_similarPic = new JLabel("����ͼƬ");
		lbl_similarPic.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2)
				{
					//˫����ͼƬ
					try {
						Desktop.getDesktop().open(new File(lbl_similarPic.getToolTipText()));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		lbl_similarPic.setBounds(0, 0, 330, 330);
		panel_similar.add(lbl_similarPic);
		lbl_similarPic.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_similarPic.setBorder(BorderFactory.createLoweredBevelBorder());
		//************************************ͼƬչʾ��************************************//
		
		//************************************ͼƬɾ��************************************//
		JButton btn_del_src = new JButton();
		ImageIcon icon=new ImageIcon(JButton.class.getResource("/pic/del.jpg"));
		btn_del_src.setHorizontalTextPosition(JButton.CENTER);
		btn_del_src.setVerticalTextPosition(JButton.CENTER);
		btn_del_src.setBorderPainted(false);
		btn_del_src.setIcon(icon);
		btn_del_src.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//ԭɾ��ͼƬ
				mgr.delPicAndBackup(lbl_srcPic.getToolTipText());
				//���±���Զ�ѡ����һ�У�������һ�У����⴦��
				updateDuplicateTableAfterDel(lbl_srcPic.getToolTipText());
			}
		});
		btn_del_src.setBounds(350, 242, 50, 50);
		duplicatePhotoPanel.add(btn_del_src);
		
		JButton btn_del_simi = new JButton();
		btn_del_simi.setHorizontalTextPosition(JButton.CENTER);
		btn_del_simi.setVerticalTextPosition(JButton.CENTER);
		btn_del_simi.setBorderPainted(false);
		btn_del_simi.setIcon(icon);
		btn_del_simi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//ɾ������ͼƬ
				mgr.delPicAndBackup(lbl_similarPic.getToolTipText());
				//���±���Զ�ѡ����һ�У�������һ�У����⴦��
				updateDuplicateTableAfterDel(lbl_srcPic.getToolTipText());
			}
		});
		btn_del_simi.setBounds(479, 242, 50, 50);
		duplicatePhotoPanel.add(btn_del_simi);
		//************************************ͼƬɾ��************************************//
		
		
		//��������tablelist
		final String[] columnNames = {"Դ�ļ�", "Դ�ļ���С", "�����ļ�", "�����ļ���С", "���ưٷֱ�"};
		final Object[][] data = new String[16][5];
		duplicateTableModel = new PhotoTableModel(columnNames, data);// myModel��ű�������
		duplicateTable = new JTable(duplicateTableModel);
		duplicateTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				String filePath = duplicateTable.getValueAt(duplicateTable.rowAtPoint(e.getPoint()), 0).toString(); //Դ�ļ�·��
				String filePath2 = duplicateTable.getValueAt(duplicateTable.rowAtPoint(e.getPoint()), 2).toString(); //�����ļ�·��
				setPic(panel_src, lbl_srcPic, filePath);
				setPic(panel_similar, lbl_similarPic, filePath2);
			}
		});
//		DefaultTableCellRenderer r = new DefaultTableCellRenderer();
//		r.setHorizontalAlignment(JTextField.CENTER);
//		renameTable.getColumn("״̬").setCellRenderer(r);
		   
		// �����������
		JScrollPane scrollPane = new JScrollPane(duplicateTable);
		scrollPane.setBounds(10, 473, 859, 234);
		duplicatePhotoPanel.add(scrollPane, BorderLayout.CENTER);
		
		//������
		progressBarDuplicate = new JProgressBar();
		progressBarDuplicate.setBounds(10, 714, 859, 14);
		duplicatePhotoPanel.add(progressBarDuplicate);
	}
	
	/**
	 * ����ͼƬ��С�ŵ�lbl���档
	 * @param jPanel 
	 * @param lbl_srcPic
	 * @param filePath
	 */
	protected void setPic(JPanel jPanel, JLabel lbl_srcPic, String filePath) {
		ImageIcon ic = new ImageIcon(filePath);
		int jp_W = jPanel.getWidth();
		int jp_H = jPanel.getHeight();
		int pic_W = ic.getIconWidth();
		int pic_H = ic.getIconHeight();
		
		
		if (pic_W > pic_H) {
			//��ͼ
			int tmpH = jp_W*pic_H/pic_W;
			int startY = (jp_H-tmpH)/2;
			lbl_srcPic.setBounds(0, startY, jPanel.getWidth(), tmpH);
		}
		else
		{
			//��ͼ
			int tmpW = jp_H*pic_W/pic_H;
			int startX = (jp_W-tmpW)/2;
			lbl_srcPic.setBounds(startX, 0, tmpW, jPanel.getHeight());
		}
		Image temp=ic.getImage().getScaledInstance(lbl_srcPic.getWidth()+10,lbl_srcPic.getHeight(),Image.SCALE_DEFAULT);  
	    ic=new ImageIcon(temp);  
	    lbl_srcPic.setIcon(ic);
	    lbl_srcPic.setToolTipText(filePath);
	}

	/*
	 * �ظ��ļ���table��ʼ��
	 */
	private void setDuplicateTabelData(String absolutePath) throws InterruptedException {
		//��ȡ�ļ�ָ��
		mgr.clearFinger();
		final List<File> lstFilePic = mgr.getFileList(absolutePath, true);
		progressBarDuplicate.setMinimum(1);
		progressBarDuplicate.setMaximum(lstFilePic.size());
		
		final PicThreadPool pool = new PicThreadPool();
		for (int i = 0; i < lstFilePic.size(); i++) {
			final int barValue = i;
			if (FileUtil.isPic(lstFilePic.get(i))) {
				// ���̼߳���ͼƬָ��
				pool.addTaskAndRun(new Runnable() {
					@Override
					public void run() {
						try {
							mgr.addFileFinger(lstFilePic.get(barValue));
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
		
		// ֹͣ�����ύ�����½�����
		pool.getPool().shutdown();
		while (true) {
			if (pool.getPool().isTerminated()) {
				//ȫִ����
				//���ý�����
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						progressBarDuplicate.setValue(lstFilePic.size());
					}
				});
				break;
			}
			//���ý�����
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					System.out.println(pool.getCnt().get()+" files is load!");
					progressBarDuplicate.setValue(pool.getCnt().get());
				}
			});
			Thread.sleep(300);
		}
		
		//*********������ȡ�����ļ�
		List<SimilarFileBean> lstSFB = mgr.getDuplicatePic(lstFilePic);
		Object[][] data = new String[lstSFB.size()][5];
		int i=0;//�к�
		for (SimilarFileBean similarFileBean : lstSFB) {
			data[i][0] = similarFileBean.getFileSrc().getAbsolutePath();// Դ�ļ���
			data[i][1] = similarFileBean.getFileSrc().length()/1024 +"k";// Դ�ļ���С
			data[i][2] = similarFileBean.getFileSimilar().getAbsolutePath();// �����ļ���
			data[i][3] = similarFileBean.getFileSimilar().length()/1024 +"k";// �����ļ���С
			data[i][4] = String.valueOf(similarFileBean.getiSimilarPercentage());// �ļ����Ʊ���
			i++;
		}
		duplicateTableModel.setData(data);
		if (i>0) {
			//��������ݣ�Ĭ��ѡ�е�һ��
			selectDuplicateTableRow(0);
		}
		btnCheckDuplicate.setEnabled(true);
	}
	
	private void updateDuplicateTableAfterDel(String sFile){
		//���ظ�Map��ɾ�����ݡ����¹���talbe
		
		/******************* ��model�У�ȥ����Ƭ�Ѿ���ɾ�����У����±��ѡ����*******************/
		int selectedRow = duplicateTable.getSelectedRow();//֮ǰѡ���� 
		for (int i = 0; i < duplicateTable.getRowCount(); i++) {
			String filePath = duplicateTable.getValueAt(i, 0).toString(); //Դ�ļ�·��
			String filePath2 = duplicateTable.getValueAt(i, 2).toString(); //�����ļ�·��
			if (filePath.equalsIgnoreCase(sFile) || filePath2.equalsIgnoreCase(sFile)) {
				duplicateTableModel.removeRow(i);  //ɾ����  
				if (i < selectedRow) {
					//ɾ�����У���ѡ����֮ǰ���� ��ѡ�е������һ�� �� ѡ����ǰ��
					selectedRow--;	//ѡ����ǰ��
				}
				i--;//ɾ�����к󣬲�Ҫ������һ����ѭ�����м��©һ��
			}
		}
		duplicateTableModel.fireTableDataChanged();	//���±������
		selectDuplicateTableRow(selectedRow);
		
		//ɾ���ڴ����Ѿ������ڵ�ͼƬ���ݡ�
		List<SimilarFileBean> lst = mgr.getLstSFB();
		List<SimilarFileBean> lstDel = new ArrayList<SimilarFileBean>();
		for (SimilarFileBean similarFileBean : lst) {
			if (similarFileBean.getFileSrc().getAbsolutePath().equals(sFile)||similarFileBean.getFileSimilar().getAbsolutePath().equals(sFile)) {
				lstDel.add(similarFileBean);//��¼Ҫɾ��������
			}
		}
		lst.removeAll(lstDel);
		mgr.setLstSFB(lst);
	}

	private void selectDuplicateTableRow(int selectedRow) {
		if (selectedRow < duplicateTable.getRowCount() && selectedRow >= 0) {
			duplicateTable.setRowSelectionInterval(selectedRow, selectedRow); // ����ѡ����
		} else {
			// ѡ�����һ�У���������ݡ���
			if (duplicateTable.getRowCount() > 0) {
				selectedRow = duplicateTable.getRowCount() - 1;
				duplicateTable.setRowSelectionInterval(selectedRow, selectedRow); // ����ѡ����
			}
		}
		if (duplicateTable.getRowCount() > 0) {
			String filePath = duplicateTable.getValueAt(selectedRow, 0).toString(); // Դ�ļ�·��
			String filePath2 = duplicateTable.getValueAt(selectedRow, 2).toString(); // �����ļ�·��
			setPic(panel_src, lbl_srcPic, filePath); // ����ͼƬ����ͼ
			setPic(panel_similar, lbl_similarPic, filePath2); // ����ͼƬ����ͼ
			fitTableColumns(duplicateTable);
		}
	}

	private void initRenamePanel() {
		JLabel lblWingserPhotoRenamer = new JLabel("��������Ƭ������", JLabel.CENTER);
		lblWingserPhotoRenamer.setFont(new Font("΢���ź�", Font.PLAIN, 24));
		lblWingserPhotoRenamer.setBounds(119, 10, 590, 30);
		renamePanel.add(lblWingserPhotoRenamer);

		final JFileChooser jfcWingserPhotoRenamer = new JFileChooser();
		jfcWingserPhotoRenamer.setCurrentDirectory(new File("D://"));

		JLabel label = new JLabel("ѡ���ļ����ļ��У�");
		label.setBounds(10, 48, 125, 30);
		renamePanel.add(label);

		// file path show
		renameTextField = new JTextField();
		renameTextField.setBounds(145, 50, 481, 28);
		renameTextField.setColumns(10);
		renameTextField.setEditable(false);
		renamePanel.add(renameTextField);

		// click chose file button
		JButton button = new JButton("ѡ���ļ�");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jfcWingserPhotoRenamer.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);// �趨ֻ��ѡ���ļ���
				int state = jfcWingserPhotoRenamer.showOpenDialog(null);// �˾��Ǵ��ļ�ѡ��������Ĵ������
				if (state == 1) {
				} else {
					File f = jfcWingserPhotoRenamer.getSelectedFile();// fΪѡ�񵽵�Ŀ¼
					renameTextField.setText(f.getAbsolutePath());
					// �������ڸ�ʽ��
					setTimeFormater();
					// ����table����Ϣ
					setRenameTabelDate(f.getAbsolutePath());
				}
			}
		});
		button.setBounds(636, 52, 93, 23);
		renamePanel.add(button);

		// sub folder include?
		checkBox = new JCheckBox("�������ļ���");
		checkBox.setSelected(true);
		checkBox.setBounds(749, 52, 103, 23);
		renamePanel.add(checkBox);

		// name formater
		JLabel label_1 = new JLabel("�����ļ�����ʽ��");
		label_1.setBounds(10, 84, 131, 23);
		renamePanel.add(label_1);

		radioButton = new JRadioButton("20131126_120700");
		radioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setTimeFormater();
				setRenameTabelDate(renameTextField.getText());
			}
		});
		radioButton.setSelected(true);
		radioButton.setBounds(145, 84, 154, 23);
		renamePanel.add(radioButton);

		radioButton_1 = new JRadioButton("20131126 12-07-00");
		radioButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setTimeFormater();
				setRenameTabelDate(renameTextField.getText());
			}
		});
		radioButton_1.setBounds(301, 84, 139, 23);
		renamePanel.add(radioButton_1);

		radioButton_2 = new JRadioButton("2013-11-23 12-07-00");
		radioButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setTimeFormater();
				setRenameTabelDate(renameTextField.getText());
			}
		});
		radioButton_2.setBounds(477, 84, 149, 23);
		renamePanel.add(radioButton_2);

		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(radioButton);
		buttonGroup1.add(radioButton_1);
		buttonGroup1.add(radioButton_2);
		setTimeFormater();//��ʼ����ʽ��
		
		JLabel label_2 = new JLabel("���⴦�����ͣ�");
		label_2.setBounds(10, 117, 93, 23);
		renamePanel.add(label_2);

		final JCheckBox checkBox_1 = new JCheckBox("΢�ű�����Ƭʱ��");
		checkBox_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mgr.setbWechatPic(checkBox_1.isSelected());
			}
		});
		checkBox_1.setSelected(true);
		checkBox_1.setBounds(145, 117, 154, 23);
		renamePanel.add(checkBox_1);

		final JCheckBox checkBox_2 = new JCheckBox("΢�ű�����Ƶʱ��");
		checkBox_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mgr.setbWechatMv(checkBox_2.isSelected());
			}
		});
		checkBox_2.setSelected(true);
		checkBox_2.setBounds(301, 117, 139, 23);
		renamePanel.add(checkBox_2);

		JCheckBox checkBox_3 = new JCheckBox("����ԭ�ļ���");
		checkBox_3.setEnabled(false);
		checkBox_3.setBounds(477, 117, 149, 23);
		renamePanel.add(checkBox_3);

		
		JLabel label1 = new JLabel("ʱ��ƫ�ƣ��룩��");
		label1.setBounds(10, 152, 125, 23);
		renamePanel.add(label1);
		
		textField_1 = new JTextField("0");
		textField_1.setBounds(145, 152, 154, 22);
		renamePanel.add(textField_1);
		textField_1.setColumns(10);
		
		JButton btn_rename = new JButton("�޸��ļ���");
		btn_rename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				//check
				int iShiftingTime = 0;
				try {
					//���������ú���
					iShiftingTime = Integer.valueOf(textField_1.getText()) * 1000;
				} catch (Exception e) {
				}
				
				
				// rename file
				if (lstFile != null && !lstFile.isEmpty()) {
					//�����������Ҹ��±��
					Object[][] data = new String[lstFile.size()][4];
					for (int i = 0; i < lstFile.size(); i++) {
						File tmpFile = lstFile.get(i);
						String newFilePath  = tmpFile.getAbsolutePath().replace(tmpFile.getName(), mgr.getNewName(tmpFile, iShiftingTime, formatter));
						data[i][0] = tmpFile.getName();// �ļ���
						
						try {
							data[i][1] = mgr.renameFile(tmpFile, newFilePath);// ���ļ������������ļ�������
							data[i][2] = "OK";// ״̬
							data[i][3] = newFilePath;// ·��
						} catch (Exception e) {
							//������ʧ��
							data[i][1] = "";
							data[i][2] = "����";// ״̬
							data[i][3] = e.getMessage();
						}
					}
					//������ɣ����±��
					renameTableModel.setData(data);
					label_3.setText("�ѳɹ������ļ���" + lstFile.size() + "������");
					fitTableColumns(renameTable);
				}
			}
		});
		btn_rename.setBounds(372, 174, 111, 48);
		renamePanel.add(btn_rename);

		final String[] columnNames = { "�ļ���", "���ļ���", "״̬", "·��" };
		final Object[][] data = new String[30][4];
		renameTableModel = new PhotoTableModel(columnNames, data);// myModel��ű�������
		renameTable = new JTable(renameTableModel);
		renameTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2)
				{
					//˫��
					Object value = renameTable.getValueAt(renameTable.rowAtPoint(e.getPoint()), 3);
					if (value!=null && value.toString().length()>0) {
						String filePath = (String) renameTable.getValueAt(renameTable.rowAtPoint(e.getPoint()), 3);
						System.out.println(filePath);
						try {
							Desktop.getDesktop().open(new File(filePath));
						} catch (IOException e1) {
							e1.printStackTrace();
						}  
					}

				}
			}
		});
		DefaultTableCellRenderer r = new DefaultTableCellRenderer();
		r.setHorizontalAlignment(JTextField.CENTER);
		renameTable.getColumn("״̬").setCellRenderer(r);
		   
		// �����������
		JScrollPane scrollPane = new JScrollPane(renameTable);
		scrollPane.setBounds(10, 233, 859, 490);
		renamePanel.add(scrollPane, BorderLayout.CENTER);

		// �������������������봰����
		label_3 = new JLabel("");
		label_3.setBounds(636, 121, 221, 15);
		renamePanel.add(label_3);
	}
	
	private void fitTableColumns(JTable myTable) {
		JTableHeader header = myTable.getTableHeader();
		int rowCount = myTable.getRowCount();
		Enumeration<TableColumn> columns = myTable.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			TableColumn column = (TableColumn) columns.nextElement();
			int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
			int width = (int) myTable.getTableHeader().getDefaultRenderer()
					.getTableCellRendererComponent(myTable, column.getIdentifier(), false, false, -1, col)
					.getPreferredSize().getWidth();
			for (int row = 0; row < rowCount; row++) {
				int preferedWidth = (int) myTable.getCellRenderer(row, col)
						.getTableCellRendererComponent(myTable, myTable.getValueAt(row, col), false, false, row, col)
						.getPreferredSize().getWidth();
				width = Math.max(width, preferedWidth);
			}
			header.setResizingColumn(column); // ���к���Ҫ
			column.setWidth(width + myTable.getIntercellSpacing().width+10);
		}
	}
	
	// ���ø���table��������ݡ�
	private void setRenameTabelDate(String absolutePath) {
		
		int iShiftingTime = 0;
		try {
			//���������ú���
			iShiftingTime = Integer.valueOf(textField_1.getText()) * 1000;
		} catch (Exception e) {
		}
		
		lstFile = mgr.getFileList(absolutePath, checkBox.isSelected());
		label_3.setText("��Ҫ�����ļ���" + lstFile.size() + "����");
		Object[][] data = new String[lstFile.size()][4];
		
		for (int i = 0; i < lstFile.size(); i++) {
			File tmpFile = lstFile.get(i);
			data[i][0] = tmpFile.getName();// �ļ���
			data[i][1] = mgr.getNewName(tmpFile, iShiftingTime, formatter);// ���ļ���
			if (tmpFile.getName().equalsIgnoreCase(data[i][1].toString())) {
				//�ļ����Ѿ�OK
				data[i][2] = "OK";// ״̬
			} else {
				data[i][2] = "���޸�";// ״̬
			}
			data[i][3] = tmpFile.getPath();// ·��
		}
		renameTableModel.setData(data);
		label_3.setText("�ѳɹ������ļ���" + lstFile.size() + "����");
		fitTableColumns(renameTable);
	}

	/**
	 * ����ͼƬ�������ĸ�ʽ����ʽ
	 */
	private void setTimeFormater() {
		if (radioButton.isSelected()) {
			formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
		} else if (radioButton_1.isSelected()) {
			formatter = new SimpleDateFormat("yyyyMMdd HH-mm-ss");
		} else if (radioButton_2.isSelected()) {
			formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		}

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PhotoToolUI window = new PhotoToolUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
