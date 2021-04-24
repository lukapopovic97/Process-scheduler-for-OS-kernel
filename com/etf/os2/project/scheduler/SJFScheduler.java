package com.etf.os2.project.scheduler;

import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.PcbData;

public class SJFScheduler extends Scheduler {

	private ArrayList<Pcb> ready;
	private float alfa;
	private boolean preemptive;
	
	public SJFScheduler(float a, boolean p) {
		if(a<0 || a>1) {
			System.err.println("\nInvalid argument, alfa must be between 0 and 1!\n");
			System.exit(-1);
		}
		alfa = a;
		preemptive=p;
		ready =new ArrayList<Pcb>();
	}
	
	@Override
	public Pcb get(int cpuId) {
		if(ready.isEmpty())return null;
		else {
			Pcb pcb=ready.remove(0);
			pcb.getPcbData().setStartTime(Pcb.getCurrentTime());
/*			StringBuilder ispis=new StringBuilder(String.format("\nUzet je proces %d(%f)\n", pcb.getId(), pcb.getPcbData().getTau()));
			for(int i=0;i<ready.size()-1;i++) {
				ispis.append(String.format("%d(%f), ", ready.get(i).getId(), ready.get(i).getPcbData().getTau()));
			}
			if(ready.size()>0)ispis.append(String.format("%d(%f)\n", ready.get(ready.size()-1).getId(), ready.get(ready.size()-1).getPcbData().getTau()));
			System.out.print(ispis);
			
			if(preemptive)for(int i=0;i<Pcb.RUNNING.length;i++) {
				if(cpuId!=i && !ready.isEmpty()) Pcb.RUNNING[i].preempt();
			}
*/			if(preemptive)preempt(cpuId);
			return pcb;
		}
	}

	@Override
	public void put(Pcb pcb) {
		if(pcb==null) return;
		if(pcb.getPcbData()==null) {
			pcb.setPcbData(new PcbData());
		}
		
		PcbData data=pcb.getPcbData();
		float tau=alfa*pcb.getExecutionTime()+(1-alfa)*data.getTau();
		data.setTau(tau);
		
		if(ready.size()==0) {
			ready.add(pcb);			
		}
		else {
			boolean added=false;
			for(int i=0;i<ready.size();i++) {
				if(ready.get(i).getPcbData().getTau()>tau) {
					ready.add(i, pcb);
					added=true;
					break;
				}
			}
			if(!added) {
				ready.add(pcb);
			}
			pcb.setTimeslice(0);
		}
		if(preemptive)preempt(-1);
/*		StringBuilder ispis=new StringBuilder(String.format("\nDodat je proces %d(%f)\n", pcb.getId(), pcb.getPcbData().getTau()));
		for(int i=0;i<ready.size()-1;i++) {
			ispis.append(String.format("%d(%f), ", ready.get(i).getId(), ready.get(i).getPcbData().getTau()));
		}
		ispis.append(String.format("%d(%f)\n ", ready.get(ready.size()-1).getId(), ready.get(ready.size()-1).getPcbData().getTau()));
		System.out.print(ispis);
*/	}

	
	private void preempt(int id) {
		int size=ready.size();
		if (size==0)return;
		int numpreempted=0;
		if(size==numpreempted)return;
		boolean pre[]=new boolean[Pcb.RUNNING.length];
		for(int i=0;i<Pcb.RUNNING.length;i++)pre[i]=false;
		if(id!=-1)pre[id]=true;
		for (int i=0;i<Pcb.RUNNING.length;i++) {
			if(pre[i]==true)continue;
			if(Pcb.RUNNING[i]==Pcb.IDLE) {
				Pcb.RUNNING[i].preempt();
				pre[i]=true;
				numpreempted++;
				if(numpreempted==size)return;
			}
		}
		
		
		for (int i=0;i<Pcb.RUNNING.length;i++) {
			if(pre[i]==true)continue;
			Pcb running=Pcb.RUNNING[i];
			PcbData data=running.getPcbData();
			long timepassed=Pcb.getCurrentTime()-data.getStartTime();
			if((data.getTau()<=timepassed)) {
				Pcb.RUNNING[i].preempt();
				pre[i]=true;
				numpreempted++;
				if(numpreempted==size)return;
			}
		}

		int ind=-1;long time=0;
		for(int j=0;j<Pcb.RUNNING.length-numpreempted;j++) {
			for (int i=0;i<Pcb.RUNNING.length;i++) {
				if(pre[i]==true)continue;
				Pcb running=Pcb.RUNNING[i];
				PcbData data=running.getPcbData();
				long timepassed=Pcb.getCurrentTime()-data.getStartTime();
				long timeleft=(long)data.getTau()-timepassed;
				if(timeleft>ready.get(numpreempted).getPcbData().getTau() && timeleft>time) {
					ind=i;
					time=timeleft;
				}
			}
			if(ind!=-1) {
				Pcb.RUNNING[ind].preempt();
				numpreempted++;
				if(numpreempted==size)return;
				ind=-1; time=0;
			}
			else return;
		}
		
				
		
/*		for(int i=0;i<Pcb.RUNNING.length;i++) {
			Pcb.RUNNING[i].preempt();
		}
*/	}

}
