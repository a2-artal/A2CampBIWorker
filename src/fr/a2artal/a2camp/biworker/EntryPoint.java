package fr.a2artal.a2camp.biworker;

import java.util.ArrayList;

public class EntryPoint {

	private static int NB_THREAD_PER_WORKER = 5;
	
	/**
	 * EntryPoint
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<BIWorker> workers = new ArrayList<BIWorker>();
		// Create a list of workers
		for (int i=0; i<NB_THREAD_PER_WORKER; i++) {
			workers.add(new BIWorker("Worker " + i));
		}
		// Start'em all
		for (BIWorker worker : workers) {
			new Thread(worker).start();
		}
	}
	
}
