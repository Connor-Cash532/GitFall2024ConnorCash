import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Queue;
import java.util.*;

/**
 * The GUI class constructs a GUI that allows the user to pick a text file with Slay the Spire cards and
 * pick a folder to save a report of the Slay the Spire cards
 *
 * The GUI class has the attributes of a filePickerButton, runReportButton, selectSaveButton
 */
public class GUI extends JFrame implements ActionListener {
    JButton filePickerButton, runReportButton, selectSaveButton;
    JLabel label, label2;
    static File inputPath;
    static File outputPath;

    /**
     * The GUI constructor sets the text of each button and disables the use of the report button
     * and save button. The constructor also adds action listeners than handle user input.
     * The buttons and two panels containing labels with text for the user are added to the Frame.
     */
    public GUI(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        filePickerButton = new JButton("Select File");
        filePickerButton.addActionListener(this);

        runReportButton = new JButton("Run Report");
        runReportButton.addActionListener(this);
        runReportButton.setEnabled(false);

        JPanel labels = new JPanel(new BorderLayout());
        label = new JLabel("<html><div style='width:400px;text-align:center'> " +
                "Slay The Spire Deck Reporter" +
                "</div></html>", SwingConstants.CENTER);

        String instructions = "The following program produces a report on a text file containing" +
                "Slay The Spire cards. The user inputs a text file containing Slay The Spire cards using" +
                "the Select File button. Then the user selects a folder that will contain the report using the" +
                "Select Report Directory button. Then the user can run the report on the deck which saves" +
                "the report in the chosen directory";

        label2 = new JLabel("<html><div style='width:400px;text-align:center'>"
                + instructions +
                "</div></html>", SwingConstants.CENTER);

        labels.setLayout(new GridLayout(2, 1));
        labels.add(label);
        labels.add(label2);

        label.setFont(label.getFont().deriveFont(36.0f));

        selectSaveButton = new JButton("Select Report Directory");
        selectSaveButton.addActionListener(this);
        selectSaveButton.setEnabled(false);

        JPanel buttons = new JPanel(new FlowLayout());

        buttons.add(filePickerButton);
        buttons.add(runReportButton);
        buttons.add(selectSaveButton);

        this.setLayout(new GridLayout(3, 1));
        this.add(labels, BorderLayout.CENTER);
        this.add(buttons, BorderLayout.CENTER);


        this.pack();
        this.setVisible(true);
    }

