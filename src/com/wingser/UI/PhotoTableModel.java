package com.wingser.UI;

import javax.swing.table.AbstractTableModel;

// 把要显示在表格中的数据存入字符串数组和Object数组中
class PhotoTableModel extends AbstractTableModel {
	
	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	// 表格中第一行所要显示的内容存放在字符串数组columnNames中
	private String[] columnNames;
	
	// 表格中各行的内容保存在二维数组data中
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

	// 下述方法是重写AbstractTableModel中的方法，其主要用途是被JTable对象调用，以便在表格中正确的显示出来。程序员必须根据采用的数据类型加以恰当实现。
	// 获得列的数目
	public int getColumnCount() {
		return columnNames.length;
	}

	// 获得行的数目
	public int getRowCount() {
		return data.length;
	}

	// 获得某列的名字，而目前各列的名字保存在字符串数组columnNames中
	public String getColumnName(int col) {
		return columnNames[col];
	}

	// 获得某行某列的数据，而数据保存在对象数组data中
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	// 判断每个单元格的类型
	// public Class getColumnClass(int c) {
	// return getValueAt(0, c).getClass();
	// }

	// 将表格声明为可编辑的
	public boolean isCellEditable(int row, int col) {
		// 4列都不可以编辑。
		if (col < 4) {
			return false;
		} else {
			return true;
		}
	}

	// 改变某个数据的值
	public void setValueAt(String value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}
	
	public void setData(Object[][] newData) {
		data = newData;
		fireTableDataChanged();
	}
	
	//删除行
    public void removeRow(int row) {
    	//table不空，行标不超
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