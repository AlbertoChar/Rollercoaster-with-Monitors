package OttoVolanteMonitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Otto {
	
	
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition canDrive = lock.newCondition();
	private final Condition canGetOn = lock.newCondition();
	private final Condition canGetOff = lock.newCondition();
	
	private boolean lightOn = false;
	int P = 0;
	int N = 5;
	
	
	void getOn() throws InterruptedException {
		lock.lock();
		while(!lightOn) {
			System.out.println("Passenger n." + P + "gets on coaster");
			P++;
			while(P>=N) {
				canDrive.signal();
			}
		}
		canGetOn.await();
		lock.unlock();
	}
	
	void getOff() throws InterruptedException {
		lock.lock();
		while(lightOn) {
			canGetOff.await();
		}
		System.out.println("Passenger n." + P + "gets off coaster");
		P = 0;
		canGetOn.signalAll();
		lock.unlock();
	}
	
	void Drive() throws InterruptedException {
		lock.lock();
		while(P>N) {
			canDrive.await();
		}
		while(!lightOn) {
			lightOn = true;
			//ride
			System.out.println(P + " riders on coaster, drive begins");
			lightOn = false;
			canGetOff.signalAll();
		}
		lock.unlock();
	}
	
	
		
}
