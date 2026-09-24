import java.util.*;
import java.io.*;
import java.lang.Math;

/**
 *  The Deck class represents a deck of cards.
 *  Attributes: A id, a hash map of the frequency of each card, a 2D array of
 *  cards where the first array is the deck of valid cards and the second array is the
 *  deck of invalid cards.
 */
public class Deck {
    private int id;
    private HashMap<String, Integer> counts;
    private Card[][] cards;

    /**
     * The Deck constructor takes in a deck of valid and invalid cards as a parameter and assigns it to
     * the cards attribute of the class.
     *
     * The constructor generates a random 9-digit id for the deck using the generateId() method.
     * The constructor generates a hashmap for the frequency of cards in the
     * deck using the getCounts(Card[] c) method.
     * @param cards
     */
    public Deck(Card[][] cards){
        if(cards.length > 0){
            this.id = generateId();
            this.counts = getCounts(cards[0]);
            this.cards = cards;
        }
        else{
            this.id = generateId();
            this.counts = null;
            this.cards = null;
        }
    }

    /**
     *
     * @param cards
     * @return a hash map of the frequency of each card
     */
    public static HashMap<String, Integer> getCounts(Card[] cards){
        String tempName = "";
        HashMap<String, Integer> counts = new HashMap<>();
        for(int i = 0; i < cards.length; i++){
            if(cards[i] != null){
                tempName = cards[i].getName();
                if(counts.containsKey(tempName))
                    counts.replace(tempName, counts.get(tempName), counts.get(tempName)+1);
                else
                    counts.put(tempName, 1);
            }
        }
        return counts;
    }

    /**
     * The method uses the cards attribute to format a queue containing strings of the invalid cards
     * which are reformatted to the format of the cards in the text file.
     * @return queue with invalid cards
     */
    public Queue<String> invalidCards(){
        Queue<String> q = new LinkedList<>();
        Queue<Card>  invalidCards = new LinkedList<>();

        if(cards.length == 2){
            for(int i = 0; i < cards[1].length; i++){
                if(cards[1][i] != null)
                    invalidCards.add(cards[1][i]);
            }
            Card tempCard;
            while(!invalidCards.isEmpty()){
                tempCard = invalidCards.poll();
                q.add(tempCard.getName() + ":" + tempCard.getScore() + "\n");
            }
        }
        return q;
    }

    /**
     * The method uses the counts hashmap to generate a histogram of the deck.
     * Strings are used in queue to allow for the method to be used to write
     * contents to a file.
     *
     *
     * @return queue of strings used for the histogram
     */
    public Queue<String> generateHistogram(){
        Queue<String> q = new LinkedList<>();
        int max = 0;
        for(String key : counts.keySet()){
            if(max < counts.get(key))
                max = counts.get(key);
        }
        //System.out.println("Histogram");
        q.add("Histogram\n");
        //System.out.println("Legend");
        q.add("Legend\n");
        int l = 1;
        for(String key : counts.keySet()){
            //System.out.println(l + ": " + key);
            q.add(l + ": " + key + "\n");
            l++;
        }
        //System.out.print("--");
        q.add("--");
        for(int i = 0 ; i < counts.size()*5; i++){
            //System.out.print("-");
            q.add("-");
        }
        //System.out.println();
        q.add("\n");
        for(int i = max; i >= 1; i--){
            //System.out.print(i + "|");
            if(i > 99)
                q.add(i + "|");
            else if(i <= 99 && i > 9)
                q.add(" " + i + "|");
            else if(i <= 9)
                q.add("  " + i + "|");
            for(String key : counts.keySet()){
                if(counts.get(key) >= (i)){
                    //System.out.print("* ");
                    q.add("*    ");
                }
                else {
                    //System.out.print("- ");
                    q.add("-    ");
                }
            }
            q.add("\n");
            //System.out.println();
        }
        //System.out.print("--");
        q.add("--");
        for(int i = 0 ; i < counts.size()*5; i++){
            //System.out.print("-");
            q.add("-");
        }
        //System.out.println();
        q.add("\n");
        //System.out.print("  ");
        q.add("    ");
        for(int k = 0; k < counts.size(); k++){
            //System.out.print((k+1) + " ");
            if(k+1 > 99){
                q.add((k+1) + "  ");
            }
            else if(k+1 > 9){
                q.add((k+1) + "   ");
            }
            else{
                q.add((k+1) + "    ");
            }
        }
        return q;
    }



    /**
     * The method accesses and returns the cards attribute
     * @return cards
     */
    public Card[][] getCards() {
        return cards;
    }

    /**
     * The method accesses and returns the Deck id
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * The method sums the energy costs of all values of the valid cards and returns the sum.
     * @return sum
     */
    public double totalCost(){
        double sum = 0;
        if(cards.length > 0){
            Card[] validCards = cards[0];
            for(Card card : validCards){
                if(card != null && card.getScore() >= 0){
                    sum += card.getScore();
                }
            }
        }
        return sum;
    }

    /**
     * The method generates a random 9-digit number.
     * @return Deck id
     */
    public static int generateId(){
        Random random = new Random();
        return  random.nextInt(900000000) + 100000000;
    }


}
