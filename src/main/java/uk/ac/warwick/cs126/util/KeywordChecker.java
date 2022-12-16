package uk.ac.warwick.cs126.util;

import uk.ac.warwick.cs126.interfaces.IKeywordChecker;

public class KeywordChecker implements IKeywordChecker {
    private static final String[] keywords = {
            "0",
            "agreeable",
            "air-headed",
            "apocalypse",
            "appetizing",
            "average",
            "awesome",
            "biohazard",
            "bland",
            "bleh",
            "burnt",
            "charming",
            "clueless",
            "cockroach",
            "cold",
            "crap",
            "dancing",
            "dead",
            "decadent",
            "decent",
            "dirty",
            "disgusting",
            "dreadful",
            "droppings",
            "dry",
            "dumpy",
            "excellent",
            "favourite",
            "feel-good",
            "flavourful",
            "frozen",
            "gem",
            "gross",
            "heart",
            "heavenly",
            "horrendous",
            "horrible",
            "incredible",
            "interesting",
            "lame",
            "lousy",
            "mediocre",
            "meh",
            "mess",
            "microwaved",
            "mouth-watering",
            "nightmares",
            "ok",
            "okay",
            "overcooked",
            "overhyped",
            "perfection",
            "polite",
            "prompt",
            "quality",
            "rude",
            "satisfaction",
            "savoury",
            "sewer",
            "singing",
            "slow",
            "so-so",
            "spongy",
            "sticky",
            "sublime",
            "succulent",
            "sucked",
            "surprised",
            "terrible",
            "tingling",
            "tired",
            "toxic",
            "traumatizing",
            "uncomfortable",
            "under-seasoned",
            "undercooked",
            "unique",
            "unprofessional",
            "waste",
            "worst",
            "yuck",
            "yummy"
    };

    public KeywordChecker() {
        // Initialise things here
    }

    public boolean isAKeyword(String word) {
        // TODO
        int start = 0;
        int end = keywords.length-1;
        while (start <= end) {
            int mid =  (start + end) / 2;
            if(keywords[mid]!=null){
                if (keywords[mid].equalsIgnoreCase(word)) {
                    return true;
                } else if (keywords[mid].compareToIgnoreCase(word) < 0) {
                    start = mid + 1;
                } else {
                    end = mid - 1;
                }
            }else{
                return false;
            }
        }
        return false;
    }

    }
