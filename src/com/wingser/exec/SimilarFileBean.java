package com.wingser.exec;

import java.io.File;

public class SimilarFileBean {

	private File fileSrc;

	private File fileSimilar;
	
	private int iSimilarPercentage = 0;

	public File getFileSrc() {
		return fileSrc;
	}

	public void setFileSrc(File fileSrc) {
		this.fileSrc = fileSrc;
	}

	public File getFileSimilar() {
		return fileSimilar;
	}

	public void setFileSimilar(File fileSimilar) {
		this.fileSimilar = fileSimilar;
	}

	public int getiSimilarPercentage() {
		return iSimilarPercentage;
	}

	public void setiSimilarPercentage(int iSimilarPercentage) {
		this.iSimilarPercentage = iSimilarPercentage;
	}
	
}
