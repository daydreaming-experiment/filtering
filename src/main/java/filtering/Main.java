package filtering;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    private static String TAG = "Main";

    public static void main(String[] args) throws InterruptedException {
        Logger.i(TAG, "yo");

        ArrayList<String> possibilities = new ArrayList<String>(Arrays.asList(new String[] {
                // From Home
                "Cooking", "Eating", "Housework (dishes, cleaning)", "Care (shower, makeup)", "Nursing", "Petting",
                "Intimate relations", "Having a nap", "Resting", "Playing music", "Making a drawing", "Video game",
                "Watching a program", "Computer, Internet or Email", "Reading",
                // From Commuting
                "Driving", "Biking", "Walking", "Train, metro, bus", "Listening to music or the radio",
                "Attempting to daydream", "Reading", "Writing or texting", "Talking", "Watching a program",
                "Video game (on phone)",
                // From Outside
                "Walking", "Biking", "Gardening", "Resting", "Having a nap", "Reading", "Talking",
                "Attempting to daydream", "Shopping", "Petting", "Exercising", "Physical game",
                // From Public place
                "Shopping, groceries", "Reading (library)", "Watching screen (cinema)", "Eating (restaurant)",
                "Talking", "Exercising",
                // From Work
                "Attending a presentation, conference, or class", "Computer, Internet or Email", "Reading",
                "Writing (coding)", "Making a drawing", "Walking", "Waiting", "Actively thinking (reasoning)"
        }));

        Timer timer = new Timer();
        timer.start("Filterer initialization");
        Filterer filterer = new Filterer(possibilities);
        long initDelay = timer.finish("Filterer initialization");
        Logger.i(TAG, "Filterer initialization: " + initDelay + "ms");

        GUI gui = new GUI(filterer);
        gui.run();
    }
}

