package com.etf.os2.project.process;

public class PcbData {

	private float tau;
	private int priority;
	private long startTime;
	private long execTime;
	
	public PcbData() {
		tau=0;
		priority=0;
		startTime=0;
		execTime=0;
	}

	public float getTau() {
		return tau;
	}

	public void setTau(float tau) {
		this.tau = tau;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority, int num) {
		if(priority<0) {this.priority=0;return;}
		if(priority>=num) {this.priority=num-1; return;}
		this.priority = priority;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getExecTime() {
		return execTime;
	}

	public void setExecTime(long execTime) {
		this.execTime = execTime;
	}
	
	
}
