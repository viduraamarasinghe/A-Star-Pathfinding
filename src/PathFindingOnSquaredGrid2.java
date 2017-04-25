import java.util.*;
import java.util.Scanner;
/*************************************************************************
 *  Author: Vidura Amarasinghe
 *  w1583055
 *  2015142
 *  Last update: 04-03-2017
 *
 *************************************************************************/

public class PathFindingOnSquaredGrid2 {
    public static int diagonalCost;
    public static int verticalHorizontalCost;
    static int gridSize = 10;
    static boolean showVisited = false;

    public static final double DRAW_RADIUS = 0.5;
    public static final double OPEN_PROBABILITY = 0.6;

    static Scanner sc = new Scanner(System.in);
    public static boolean allowDiagonal = true;

    //Cell stores its gCost, finalCost, its i,j position, its parent Cell
    static class Cell{
        int gCost = 0; //Heuristic cost
        int heuristicCost = 0;
        int finalCost = 1000000; //G+H
        int i, j;
        Cell parent;

        Cell(int i, int j){
            this.i = i;
            this.j = j;
        }

    }

    //Blocked cells are just null values in grid
    static Cell [][] grid = new Cell[gridSize][gridSize];

    //PriorityQueue holds open cells
    static PriorityQueue<Cell> open;

    //boolean array points to closed cells (tr)
    static boolean closed[][];

    static int startI =-1, startJ = -1;
    static int endI = -1, endJ = -1;

    //Blocked values are set in grid
    public static void setBlocked(int i, int j){
        grid[i][j] = null;
    }

    public static void setStartCell(int i, int j){
        startI = i;
        startJ = j;
    }

    public static void setEndCell(int i, int j){
        endI = i;
        endJ = j;
    }
    //traces back the path from end node onwards
    private static void tracePath(){
        if(closed[PathFindingOnSquaredGrid2.endI][PathFindingOnSquaredGrid2.endJ]){
            Cell current = grid[PathFindingOnSquaredGrid2.endI][PathFindingOnSquaredGrid2.endJ];

            //StdDraw.setPenColor(StdDraw.RED);
            while(current.parent!=null){
                StdDraw.setPenRadius(0.007);
                if (current.i != endI || current.j != endJ) {
                    switch (diagonalCost) {
                        case 10:
                            StdDraw.setPenColor(85, 173, 122);
                            StdDraw.square(current.i, current.j, 0.15);
                            break;
                        case 14:
                            StdDraw.setPenColor(30, 93, 136);
                            StdDraw.setPenRadius(0.0085);
                            StdDraw.circle(current.i, current.j, 0.3);
                            break;
                        case 20:
                            StdDraw.setPenColor(35, 41, 46);
                            StdDraw.setPenRadius(0.008);
                            StdDraw.circle(current.i, current.j, 0.05);
                            break;
                    }
                }
                StdDraw.line(current.parent.i, current.parent.j, current.i, current.j);
                //System.out.print(" -> "+current.parent);
                current = current.parent;
            }
        }else System.out.println("No possible path");
    }

