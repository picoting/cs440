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

        //initialize empty queue for BFS and add starting vertex
        Queue<Vertex> q = new LinkedList<Vertex>();
        q.add(src);

        //initialize hashmaps for parents, add source
        Map<Vertex, Set<Vertex>> parents = new HashMap<Vertex, Set<Vertex>>();
        parents.put(src, null);

        //intialize set of visited nodes, add source node
        Set<Vertex> visited = new HashSet<Vertex>();
        visited.add(src);

        //BFS loop
        while (!q.isEmpty()) {

        }

        return null;
    }

    @Override
    public boolean shouldReplacePlan(StateView state)
    {
        return false;
    }

}
