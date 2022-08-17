import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Train {

    private Map<String, Integer> tubeStations;
    private float[][] grid;

    public Train(){
        tubeStations = new HashMap<>();

        try{
            referData();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void referData() throws Exception{

        int compute = 0;
        String line = "";
        String splitBy = ",";
        try
        {
            BufferedReader file = new BufferedReader(new FileReader("details.csv"));

            while ((line = file.readLine()) != null)
            {
                String[] trainData = line.split(splitBy);
                if(!this.tubeStations.containsKey(trainData[0])) {
                    this.tubeStations.put(trainData[0], compute++);
                }
                if(!this.tubeStations.containsKey(trainData[1])) {
                    this.tubeStations.put(trainData[1], compute++);
                }
            }

            this.grid = new float[compute][compute];

            for(int i=0; i<compute; i++){
                for(int j=0; j<compute; j++){
                    grid[i][j] = 0;
                }
            }
            file = new BufferedReader(new FileReader("details.csv"));

            while ((line = file.readLine()) != null)
            {
                String[] data = line.split(splitBy);
                grid[tubeStations.get(data[0])][tubeStations.get(data[1])] = Float.parseFloat(data[2]);
                grid[tubeStations.get(data[0])][tubeStations.get(data[1])] = Float.parseFloat(data[2]);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void findShortestPath(int src, int destination) {

        int compute = grid.length;
        boolean[] exploredVertex = new boolean[compute];
        float[] gap = new float[compute];
        int[] last = new int[compute];

        for (int i = 0; i < compute; i++) {
            exploredVertex[i] = false;
            gap[i] = Integer.MAX_VALUE;
            last[i] = -1;
        }

        gap[src] = 0;
        for (int i = 0; i < compute; i++) {

            int x = findMinimumDistance(gap, exploredVertex);
            exploredVertex[x] = true;

            for (int w = 0; w < compute; w++) {
                if (!exploredVertex[w] && grid[x][w] != 0 && (gap[x] + grid[x][w] < gap[w])) {
                    gap[w] = gap[x] + grid[x][w];
                    last[w] = x;
                }
            }
        }

        printStationPath(discoverPath(last, src, destination), src, destination);

    }
    private static int findMinimumDistance(float[] gap, boolean[] exploredVertex) {

        float minimumDistance = Float.MAX_VALUE;
        int minimumDistanceVertex = -1;

        for (int i = 0; i < gap.length; i++) {

            if (!exploredVertex[i] && gap[i] < minimumDistance) {
                minimumDistance = gap[i];
                minimumDistanceVertex = i;
            }
        }
        return minimumDistanceVertex;
    }

    public int[] discoverPath(int last[], int src, int destination){

        int compute = last.length;
        int routeNodeCount = 0;
        int[] pastRoute = new int[compute];
        int currentNode = destination;

        while(currentNode != -1){
            pastRoute[routeNodeCount++] = currentNode;
            currentNode = last[currentNode];
        }

        int[] route = new int[routeNodeCount];

        for(int i=0; i<routeNodeCount; i++){
            route[i] = pastRoute[routeNodeCount-1-i];
        }
        return route;
    }

    public String getTubeStation(int value){

        for(Map.Entry<String, Integer> entry: tubeStations.entrySet()) {

            if(entry.getValue() == value) {
                return entry.getKey();
            }
        }
        return "NA";
    }

    public void printStationPath(int[] route, int src, int destination){

        float wholeTime = 0;

        System.out.println("\nROUTE : " + getTubeStation(src) + " to " + getTubeStation(destination));

        for(int i=0; i<route.length-1; i++){

            float time = this.grid[route[i]][route[i+1]];
            wholeTime += time;
            System.out.println(String.format("%-5s", "(" + (i+1) + ") ") + String.format("%-22s", getTubeStation(route[i])) + " to "
                    + String.format("%-22s", getTubeStation(route[i+1])) + " " + time + " minutes");
        }

        System.out.println("Total ride time :  " + String.format("%.2f", wholeTime) + " minutes");
    }

    public void printAllTubeStations(){

        for(int i=0; i<tubeStations.size(); i++) {
            if(i % 2 == 0) {
                System.out.print("\n");
            }

            System.out.print(String.format("%-5s  %-40s","[" + (i + 1) + "]", getTubeStation(i)));
        }

    }

    public boolean verifyUserInputs(int value){

        return value >= 0 && value < tubeStations.size();
    }

    public static void main(String[] args) {

        int strt, destination;
        Train tl = new Train();

        while(true){

            System.out.println("\n___| Tube Station Route |___\n");
            System.out.println("\nPress 0 to Exit");

            tl.printAllTubeStations();

            Scanner inputSc = new Scanner(System.in);
            System.out.print("\nEnter the starting station number - ");
            if((strt = inputSc.nextInt()) == 0) break;
            System.out.print("Enter the end station number - ");
            if((destination = inputSc.nextInt()) == 0) break;

            strt -= 1;
            destination -= 1;

            if(tl.verifyUserInputs(strt) && tl.verifyUserInputs(destination)){
                tl.findShortestPath(strt, destination);
            }
            else {
                System.out.println("\nINVALID ENTER !");
            }


        }

    }
}