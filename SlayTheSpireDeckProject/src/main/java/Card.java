/**
 * The Card class represents a Card object which has the attributes of a name and a score.
 */
public class Card {
    private String name;
    private double score;

    /**
     * Constructor for a card
     * @param score
     * @param name
     */
    public Card(double score, String name){
        this.score = score;
        this.name = name;
    }

    /**
     * Accessor method for a card's name attribute
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Accessor method for a card's score attribute
     * @return score
     */
    public double getScore() {
        return score;
    }

}
