package src.labs.infexf.agents;

import java.util.List;
import java.util.Set;

// SYSTEM IMPORTS
import edu.bu.labs.infexf.agents.SpecOpsAgent;
import edu.bu.labs.infexf.distance.DistanceMetric;
import edu.bu.labs.infexf.graph.Vertex;
import edu.bu.labs.infexf.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;


// JAVA PROJECT IMPORTS


public class InfilExfilAgent
    extends SpecOpsAgent
{

    public InfilExfilAgent(int playerNum)
    {
        super(playerNum);
    }

    private double dangerWeight(Vertex src,
                                Vertex  dst,
                                Vertex enemy)
    {
        //trying a gaussian function...?
        float amplitude = 1000;
        float decayFactor = 0.3f;

        float distance = DistanceMetric.euclideanDistance(enemy, dst);

        if (distance < 9) {
            return amplitude;
        }
        else {
            return (amplitude * Math.exp(-decayFactor * (distance - 9)));
        }
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
        //System.out.print("source: ");
        //System.out.println(src);

        //System.out.print("destination: ");
        //System.out.println(dst);

        float totalWeight = 0;
        float finalWeight;

        Set<Integer> enemyPlayers = this.getOtherEnemyUnitIDs();

        if (dst != null) {
            for (int id : enemyPlayers) {
                int enemyX = state.getUnit(id).getXPosition();
                int enemyY = state.getUnit(id).getYPosition();

                Vertex enemyLoc = new Vertex(enemyX, enemyY);

                
                //double weight = dangerWeight(src, dst, enemyLoc);
                //System.out.println(weight);
                totalWeight += dangerWeight(src, dst, enemyLoc);
            }
        }
        finalWeight = totalWeight /= enemyPlayers.size();
        //System.out.print("weighted edge: ");
        //System.out.println(dangerWeight);
        return finalWeight;
        //return 1f;
    }

    @Override
    public boolean shouldReplacePlan(StateView state)
    {
        Vertex myPosition = getEntryPointVertex();
        Set<Integer> enemyPlayers = this.getOtherEnemyUnitIDs();
        double currentRisk = 0.0;

        for (int id : enemyPlayers) {

            if (state.getUnit(id) == null) {
                continue;
            }
            int enemyX = state.getUnit(id).getXPosition();
            int enemyY = state.getUnit(id).getYPosition();

            Vertex enemyLoc = new Vertex(enemyX, enemyY);
            //int radius = enemy.getTemplateView().getRange() + 1;
            currentRisk += dangerWeight(myPosition, myPosition, enemyLoc);
        }

        currentRisk /= enemyPlayers.size();

        System.out.println(currentRisk);

        if (currentRisk >= 100) {
            System.out.println("changing paths");
            return true;
        }

        return false;
    }

}
