package dev.biogo;

public class Status {
    private int medals, ranking, trophies, xp;

    public Status(){}

    public Status(int medals, int ranking, int trophies, int xp) {
        this.medals = medals;
        this.ranking = ranking;
        this.trophies = trophies;
        this.xp = xp;
    }

    public int getMedals() {
        return medals;
    }

    public int getRanking() {
        return ranking;
    }

    public int getTrophies() {
        return trophies;
    }

    public int getXp() {
        return xp;
    }
}
