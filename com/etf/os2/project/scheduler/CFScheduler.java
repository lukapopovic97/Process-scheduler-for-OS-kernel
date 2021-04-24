package com.etf.os2.project.scheduler;

import java.util.ArrayList;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.Pcb.ProcessState;
import com.etf.os2.project.process.PcbData;

public class CFScheduler extends Scheduler {

	private ArrayList<Pcb> ready;
	
	public CFScheduler() {
		ready=new ArrayList<Pcb>();
	} 
	
	@Override
	public Pcb get(int cpuId) {
		if(!ready.isEmpty()) {
			Pcb pcb=ready.remove(0);
			long ts=Math.round(((double)Pcb.getCurrentTime()-pcb.getPcbData().getStartTime())/Pcb.getProcessCount());
			pcb.setTimeslice(ts);
/*			StringBuilder ispis=new StringBuilder(String.format("\nUzet je proces %d(%d)", pcb.getId(), pcb.getPcbData().getExecTime()));
			ispis.append(" ts="+ts+"\n");
			for(int i=0;i<ready.size()-1;i++) {
				ispis.append(String.format("%d(%d), ", ready.get(i).getId(), ready.get(i).getPcbData().getExecTime()));
			}
			if(ready.size()>0)ispis.append(String.format("%d(%d)\n", ready.get(ready.size()-1).getId(), ready.get(ready.size()-1).getPcbData().getExecTime()));
			System.out.print(ispis);
*/			return pcb;
		}
		return null;
	}

	@Override
	public void put(Pcb pcb) {
		if(pcb==null)return;
		if(pcb.getPcbData()==null) {
			pcb.setPcbData(new PcbData());
		}
		PcbData data=pcb.getPcbData();
		if(pcb.getPreviousState()==ProcessState.BLOCKED||pcb.getPreviousState()==ProcessState.CREATED) {
			data.setExecTime(0);
		}
		else if(pcb.getPreviousState()==ProcessState.RUNNING) {
			data.setExecTime(data.getExecTime()+pcb.getExecutionTime());
		}
		data.setStartTime(Pcb.getCurrentTime());
		
		long exectime=data.getExecTime();
		
		if(ready.size()==0) {
			ready.add(pcb);			
		}
		else {
			boolean added=false;
			for(int i=0;i<ready.size();i++) {
				if(ready.get(i).getPcbData().getExecTime()>exectime) {
					ready.add(i, pcb);
					added=true;
					break;
				}
			}
			if(!added) {
				ready.add(pcb);
			}
		}
		/*StringBuilder ispis=new StringBuilder(String.format("\nDodat je proces %d(%d)\n", pcb.getId(), pcb.getPcbData().getExecTime()));
		for(int i=0;i<ready.size()-1;i++) {
			ispis.append(String.format("%d(%d), ", ready.get(i).getId(), ready.get(i).getPcbData().getExecTime()));
		}
		if(ready.size()>0)ispis.append(String.format("%d(%d)\n", ready.get(ready.size()-1).getId(), ready.get(ready.size()-1).getPcbData().getExecTime()));
		System.out.print(ispis);
		*/
	}

}
