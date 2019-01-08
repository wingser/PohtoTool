package com.wingser.UI;

import javax.swing.table.AbstractTableModel;

// ��Ҫ��ʾ�ڱ���е����ݴ����ַ��������Object������
class PhotoTableModel extends AbstractTableModel {
	
	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	// ����е�һ����Ҫ��ʾ�����ݴ�����ַ�������columnNames��
	private String[] columnNames;
	
	// ����и��е����ݱ����ڶ�ά����data��
	Object[][] data;

	public PhotoTableModel(String[] columnNames, Object[][] data) {
		this.columnNames = columnNames;
		this.data = data;
	}
	public String[] getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	// ������������дAbstractTableModel�еķ���������Ҫ��;�Ǳ�JTable������ã��Ա��ڱ������ȷ����ʾ����������Ա������ݲ��õ��������ͼ���ǡ��ʵ�֡�
	// ����е���Ŀ
	public int getColumnCount() {
		return columnNames.length;
	}

	// ����е���Ŀ
	public int getRowCount() {
		return data.length;
	}

	// ���ĳ�е����֣���Ŀǰ���е����ֱ������ַ�������columnNames��
	public String getColumnName(int col) {
		return columnNames[col];
	}

	// ���ĳ��ĳ�е����ݣ������ݱ����ڶ�������data��
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	// �ж�ÿ����Ԫ�������
	// public Class getColumnClass(int c) {
	// return getValueAt(0, c).getClass();
	// }

	// ���������Ϊ�ɱ༭��
	public boolean isCellEditable(int row, int col) {
		// 4�ж������Ա༭��
		if (col < 4) {
			return false;
		} else {
			return true;
		}
	}

	// �ı�ĳ�����ݵ�ֵ
	public void setValueAt(String value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}
	
	public void setData(Object[][] newData) {
		data = newData;
		fireTableDataChanged();
	}
	
	//ɾ����
    public void removeRow(int row) {
    	//table���գ��б겻��
    	if (getRowCount()>0 && row < getRowCount()) {
    		Object[][] tmpData = new Object[data.length-1][columnNames.length];
    		for (int i = 0,j=0; i < data.length; i++) {
    			if (i != row) {
    				tmpData[j] = data[i];
    				j++;
    			}
    		}
    		setData(tmpData);
    		fireTableRowsDeleted(row, row);
		}
    }
}