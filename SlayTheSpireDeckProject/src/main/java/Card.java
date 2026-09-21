public class Card {
    private String name;
    private double score;
    public Card(double score, String name){
        this.score = score;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }

}
