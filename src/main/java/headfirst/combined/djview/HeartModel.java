package headfirst.combined.djview;

import java.util.*;

public class HeartModel implements HeartModelInterface, Runnable {
	private static HeartModel uniqueInstace;

	ArrayList beatObservers = new ArrayList();
	ArrayList bpmObservers = new ArrayList();
	int time = 1000;
	int bpm = 90;
	int intentos=-1;
	Random random = new Random(System.currentTimeMillis());
	Thread thread;
	String name="Heart";




	public HeartModel() {
		thread = new Thread(this);
		thread.start();
	}
	public String getName(){
		return name;
	}

	public static HeartModel getInstace(){
		if(uniqueInstace==null){
			uniqueInstace=new HeartModel();
		}
		uniqueInstace.increaseIntentos();
		return uniqueInstace;
	}
	public void increaseIntentos(){
		intentos++;
		notifyBPMObservers();
	}
	public void on(){
		thread = new Thread(this);
		thread.start();
		random = new Random(System.currentTimeMillis());
	}
	public void run() {
		int lastrate = -1;

		for(;;) {
			int change = random.nextInt(10);
			if (random.nextInt(2) == 0) {
				change = 0 - change;
			}
			int rate = 60000/(time + change);
			if (rate < 120 && rate > 50) {
				time += change;
				notifyBeatObservers();
				if (rate != lastrate) {
					lastrate = rate;
					notifyBPMObservers();
				}
			}
			try {
				Thread.sleep(time);
			} catch (Exception e) {}
		}
	}
//	public int getHeartRate() {return 60000/time;}
	public int getHeartRate() {return intentos;}

	public void registerObserver(BeatObserver o) {
		beatObservers.add(o);
	}

	public void removeObserver(BeatObserver o) {
		int i = beatObservers.indexOf(o);
		if (i >= 0) {
			beatObservers.remove(i);
		}
	}

	public void notifyBeatObservers() {
		for(int i = 0; i < beatObservers.size(); i++) {
			BeatObserver observer = (BeatObserver)beatObservers.get(i);
			observer.updateBeat();
		}
	}

	public void registerObserver(BPMObserver o) {
		bpmObservers.add(o);
	}

	public void removeObserver(BPMObserver o) {
		int i = bpmObservers.indexOf(o);
		if (i >= 0) {
			bpmObservers.remove(i);
		}
	}

	public void notifyBPMObservers() {
		for(int i = 0; i < bpmObservers.size(); i++) {
			BPMObserver observer = (BPMObserver)bpmObservers.get(i);
			observer.updateBPM();
		}
	}
	public void registerObserver(JUG1Observer o) {}
	public void registerObserver(JUG2Observer o) {}
	public void removeObserver(JUG1Observer o){}
	public void removeObserver(JUG2Observer o){}
	public int getJUG1(){return 0; }
	public int getJUG2(){return 0;}
}
