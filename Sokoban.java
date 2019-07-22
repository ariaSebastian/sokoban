import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
 
public class Sokoban {
    String destBoard, currBoard;
    boolean player = false;
    int playerX, playerY, nCols;
 
    Sokoban(String ruta) throws FileNotFoundException, IOException {
        // nCols = board[0].length();
        nCols = 0;
        destBoard = "";
        currBoard = "";
 
        String line;
        String board = "";
        FileReader f = new FileReader(ruta);
        BufferedReader b = new BufferedReader(f);
        while((line = b.readLine()) != null){
            if(line.contains(",")){
                if(!player){
                    player = true;
                    destBoard = "" + board;

                    String[] position = line.split(",");
                    playerX = Integer.parseInt(position[1]);
                    playerY = Integer.parseInt(position[0]);
                    int pos = playerY * nCols + playerX;
                    char[] sceneChar = board.toCharArray();
                    sceneChar[pos] = '@';
                    board = new String(sceneChar);
                } else {
                    String[] position = line.split(",");
                    int x = Integer.parseInt(position[1]);
                    int y = Integer.parseInt(position[0]);
                    int pos = y * nCols + x;
                    char[] sceneChar = board.toCharArray();
                    sceneChar[pos] = 'B';
                    board = new String(sceneChar);
                }
            } else {
                board += line;
            }

            if(nCols == 0){
                nCols = line.length();
            }
        }

        System.out.println("INITIAL BOARD:");
        printBoard(board);
        b.close();


        for (int c = 0; c < board.length(); c++) {
            char ch = board.charAt(c);

            char newCharCurr = ch != 'X' ? ch : '0';

            currBoard += newCharCurr;
        }

        // System.out.println("DEST:");
        // printBoard(destBoard);
        // System.out.println("CURR:");
        // printBoard(currBoard);
    }
 
    String move(int x, int y, int dx, int dy, String trialBoard) {
 
        int newPlayerPos = (y + dy) * nCols + x + dx;
 
        if (trialBoard.charAt(newPlayerPos) != '0')
            return null;
 
        char[] trial = trialBoard.toCharArray();
        trial[y * nCols + x] = '0';
        trial[newPlayerPos] = '@';
 
        return new String(trial);
    }
 
    String push(int x, int y, int dx, int dy, String trialBoard) {
 
        int newBoxPos = (y + 2 * dy) * nCols + x + 2 * dx;
 
        if (trialBoard.charAt(newBoxPos) != '0')
            return null;
 
        char[] trial = trialBoard.toCharArray();
        trial[y * nCols + x] = '0';
        trial[(y + dy) * nCols + x + dx] = '@';
        trial[newBoxPos] = 'B';
 
        return new String(trial);
    }
 
    boolean isSolved(String trialBoard) {
        for (int i = 0; i < trialBoard.length(); i++)
            if ((destBoard.charAt(i) == 'X')
                    != (trialBoard.charAt(i) == 'B'))
                return false;

        System.out.println("SOL:");
        printBoard(trialBoard);
        
        return true;
    }

    void printBoard(String board){
        String sol = "";
        System.out.println("");
        System.out.println(board);
        System.out.println("");

        for (int i = 0; i < board.length(); i++){
            sol += board.charAt(i);
            if(((i + 1 ) % nCols) == 0){
                System.out.println(sol);
                sol = "";
            }
        }
        System.out.println("");
    }
 
    void solve() {
        char[][] dirLabels = {{'u', 'U'}, {'d', 'D'}, {'l', 'L'}, {'r', 'R'}};
        int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
 
        System.out.println("");
        System.out.println("==================================================");
        System.out.println("============= ALGORITHM BY AMPLITUDE =============");
        System.out.println("==================================================");
        System.out.println("");
        long tInicio, tFin, time;
        tInicio = System.currentTimeMillis();
        System.out.println("Movimientos: " + algAmplitude(dirs, dirLabels));
        tFin = System.currentTimeMillis();
        time = tFin - tInicio;
        System.out.println("");
        System.out.println("Tiempo de ejecución en milisegundos: " + time); 
        System.out.println("");
        System.out.println("");
        System.out.println("==================================================");
        System.out.println("=============== ALGORITHM BY DEPTH ===============");
        System.out.println("==================================================");
        System.out.println("");
        tInicio = System.currentTimeMillis();
        System.out.println("Movimientos: " + algDepth(dirs, dirLabels));
        tFin = System.currentTimeMillis();
        time = tFin - tInicio;
        System.out.println("");
        System.out.println("Tiempo de ejecución en milisegundos: " + time); 
        System.out.println("");

        // return algDepth(dirs, dirLabels);
    }

