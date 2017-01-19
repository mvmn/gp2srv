package x.mvmn.lang.util;

public class WaitUtil {

	public static void ensuredWait(long totalWaitTime) {
		long waitTime = totalWaitTime;
		long startTime = System.currentTimeMillis();
		long projectedEndTime = startTime + totalWaitTime;
		do {
			try {
				if (waitTime > 0) {
					Thread.sleep(waitTime);
				}
			} catch (InterruptedException e) {
				// Wait time = Total wait time - passed time (e.g. total wait for 10 sec, interrupted after 3 sec, still 7 sec to wait)
				// Passed time = now - start time
				waitTime = totalWaitTime - (System.currentTimeMillis() - startTime);
			}
		} while (System.currentTimeMillis() < projectedEndTime);
	}
}
