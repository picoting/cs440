package src.pas.chess.heuristics;

import edu.bu.chess.game.player.Player;
import edu.bu.chess.search.DFSTreeNode;
import edu.bu.chess.game.piece.Piece;
import edu.bu.chess.game.piece.PieceType;
import edu.bu.chess.game.move.Move;
import edu.bu.chess.game.move.MoveType;
import edu.bu.chess.game.move.PromotePawnMove;
import edu.bu.chess.utils.Coordinate;
import edu.cwru.sepia.util.Direction;

public class CustomHeuristics {

    /**
     * Get the max player from a node
     *
     * @param node
     * @return The max player
     */
    public static Player getMaxPlayer(DFSTreeNode node) {
        return node.getMaxPlayer();
    }

    /**
     * Get the min player from a node
     *
     * @param node
     * @return The min player
     */
    public static Player getMinPlayer(DFSTreeNode node) {
        // Determine the min player based on the game's current player and the max player
        return getMaxPlayer(node).equals(node.getGame().getCurrentPlayer()) ?
                node.getGame().getOtherPlayer() : node.getGame().getCurrentPlayer();
    }

    public static class OffensiveHeuristics {
        /**
         * Get the number of opponent pieces the max player is threatening.
         *
         * @param node
         * @return Number of pieces threatened by max player
         */
        public static int getNumberOfPiecesMaxPlayerIsThreatening(DFSTreeNode node) {
            int numPiecesMaxPlayerIsThreatening = 0;
            
            // Loop over each piece of the max player
            for (Piece piece : node.getGame().getBoard().getPieces(getMaxPlayer(node))) {
                // Count the capture moves for each piece
                numPiecesMaxPlayerIsThreatening += piece.getAllCaptureMoves(node.getGame()).size();
            }
            
            return numPiecesMaxPlayerIsThreatening;
        }
    }

    public static class DefensiveHeuristics {

        /**
         * Calculate the clamped piece value total around the max player's king.
         * Adds points for friendly pieces and subtracts for enemy pieces.
         *
         * @param node
         * @return Total piece value around the max player's king, clamped to be non-negative
         */
        public static int getClampedPieceValueTotalSurroundingMaxPlayersKing(DFSTreeNode node) {
            int maxPlayerKingSurroundingPiecesValueTotal = 0;

            // Find the king piece of the max player
            Piece kingPiece = node.getGame().getBoard().getPieces(getMaxPlayer(node), PieceType.KING).iterator().next();
            Coordinate kingPosition = node.getGame().getCurrentPosition(kingPiece);

            // Check all neighboring positions around the king
            for (Direction direction : Direction.values()) {
                Coordinate neighborPosition = kingPosition.getNeighbor(direction);

                if (node.getGame().getBoard().isInbounds(neighborPosition) && node.getGame().getBoard().isPositionOccupied(neighborPosition)) {
                    Piece piece = node.getGame().getBoard().getPieceAtPosition(neighborPosition);
                    int pieceValue = Piece.getPointValue(piece.getType());

                    if (piece != null) {
                        if (node.getGame().getBoard().getPieces(getMaxPlayer(node)).contains(piece)) {
                            // Friendly piece: add value
                            maxPlayerKingSurroundingPiecesValueTotal += pieceValue;
                        } else {
                            // Enemy piece: subtract value
                            maxPlayerKingSurroundingPiecesValueTotal -= pieceValue;
                        }
                    }
                }
            }

            // Clamp to zero to avoid negative values for king safety
            return Math.max(0, maxPlayerKingSurroundingPiecesValueTotal);
        }

        /**
         * Calculate the number of pieces threatening the max player.
         *
         * @param node
         * @return Number of pieces threatening the max player
         */
        public static int getNumberOfPiecesThreateningMaxPlayer(DFSTreeNode node) {
            int numPiecesThreateningMaxPlayer = 0;

            // Loop over each piece of the min player (opponent)
            for (Piece piece : node.getGame().getBoard().getPieces(getMinPlayer(node))) {
                // Count the capture moves for each opponent piece
                numPiecesThreateningMaxPlayer += piece.getAllCaptureMoves(node.getGame()).size();
            }

            return numPiecesThreateningMaxPlayer;
        }
    }

    /**
     * Calculate an offensive heuristic value for the max player, considering threatened pieces
     * and potential pawn promotions.
     *
     * @param node
     * @return Offensive heuristic value for max player
     */
    public static double getOffensiveMaxPlayerHeuristicValue(DFSTreeNode node) {
        // Points for pieces the max player is threatening
        int threatenedPiecesValue = OffensiveHeuristics.getNumberOfPiecesMaxPlayerIsThreatening(node);
        
        // Points for potential pawn promotion
        double promotionValue = 0.0;
        Move move = node.getMove();
        if (move.getType() == MoveType.PROMOTEPAWNMOVE) {
            PromotePawnMove promoteMove = (PromotePawnMove) move;
            promotionValue += Piece.getPointValue(promoteMove.getPromotedPieceType());
        }

        // Add a slight nonlinear weight to the heuristic to encourage stronger offensive positions
        double exponent = 1.15;
        return Math.pow(threatenedPiecesValue + promotionValue, exponent);
    }

    /**
     * Calculate the weighted piece values for the max player based on remaining pieces.
     *
     * @param node
     * @return Weighted piece value for the max player
     */
    public static double getMaxPlayerWeightedPieceValues(DFSTreeNode node) {
        double weightedValue = 0.0;

        // Iterate over key piece types to calculate their values based on number remaining
        for (PieceType pieceType : new PieceType[] { PieceType.PAWN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK, PieceType.QUEEN }) {
            weightedValue += Piece.getPointValue(pieceType) * node.getGame().getNumberOfAlivePieces(getMaxPlayer(node), pieceType);
        }

        return weightedValue;
    }

    /**
     * Calculate the weighted piece values for the min player based on remaining pieces.
     *
     * @param node
     * @return Weighted piece value for the min player
     */
    public static double getMinPlayerWeightedPieceValues(DFSTreeNode node) {
        double weightedValue = 0.0;

        // Iterate over key piece types to calculate their values based on number remaining
        for (PieceType pieceType : new PieceType[] { PieceType.PAWN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK, PieceType.QUEEN }) {
            weightedValue += Piece.getPointValue(pieceType) * node.getGame().getNumberOfAlivePieces(getMinPlayer(node), pieceType);
        }

        return weightedValue;
    }

    /**
     * Calculate a heuristic value for the max player using offensive and defensive heuristics.
     *
     * @param node
     * @return Heuristic value for max player
     */
    public static double getMaxPlayerHeuristicValue(DFSTreeNode node) {
        // Offensive heuristic: based on pieces the max player is threatening
        double offensiveValue = getOffensiveMaxPlayerHeuristicValue(node);
        
        // Defensive heuristic: based on piece value around the max player's king
        int defensiveValue = DefensiveHeuristics.getClampedPieceValueTotalSurroundingMaxPlayersKing(node);

        // Combining offensive and defensive heuristics
        return offensiveValue + defensiveValue;
    }
}
