import java.util.List;

public class SOSGameState {
    private final int blueScore;
    private final int redScore;
    private final boolean isGameEnded;
    private final boolean isBlueTurn;
    private final List<int[]> sosCoordinates;

    public SOSGameState(int blueScore, int redScore, boolean isGameEnded, 
                     boolean isBlueTurn, List<int[]> sosCoordinates) {
        this.blueScore = blueScore;
        this.redScore = redScore;
        this.isGameEnded = isGameEnded;
        this.isBlueTurn = isBlueTurn;
        this.sosCoordinates = sosCoordinates;
    }

    public int getBlueScore() {
        return blueScore;
    }

    public int getRedScore() {
        return redScore;
    }

    public boolean isGameEnded() {
        return isGameEnded;
    }

    public boolean isBlueTurn() {
        return isBlueTurn;
    }

    public List<int[]> getSosCoordinates() {
        return sosCoordinates;
    }

    public String getGameResult() {
        if (!isGameEnded) {
            return "Game in progress";
        }
        if (blueScore == redScore) {
            return "Draw";
        }
        return blueScore > redScore ? "Blue wins" : "Red wins";
    }
}