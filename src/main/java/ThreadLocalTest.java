public class ThreadLocalTest {

    private static final ThreadLocal<String> holder = new ThreadLocal<>();

    public static void main(String[] args) {
        ThreadLocalTest threadLocalTest = new ThreadLocalTest();

        new Thread(() -> {
            String threadName = "Thread1";
            try {
                holder.set(threadName);
                System.out.println(threadName + "set value and sleep");
                Thread.sleep(1000);
                System.out.println(threadName + " waked up");
                System.out.println(threadName + " value " + holder.get());
            } catch (InterruptedException e) {
            }
        }).start();

        new Thread(() -> {
            String threadName = "Thread2";
            try {
                holder.set(threadName);
                System.out.println(threadName + "set value and sleep");
                Thread.sleep(1000);
                System.out.println(threadName + " waked up");
                System.out.println(threadName + " value " + holder.get());
            } catch (InterruptedException e) {
            }
        }).start();
    }

}