    String algAmplitude(int[][] dirs, char[][] dirLabels){
        class Board {
            String cur, sol;
            int x, y;
 
            Board(String s1, String s2, int px, int py) {
                cur = s1;
                sol = s2;
                x = px;
                y = py;
            }
        }

        Set<String> history = new HashSet<>();
        LinkedList<Board> open = new LinkedList<>();
 
        history.add(currBoard);
        open.add(new Board(currBoard, "", playerX, playerY));
 
        while (!open.isEmpty()) {
            Board item = open.poll();
            String cur = item.cur;
            String sol = item.sol;
            int x = item.x;
            int y = item.y;
 
            for (int i = 0; i < dirs.length; i++) {
                String trial = cur;
                int dx = dirs[i][0];
                int dy = dirs[i][1];
 
                if (trial.charAt((y + dy) * nCols + x + dx) == 'B') {
 
                    if ((trial = push(x, y, dx, dy, trial)) != null) {
 
                        if (!history.contains(trial)) {
 
                            String newSol = sol + dirLabels[i][1];
 
                            if (isSolved(trial))
                                return newSol;
 
                            open.add(new Board(trial, newSol, x + dx, y + dy));
                            history.add(trial);
                        }
                    }
 
                // otherwise try changing position
                } else if ((trial = move(x, y, dx, dy, trial)) != null) {
 
                    if (!history.contains(trial)) {
                        String newSol = sol + dirLabels[i][0];
                        open.add(new Board(trial, newSol, x + dx, y + dy));
                        history.add(trial);
                    }
                }
            }
        }

        return "No solution";
    }

    String algDepth(int[][] dirs, char[][] dirLabels){
        class Board {
            String cur, sol;
            int x, y;
 
            Board(String s1, String s2, int px, int py) {
                cur = s1;
                sol = s2;
                x = px;
                y = py;
            }
        }

        Set<String> history = new HashSet<>();
        LinkedList<Board> open = new LinkedList<>();
 
        history.add(currBoard);
        open.add(new Board(currBoard, "", playerX, playerY));
 
        while (!open.isEmpty()) {
            Board item = open.poll();
            String cur = item.cur;
            String sol = item.sol;
            int x = item.x;
            int y = item.y;
 
            for (int i = 0; i < dirs.length; i++) {
                String trial = cur;
                int dx = dirs[i][0];
                int dy = dirs[i][1];
 
                // are we standing next to a box ?
                if (trial.charAt((y + dy) * nCols + x + dx) == 'B') {
 
                    // can we push it ?
                    if ((trial = push(x, y, dx, dy, trial)) != null) {
 
                        // or did we already try this one ?
                        if (!history.contains(trial)) {
 
                            String newSol = sol + dirLabels[i][1];
 
                            if (isSolved(trial))
                                return newSol;
 
                            open.addFirst(new Board(trial, newSol, x + dx, y + dy));
                            history.add(trial);
                        }
                    }
 
                // otherwise try changing position
                } else if ((trial = move(x, y, dx, dy, trial)) != null) {
 
                    if (!history.contains(trial)) {
                        String newSol = sol + dirLabels[i][0];
                        open.add(new Board(trial, newSol, x + dx, y + dy));
                        history.add(trial);
                    }
                }
            }
        }

        return "No solution";
    }

 
    public static void main(String[] args) throws IOException {
        new Sokoban("./" + args[0]).solve();
    }
}