    //this is done for each neighbouring node until the moment an OPEN NODE or a NODE WHOSE PREVIOUSLY CONTAINED
    // FINAL COS
    static void updateCost(Cell current, Cell t, int movementCost){
        //if t is not a cell (out of bounds?) OR if closed[t.i][t.j] return;
        if(t == null || closed[t.i][t.j])return;

        int distanceX = Math.abs(endI - t.i);
        int distanceY = Math.abs(endJ - t.j);

        if (verticalHorizontalCost == diagonalCost){
            if (distanceX > distanceY)
                t.heuristicCost = diagonalCost * distanceY + verticalHorizontalCost * (distanceX - distanceY);
            else
                t.heuristicCost = diagonalCost * distanceX + verticalHorizontalCost * (distanceY - distanceX);
        } else  {
            if (distanceX > distanceY)
                t.heuristicCost = (diagonalCost + 4) * distanceY + verticalHorizontalCost * (distanceX - distanceY);
            else
                t.heuristicCost = (diagonalCost + 4) * distanceX + verticalHorizontalCost * (distanceY - distanceX);
        }

        //IF MANHATTAN
/*        if (diagonalCost == 2){
            t.heuristicCost = distanceX + distanceY;
        } else {
                if (distanceX > distanceY)
                    t.heuristicCost = diagonalCost * distanceY + verticalHorizontalCost * (distanceX - distanceY);
                else
                    t.heuristicCost = diagonalCost * distanceX + verticalHorizontalCost * (distanceY - distanceX);
        }*/

        //final cost is equal to the heuristic cost + the cost;
        int newGCost = current.gCost + movementCost;
        int newFinalCost = t.heuristicCost + current.gCost + movementCost;

        //boolean inOpen holds true IF Cell t is contained within the PriorityQueue open
        //NOTE grid holds all values irrespective of open or close, except for BLOCKED which is null
        boolean inOpen = open.contains(t);

        //if neighbouring cell is in not in open OR if the new final cost is lower than the previously calculated Final Cost
        if(!inOpen || newFinalCost<t.finalCost){
            t.gCost = newGCost;
            t.finalCost = newFinalCost ;
            t.parent = current;
            //if node was not in open, then it is added to the open PriorityQueue, it is at this point that the
            //Queue is sorted automatically by the lambda override written in Line 208
            if(!inOpen)open.add(t);
        }

        //If boolean is true then show which nodes are visited
        if (showVisited){
            StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
            StdDraw.filledSquare(t.i,t.j, DRAW_RADIUS);
        }
    }

