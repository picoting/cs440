package src.pas.chess.moveorder;

// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;
import edu.bu.chess.game.move.MoveType;

import java.util.ArrayList;
import java.util.List;

public class CustomMoveOrderer extends Object {

    /**
     * Orders moves to prioritize beneficial moves, improving Alpha-Beta pruning efficiency.
     * @param nodes The nodes to order (these are children of a DFSTreeNode) that we are about to consider in the search.
     * @return The ordered nodes.
     */
    public static List<DFSTreeNode> order(List<DFSTreeNode> nodes) {
        List<DFSTreeNode> captureNodes = new ArrayList<>();
        List<DFSTreeNode> promotionNodes = new ArrayList<>();
        List<DFSTreeNode> enPassantNodes = new ArrayList<>();
        List<DFSTreeNode> castleNodes = new ArrayList<>();
        List<DFSTreeNode> movementNodes = new ArrayList<>();

        for (DFSTreeNode node : nodes) {
            if (node.getMove() != null) {
                MoveType moveType = node.getMove().getType();
                
                switch (moveType) {
                    case CAPTUREMOVE:
                        captureNodes.add(node);
                        break;
                    case PROMOTEPAWNMOVE:
                        promotionNodes.add(node);
                        break;
                    case ENPASSANTMOVE:
                        enPassantNodes.add(node);
                        break;
                    case CASTLEMOVE:
                        castleNodes.add(node);
                        break;
                    case MOVEMENTMOVE:
                    default:
                        movementNodes.add(node);
                        break;
                }
            } else {
                movementNodes.add(node);
            }
        }

        // Combine lists in the preferred order
        List<DFSTreeNode> orderedNodes = new ArrayList<>();
        orderedNodes.addAll(captureNodes);
        orderedNodes.addAll(promotionNodes);
        orderedNodes.addAll(enPassantNodes);
        orderedNodes.addAll(castleNodes);
        orderedNodes.addAll(movementNodes);

        return orderedNodes;
    }
}
