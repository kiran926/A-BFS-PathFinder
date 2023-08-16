import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.ArrayList;

/**
 * Class containing pathfinding algorithms, and algorithms for recreating the shortest path.
 */
public class Algorithm {
    
    /**
      * Computes length of shortest path from specified start node to specified end node using A* pathfinding.
      * Returns a matrix representing the final node and depth, as well as parent of each color,
      * which allows for reconstruction of shortest path to end node .
      * @param graph - Graph in adjacency matrix form
      * @param start - Start node
      * @param end   - End node
      * @param vizualization - Reference to linked list, to which to save each computation step, for future vizualization
      * @param saveVizualization - Boolean indicating whether to supply computation steps to vizualization linked list.
      * @return
      */
    public static int[][][] A_Star(int[][][][] graph, int[] start, int[] end, LinkedList<ArrayList<ArrayList<int[]>>> vizualization, boolean saveVizualization) {
        
        // Define matrix to store node information
        int[][][] nodes = new int[graph.length][graph[0].length][8];
        //                                                      ^ {openness, f, g, h, parentX, parentY, thisX, thisY}
        //                                                         0 = open, 1 = closed, 2 = unspecified

        // Fill out matrix with values
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                nodes[i][j] = new int[]{2, Integer.MAX_VALUE, Integer.MAX_VALUE, Math.abs(i - end[0]) + Math.abs(j - end[1]), -1, -1, i, j}; // Use Manhattan distance for g values
            }
        }

        // Create comparator for priorityqueues
        Comparator<int[]> qc = new Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                return a[1] - b[1]; // Compare using calculated f values for nodes
            }
        };

        // Create priorityqueues for open and closed nodes
        PriorityQueue<int[]> open = new PriorityQueue<>(qc);
        PriorityQueue<int[]> closed = new PriorityQueue<>(qc);

        // Enqueue start node
        nodes[start[0]][start[1]] = new int[]{0, 0, 0, nodes[start[0]][start[1]][3], -1, -1, start[0], start[1]};
        open.add(nodes[start[0]][start[1]]);

        // Iterate while there are still open nodes
        while (!open.isEmpty()) {

            // Remove first node in queue
            int[] cnode = open.poll();

            // Check if cnode is end/target node
            if (cnode[6] == end[0] && cnode[7] == end[1]) {
                break;
            }

            // Get adjacent nodes
            int[][] adjacent = graph[cnode[6]][cnode[7]];

            // Iterate over each adjacent node
            for (int[] n : adjacent) {

                // If there is an actual edge to adjacent node
                if (n[2] == 0) {continue;}

                // Get information on adjacent node
                int list = nodes[n[0]][n[1]][0];
                int pref = nodes[n[0]][n[1]][1];
                int newg = nodes[cnode[6]][cnode[7]][2] + 1;
                int newf = nodes[n[0]][n[1]][3] + newg;

                // If newf isnt better than the adjacent node's previous f, dont do anything
                if (newf >= pref) {continue;}

                // Compute new node spec
                int[] newspec = new int[]{0, newf, newg, nodes[n[0]][n[1]][3], cnode[6], cnode[7], n[0], n[1]};


                // Act depending on if the node is in the open or closed list
                if (list == 2) {
                    // Update node with new f, g, and parentX, parentY values based on current node
                    nodes[n[0]][n[1]] = newspec;
                    // Add node to list of open nodes
                    open.add(nodes[n[0]][n[1]]);

                } else if (list == 1) {

                    if (saveVizualization) {
                        // Remove node from list of closed nodes
                        closed.remove(nodes[n[0]][n[1]]);
                    }
                    // Update node with new f, g, and parentX, parentY values based on current node
                    nodes[n[0]][n[1]] = newspec;
                    // Add node to list of open nodes
                    open.add(nodes[n[0]][n[1]]);

                } else if (list == 0) {
                    // Remove node from queue
                    open.remove(nodes[n[0]][n[1]]);
                    // Update node with new f, g, and parentX, parentY values based on current node
                    nodes[n[0]][n[1]] = newspec;
                    // Add node back to queue
                    open.add(nodes[n[0]][n[1]]);
                }
            }

            // Remove self from open queue
            open.remove(cnode);

            // Update node as closed
            nodes[cnode[6]][cnode[7]][0] = 1;

            if (saveVizualization) {
                // Add node to closed nodes
                closed.add(cnode);

                // Add copy of queues to visualization list
                ArrayList<int[]> o = new ArrayList<>();
                for (int[] n : open) {
                    o.add(new int[] {n[6], n[7]});
                }

                ArrayList<int[]> c = new ArrayList<>();
                for (int[] n : closed) {
                    c.add(new int[] {n[6], n[7]});
                }

                ArrayList<ArrayList<int[]>> openclosed = new ArrayList<>();
                openclosed.add(o);
                openclosed.add(c);

                vizualization.addLast(openclosed);
            }
            
        }

        return nodes;
    }

    public static ArrayList<int[]> A_Star_path(int[][][] result, int[] start, int[] end) {

        // Prepare ArrayList to hold path
        ArrayList<int[]> path = new ArrayList<>();
        
        // Set initial node to parent of end node
        int[] node = new int[] {result[end[0]][end[1]][4], result[end[0]][end[1]][5]};

        // If end node has no parent, no path has been found
        if (node[0] == -1 && node[1] == -1) {return path;}

        while (node[0] != start[0] || node[1] != start[1]) {
            // Add node to path
            path.add(node);

            // Set node to parent of current node
            node = new int[] {result[node[0]][node[1]][4], result[node[0]][node[1]][5]};
        }

        // Return path
        return path;
    }

      /**
      * Computes length of shortest path from specified start node to specified end node using Breadth First Search.
      * Returns a matrix representing the final node and depth, as well as parent of each color,
      * which allows for reconstruction of shortest path to end node.
      * @param graph - Graph in adjacency matrix form
      * @param start - Start node
      * @param end   - End node
      * @param vizualization - Reference to linked list, to which to save each computation step, for future vizualization
      * @param saveVizualization - Boolean indicating whether to supply computation steps to vizualization linked list.
      * @return
      */
    public static int[][][] BFS(int[][][][] graph, int[] start, int[] end, LinkedList<ArrayList<ArrayList<int[]>>> vizualization, boolean saveVizualization) {
        // Define matrix to store node information
        int[][][] nodes = new int[graph.length][graph[0].length][4];
        //                                                       ^ {color, depth, parentX, parentY}
        //                                                          colors: 0=white, 1=gray, 2=black

        // Fill out matrix with empty values
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                nodes[i][j] = new int[]{0, Integer.MAX_VALUE, -1, -1}; // Standard values for all nodes.
            }
        }

        // Define queue to hold currently reviewed nodes
        ArrayDeque<int[]> q = new ArrayDeque<int[]>();

        // Enqueue start node
        nodes[start[0]][start[1]] = new int[]{1, 0, -1, -1};
        q.addLast(start);

        // Define ArrayList to hold black nodes
        ArrayList<int[]> blacknodes = new ArrayList<>();

        // Define ArrayList to hold gray nodes
        ArrayList<int[]> graynodes = new ArrayList<>();

        // While queue not empty
        while (q.size() > 0) {

            // Remove first node in queue
            int[] cnode = q.removeFirst();

            // Fetch adjacent nodes using adjacency map
            int[][] adjacent = graph[cnode[0]][cnode[1]];

            // Iterate over each adjacent node (there exists an edge)
            for (int[] n : adjacent) {

                // Only consider node, if there is an edge to it, and its color is white.
                if ((n[2] != 0) && (nodes[n[0]][n[1]][0] == 0)) {
                    // Enqueue new node
                    q.addLast(new int[]{n[0], n[1]});

                    // Set color, depth and parent
                    nodes[n[0]][n[1]] = new int[] {1, nodes[cnode[0]][cnode[1]][1] + 1, cnode[0], cnode[1]}; // Set color to gray

                    if (saveVizualization) {
                        // Add to gray nodes
                        graynodes.add(new int[]{n[0], n[1]});
                    }
                }  
            }

            // Set color to black
            nodes[cnode[0]][cnode[1]][0] = 2;

            if (saveVizualization) {
                // Remove from gray nodes and add to black nodes
                graynodes.remove(cnode); // Might cause problems, because references
                blacknodes.add(cnode);

                // Copy arraylists
                ArrayList<int[]> gnc = new ArrayList<>();
                for (int[] n : graynodes) {
                    gnc.add(n.clone());
                }

                // Copy arraylists
                ArrayList<int[]> bnc = new ArrayList<>();
                for (int[] n : blacknodes) {
                    bnc.add(n.clone());
                }

                // Add to linkedlist
                ArrayList<ArrayList<int[]>> bgn = new ArrayList<>();
                bgn.add(gnc);
                bgn.add(bnc);
                vizualization.addLast(bgn);
            }

            // Check if end node has been reached
            int[] endparent = new int[]{nodes[end[0]][end[1]][2], nodes[end[0]][end[1]][3]};
            if (endparent[0] != -1 && endparent[1] != -1) {
                return nodes;
            }
        }

        // Returns nodes matrix
        return nodes;
    }

    public static ArrayList<int[]> BFS_path(int[][][] result, int[] start, int[] end) {

        // Set initial node to parent of end node
        int[] node = new int[] {result[end[0]][end[1]][2], result[end[0]][end[1]][3]};

        // Prepare ArrayList to hold path
        ArrayList<int[]> path = new ArrayList<>();

        while (node[0] != start[0] || node[1] != start[1]) {
            // Add node to path
            path.add(node);

            // Set node to parent of current node
            node = new int[] {result[node[0]][node[1]][2], result[node[0]][node[1]][3]};
        }

        // Return path
        return path;
    }

    /**
     * OTHER ALGORITHMS GO HERE
     */

}