    //Closed and open variables are initialized in this method
    //Traversal occurs here
    public static void AStar(){

        //add the start location to open list.
        try {
            open.add(grid[startI][startJ]);
        } catch (NullPointerException e){

        }
        //showGrid();
        Cell current;

        //the rest of the nodes are being added to the open PriorityQueue while the first in queue is being removed and assigned to current
        while(true){
            current = open.poll();

            //if current is null break out of the loop because
            //The open PriorityQueue stores null values at coordinates where the Cell is blocked
            if(current==null)break;

            closed[current.i][current.j]=true;

            if(current.equals(grid[endI][endJ])){
                if (verticalHorizontalCost>1) {
                    System.out.println("Final Cost: " + current.finalCost / 10.0);
                } else {
                    System.out.println("Final Cost: " + current.finalCost / 1.0);
                }
                //updateCost(current,, V_H_COST);
                return;
            }

            Cell t;

            try {
                //if current cell has neighbouring cells to its left
                if (current.i - 1 >= 0) {
                    //t is is the immediate left neighbouring cell
                    t = grid[current.i - 1][current.j];
                    //check and update the cost for the LEFT neighbouring cell
                    updateCost(current, t, current.gCost + verticalHorizontalCost);

                    //if the current cell has neighbouring cells underneath it
                    if (allowDiagonal && current.j - 1 >= 0) {
                        //check and update the cost of the BOTTOM LEFT neighbouring cell
                        t = grid[current.i - 1][current.j - 1];
                        //if BOTH the LEFT and BOTTOM neighbouring nodes are blocked we cannot traverse through BOTTOM LEFT
                        if (grid[current.i - 1][current.j] != null || grid[current.i][current.j - 1] != null)
                            updateCost(current, t, diagonalCost);
                    }

                    //if the current cell has neighbouring cells on top of it
                    if (allowDiagonal && current.j + 1 < grid[0].length) {
                        //check and update the cost of the TOP LEFT neighbouring cell
                        t = grid[current.i - 1][current.j + 1];
                        //if BOTH the TOP and LEFT neighbouring nodes are blocked we cannot traverse through TOP LEFT
                        if (grid[current.i - 1][current.j] != null || grid[current.i][current.j + 1] != null)
                            updateCost(current, t, diagonalCost);
                    }
                }

                if (current.j - 1 >= 0) {
                    t = grid[current.i][current.j - 1];
                    //check and update the cost of the BOTTOM neighbouring cell
                    updateCost(current, t, verticalHorizontalCost);
                }

                if (current.j + 1 < grid[0].length) {
                    t = grid[current.i][current.j + 1];
                    //check and update the cost of the TOP neighbouring cell
                    updateCost(current, t, verticalHorizontalCost);
                }

                if (current.i + 1 < grid.length) {
                    t = grid[current.i + 1][current.j];
                    //check and update the cost of the RIGHT neighbouring cell
                    updateCost(current, t, verticalHorizontalCost) ;


                    if (allowDiagonal && allowDiagonal && current.j - 1 >= 0) {
                        t = grid[current.i + 1][current.j - 1];
                        //if BOTH the BOTTOM and RIGHT neighbouring nodes are blocked we cannot traverse through BOTTOM RIGHT
                        if (grid[current.i + 1][current.j] != null || grid[current.i][current.j - 1] != null)
                            //check and update the cost of the BOTTOM RIGHT neighbouring cell
                            updateCost(current, t, diagonalCost);
                    }

                    if (allowDiagonal && current.j + 1 < grid[0].length) {
                        //check and update the cost of the TOP RIGHT neighbouring cell
                        t = grid[current.i + 1][current.j + 1];
                        //if BOTH the TOP and RIGHT neighbouring nodes are blocked we cannot traverse through TOP RIGHT
                        if (grid[current.i + 1][current.j] != null || grid[current.i][current.j + 1] != null)
                            updateCost(current, t, diagonalCost);
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e){

            }
        }
    }

    //testing by changing block to a boolean array
    public static void initializeAndSort(boolean[][] blocked){
        //Reset
        grid = new Cell[gridSize][gridSize];
        closed = new boolean[gridSize][gridSize];
        open = new PriorityQueue<>((Object o1, Object o2) -> {
            //PriorityQueue will be ordered in this manner. We override the method compare in Java.Util.Comparator
            Cell c1 = (Cell)o1;
            Cell c2 = (Cell)o2;

//            return c1.finalCost<c2.finalCost?-1:
//                    c1.finalCost>c2.finalCost?1:0;
            if (c1.finalCost < c2.finalCost){
                return -1;
            } else if (c1.finalCost<c2.finalCost) {
                return 1;
            } else
                return 0;
        });

        //Set start position
        setStartCell(startI, startJ);  //Setting to 0,0 by default. Will be useful for the UI part

        //Set End Location
        setEndCell(endI, endJ);

        //here the heuristic cost is being calculated for every node, but we dont want that
        for(int i=0;i<gridSize;++i){
            for(int j=0;j<gridSize;++j){
                grid[i][j] = new Cell(i, j);
            }
        }

//        grid[startI][startJ].finalCost = 0;

           /*
             Set blocked cells. Simply set the cell values to null
             for blocked cells.
           */
        for(int i = 0; i< PathFindingOnSquaredGrid2.gridSize; ++i){
            for(int j = 0; j< PathFindingOnSquaredGrid2.gridSize; j++)
                if (blocked[i][j]){
                    setBlocked(i,j);
                }
        }

        //Display STARTING and END points with circles
        for(int i=0;i<gridSize;++i){
            for(int j=0;j<gridSize;++j){
                //if i and j and are the starting coordinates print SO
                if(i==startI&&j==startJ) {
                    StdDraw.circle(i,j,DRAW_RADIUS); //Source
                }
                else if(i==endI && j==endJ) {
                    StdDraw.circle(i,j,DRAW_RADIUS);  //Destination
                }
            }
        }
        System.out.println();

        //AStar method is called after what is d
        AStar();
        tracePath();
        //printCost();
    }
    private static void printCost(){
        for(int i=0;i<gridSize;++i){
            for(int j=0;j<gridSize;++j){
                try{
                    if(grid[i][j].finalCost <= 100000)
                        StdDraw.text(i, j, grid[i][j].heuristicCost + "");
                } catch (NullPointerException e){

                }
            }
        }
    }

    // draw the N-by-N boolean matrix to standard draw
    private static void showGrid(boolean[][] blocked, boolean which) {
        StdDraw.setCanvasSize(1300,1300);

        StdDraw.setXscale(-1.5, gridSize + 2.5);
        StdDraw.setYscale(-1.5, gridSize + 2.5);

        StdDraw.setFont(StdDraw.getFont().deriveFont(1));

        StdDraw.setPenColor(35, 41, 46);
        StdDraw.textLeft(gridSize - 0.4, gridSize/2, "Manhattan");

        StdDraw.setPenColor(30, 93, 136);
        StdDraw.textLeft(gridSize - 0.4, gridSize/2 - 1, "Euclidian");

        StdDraw.setPenColor(85, 173, 122);
        StdDraw.textLeft(gridSize - 0.4, gridSize/2 - 2, "Chebychev");

        StdDraw.setFont(StdDraw.getFont().deriveFont(0));
        StdDraw.setPenColor(StdDraw.BLACK);

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (blocked[i][j])
                    StdDraw.filledSquare(i, j, DRAW_RADIUS);
                else
                    StdDraw.square(i, j, DRAW_RADIUS);
            }
            StdDraw.text(i, -1, Integer.toString(i));
        }
        for (int j = 0; j < gridSize; j++)
            StdDraw.text(-1, j, Integer.toString(j));
    }

