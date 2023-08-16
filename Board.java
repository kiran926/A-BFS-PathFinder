/**
 * Enum representing different cell types
 */
enum Cell {
    FREE(0),
    START(1),
    WALL(2),
    END(3);

    private final int value;
    private Cell(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }

    public static Cell getEnum(int value) {
        for (Cell c : Cell.values()) {
            if(value == c.getValue()) {
                return c;
            }
        }
        return Cell.WALL;
    }
}

/**
 * A class representing a board/grid consisting of Cells.
 */
public class Board {

    private Cell[][] board;
    private int xSize;
    private int ySize;

    private int[] start;
    private boolean startset = false;

    private int[] end;
    private boolean endset = false;

    public Board(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.board = new Cell[ySize][xSize];
        clearBoard();
    }

    /**
     * Fills the board with free cells and notes start and end nodes as not set.
     */
    public void clearBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = Cell.FREE;
            }
        }
        startset = false;
        endset = false;
    }

    /**
     * Places a specific tile type at a point on the board.
     * @param tileType - Type of tile/cell to place.
     * @param xPos - x-position on the board to place tile.
     * @param yPos - y-position on the board to place tile.
     */
    public void setTile(Cell tileType, int xPos, int yPos) {
        // If tile being set is START or END tile, update information automatically, and reset already placed start and end tiles
        if (tileType == Cell.START) {
            if (startset) {
                setTile(Cell.FREE, start[0], start[1]); // remove already placed start cell
            } 
            start = new int[]{xPos, yPos}; // place current startcell
            startset = true;
        } else if (tileType == Cell.END) {
            if (endset) {
                setTile(Cell.FREE, end[0], end[1]); // remove already placed end cell
            }
            end = new int[]{xPos, yPos}; // place current endcell
            endset = true;
        } else {
            // check if tile that is being overwritten is a start or end tile
            if (getTile(xPos, yPos) == Cell.START) {
                startset = false;
            } else if (getTile(xPos, yPos) == Cell.END) {
                endset = false;
            } 
        }

        board[yPos][xPos] = tileType; // place cell/tile to actual board
    }

    /**
     * Gets type of tile/cell at specific position.
     * @param xPos
     * @param yPos
     * @return
     */
    public Cell getTile(int xPos, int yPos) {
        return board[yPos][xPos];
    }

    /**
     * Returns the board.
     * @return
     */
    public Cell[][] getBoard() {
        return board;
    }

    /**
     * Returns the x-Size of the board.
     * @return
     */
    public int getXSize() {
        return xSize;
    }

    /**
     * Returns the y-Size of the board.
     * @return
     */
    public int getYSize() {
        return ySize;
    }

    /**
     * Returns whether the start node has been set.
     * @return
     */
    public boolean isStartSet() {
        return startset;
    }

    /**
     * Returns whether the end node has been set.
     * @return
     */
    public boolean isEndSet() {
        return endset;
    }

    /**
     * Returns the coordinates of the start node.
     * @return
     */
    public int[] getStart() {
        return start;
    }

    /**
     * Returns the coordinates of the end node.
     * @return
     */
    public int[] getEnd() {
        return end;
    }

    /**
     * Computes coordinates of the cells adjacent straight to cell with specified x and y coordinates.
     * @param xPos
     * @param yPos
     * @return
     */
    private int[][] getAdjacent(int xPos, int yPos) {

        int[][] adj = new int[4][2];
        adj[0] = new int[] {xPos - 1, yPos};
        adj[1] = new int[] {xPos + 1, yPos};
        adj[2] = new int[] {xPos, yPos - 1};
        adj[3] = new int[] {xPos, yPos + 1};

        return adj;
    }

    /**
     * Computes coordinates of the cells adjacent diagonally to cell with specified x and y coordinates.
     * @param xPos
     * @param yPos
     * @return
     */
    private int[][] getAdjacent_Diagonal(int xPos, int yPos) {

        int[][] adj = new int[4][2];

        adj[0] = new int[] {xPos - 1, yPos - 1};
        adj[1] = new int[] {xPos - 1, yPos + 1};
        adj[2] = new int[] {xPos + 1, yPos - 1};
        adj[3] = new int[] {xPos + 1, yPos + 1};

        return adj;
    }

    /**
     * Parses the board and computes weighted bidirectional graph.
     * @return - weighted bidirectional graph in adjacency matrix representation.
     */
    public int[][][][] getGraph(boolean diagonals) {
        
        /**
         * Edges are stored in adjacency matrix representation on the form:
         * new int[xSize][ySize][4][3]
         *                          ^ xCoordinate, yCoordinate and weight of edge
         *                       ^ number of adjacent nodes (8 if with diagonals, 4 if without)
         *                ^ yCoordinate of edge target node
         *         ^ xCoordinate of edge target node
         */

        // Create adjacency matrix
        int[][][][] adj = new int[xSize][ySize][diagonals ? 8 : 4][3];
        
        // Iterate for each cell
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {

                // Get a reference to the list of outgoing edges from this node
                int[][] edges = adj[x][y];

                // Fetch if current cell is wall
                boolean isWall = getTile(x, y) == Cell.WALL;

                // Grab adjacent nodes
                int[][] adjacent = getAdjacent(x, y);

                // Iterate for each adjacent node
                for (int i = 0; i < adjacent.length; i++) {
                    // Save coordinates of adjacent cell
                    int ax = adjacent[i][0];
                    int ay = adjacent[i][1];

                    // Put no connection in as default.
                    edges[i] = new int[] {ax, ay, 0};

                    // If current cell isnt wall and adjacent cell isnt wall, and in boundaries
                    if (!isWall && (0 <= ax && ax < xSize) && (0 <= ay && ay < ySize) && !(getTile(ax, ay) == Cell.WALL)) {
                        // Put in edge with weight 1
                        edges[i] = new int[] {ax, ay, 1};
                    }
                }

                // If diagonals are enabled do same as for regular, but also with diagonals
                if (diagonals) {
                    int[][] adjacentDiagonals = getAdjacent_Diagonal(x, y);

                    // Iterate for each adjacent node
                    for (int i = 0; i < adjacentDiagonals.length; i++) {
                        // Save coordinates of adjacent cell
                        int ax = adjacentDiagonals[i][0];
                        int ay = adjacentDiagonals[i][1];

                        // Put no connection in as default.
                        edges[i+4] = new int[] {ax, ay, 0};

                        // If current cell isnt wall and adjacent cell isnt wall, and in boundaries
                        if (!isWall && (0 <= ax && ax < xSize) && (0 <= ay && ay < ySize) && !(getTile(ax, ay) == Cell.WALL)) {
                            // Put in diagonal edge
                            edges[i+4] = new int[] {ax, ay, 1}; // Diagonal edges have weight 1, but possible to specify otherwise.
                        }
                    }
                }
            }
        }

        // Return adjacency matrix.
        return adj;
    }
}