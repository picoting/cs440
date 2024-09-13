package src.labs.stealth.agents;

// SYSTEM IMPORTS
import edu.bu.labs.stealth.agents.MazeAgent;
import edu.bu.labs.stealth.graph.Vertex;
import edu.bu.labs.stealth.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;

import java.util.HashMap;
import java.util.HashSet;   // will need for dfs
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;     // will need for dfs
import java.util.Set;       // will need for dfs


// JAVA PROJECT IMPORTS


public class DFSMazeAgent
    extends MazeAgent
{

    public DFSMazeAgent(int playerNum)
    {
        super(playerNum);
    }

    @Override
    public Path search(Vertex src,
                       Vertex goal,
                       StateView state)
    {
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
        Stack<Path> q = new Stack<Path>();
        q.add(new Path(src));

        //initialize hashmaps for parents, add source
        Map<Vertex, Vertex> parents = new HashMap<Vertex, Vertex>();
        parents.put(src, null);

        System.out.println("finished initializing, starting BFS loop");

        //BFS loop
        while (!q.isEmpty()) {
            //poll the top vertex on the queue
            Path path = q.pop();
            Vertex current = path.getDestination();

            //check each cardinally adjacent node
            for (int[] dir: directions) {
                int newXCoor = current.getXCoordinate() + dir[0];
                int newYCoor = current.getYCoordinate() + dir[1];

                if (goal.equals(new Vertex(newXCoor, newYCoor))){
                    return path;
                }

                //ensure the neighboring vertex is on the grid
                if (state.inBounds(newXCoor, newYCoor)) {

                    //check that there is no resource (tree) there... i think that works....
                    if (!state.isResourceAt(newXCoor, newYCoor)){

                        //check that the node has not already been discovered
                        Vertex neighbor = new Vertex(newXCoor, newYCoor);
                        if (!parents.containsKey(neighbor)) {
                            
                            //add neighbor to queue, parents map, and visited set
                            q.push(new Path(neighbor, 1f, path));
                            parents.put(neighbor, current);
                            //visited.add(neighbor);
                            //System.out.println("discovered new neighbor");
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
