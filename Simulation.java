import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.Scanner;

class Simulation extends TimerTask {
    
    // Prefabs
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[35m";
    public static final String ANSI_ORANGE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";

    // Constants
    public static final String FIRE = ANSI_RED + "F" + ANSI_RESET;
    public static final String ACT_SENSOR = ANSI_GREEN + "S" + ANSI_RESET;
    public static final String WARNED_SENSOR = ANSI_GREEN + "S" + ANSI_RESET;
    public static final String GRASS = " ";
    public static final String SENSOR = "S";

    // User Constants
    public static final int FIRE_SPREAD = 75;
    public static final int SPEED = 700;
    public static final int SENSORDENSITY = 4;


    int fireSpread = 0;
    static int sensorsTriggered = 0;
    static boolean lock = true;
    static Simulation dummy;
    static String map[][] = new String[20][20];
    static int sensormap[][] = new int[20][20];
    static ArrayList<Posi> list = new ArrayList<>();

    static Random rand = new Random();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String args[]) {
        dummy = new Simulation();
        Posi seedPosi = new Posi(5,5);
        list.add(seedPosi);
        resetMap();
        TimerTask timerTask = new Simulation();
        // running timer task as daemon thread
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, SPEED);
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
        spreadFire();
        // predictFire();
        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 20; j++){
                System.out.print(" " + map[i][j]);
            }System.out.println("");
        }
        System.out.println("\nBlocks affected by fire - " + ANSI_GREEN + (fireSpread + 1) + ANSI_RESET);
        System.out.println("Sensors Triggered - " + ANSI_GREEN + sensorsTriggered + ANSI_RESET);
        fireSpread++;
        if(fireSpread > FIRE_SPREAD){
            synchronized(dummy){
                dummy.notify();
            }
        }
    }

    public static void spreadFire(){
        
        while(true){
            int listIndex = rand.nextInt(list.size());
            int option = rand.nextInt(8) + 1;
            Posi posi = list.get(listIndex);

            int i = posi.getI();
            int j = posi.getJ();

            switch(option){
                case 1:
                    i+=1;
                    j+=1;
                    break;
                case 2:
                    i+=1;
                    break;
                case 3:
                    i+=1;
                    j-=1;
                    break;
                case 4:
                    j-=1;
                    break;
                case 5:
                    i-=1;
                    j-=1;
                    break;
                case 6:
                    i-=1;
                    break;
                case 7:
                    i-=1;
                    j+=1;
                    break;
                case 8:
                    j+=1;
                    break;
            }

            if(i < 0 || i > 19 || j <0 || j > 19){
                continue;
            }
            else if(map[i][j].contains(GRASS)){
                map[i][j] = FIRE;
                for(int k = -1; k <= 1; k++){
                    for(int l = -1; l <= 1; l++){
                        if(i < 1 || i > 18 || j < 1 || j > 18){
                            continue;
                        }
                        if(map[i+k][j+l].contains(SENSOR)){
                            map[i+k][j+l] = ACT_SENSOR;
                            if(sensormap[i+k][j+l] == 0){
                                sensormap[i+k][j+l] = 1;
                                sensorsTriggered++;
                            }
                        }
                    }
                }
                list.add(new Posi(i,j));
                break;
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

    public static void resetMap(){
        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 20; j++){
                if(i % SENSORDENSITY == 0 && j % SENSORDENSITY == 0){
                    map[i][j] = SENSOR;
                }else{
                    map[i][j] = GRASS;
                }
            }
        }
    } 

}

class Posi{

    int i, j;
    public Posi(int i, int j){
        this.i = i;
        this.j = j;
    }
    public int getI() {
        return i;
    }
    public int getJ() {
        return j;
    }

}