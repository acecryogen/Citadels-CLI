package citadels;

import java.util.*;

public class ScoreManager {
    private List<Player> players;
    private int round;

    public ScoreManager(List<Player> players, int round) {
        this.players = players;
        this.round = round;
    }

    public void showScores() {
        int max = 0;
        Player winner = null;
        Player firstToEight = findFirstToEight();

        for (Player p : players) {
            int score = calculatePlayerScore(p, firstToEight);
            displayScore(p, score);
            if (score > max) {
                max = score;
                winner = p;
            }
        }
        announceWinner(winner);
    }

    private Player findFirstToEight() {
        Player firstToEight = null;
        int firstToEightRound = Integer.MAX_VALUE;

        for (Player p : players) {
            if (p.getCity().size() >= 8 && round < firstToEightRound) {
                firstToEight = p;
                firstToEightRound = round;
            }
        }
        return firstToEight;
    }

    private int calculatePlayerScore(Player p, Player firstToEight) {
        int score = 0;
        Set<String> colors = new HashSet<>();
        boolean hasHaunted = false, hasSchool = false;

        // Calculate district points and collect colors
        for (DistrictCard d : p.getCity()) {
            score += calculateDistrictPoints(d);
            if (d.name.equals("Haunted City"))
                hasHaunted = true;
            if (d.name.equals("School of Magic"))
                hasSchool = true;
            if (!d.color.equals("purple"))
                colors.add(d.color);
        }

        // Add color bonus
        score += calculateColorBonus(colors, hasHaunted, hasSchool);

        // Add completion bonus
        score += calculateCompletionBonus(p, firstToEight);

        return score;
    }

    private int calculateDistrictPoints(DistrictCard d) {
        if (d.name.equals("Dragon Gate") || d.name.equals("University")) {
            return 8;
        }
        return d.cost;
    }

    private int calculateColorBonus(Set<String> colors, boolean hasHaunted, boolean hasSchool) {
        if (hasHaunted || hasSchool) {
            colors.addAll(Arrays.asList("yellow", "blue", "green", "red"));
        }

        boolean allColors = colors.containsAll(Arrays.asList("yellow", "blue", "green", "red"));
        return allColors ? 3 : 0;
    }

    private int calculateCompletionBonus(Player p, Player firstToEight) {
        if (p.getCity().size() >= 8) {
            return (p == firstToEight) ? 4 : 2;
        }
        return 0;
    }

    private void displayScore(Player p, int score) {
        System.out.println("Player " + p.getId() + " score: " + score + " (districts: " + p.getCity().size() + ")");
        displayPurpleBonuses(p);
    }

    private void displayPurpleBonuses(Player p) {
        for (DistrictCard d : p.getCity()) {
            if (d.name.equals("Dragon Gate") || d.name.equals("University")) {
                System.out.println("Player " + p.getId() + " gets 8 points for " + d.name + ".");
            }
        }
    }

    private void announceWinner(Player winner) {
        if (winner != null) {
            System.out.println("Winner: Player " + winner.getId() + (winner.isHuman() ? " (You!)" : ""));
        }
    }
}