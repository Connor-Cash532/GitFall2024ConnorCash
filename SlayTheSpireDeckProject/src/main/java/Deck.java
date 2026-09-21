import java.util.*;
import java.io.*;
import java.lang.Math;
public class Deck {
    private int id;
    private HashMap<String, Integer> counts;
    private Card[][] cards;
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

    public HashMap<String, Integer> getCounts(){
        return counts;
    }

    public Card[][] getCards() {
        return cards;
    }

    public int getId() {
        return id;
    }

    public double totalCost(){
        double sum = 0;
        if(cards.length > 0){
            Card[] validCards = cards[0];
            for(Card card : validCards){
                if(card != null && card.getScore() > 0){
                    sum += card.getScore();
                }
            }
        }
        return sum;
    }

    public static int generateId(){
        Random random = new Random();
        return  random.nextInt(900000000) + 100000000;
    }

    public static boolean iDExists(String id) throws FileNotFoundException {
        HashSet<String> ids = getFileText("Decks.txt");
        if(ids != null){
            return ids.contains(id);
        }
        return false;
    }

    public static HashSet<String> getFileText(String filePath) throws FileNotFoundException {
        File obj2 = new File(filePath);
        if(obj2.exists()){
            Scanner scan = new Scanner(obj2);
            String temp = "";
            HashSet<String> ids = new HashSet<>();
            int i = 0;
            while(scan.hasNextLine()){
                temp = scan.nextLine();
                if(!ids.contains(temp))
                    ids.add(temp);
            }
            return ids;
        }
        return null;
    }

}
