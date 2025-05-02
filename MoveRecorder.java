import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MoveRecorder {
    private List<String> moves = new ArrayList<>();

    public void recordMove(int row, int col, String playerType, String color, char letter) {
        moves.add(row + "," + col + "," + playerType + "," + color + "," + letter);
    }

    public void recordMove(int row, int col, String color, char letter, boolean isAI) {
        String aiOrPlayer = isAI ? "AI" : "Player";
        moves.add(row + "," + col + "," + color + "," + letter + "," + aiOrPlayer);
    }

    public void saveToFile(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String move : moves) {
                writer.write(move);
                writer.newLine();
            }
        }
    }

    public List<String> loadFromFile(String filePath) throws IOException {
        List<String> loadedMoves = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                loadedMoves.add(line);
            }
        }
        return loadedMoves;
    }

    public void clear() {
        moves.clear();
    }
}
