package uk.ac.warwick.cs126.stores;

import uk.ac.warwick.cs126.interfaces.IReviewStore;
import uk.ac.warwick.cs126.models.Favourite;
import uk.ac.warwick.cs126.models.Review;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Locale;

import javax.print.DocFlavor.STRING;

import org.apache.commons.io.IOUtils;

import uk.ac.warwick.cs126.structures.*;

import uk.ac.warwick.cs126.util.DataChecker;
import uk.ac.warwick.cs126.util.KeywordChecker;
import uk.ac.warwick.cs126.util.StringFormatter;

public class ReviewStore implements IReviewStore {

    private DataChecker dataChecker;
    private HashMap<Long, Review> reviewHashMap;
    private SortedArrayList<Long> blacklistedID;
    private MyArrayList<Review> oldReviews;
    private MySort<Review> reviewSort;
    private Comparator<Review> byReviewID;
    private Comparator<Review> byReviewDate;
    private Comparator<Review> byRating;
    private KeywordChecker keywordChecker;
    private StringFormatter stringFormatter;

    public ReviewStore() {
        // Initialise variables here
        dataChecker = new DataChecker();
        reviewHashMap = new HashMap<>();
        blacklistedID = new SortedArrayList<>();
        oldReviews = new MyArrayList<>();
        reviewSort = new MySort<>();
        byReviewID = new ByReviewID();
        // byReviewDate = new ByReviewDate();
        byRating = new ByReviewRating();
        stringFormatter = new StringFormatter();
        keywordChecker = new KeywordChecker();

    }

