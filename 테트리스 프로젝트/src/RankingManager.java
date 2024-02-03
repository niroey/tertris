import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankingManager {
    private static final int MAX_RANKING_SIZE = 5;
    private List<RankingEntry> rankingEntries;

    public RankingManager() {
        rankingEntries = new ArrayList<>();
    }

    public void addEntry(String playerName, int score) {
        rankingEntries.add(new RankingEntry(playerName, score));
        sortRanking();
        trimRanking();
    }

    public List<RankingEntry> getRanking() {
        return Collections.unmodifiableList(rankingEntries);
    }

    private void sortRanking() {
        rankingEntries.sort(Collections.reverseOrder());
    }

    private void trimRanking() {
        while (rankingEntries.size() > MAX_RANKING_SIZE) {
            rankingEntries.remove(rankingEntries.size() - 1);
        }
    }

    // RankingEntry 클래스를 public으로 변경
    public static class RankingEntry implements Comparable<RankingEntry> {
        private String playerName;
        private int score;

        public RankingEntry(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }

        @Override
        public int compareTo(RankingEntry other) {
            return Integer.compare(score, other.score);
        }
    }
}