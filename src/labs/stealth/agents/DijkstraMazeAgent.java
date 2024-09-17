package src.labs.stealth.agents;

// SYSTEM IMPORTS
import edu.bu.labs.stealth.agents.MazeAgent;
import edu.bu.labs.stealth.graph.Vertex;
import edu.bu.labs.stealth.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.util.Direction;                           // Directions in Sepia


import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue; // heap in java
import java.util.Queue;
import java.util.Set;


// JAVA PROJECT IMPORTS


public class DijkstraMazeAgent
    extends MazeAgent
{

    public DijkstraMazeAgent(int playerNum)
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
        Queue<Path> q = new LinkedList<Path>();
        q.add(new Path(src));

        //initialize hashmaps for parents, add source
        Map<Vertex, Vertex> parents = new HashMap<Vertex, Vertex>();
        parents.put(src, null);

        System.out.println("finished initializing, starting BFS loop");

        //BFS loop
        while (!q.isEmpty()) {
            //poll the top vertex on the queue
            Path path = q.poll();
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
                            q.add(new Path(neighbor, 1f, path));
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