    public Review[] loadReviewDataToArray(InputStream resource) {
        Review[] reviewArray = new Review[0];

        try {
            byte[] inputStreamBytes = IOUtils.toByteArray(resource);
            BufferedReader lineReader = new BufferedReader(
                    new InputStreamReader(new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int lineCount = 0;
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (!("".equals(line))) {
                    lineCount++;
                }
            }
            lineReader.close();

            Review[] loadedReviews = new Review[lineCount - 1];

            BufferedReader tsvReader = new BufferedReader(
                    new InputStreamReader(new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int reviewCount = 0;
            String row;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            tsvReader.readLine();
            while ((row = tsvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split("\t");
                    Review review = new Review(Long.parseLong(data[0]), Long.parseLong(data[1]),
                            Long.parseLong(data[2]), formatter.parse(data[3]), data[4], Integer.parseInt(data[5]));
                    loadedReviews[reviewCount++] = review;
                }
            }
            tsvReader.close();

            reviewArray = loadedReviews;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return reviewArray;
    }

    public boolean addReview(Review review) {
        // TODO
        // is it valid?
        if (dataChecker.isValid(review) == true) {
            //System.out.println("valid");
            // is it a duplicate?
            if (reviewHashMap.find(review.getID()) == false) {
                //System.out.println("unique");
                // has it been blacklisted
                if (blacklistedID.findBinary(0, blacklistedID.size(), review.getID()) == null) {
                    //System.out.println("not blacklisted");
                    KeyValuePair<Long, Review>[] reviewKV = reviewHashMap.getAllKeyValues();
                    // is there a review in the store with the same customer and restaurant review
                    for (int i = 0; i < reviewKV.length; i++) {
                        if (reviewKV[i].getValue().getCustomerID().equals(review.getCustomerID())
                                && reviewKV[i].getValue().getRestaurantID().equals(review.getRestaurantID())) {
                            //System.out.println("old review here already");
                            // if there is a review already, pick the newer one
                            if (review.getDateReviewed().compareTo(reviewKV[i].getValue().getDateReviewed()) > 0) {
                                // if the review is newer than the existing one, replace it and add the old one
                                // to the old list
                                oldReviews.add(reviewKV[i].getValue());
                                reviewHashMap.remove(reviewKV[i].getValue().getID());
                                // blacklistedID.addSorted(reviewKV[i].getValue().getID());
                                reviewHashMap.add(review.getID(), review);
                                // System.out.println("newer");
                                return true;
                            } else {
                                // if the review is older add it to the old reviews
                                oldReviews.add(review);
                                // blacklistedID.addSorted(review.getID());
                                //System.out.println("older");
                                return false;
                            }
                        }
                    }
                    // this review is entirely unique
                    reviewHashMap.add(review.getID(), review);
                    //System.out.println("no old reviews here already");
                    return true;
                }

            } else {
                // id has already been blacklisted
                // System.out.println("blacklisted");
                return false;
            }
        } else {
            // id already in use/duplicate id
            // if there is another favourite with the same customer id/restaurant id that
            // has not been blacklisted, find the oldest one and replace the favourite that
            // is being blacklisted
            // System.out.println("duplicate id");
            for (int i = oldReviews.size() - 1; i >= 0; i--) {
                if (blacklistedID.findBinary(0, blacklistedID.size(), oldReviews.get(i).getID()) == null
                        && oldReviews.get(i).getCustomerID().equals(review.getCustomerID())
                        && oldReviews.get(i).getRestaurantID().equals(review.getRestaurantID())) {
                    // because it is iterating linearly and backwards the first match it gets should
                    // be the newest
                    // possible review
                    reviewHashMap.add(oldReviews.get(i).getID(), oldReviews.get(i));
                    //System.out.println("replacement found");
                    break;

                }
            }
            //System.out.println("blacklisted");
            blacklistedID.addSorted(review.getID());
            reviewHashMap.remove(review.getID());
            return false;
        }
        // not valid
        //System.out.println("not valid");
        return false;

    }

    public boolean addReview(Review[] reviews) {
        // TODO
        if (reviews == null) {
            return false;
        }else{
            for(int i =0;i<reviews.length;i++){
                addReview(reviews[i]);
            }
            return true;
        }
    }

    public Review getReview(Long id) {
        // TODO
        if (reviewHashMap.find(id) == true) {
            return reviewHashMap.get(id);
        }
        return null;
    }

    public Review[] getReviews() {
        // TODO
        KeyValuePair<Long, Review>[] reviewKV = reviewHashMap.getAllKeyValues();
        if(reviewKV.length>0) {
            Review[] reviews = new Review[reviewKV.length];
            Review[] sortedReviews = new Review[reviewKV.length];
            for (int i = 0; i < reviewKV.length; i++) {
                reviews[i] = reviewKV[i].getValue();
            }
            reviewSort.mergeSort(reviews, sortedReviews, reviews.length, new ByReviewID());
            return sortedReviews;
        }else{
            return new Review[0];
        }
    }

    public Review[] getReviewsByDate() {
        // TODO

        KeyValuePair<Long, Review>[] reviewKV = reviewHashMap.getAllKeyValues();
        Review[] reviews = new Review[reviewKV.length];
        Review[] sortedReviews = new Review[reviewKV.length];
        for (int i = 0; i < reviewKV.length; i++) {
            reviews[i] = reviewKV[i].getValue();
        }
        reviewSort.mergeSort(reviews, sortedReviews, reviews.length, new ByReviewDate());
        return sortedReviews;

    }

    public Review[] getReviewsByRating() {
        // TODO
        KeyValuePair<Long, Review>[] reviewKV = reviewHashMap.getAllKeyValues();
        Review[] reviews = new Review[reviewKV.length];
        Review[] sortedReviews = new Review[reviewKV.length];
        for (int i = 0; i < reviewKV.length; i++) {
            reviews[i] = reviewKV[i].getValue();
        }
        reviewSort.mergeSort(reviews, sortedReviews, reviews.length, new ByReviewRating());
        return sortedReviews;
    }

    public Review[] getReviewsByCustomerID(Long id) {
        // TODO
        KeyValuePair<Long, Review>[] reviewKV = reviewHashMap.getAllKeyValues();
        MyArrayList<Review> sameID = new MyArrayList<>();

        for (int i = 0; i < reviewKV.length; i++) {
            // get all the reviews with the same customer id
            if (reviewKV[i].getValue().getCustomerID().equals(id)) {
                sameID.add(reviewKV[i].getValue());
            }
        }
        // put the reviews into an array and sort
        Review[] allReviews = new Review[sameID.size()];
        Review[] sortedReviews = new Review[sameID.size()];
        for (int i = 0; i < sameID.size(); i++) {
            allReviews[i] = sameID.get(i);
        }
        reviewSort.mergeSort(allReviews, sortedReviews, allReviews.length, new ByReviewDate());
        return sortedReviews;

    }

    public Review[] getReviewsByRestaurantID(Long id) {
        // TODO
        KeyValuePair<Long, Review>[] reviewKV = reviewHashMap.getAllKeyValues();
        MyArrayList<Review> sameID = new MyArrayList<>();

        for (int i = 0; i < reviewKV.length; i++) {
            // get all the reviews with the same Restaurant id
            if (reviewKV[i].getValue().getRestaurantID().equals(id)) {
                sameID.add(reviewKV[i].getValue());
            }
        }
        // put the reviews into an array and sort
        Review[] allReviews = new Review[sameID.size()];
        Review[] sortedReviews = new Review[sameID.size()];
        for (int i = 0; i < sameID.size(); i++) {
            allReviews[i] = sameID.get(i);
        }
        reviewSort.mergeSort(allReviews, sortedReviews, allReviews.length, new ByReviewDate());
        return sortedReviews;
    }

    public float getAverageCustomerReviewRating(Long id) {
        // TODO
        int counter = 0;
        Float mean = 0.0f;// pretty sure the ratings have to be integers
        Review[] reviews = getReviewsByCustomerID(id);
        for (int i = 0; i < reviews.length; i++) {
            mean += (float) reviews[i].getRating();
            counter++;
        }
        if (counter != 0) {
            mean = mean / counter;
        }
        return mean;
    }

    public float getAverageRestaurantReviewRating(Long id) {
        // TODO
        int counter = 0;
        Float mean = 0.0f;
        Review[] reviews = getReviewsByRestaurantID(id);
        for (int i = 0; i < reviews.length; i++) {
            mean += (float) reviews[i].getRating();
            counter++;
        }
        if (counter != 0) {
            mean = mean / counter;
        }
        return mean;
    }

    public int[] getCustomerReviewHistogramCount(Long id) {
        // TODO
        int[] histogram = new int[5];
        Review[] reviews = getReviewsByCustomerID(id);
        if (reviews.length == 0) {
            return new int[5];
        } else {
            for (int i = 0; i < reviews.length; i++) {
                int rating = reviews[i].getRating();
                switch (rating) {
                    case 1:
                        histogram[0]++;
                        break;
                    case 2:
                        histogram[1]++;
                        break;
                    case 3:
                        histogram[2]++;
                        break;
                    case 4:
                        histogram[3]++;
                        break;
                    case 5:
                        histogram[4]++;
                        break;
                    default:
                        return new int[5];
                }
            }
            return histogram;
        }
    }

    public int[] getRestaurantReviewHistogramCount(Long id) {
        // TODO
        int[] histogram = new int[5];
        Review[] reviews = getReviewsByRestaurantID(id);
        if (reviews.length == 0) {
            return new int[5];
        }
        for (int i = 0; i < reviews.length; i++) {
            int rating = reviews[i].getRating();
            switch (rating) {
                case 1:
                    histogram[0]++;
                    break;
                case 2:
                    histogram[1]++;
                    break;
                case 3:
                    histogram[2]++;
                    break;
                case 4:
                    histogram[3]++;
                    break;
                case 5:
                    histogram[4]++;
                    break;
                default:
                    return new int[5];
            }
        }
        return histogram;
    }

    public Long[] getTopCustomersByReviewCount() {
        //TODO
        //traverse the topk array backwards because the smallest will be first
        Long[] topTwenty = new Long[20];//to return
        KeyValuePair<Integer, Review>[] tempTwenty = new KeyValuePair[20];//to store the key values after being sorted
        SortedArrayList<Long> customerIDs = new SortedArrayList<>();//to check the customerids are unique
        KeyValuePair<Long, Review>[] reviewKV = reviewHashMap.getAllKeyValues();//all the reviews
        MyArrayList<KeyValuePair<Integer, Review>> reviewCount = new MyArrayList<>();//all the reviews with unique customer ids and how many reviews they customer has
        Heap<KeyValuePair<Integer, Review>> top20 = new Heap<KeyValuePair<Integer, Review>>(20, new ByCustomerReview());//heap to get the top 20

        //adding the unique reviewss and their count
        for (int i = 0; i < reviewKV.length; i++) {
            Long customerID = reviewKV[i].getValue().getCustomerID();
            if (customerIDs.findBinary(0, customerIDs.size(), customerID) == null) {
                reviewCount.add(new KeyValuePair<Integer, Review>(getReviewsByCustomerID(customerID).length, getReviewsByCustomerID(customerID)[0]));
                customerIDs.addSorted(customerID);
                //System.out.println("new customer: "+customerID);
            }
        }

        //use the heap to get the top 20
        top20.topK(reviewCount, tempTwenty, 20, new ByCustomerReview());

        //get all the customer ids of the top 20- it comes out backwards
        int j = tempTwenty.length - 1;
        int i = 0;
        while (j >= 0 && i < 20) {
            if (tempTwenty[j] != null) {
                topTwenty[i] = tempTwenty[j].getValue().getCustomerID();
                i++;
            }
            j--;
        }
        return topTwenty;
    }

    public Long[] getTopRestaurantsByReviewCount() {
        // TODO-COULDNT WORK OUT HOW TO IMPLEMENT THE MINIMUM CHECK
        // take the first 20 favourites with unique restaurant ids
        //traverse the topk array backwards because the smallest will be first
        Long[] topTwenty = new Long[20];//to return
        KeyValuePair<Integer, Review>[] tempTwenty = new KeyValuePair[20];//to store the key values after being sorted
        SortedArrayList<Long> restaurantIDs = new SortedArrayList<>();//to check the restaurantids are unique
        KeyValuePair<Long, Review>[] reviewKV = reviewHashMap.getAllKeyValues();//all the reviews
        MyArrayList<KeyValuePair<Integer, Review>> reviewCount = new MyArrayList<>();//all the reviews with unique restaurant ids and how many reviews they customer has
        Heap<KeyValuePair<Integer, Review>> top20 = new Heap<KeyValuePair<Integer, Review>>(20, new ByRestaurantReview());//heap to get the top 20

        //adding the unique reviewss and their count
        for (int i = 0; i < reviewKV.length; i++) {
            Long restaurantID = reviewKV[i].getValue().getRestaurantID();
            if (restaurantIDs.findBinary(0, restaurantIDs.size(), restaurantID) == null) {
                reviewCount.add(new KeyValuePair<Integer, Review>(getReviewsByRestaurantID(restaurantID).length, getReviewsByRestaurantID(restaurantID)[0]));
                restaurantIDs.addSorted(restaurantID);
                //System.out.println("new restaurant: "+restaurantID);
            }
        }

        //use the heap to get the top 20
        top20.topK(reviewCount, tempTwenty, 20, new ByRestaurantReview());

        //get all the restaurant ids of the top 20- it comes out backwards
        int j = tempTwenty.length - 1;
        int i = 0;
        while (j >= 0 && i < 20) {
            if (tempTwenty[j] != null) {
                topTwenty[i] = tempTwenty[j].getValue().getRestaurantID();
                i++;
            }
            j--;
        }
        return topTwenty;

    }

    public Long[] getTopRatedRestaurants() {
        // TODO-COULDNT WORK OUT HOW TO IMPLEMENT THE MINIMUM CHECK
        //traverse the topk array backwards because the smallest will be first
        Long[] topTwenty = new Long[20];//to return
        KeyValuePair<Float, Review>[] tempTwenty = new KeyValuePair[20];//to store the key values after being sorted
        SortedArrayList<Long> restaurantIDs = new SortedArrayList<>();//to check the restaurantids are unique
        KeyValuePair<Long, Review>[] reviewKV = reviewHashMap.getAllKeyValues();//all the reviews
        MyArrayList<KeyValuePair<Float, Review>> reviewCount = new MyArrayList<>();//all the reviews with unique restaurant ids and how many reviews they customer has
        Heap<KeyValuePair<Float, Review>> top20 = new Heap<KeyValuePair<Float, Review>>(20, new ByRestaurantRating());//heap to get the top 20

        //adding the unique reviews and their count
        for (int i = 0; i < reviewKV.length; i++) {
            Long restaurantID = reviewKV[i].getValue().getRestaurantID();
            if (restaurantIDs.findBinary(0, restaurantIDs.size(), restaurantID) == null) {
                reviewCount.add(new KeyValuePair<Float, Review>(getAverageRestaurantReviewRating(restaurantID), getReviewsByRestaurantID(restaurantID)[0]));
                restaurantIDs.addSorted(restaurantID);
                //System.out.println("new restaurant: "+restaurantID);
            }
        }

        //use the heap to get the top 20
        top20.topK(reviewCount, tempTwenty, 20, new ByRestaurantRating());

        //get all the restaurant ids of the top 20- it comes out backwards
        int j = tempTwenty.length - 1;
        int i = 0;
        while (j >= 0 && i < 20) {
            if (tempTwenty[j] != null) {
                topTwenty[i] = tempTwenty[j].getValue().getRestaurantID();
                i++;
            }
            j--;
        }
        return topTwenty;
    }

    public String[] getTopKeywordsForRestaurant(Long id) {
        // TODO
        String[] topFive = new String[5];//to return
        KeyValuePair<Integer, String>[] tempFive = new KeyValuePair[5];//to store the key values after being sorted
        SortedArrayList<String> keywords = new SortedArrayList<>();//to check the words are unique and not being repeated
        MyArrayList<KeyValuePair<Integer, String>> wordCount = new MyArrayList<>();
        Heap<KeyValuePair<Integer, String>> top5 = new Heap<KeyValuePair<Integer, String>>(5, new ByKeywordCount());
        Review[] reviews = getReviewsByRestaurantID(id);

        //add the unique words and their count
        for(int k = 0;k<reviews.length;k++) {
            //get a review and make each word an element in an array
            String[] review = reviews[k].getReview().replaceAll("(?:[^a-zA-Z -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+"); //review to go through
            for (int i = 0; i < review.length; i++) {
                //for each word in the array
                if (keywordChecker.isAKeyword(review[i]) == true) {
                    //if it is a keyword add to the list of keywords
                    if (keywords.findBinary(0, keywords.size(), review[i]) == null) {
                        //if it is the first occurence add to the wordcount list
                        //System.out.println(review[i]);
                        keywords.addSorted(review[i]);
                        wordCount.add(new KeyValuePair<Integer, String>(1, review[i].toLowerCase()));
                    } else {
                        //if it is not the first occurence, add to the counter
                        for (int j = 0; j < wordCount.size(); j++) {
                            if (review[i].equalsIgnoreCase(wordCount.get(j).getValue())) {
                                int count = wordCount.get(j).getKey();
                                wordCount.get(j).setKey(count + 1);
                            }
                        }
                    }
                }
            }
        }
        for(int i =0;i<wordCount.size();i++){
            //System.out.println(wordCount.get(i));
        }
        top5.topK(wordCount, tempFive, 5, new ByKeywordCount());
        int j = tempFive.length-1;
        int i = 0;
        while (j >= 0 && i < 5) {
            if (tempFive[j] != null) {
                topFive[i] = tempFive[j].getValue();
                //System.out.println(topFive[i]);
                i++;
            }
            j--;
        }
        return topFive;

    }

    public Review[] getReviewsContaining(String searchTerm) {
        // TODO
        // String searchTermConverted = stringFormatter.convertAccents(searchTerm);
       if (searchTerm != "") {
            Review[] reviews = getReviews();
            MyArrayList<Review> reviewsContainingList = new MyArrayList<>();
            String searchTermConvertedFaster = stringFormatter.convertAccentsFaster(searchTerm).toLowerCase();
            for (int i = 0; i < reviews.length; i++) {
                // System.out.println(reviews[i]);
                //String[] review = reviews[i].getReview().replaceAll("(?:[^a-zA-Z -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
                //i could make into a sortedarraylist but that doubles space
              //  for (int j = 0; j < review.length; j++) {
                    if (reviews[i].getReview().toLowerCase().contains(searchTermConvertedFaster)) {
                        reviewsContainingList.add(reviews[i]);
                        //break;//hopefully only adds each review once
                    }
               // }
            }
            if (reviewsContainingList.size() > 0) {
                Review[] reviewsContaining = new Review[reviewsContainingList.size()];
                Review[] sortedReviews = new Review[reviewsContainingList.size()];
                for(int i =0;i<reviewsContainingList.size();i++){
                    reviewsContaining[i] = reviewsContainingList.get(i);
                }
                return reviewSort.mergeSort(reviewsContaining,sortedReviews,reviewsContaining.length, new ByReviewDate());

            } else return new Review[0];
        } else {
        return new Review[0];
        }
    }

    class ByReviewID implements Comparator<Review> {
        @Override
        public int compare(Review r1, Review r2) {

            return r1.getID().compareTo(r2.getID());
        }
    }

    class ByReviewDate implements Comparator<Review> {
        @Override
        public int compare(Review r1, Review r2) {
            if (r1.getDateReviewed().compareTo(r2.getDateReviewed()) == 0) {
                return r1.getID().compareTo(r2.getID());
            } else {
                return (-1) * r1.getDateReviewed().compareTo(r2.getDateReviewed());
            }
        }
    }

    class ByReviewRating implements Comparator<Review> {
        @Override
        public int compare(Review r1, Review r2) {
            if (r1.getRating() == r2.getRating()) {
                if (r1.getDateReviewed() == r2.getDateReviewed()) {
                    return r1.getID().compareTo(r2.getID());
                } else {
                    return (-1) * r1.getDateReviewed().compareTo(r2.getDateReviewed());
                }
            } else if (r1.getRating() > r2.getRating()) {
                return -1;
            } else {
                return 1;
            }

        }
    }

    class ByCustomerReview implements Comparator<KeyValuePair<Integer, Review>> {
        @Override
        public int compare(KeyValuePair<Integer, Review> kv1, KeyValuePair<Integer, Review> kv2) {
            if (kv1.getKey().equals(kv2.getKey())) {
                if (kv1.getValue().getDateReviewed().equals(kv2.getValue().getDateReviewed())) {
                    return (-1) * kv1.getValue().getCustomerID().compareTo(kv2.getValue().getCustomerID());
                } else {
                    return (-1) * kv1.getValue().getDateReviewed().compareTo(kv2.getValue().getDateReviewed());
                }
            } else {
                return kv1.getKey().compareTo(kv2.getKey());
            }
        }
    }

    class ByRestaurantReview implements Comparator<KeyValuePair<Integer, Review>> {
        @Override
        public int compare(KeyValuePair<Integer, Review> kv1, KeyValuePair<Integer, Review> kv2) {
            if (kv1.getKey().equals(kv2.getKey())) {
                if (kv1.getValue().getDateReviewed().equals(kv2.getValue().getDateReviewed())) {
                    return (-1) * kv1.getValue().getRestaurantID().compareTo(kv2.getValue().getRestaurantID());
                } else {
                    return (-1) * kv1.getValue().getDateReviewed().compareTo(kv2.getValue().getDateReviewed());
                }
            } else {
                return kv1.getKey().compareTo(kv2.getKey());
            }
        }
    }

    class ByRestaurantRating implements Comparator<KeyValuePair<Float, Review>> {
        @Override
        public int compare(KeyValuePair<Float, Review> kv1, KeyValuePair<Float, Review> kv2) {
            if (kv1.getKey().equals(kv2.getKey())) {
                if (kv1.getValue().getDateReviewed().equals(kv2.getValue().getDateReviewed())) {
                    return (-1) * kv1.getValue().getRestaurantID().compareTo(kv2.getValue().getRestaurantID());
                } else {
                    return (-1) * kv1.getValue().getDateReviewed().compareTo(kv2.getValue().getDateReviewed());
                }
            } else {
                return kv1.getKey().compareTo(kv2.getKey());
            }
        }
    }

    class ByKeywordCount implements Comparator<KeyValuePair<Integer, String>> {
        @Override
        public int compare(KeyValuePair<Integer, String> kv1, KeyValuePair<Integer, String> kv2) {
            if (kv1.getKey().equals(kv2.getKey())) {
                return (-1) * kv1.getValue().compareToIgnoreCase(kv2.getValue());
            } else {
                return kv1.getKey().compareTo(kv2.getKey());
            }
        }
    }
}