    private static boolean[][] generateRandomMatrix(int N, double p) {
        boolean[][] a = new boolean[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                a[i][j] = !StdRandom.bernoulli(p);
        return a;
    }

    public static void getInput(boolean[][] blocked) {
        for (boolean valid = false; !valid; ) {
            StdOut.println("Enter i for A");
            startI = sc.nextInt();
            StdOut.println("Enter j for A");
            startJ = sc.nextInt();
            if (blocked[startI][startJ] || startI > gridSize || startJ > gridSize) {
                valid = false;
                StdOut.println("The cell you chose for A is blocked or does not exist, please re-enter");
            } else {
                StdOut.println("Enter i for B");
                endI = sc.nextInt();
                StdOut.println("Enter j for B");
                endJ = sc.nextInt();
                if ( startI > gridSize || startJ > gridSize || blocked[endI][endJ]) {
                    valid = false;
                    StdOut.println("The cell you chose for B is blocked or does not exist, please re-enter");
                } else
                    valid = true;
            }
        }
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.circle(startI, startJ,DRAW_RADIUS);
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.circle(endI, endJ, DRAW_RADIUS);
    }

    public static void drawEuclidian(boolean[][] randomlyGenMatrix){
        verticalHorizontalCost = 10;
        diagonalCost = 14;
        allowDiagonal = true;
        Stopwatch stopwatch = new Stopwatch();

        double start = stopwatch.elapsedTime();
        System.out.println("Euclidian");
        initializeAndSort(randomlyGenMatrix);

        double timeElapsed = stopwatch.elapsedTime() - start;
        System.out.println(timeElapsed);
        System.out.println();
    }
    public static void drawManhattan(boolean[][] randomlyGenMatrix){
        verticalHorizontalCost = 1;
        diagonalCost = 2;
        allowDiagonal = false;
        Stopwatch stopwatch = new Stopwatch();

        double start = stopwatch.elapsedTime();
        System.out.println("Manhattan");
        allowDiagonal = false;
        StdDraw.setPenColor(35, 41, 46);
        initializeAndSort(randomlyGenMatrix);
        double timeElapsed = stopwatch.elapsedTime() - start;
        System.out.println(timeElapsed);
        System.out.println();
    }
    public static void drawChebycev(boolean[][] randomlyGenMatrix){
        verticalHorizontalCost = 1;
        diagonalCost = 1;
        allowDiagonal = true;
        Stopwatch stopwatch = new Stopwatch ();

        double start = stopwatch.elapsedTime();
        System.out.println("Chebychev");
        StdDraw.setPenColor(85, 173, 122);
        initializeAndSort(randomlyGenMatrix);

        double timeElapsed = stopwatch.elapsedTime() - start;
        System.out.println(timeElapsed);
        System.out.println();
    }
    public static void main(String[] args) throws Exception{
        //generateRandomMatrix boolean matrix is generated
        boolean[][] randomlyGenMatrix = generateRandomMatrix(gridSize, OPEN_PROBABILITY);

        System.out.println("Number of Cells: " + gridSize * gridSize);
        System.out.println();

        //drawing the grid and then getting the input
        showGrid(randomlyGenMatrix, true);
        getInput(randomlyGenMatrix);

        showVisited = false;
        drawEuclidian(randomlyGenMatrix);
        drawManhattan(randomlyGenMatrix);
        drawChebycev(randomlyGenMatrix);

    }
}