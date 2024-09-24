package src.labs.infexf.agents;

import java.util.Set;

// SYSTEM IMPORTS
import edu.bu.labs.infexf.agents.SpecOpsAgent;
import edu.bu.labs.infexf.distance.DistanceMetric;
import edu.bu.labs.infexf.graph.Vertex;
import edu.bu.labs.infexf.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;


// JAVA PROJECT IMPORTS


public class InfilExfilAgent
    extends SpecOpsAgent
{

    public InfilExfilAgent(int playerNum)
    {
        super(playerNum);
    }

    // if you want to get attack-radius of an enemy, you can do so through the enemy unit's UnitView
    // Every unit is constructed from an xml schema for that unit's type.
    // We can lookup the "range" of the unit using the following line of code (assuming we know the id):
    //     int attackRadius = state.getUnit(enemyUnitID).getTemplateView().getRange();
    @Override
    public float getEdgeWeight(Vertex src,
                               Vertex dst,
                               StateView state)

    {
        System.out.print("source: ");
        System.out.println(src);

        System.out.print("destination: ");
        System.out.println(dst);

        Set<Integer> enemyPlayers = this.getOtherEnemyUnitIDs();


        //float baseWeight = 0;
        float dangerWeight = 0; 

        //trying a gaussian function...?
        float peakDistance = 2;
        float amplitude = 1000;
        float sigma = 3;

        for (int id : enemyPlayers) {
            int enemyX = state.getUnit(id).getXPosition();
            int enemyY = state.getUnit(id).getYPosition();

            Vertex enemyLoc = new Vertex(enemyX, enemyY);
            float distance = 0;

            if (dst != null) {
                distance = DistanceMetric.euclideanDistance(enemyLoc, dst);
            }
            else {
                distance = DistanceMetric.euclideanDistance(enemyLoc, src);
            }

            double flatDistance = Math.sqrt(distance);

            //System.out.println(distance);

            double weight = amplitude * Math.exp(-((flatDistance - peakDistance) * (flatDistance - peakDistance)) / (2 * sigma * sigma));

            dangerWeight += weight;
        }
        System.out.print("weighted edge: ");
        System.out.println(dangerWeight);
        System.out.println();
        return dangerWeight;
        //return 1f;
    }

    @Override
    public boolean shouldReplacePlan(StateView state)
    {
        //Set<Integer> enemyPlayers = this.getOtherEnemyUnitIDs();
        //Integer numEnemies = enemyPlayers.size();
        System.out.print("source: ");
        System.out.println(getEntryPointVertex());

        System.out.print("destination: ");
        System.out.println(getNextVertexToMoveTo());

        double danger = getEdgeWeight(getEntryPointVertex(), getNextVertexToMoveTo(), state);
        System.out.print("danger value: ");
        System.out.println(danger);

        if (danger > 900) {
            System.out.println("changing paths....");
            return true;
        }
        return false;
    }

}
