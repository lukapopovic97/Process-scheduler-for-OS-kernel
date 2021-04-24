package com.etf.os2.project.scheduler;

import java.util.LinkedList;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.Pcb.ProcessState;
import com.etf.os2.project.process.PcbData;

public class MFQScheduler extends Scheduler {

	private int numqueues;
	private long[] timeslices;
	private LinkedList<Pcb> ready[];
	
	public MFQScheduler(int num, long[] quants) {
		numqueues=num;
		timeslices=quants;
		ready = new LinkedList[num];
		for(int i=0;i<num;i++)ready[i]=new LinkedList<Pcb>();
	}
	
	@Override
	public Pcb get(int cpuId) {
		for(int i=0;i<numqueues;i++) {
			if(!ready[i].isEmpty()) {
				Pcb pcb=ready[i].removeFirst();
/*				StringBuilder ispis=new StringBuilder(String.format("\nUzet je proces %d(%d)\n", pcb.getId(), pcb.getPcbData().getPriority()));
				for(int j=0;j<numqueues;j++) {
					for (int k=0;k<ready[j].size();k++) {
						Pcb p=ready[j].get(k);
						ispis.append(p.getId()+"("+p.getPcbData().getPriority()+"), ");
					}
					ispis.append("\n");
				}
				System.out.print(ispis);
*/				return pcb;
			}
		}
		return null;
	}

	@Override
	public void put(Pcb pcb) {
		if(pcb==null) return;
		if(pcb.getPcbData()==null) {
			pcb.setPcbData(new PcbData());
			pcb.getPcbData().setPriority(pcb.getPriority(), numqueues);
		}
		else {
			PcbData data=pcb.getPcbData();
			if(pcb.getPreviousState()==ProcessState.BLOCKED) {
				data.setPriority(data.getPriority()-1, numqueues);
			}
			else if(pcb.getTimeslice()<=pcb.getExecutionTime() && pcb.getTimeslice()!=0) {
				data.setPriority(data.getPriority()+1, numqueues);
			}
		}

		pcb.setTimeslice(timeslices[pcb.getPcbData().getPriority()]);
		ready[pcb.getPcbData().getPriority()].add(pcb);
		
/*		StringBuilder ispis=new StringBuilder(String.format("\nDodat je proces %d(%d) ", pcb.getId(), pcb.getPcbData().getPriority()));
		ispis.append("ts="+pcb.getTimeslice()+"\n");
		for(int j=0;j<numqueues;j++) {
			for (int k=0;k<ready[j].size();k++) {
				Pcb p=ready[j].get(k);
				ispis.append(p.getId()+"("+p.getPcbData().getPriority()+"), ");
			}
			ispis.append("\n");
		}
		System.out.print(ispis);
*/	}

}
