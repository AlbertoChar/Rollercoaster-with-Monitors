package OttoVolanteMonitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class OttoVolanteMonitor {
	
	private final int N = 3;
	volatile private int P = 0;
	volatile private boolean lightOn = false;
	
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition DOktoRide = lock.newCondition();
	private final Condition POktoRide = lock.newCondition();
	private final Condition OktoGetOn = lock.newCondition();
	private final Condition OktoGetOff = lock.newCondition();
	
	volatile private boolean driverArrived = false;
	
	void getOn(int Name) throws InterruptedException {
		lock.lock();
		while(lightOn) {
			System.out.println("Passenger " + Name + ": ride ongoing, so waits..");
			OktoGetOn.await();
		}
		P++;
		while(P < N || !driverArrived) {
			System.out.println("Passanger " + Name + ": P=" + P + ", driver arrived = " + driverArrived + ", so waits..");
			POktoRide.await();
		}
		System.out.println("Passanger " + Name + "P: " + P + ", driver arrived" + driverArrived + " so signals driver and rides");
		DOktoRide.signal();
		lock.unlock();
	}
	
	void getOff(int Name) throws InterruptedException {
		lock.lock();
		while(lightOn) {
			System.out.println("Passenger " + Name + ": waits for light off..");
			OktoGetOff.await();
		}
		System.out.println("Passenger " + Name + ": light is off, so gets off");
		lock.unlock();
	}
	
	void drive() throws InterruptedException {
		lock.lock();
		driverArrived = true;
		while(P < N) {
			System.out.println("Driver: " + P + ", so waits..");
			DOktoRide.await();	
		}
		if(!lightOn) {
			lightOn = true;
			System.out.println("Driver: switches light on, signals all passengers, new ride starts");
			DOktoRide.signalAll();
		} else {
			P = 0;
			lightOn = false;
			System.out.println("Driver: sets P = 0, switches lights off, signals all passengers, end of ride");
			OktoGetOff.signalAll();
			OktoGetOn.signalAll();
			driverArrived = false;
		}
		lock.unlock();
	}
	
	

}