    /**
     * The action performed method implements the action listener interface.
     *
     * If the ActionEvent is the filePickerButton then the JFileChooser object and FileNameExtensionFilter
     * is used to allow the user to select a file. Once the user chooses a file
     * the selected file is saved to the static inputFile variable and the button that allows the user
     * to set a save directory is enabled.
     *
     * If the ActionEvent is the runReportButton then the inputFile is read using the getLines method, valid
     * and invalid cards are saved in a 2D array of cards, a Deck object is instantiated with the cards, and
     * then a report for the deck is generated.
     *
     * If the ActionEvent is the selectSaveButton then a JFileChooser object allows the user to select a directory
     * which is saved to the outputPath variable. When the user selects a file, the report button is enabled.
     *
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource()==filePickerButton) {

            JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("txt files","txt");
            fc.setFileFilter(filter);
            fc.setCurrentDirectory(new File("."));
            int response = fc.showOpenDialog(null);

            if(response == JFileChooser.APPROVE_OPTION) {
                File file = new File(fc.getSelectedFile().getAbsolutePath());
                inputPath = file;
                System.out.println(file);
                selectSaveButton.setEnabled(true);
            }
        }
        if(e.getSource()==runReportButton) {
            System.out.println("hhh");
            String[] lines;
            try {
                lines = getFileText(inputPath.getAbsolutePath());
                Card[][] cards = getCards(lines);
                Deck d1 = new Deck(cards);
                generateReport(d1);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        if(e.getSource() == selectSaveButton){
            JFileChooser fc2 = new JFileChooser();
            fc2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc2.setCurrentDirectory(new File("."));
            int response = fc2.showSaveDialog(null);

            if(response == JFileChooser.APPROVE_OPTION) {
                outputPath = new File(fc2.getSelectedFile().getAbsolutePath());
                System.out.println(fc2.getSelectedFile().getAbsolutePath());
                runReportButton.setEnabled(true);
            }
        }
    }

    /**
     * The generateReport method takes in a deck of cards a generates a report containing the total
     * cost of the valid cards in the deck, a histogram of the deck, and a list of the invalid cards in the
     * deck.
     *
     * The method first counts the number of non-null valid cards in the deck and non-null invalid cards
     * in the deck.
     *
     * The method then checks if there are more than 1000 cards in the deck, the deck is null, or there are
     * more than 10 improperly formatted cards where a proper format is "cardName:cardCost" and an improper format
     * is anything else. If these checks are all false then a string is built by using the deck attributes:
     * getId, totalCost, generateHistogram, and invalidCards. Then the string is written to a document using
     * the Itext maven library. Once the document is closed invalidFormatCount is set to zero to prevent it
     * from accumulating after a user generates multiple reports.
     *
     *
     *
     * @param d
     * @throws FileNotFoundException
     */
    public static void generateReport(Deck d) throws FileNotFoundException {
        String s = "";

        HashSet<Card> uniqCard = new HashSet<>();
        for(int i = 0; i < d.getCards()[0].length; i++){
            if(!uniqCard.contains(d.getCards()[0][i]))
                uniqCard.add(d.getCards()[0][i]);
        }
        int numUniqCards = uniqCard.size();
        int numInvalidCards = 0;
        for(int i = 0; i < d.getCards()[1].length; i++){
            if(d.getCards()[1][i] != null)
                numInvalidCards++;
        }
        int numValidCards = 0;
        for(int i = 0; i < d.getCards()[0].length; i++){
            if(d.getCards()[0][i] != null)
                numValidCards++;
        }

        for(Card c : d.getCards()[0]) {
            if(c != null)
                System.out.println(c.getName());
        }

        // Checks for if a deck of cards should generate a void report
        if(d.getCards() != null && invalidFormatCount <= 10 && d.getCards()[0].length <= 1000
                && (numInvalidCards <= 10 || (numValidCards + numInvalidCards > 1000)) && numValidCards != 0){
            int id  = d.getId();
            double cost = d.totalCost();
            Queue<String> q = d.generateHistogram();
            Queue<String> invalidCards = d.invalidCards();

            try {
                s += "Deck ID: " + id + "\n";
                s += "Total Cost of Deck: " + cost + " energy\n\n";
                while(!q.isEmpty()){
                    s += q.poll();
                }
                s += "\n\nInvalid Cards:\n";
                while(!invalidCards.isEmpty()){
                    s += invalidCards.poll() + "\n";
                }
                while(!invalidFormat.isEmpty()){
                    s += invalidFormat.poll() + "\n";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(s);


            // The two lines below set the page size so that the document does not wrap around the histogram
            float width = 55 * numUniqCards;
            Rectangle pageSize = new Rectangle(width, 800f);

            Document doc  = new Document(pageSize);
            File outputFile = new File(outputPath, "SlaySpire_" + id + ".pdf");

            FileOutputStream stream = new FileOutputStream(outputFile);
            PdfWriter.getInstance(doc, stream);

            // Courier font is used because it is monospace
            Font f1 = new Font(Font.COURIER);

            // Writing the string to the document
            doc.open();
            Chunk c  = new Chunk();
            c.setFont(f1);
            c.append(s);
            c.setCharacterSpacing(2);
            Paragraph p = new Paragraph();
            p.add(c);
            doc.add(p);
            doc.close();

            // Reset the invalidCount for subsequent reports
            invalidFormatCount = 0;
            invalidFormat = new LinkedList<>();

        }
        else{
            generateVoidReport(d);
        }
    }

    /**
     * The referenceMap builds a HashMap of the valid Slay The Spire costs where the Key
     * is the name of the Card and the Value is the cost of the card.
     * @return
     */
    public static HashMap<String, String> referenceMap(){
        String name = "";
        String tempCost = "";
        HashMap<String, String> validCardsAndCosts = new HashMap<>();
        for(int i = 0; i < VALID_CARDS.length; i++){
            name = VALID_CARDS[i].substring(0, VALID_CARDS[i].indexOf(58));
            tempCost = VALID_CARDS[i].substring(VALID_CARDS[i].indexOf(58) + 1);
            validCardsAndCosts.put(name, tempCost);
        }
        return validCardsAndCosts;
    }

    /**
     * getCards(String[] textFile) takes in each line of the text file parses it for a card name
     * and its cost. The method checks if the card is a valid Slay The Spire card by using
     * the referenceMap() method to check if the name exists. If the card is valid then it is added
     * to a validCards array.
     *
     * @param textFile
     * @return deck
     */
    public static Card[][] getCards(String[] textFile){
        HashMap<String, String> validCardsAndCosts = referenceMap();
        double cost = 0;
        String name = "";
        String tempCost = "";
        Card[] validCards = new Card[textFile.length];
        Card[] invalidCards = new Card[textFile.length];
        for(int i = 0; i < textFile.length; i++){
            if(textFile[i].contains(":")){
                name = textFile[i].substring(0, textFile[i].indexOf(58));
                tempCost = textFile[i].substring(textFile[i].indexOf(58) + 1);
                if(isValidCost(tempCost)){
                    cost = Double.parseDouble(tempCost);
                    if(validCardsAndCosts.containsKey(name) && validCardsAndCosts.get(name).equals(tempCost)){
                        validCards[i] = new Card(cost, name);
                    }
                    else
                        invalidCards[i] = new Card(cost, name);
                }
                else{
                    invalidFormat.add(textFile[i]);
                    invalidFormatCount++;
                }
            }
            else{
                invalidFormat.add(textFile[i]);
                invalidFormatCount++;
            }

        }
        Card[][] decks = new Card[2][textFile.length];
        decks[0] = validCards;
        decks[1] = invalidCards;
        return decks;
    }

    /**
     * The getFileText method adds every line of a text file to an array of Strings.
     *
     * @param filePath
     * @return String[]
     * @throws FileNotFoundException
     */
    public static String[] getFileText(String filePath) throws FileNotFoundException {
        try{
            File obj2 = new File(filePath);
            Scanner scan2 = new Scanner(obj2);
            int j = 0;
            while(scan2.hasNextLine()){
                scan2.nextLine();
                j++;
            }
            File obj = new File(filePath);
            Scanner scan = new Scanner(obj);
            String[] lines = new String[j];
            int i = 0;
            while(scan.hasNextLine()){
                lines[i] = scan.nextLine();
                i++;
            }
            return lines;
        } catch (FileNotFoundException fne){
            System.out.println("File Does Not Exist");
            throw fne;
        }

    }

    /**
     * The method returns true if the cost is a Double or Integer, and returns false if a NumberFormatException
     * is raised which means the parameter is not a number.
     * @param line
     * @return
     */
    public static boolean isValidCost(String line){
        try {
            Double.parseDouble(line);
            Integer.parseInt(line);
            return true;
        }
        catch (NumberFormatException nfe){
            return false;
        }
    }

    /**
     * The function generates a void report by writing VOID to a document and saving the document
     * at the output path.
     * @param d
     * @throws FileNotFoundException
     */
    public static void generateVoidReport(Deck d) throws FileNotFoundException {
        String fileName = "SlaySpire_" + Integer.toString(d.getId()) + "(VOID).pdf";
        try{
            Document doc  = new Document();
            File outputFile = new File(outputPath, fileName);
            FileOutputStream stream = new FileOutputStream(outputFile);

            PdfWriter.getInstance(doc, stream);
            doc.open();
            doc.add(new Paragraph("VOID"));
            doc.close();
            invalidFormatCount = 0;
        } catch (FileNotFoundException fne){
            System.out.println("Error");
            throw fne;
        }
    }

    /*
        invalidFormatCount counts the number of times a line in the file does not follow the CardName:CardCost format
     */
    private static int invalidFormatCount = 0;
    /*
        invalidFormat adds strings in the file which do not follow the CardName:CardCost format.
     */
    private static Queue<String> invalidFormat = new LinkedList<>();

    /* VALID_CARDS represents the valid Slay The Spire cards which were obtained using the Slay the Spire
        reference. Both the name and costs are represented in the array and also upgraded versions of the
        cards. An upgraded card is shown by the + symbol at the end of the name, e.g. Entrench+:1
     */
    static final String[] VALID_CARDS = {
            "Bash:2", "Defend:1", "Strike:1", "Anger:0", "Armaments:1", "Body Slam+:0", "Body Slam:1",
            "Clash:0", "Cleave:1", "Clothesline:2", "Flex:0", "Havoc+:0", "Havoc:1", "Headbutt:1",
            "Heavy Blade:2", "Iron Wave:1", "Perfected Strike:2", "Pommel Strike:1", "Shrug It Off:1",
            "Sword Boomerang:1", "Thunderclap:1", "True Grit:1", "Twin Strike:1", "Warcry:0",
            "Wild Strike:1", "Battle Trance:0", "Blood for Blood+:3", "Blood for Blood:4",
            "Bloodletting:0", "Burning Pact:1", "Carnage:2", "Combust:1", "Dark Embrace+:1",
            "Dark Embrace:2", "Disarm:1", "Dropkick:1", "Dual Wield:1", "Entrench+:1", "Entrench:2",
            "Evolve:1", "Feel No Pain:1", "Fire Breathing:1", "Flame Barrier:2", "Ghostly Armor:1",
            "Hemokinesis:1", "Infernal Blade+:0", "Infernal Blade:1", "Inflame:1", "Intimidate:0",
            "Metallicize:1", "Power Through:1", "Pummel:1", "Rage:0", "Rampage:1", "Reckless Charge:0",
            "Rupture:1", "Searing Blow:2", "Second Wind:1", "Seeing Red+:0", "Seeing Red:1", "Sentinel:1",
            "Sever Soul:2", "Shockwave:2", "Spot Weakness:1", "Uppercut:2", "Barricade+:2", "Barricade:3",
            "Berserk:0", "Bludgeon:3", "Brutality:0", "Corruption+:2", "Corruption:3", "Demon Form:3",
            "Double Tap:1", "Exhume+:0", "Exhume:1", "Feed:1", "Fiend Fire:2", "Immolate:2", "Impervious:2",
            "Juggernaut:2", "Limit Break:1", "Offering:0", "Reaper:2", "Defend:1", "Neutralize:0", "Strike:1",
            "Survivor:1", "Acrobatics:1", "Backflip:1", "Bane:1", "Blade Dance:1", "Cloak and Dagger:1",
            "Dagger Spray:1", "Dagger Throw:1", "Deadly Poison:1", "Deflect:0", "Dodge and Roll:1",
            "Flying Knee:1", "Outmaneuver:1", "Piercing Wail:1", "Poisoned Stab:1", "Prepared:0",
            "Quick Slash:1", "Slice:0", "Sneaky Strike:2", "Sucker Punch:1", "Accuracy:1", "All-Out Attack:1",
            "Backstab:0", "Blur:1", "Bouncing Flask:2", "Calculated Gamble:0", "Caltrops:1", "Catalyst:1",
            "Choke:2", "Concentrate:0", "Crippling Cloud:2", "Dash:2", "Distraction+:0", "Distraction:1",
            "Endless Agony:0", "Escape Plan:0", "Eviscerate:3", "Expertise:1", "Finisher:1", "Flechettes:1",
            "Footwork:1", "Heel Hook:1", "Infinite Blades:1", "Leg Sweep:2", "Masterful Stab:0",
            "Noxious Fumes:1", "Predator:2", "Riddle with Holes:2", "Setup+:0", "Setup:1", "Terror+:0",
            "Terror:1", "Well-Laid Plans:1", "A Thousand Cuts:2", "Adrenaline:0", "After Image:1",
            "Alchemize+:0", "Alchemize:1", "Bullet Time+:2", "Bullet Time:3", "Burst:1", "Corpse Explosion:2",
            "Die Die Die:1", "Envenom+:1", "Envenom:2", "Glass Knife:1", "Grand Finale:0", "Nightmare+:2",
            "Nightmare:3", "Phantasmal Killer+:0", "Phantasmal Killer:1", "Storm of Steel:1",
            "Tools of the Trade+:0", "Tools of the Trade:1", "Unload:1", "Wraith Form:3", "Defend:1",
            "Dualcast+:0", "Dualcast:1", "Strike:1", "Zap+:0", "Zap:1", "Ball Lightning:1", "Barrage:1",
            "Beam Cell:0", "Charge Battery:1", "Claw:0", "Cold Snap:1", "Compile Driver:1", "Coolheaded:1",
            "Go for the Eyes:0", "Hologram:1", "Leap:1", "Rebound:1", "Recursion+:0", "Recursion:1",
            "Stack:1", "Steam Barrier:0", "Streamline:2", "Sweeping Beam:1", "TURBO:0", "Aggregate:1",
            "Auto-Shields:1", "Blizzard:1", "Boot Sequence:0", "Bullseye:1", "Capacitor:1", "Chaos:1",
            "Chill:0", "Consume:2", "Darkness:1", "Defragment:1", "Doom and Gloom:2", "Double Energy+:0",
            "Double Energy:1", "Equilibrium:2", "FTL:0", "Force Field:4", "Fusion+:1", "Fusion:2",
            "Genetic Algorithm:1", "Glacier:2", "Heatsinks:1", "Hello World:1", "Loop:1", "Melter:1",
            "Overclock:0", "Recycle+:0", "Recycle:1", "Reprogram:1", "Rip and Tear:1", "Scrape:1",
            "Self Repair:1", "Skim:1", "Static Discharge:1", "Storm:1", "Sunder:3", "White Noise+:0",
            "White Noise:1", "All for One:2", "Amplify:1", "Biased Cognition:1", "Buffer:2", "Core Surge:1",
            "Creative AI+:2", "Creative AI:3", "Echo Form:3", "Electrodynamics:2", "Fission:0", "Hyperbeam:2",
            "Machine Learning:1", "Meteor Strike:5", "Rainbow:2", "Reboot:0", "Seek:0", "Thunder Strike:3",
            "Defend:1", "Eruption+:1", "Eruption:2", "Strike:1", "Vigilance:2", "Bowling Bash:1",
            "Consecrate:0", "Crescendo+:0", "Crescendo:1", "Crush Joints:1", "Cut Through Fate:1",
            "Empty Body:1", "Empty Fist:1", "Evaluate:1", "Flurry of Blows:0", "Flying Sleeves:1",
            "Follow-Up:1", "Halt:0", "Just Lucky:0", "Pressure Points:1", "Prostrate:0", "Protect:2",
            "Sash Whip:1", "Third Eye:1", "Tranquility+:0", "Tranquility:1", "Battle Hymn:1",
            "Carve Reality:1", "Conclude:1", "Deceive Reality:1", "Empty Mind:1", "Fasting:2",
            "Fear No Evil:1", "Foreign Influence:0", "Foresight:1", "Indignation:1", "Inner Peace:1",
            "Like Water:1", "Meditate:1", "Mental Fortress:1", "Nirvana:1", "Perseverance:1", "Pray:1",
            "Reach Heaven:2", "Rushdown+:0", "Rushdown:1", "Sanctity:1", "Sands of Time:4",
            "Signature Move:2", "Simmering Fury:1", "Study+:1", "Study:2", "Swivel:2", "Talk to the Hand:1",
            "Tantrum:1", "Wallop:2", "Wave of the Hand:1", "Weave:0", "Wheel Kick:2", "Windmill Strike:2",
            "Worship:2", "Wreath of Flame:1", "Alpha:1", "Blasphemy:1", "Brilliance:1", "Deva Form:3",
            "Devotion:1", "Establishment:1", "Judgment:1", "Lesson Learned:2", "Master Reality+:0",
            "Master Reality:1", "Omniscience+:3", "Omniscience:4", "Ragnarok:3", "Scrawl+:0", "Scrawl:1",
            "Spirit Shield:2", "Vault+:2", "Vault:3", "Wish:3", "Bandage Up:0", "Blind:0",
            "Dark Shackles:0", "Deep Breath:0", "Discovery:1", "Dramatic Entrance:0", "Enlightenment:0",
            "Finesse:0", "Flash of Steel:0", "Forethought:0", "Good Instincts:0", "Impatience:0",
            "Jack of All Trades:0", "Madness+:0", "Madness:1", "Mind Blast+:1", "Mind Blast:2",
            "Panacea:0", "Panic Button:0", "Purity:0", "Swift Strike:0", "Trip:0", "Apotheosis+:1",
            "Apotheosis:2", "Chrysalis:2", "Hand of Greed:2", "Magnetism+:1", "Magnetism:2",
            "Master of Strategy:0", "Mayhem+:1", "Mayhem:2", "Metamorphosis:2", "Panache:0",
            "Sadistic Nature:0", "Secret Technique:0", "Secret Weapon:0", "The Bomb:2",
            "Thinking Ahead:0", "Violence:0", "Apparition:1", "Beta+:1", "Beta:2", "Bite:1",
            "Expunger:1", "Insight:0", "J.A.X.:0", "Miracle:0", "Omega:3", "Ritual Dagger:1",
            "Safety:1", "Shiv:0", "Smite:1", "Through Violence:0", "Pride:1", "Slimed:1"
    };
}
