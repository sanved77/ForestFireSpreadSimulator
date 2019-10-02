import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

class Simulation extends TimerTask {

    int nagdiNum = 0;
    static boolean lock = true;
    static Simulation dummy;

    public static void main(String args[]) {
        dummy = new Simulation();
        TimerTask timerTask = new Simulation();
        // running timer task as daemon thread
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
        System.out.println("TimerTask started");

        synchronized (dummy) {
            try {
                dummy.wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        clearScreen();
        System.out.println("nagdi - " + lock + ", " + nagdiNum++);
        if(nagdiNum > 5){
            synchronized(dummy){
                dummy.notify();
            }
        }
    }

    public static void clearScreen(){
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}