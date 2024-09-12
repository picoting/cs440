package src.labs.stealth.agents;

// SYSTEM IMPORTS
import edu.bu.labs.stealth.agents.MazeAgent;
import edu.bu.labs.stealth.graph.Vertex;
import edu.bu.labs.stealth.graph.Path;
import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.environment.model.state.State.StateView;

import java.util.HashMap;
import java.util.HashSet;       // will need for bfs
import java.util.Queue;         // will need for bfs
import java.util.LinkedList;    // will need for bfs
import java.util.Map;
import java.util.Set;           // will need for bfs


// JAVA PROJECT IMPORTS


public class BFSMazeAgent
    extends MazeAgent
{

    public BFSMazeAgent(int playerNum)
    {
        super(playerNum);
    }

    @Override
    public Path search(Vertex src,
                       Vertex goal,
                       StateView state)
    { 
        //get size of grid
        int rows = state.getXExtent();
        int cols = state.getYExtent();

        //initialize cardinally adjacent directions
        int[][] directions = { 
            {0, -1},
            {0, 1},
            {-1, 0},
            {1, 0},
            {-1, 1},
            {-1, -1},
            {1, -1},
            {1,1}
        };

        //initialize empty queue for BFS and add starting vertex
        Queue<Vertex> q = new LinkedList<Vertex>();
        q.add(src);

        //initialize hashmaps for parents, add source
        Map<Vertex, Vertex> parents = new HashMap<Vertex, Vertex>();
        parents.put(src, null);

        //intialize set of visited nodes, add source node
        Set<Vertex> visited = new HashSet<Vertex>();
        visited.add(src);

        System.out.println("finished initializing, starting BFS loop");

        //BFS loop
        while (!q.isEmpty()) {
            //poll the top vertex on the queue
            Vertex current = q.poll();

            //check if we have reached target node
            if (current.equals(goal)) {
                //initialize null path, start path at goal node
                Path path = null;
                Vertex curr = parents.get(goal);

                //backtrack through parents map
                while (curr != null) {
                    Vertex parent = parents.get(curr);
                    path = new Path(curr, 1, path);
                    curr = parent;
                }
                System.out.print("returning path:");
                System.out.println(path);
                return path;
            }

            //check each cardinally adjacent node
            for (int[] dir: directions) {
                int newXCoor = current.getXCoordinate() + dir[0];
                int newYCoor = current.getYCoordinate() + dir[1];

                //ensure the neighboring vertex is on the grid
                if (state.inBounds(newXCoor, newYCoor)) {

                    //check that there is no resource (tree) there... i think that works....
                    if (!state.isResourceAt(newXCoor, newYCoor)){

                        //check that the node has not already been discovered
                        Vertex neighbor = new Vertex(newXCoor, newYCoor);
                        if (!visited.contains(neighbor)) {
                            
                            //add neighbor to queue, parents map, and visited set
                            q.add(neighbor);
                            parents.put(neighbor, current);
                            visited.add(neighbor);
                            System.out.println("discovered new neighbor");
                        }
                    }
                }

            }
        }

        //return null if we never find the goal node
        System.out.println("goal node never reached, returning null");
        return null;
    }

    @Override
    public boolean shouldReplacePlan(StateView state)
    {
        return false;
    }

}
