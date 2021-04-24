package com.etf.os2.project.scheduler;

import com.etf.os2.project.process.Pcb;

public abstract class Scheduler {
	public abstract Pcb get(int cpuId);

    public abstract void put(Pcb pcb);

    public static Scheduler createScheduler(String[] args) {
    	if(args.length<1) {
    		System.err.println("\nScheduler not specified\n");
    		System.exit(-1);
    	}
		String type = args[0].toLowerCase();
		switch(type) {
		case "sjf":
			if(args.length<3) {
				System.err.println("\nWrong amount of arguments\n");
				System.exit(-1);
			}
			try {
				float alfa=Float.parseFloat(args[1]);
				boolean preemptive = Boolean.parseBoolean(args[2]);
				return new SJFScheduler(alfa, preemptive);
			}
			catch (Exception e) {
				System.err.println("\nCan't read arguments\n");
				System.exit(-1);
			}
		case "mfq":
			try {
				int num=Integer.parseInt(args[1]);
				if(num<=0) {
					System.err.println("\nNumber of queues must be greater than 0!\n");
					System.exit(-1);
				}
				if(args.length<num+2) {
					System.err.println("\nWrong amount of arguments\n");
					System.exit(-1);
				}
				long quants[] = new long[num];
				for(int i=0;i<num;i++) {
					quants[i]=Long.parseLong(args[i+2]);
					if(quants[i]<0) {
						System.err.println("\nTime slice can't be less than 0!\n");
						System.exit(-1);
					}
				}
				return new MFQScheduler(num, quants);
			}
			catch (Exception e) {
				System.err.println("\nCan't read arguments\n");
				System.exit(-1);
			}
		case "cf":
			return new CFScheduler();
		default:
			System.err.println("\nScheduler with that name is not supported!\n");
			System.exit(-1);
		}
		return null;
    }
}
