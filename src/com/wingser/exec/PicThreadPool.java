package com.wingser.exec;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class PicThreadPool {

	private ExecutorService fixedThreadPool;
	
	private AtomicInteger cnt;

	public PicThreadPool() {
		fixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		cnt = new AtomicInteger();
	}

	public void addTaskAndRun(Runnable r) {
		fixedThreadPool.execute(r);
	}
	
	public ExecutorService getPool() {
		return fixedThreadPool;
	}

	public AtomicInteger getCnt() {
		return cnt;
	}
}