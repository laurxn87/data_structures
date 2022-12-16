package uk.ac.warwick.cs126.stores;

import uk.ac.warwick.cs126.interfaces.IFavouriteStore;
import uk.ac.warwick.cs126.models.Favourite;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import org.apache.commons.io.IOUtils;

import uk.ac.warwick.cs126.models.Review;
import uk.ac.warwick.cs126.structures.HashMap;
import uk.ac.warwick.cs126.structures.KeyValuePair;
import uk.ac.warwick.cs126.structures.MyArrayList;
import uk.ac.warwick.cs126.structures.SortedArrayList;
import uk.ac.warwick.cs126.util.DataChecker;
import uk.ac.warwick.cs126.structures.MySort;
import uk.ac.warwick.cs126.structures.Heap;

public class FavouriteStore implements IFavouriteStore {

    // private MyArrayList<Favourite> favouriteArray;
    private DataChecker dataChecker;
    private HashMap<Long, Favourite> favouriteHashMap;// can i trust this id?
    private SortedArrayList<Long> blacklistedID;
    private MyArrayList<Favourite> oldFavourites;
    private MySort<Favourite> favouriteSort;
    private MySort<KeyValuePair<Integer,Long>> kvSort;
    //private Comparator<Favourite>  byFavouriteID;
    //private Comparator<Favourite>  byFavouriteDate;
    //private Comparator<Favourite>  byRestaurantDate;


    public FavouriteStore() {
        // Initialise variables here
        // favouriteArray = new MyArrayList<>();
        dataChecker = new DataChecker();
        favouriteHashMap = new HashMap<>();
        blacklistedID = new SortedArrayList<>();
        oldFavourites = new MyArrayList<>();
        favouriteSort = new MySort<>();
        kvSort = new MySort<>();
        //byFavouriteID = new ByFavouriteID();
        //byFavouriteDate = new ByFavouriteDate();
        //byRestaurantDate = new ByRestaurantDate();
    }

    public Favourite[] loadFavouriteDataToArray(InputStream resource) {
        Favourite[] favouriteArray = new Favourite[0];

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

            Favourite[] loadedFavourites = new Favourite[lineCount - 1];

            BufferedReader csvReader = new BufferedReader(
                    new InputStreamReader(new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int favouriteCount = 0;
            String row;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            csvReader.readLine();
            while ((row = csvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split(",");
                    Favourite favourite = new Favourite(Long.parseLong(data[0]), Long.parseLong(data[1]),
                            Long.parseLong(data[2]), formatter.parse(data[3]));
                    loadedFavourites[favouriteCount++] = favourite;
                }
            }
            csvReader.close();

            favouriteArray = loadedFavourites;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return favouriteArray;
    }

    public boolean addFavourite(Favourite favourite) {
        // TODO - PROBABLY NEEDS A REDESIGN
        // valid?
        // duplicate id-yes=blacklist id and remove current
        // blacklisted?
        // is there a favourite already that is older with the same customer and
        // restaurant id-yes=replace with the new and blacklist the old
        // if the customer has alreay favourited a restaurant if everything is valid
        // choose the older favourite
        // HARDER

        if (dataChecker.isValid(favourite) == true) {
            if (favouriteHashMap.find(favourite.getID()) == false) {
                if (blacklistedID.findBinary(0, blacklistedID.size(), favourite.getID()) == null) {
                    KeyValuePair<Long, Favourite>[] favouriteKV = favouriteHashMap.getAllKeyValues();
                    for (int i = 0; i < favouriteKV.length; i++) {
                        if (favouriteKV[i].getValue().getCustomerID().equals(favourite.getCustomerID())
                                && favouriteKV[i].getValue().getRestaurantID().equals(favourite.getRestaurantID())) {
                            if (favourite.getDateFavourited()
                                    .compareTo(favouriteKV[i].getValue().getDateFavourited()) < 0) {
                                oldFavourites.add(favouriteKV[i].getValue());
                                favouriteHashMap.remove(favouriteKV[i].getValue().getID());
                                // blacklistedID.addSorted(favouriteKV[i].getValue().getID());
                                favouriteHashMap.add(favourite.getID(), favourite);
                                return true;
                            } else {
                                // there is an old favourite already there/if it is newer
                                oldFavourites.add(favourite);
                                //blacklistedID.addSorted(favourite.getID());
                                return false;
                            }
                        }
                    }
                    // this favourite is entirely unique
                    favouriteHashMap.add(favourite.getID(), favourite);
                    return true;

                }
            } else {
                // id has already been blacklisted
                return false;
            }
        } else {
            // id already in use/duplicate id
            // if there is another favourite with the same customer id/restaurant id that
            // has not been blacklisted, find the oldest one and replace the favourite that
            // is being blacklisted
            for (int i = 0; i < oldFavourites.size(); i++) {
                if (blacklistedID.findBinary(0, blacklistedID.size(), oldFavourites.get(i).getID()) == null
                        && oldFavourites.get(i).getCustomerID().equals(favourite.getCustomerID())
                        && oldFavourites.get(i).getRestaurantID().equals(favourite.getRestaurantID()) ){
                    // because it is iterating linearly the first match it gets should be the oldest
                    // possible match
                    favouriteHashMap.add(oldFavourites.get(i).getID(), oldFavourites.get(i));
                    break;

                }
            }
            blacklistedID.addSorted(favourite.getID());
            favouriteHashMap.remove(favourite.getID());
            return false;
        }

    // not valid
        return false;
}

    public boolean addFavourite(Favourite[] favourites) {
        // TODO
        if(favourites!=null){
        for (int i = 0; i < favourites.length; i++) {
            addFavourite(favourites[i]);
        }
        return true;
        }else{
            return false;
        }
    }

    public Favourite getFavourite(Long id) {
        // TODO-could clean this up a little at the end
        if (favouriteHashMap.find(id) == true) {
            return favouriteHashMap.get(id);
        }
        return null;
    }

    public Favourite[] getFavourites() {
        // TODO
      KeyValuePair<Long, Favourite>[] favouriteKV = favouriteHashMap.getAllKeyValues();
        Favourite[] allFavourites = new Favourite[favouriteKV.length];
        Favourite[] sortedFavourites = new Favourite[favouriteKV.length];
        for (int i = 0; i < favouriteKV.length; i++) {
            allFavourites[i] = favouriteKV[i].getValue();
        }
        favouriteSort.mergeSort(allFavourites, sortedFavourites, allFavourites.length, new ByFavouriteID());
        return sortedFavourites;
    }

    public Favourite[] getFavouritesByCustomerID(Long id) {
        // TODO-this can probably be improved
        KeyValuePair<Long, Favourite>[] favouriteKV = favouriteHashMap.getAllKeyValues();
        MyArrayList<Favourite> sameID = new MyArrayList<>();

        for (int i = 0; i < favouriteKV.length; i++) {
            // get all the favourites with the same customer id
            if (favouriteKV[i].getValue().getCustomerID().equals(id)) {
                sameID.add(favouriteKV[i].getValue());
            }
        }
        // put the favourites into an array and sort
        Favourite[] allFavourites = new Favourite[sameID.size()];
        Favourite[] sortedFavourites = new Favourite[sameID.size()];
        for (int i = 0; i < sameID.size(); i++) {
            allFavourites[i] = sameID.get(i);
        }
        favouriteSort.mergeSort(allFavourites, sortedFavourites, allFavourites.length, new ByFavouriteDate());
        return sortedFavourites;
    }

    public Favourite[] getFavouritesByRestaurantID(Long id) {
        // TODO-probs be improved
        KeyValuePair<Long, Favourite>[] favouriteKV = favouriteHashMap.getAllKeyValues();
        MyArrayList<Favourite> sameID = new MyArrayList<>();

        for (int i = 0; i < favouriteKV.length; i++) {
            // get all the favourites with the same customer id
            if (favouriteKV[i].getValue().getRestaurantID().equals(id)) {
                sameID.add(favouriteKV[i].getValue());
            }
        }
        // put the favourites into an array and sort
        Favourite[] allFavourites = new Favourite[sameID.size()];
        Favourite[] sortedFavourites = new Favourite[sameID.size()];
        for (int i = 0; i < sameID.size(); i++) {
            allFavourites[i] = sameID.get(i);
        }
        favouriteSort.mergeSort(allFavourites, sortedFavourites, allFavourites.length, new ByFavouriteDate());
        return sortedFavourites;}

    public Long[] getCommonFavouriteRestaurants(Long customer1ID, Long customer2ID) {
        // TODO-probs be improved
        Favourite[] c1 = getFavouritesByCustomerID(customer1ID);
        Favourite[] c2 = getFavouritesByCustomerID(customer2ID);
        MyArrayList<Favourite> commonFavesList = new MyArrayList<>();

        SortedArrayList<Long> c2restID = new SortedArrayList<>();
        for (int i = 0; i < c2.length; i++) {
            c2restID.addSorted(c2[i].getRestaurantID());
        }

        // finding the common favourites
        for (int i = 0; i < c1.length; i++) {
            // if the restaurant id matches
            if (c2restID.findBinary(0, c2restID.size(), c1[i].getRestaurantID()) != null) {
                // find the matching favourite
                for (int j = 0; j < c2.length; j++) {
                    if (c1[i].getRestaurantID().equals(c2[j].getRestaurantID())) {
                        // add the latest favourite
                        if (c1[i].getDateFavourited().compareTo(c2[j].getDateFavourited()) >= 0) {
                            commonFavesList.add(c1[i]);
                        } else {
                            commonFavesList.add(c2[j]);
                        }
                        break;// really only want one copy
                    }
                }

            }
        }

        // copy the common favourites to an array to sort
        Favourite[] commonFaves = new Favourite[commonFavesList.size()];
        Favourite[] sortedFaves = new Favourite[commonFavesList.size()];
        for(int i = 0;i<commonFaves.length;i++){
            commonFaves[i] = commonFavesList.get(i);
        }
        favouriteSort.mergeSort(commonFaves, sortedFaves, commonFaves.length, new ByRestaurantDate());
        Long[] commonIDs = new Long[commonFaves.length];
        for (int i = 0; i < commonFaves.length; i++) {
            commonIDs[i] = commonFaves[i].getRestaurantID();
        }
        return commonIDs;
    }

    public Long[] getMissingFavouriteRestaurants(Long customer1ID, Long customer2ID) {
        // TODO
        // get all the favourites from customer 1
        Favourite[] c1 = getFavouritesByCustomerID(customer1ID);
        Long[] intersection = getCommonFavouriteRestaurants(customer1ID, customer2ID);
        SortedArrayList<Long> intersectionList = new SortedArrayList<>();
        for (int i = 0; i < intersection.length; i++) {
            intersectionList.addSorted(intersection[i]);
           // System.out.println(intersection[i]);
        }
        MyArrayList<Favourite> missing = new MyArrayList<>();
        // loop through, if the restaurant id isnt in the intersection add to the
        // arraylist

        for (int i = 0; i < c1.length; i++) {
            if (intersectionList.findBinary(0, intersectionList.size(), c1[i].getRestaurantID()) == null) {
                missing.add(c1[i]);
               // System.out.println(c1[i].getRestaurantID());
            }
        }
        Favourite[] missingFavourites = new Favourite[missing.size()];
        Favourite[] sortedFavourites = new Favourite[missing.size()];
        for(int i =0;i<missingFavourites.length;i++){
            missingFavourites[i] = missing.get(i);
        }
        favouriteSort.mergeSort(missingFavourites, sortedFavourites, missingFavourites.length,new ByRestaurantDate());

        Long[] sortedID = new Long[sortedFavourites.length];
        for(int i =0;i< sortedFavourites.length;i++){
            sortedID[i] = sortedFavourites[i].getRestaurantID();
        }
        return sortedID;

}

    public Long[] getNotCommonFavouriteRestaurants(Long customer1ID, Long customer2ID) {
        // TODO - other method is to do missing restaurants twice but that might be more
        // confusing
        Favourite[] c1 = getFavouritesByCustomerID(customer1ID);
        Favourite[] c2 = getFavouritesByCustomerID(customer2ID);
        Long[] intersection = getCommonFavouriteRestaurants(customer1ID, customer2ID);
        SortedArrayList<Long> intersectionList = new SortedArrayList<>();
        MyArrayList<Favourite> notCommonList = new MyArrayList<>();
        // copy the array so we can binary search
        for (int i = 0; i < intersection.length; i++) {
            intersectionList.addSorted(intersection[i]);
        }
        for (int i = 0; i < c1.length; i++) {
            // if the element is not in the intersection
            if (intersectionList.findBinary(0, intersectionList.size(), c1[i].getRestaurantID()) == null) {
                notCommonList.add(c1[i]);
            }

        }
        for (int i = 0; i < c2.length; i++) {
            // if the element is not in the intersection
            if (intersectionList.findBinary(0, intersectionList.size(), c2[i].getRestaurantID()) == null) {
                notCommonList.add(c2[i]);
            }

        }
        Favourite[] notCommonFaves = new Favourite[notCommonList.size()];
        Favourite[] sortedFaves = new Favourite[notCommonList.size()];
        for(int i =0;i<notCommonFaves.length;i++){
            notCommonFaves[i] = notCommonList.get(i);
        }
        favouriteSort.mergeSort(notCommonFaves, sortedFaves, notCommonFaves.length, new ByRestaurantDate());
        Long[] notCommonIDs = new Long[notCommonFaves.length];
        for (int i = 0; i < sortedFaves.length; i++) {
            notCommonIDs[i] = sortedFaves[i].getRestaurantID();
        }
        return notCommonIDs;

    }

    public Long[] getTopCustomersByFavouriteCount() {
        //HEAPSORT = O(nlogk)
        // TODO-COULDNT WORK OUT HOW TO IMPLEMENT THE MINIMUM CHECK
        //traverse the topk array backwards because the smallest will be first
        Long[] topTwenty = new Long[20];//to return
        KeyValuePair<Integer,Favourite>[] tempTwenty = new KeyValuePair[20];//to store the key values after being sorted
        SortedArrayList<Long> customerIDs = new SortedArrayList<>();//to check the customerids are unique
        KeyValuePair<Long, Favourite>[] favouriteKV = favouriteHashMap.getAllKeyValues();//all the favourites
        MyArrayList<KeyValuePair<Integer,Favourite>> favouriteCount = new MyArrayList<>();//all the favourites with unique customer ids and how many reviews they customer has
        Heap<KeyValuePair<Integer,Favourite>> top20 = new Heap<KeyValuePair<Integer,Favourite>>(20,new ByCustomerFavourite());//heap to get the top 20

        //adding the unique favourites and their count
        for(int i =0;i<favouriteKV.length;i++) {
            Long customerID = favouriteKV[i].getValue().getCustomerID();
            if (customerIDs.findBinary(0, customerIDs.size(), customerID) == null) {
                favouriteCount.add(new KeyValuePair<Integer, Favourite>(getFavouritesByCustomerID(customerID).length, getFavouritesByCustomerID(customerID)[0]));
                customerIDs.addSorted(customerID);
                //System.out.println("new customer: "+customerID);
            }
        }

        //use the heap to get the top 20
        top20.topK(favouriteCount,tempTwenty,20,new ByCustomerFavourite());

        //get all the customer ids of the top 20- it comes out backwards
        int j = tempTwenty.length-1;
        int i =0;
        while(j >= 0 && i<20) {
            if(tempTwenty[j]!=null) {
                topTwenty[i] = tempTwenty[j].getValue().getCustomerID();
                i++;
            }
            j--;
        }
        return topTwenty;

    }

    public Long[] getTopRestaurantsByFavouriteCount() {
        // TODO-COULDNT WORK OUT HOW TO IMPLEMENT THE MINIMUM CHECK
        Long[] topTwenty = new Long[20];//to return
        KeyValuePair<Integer,Favourite>[] tempTwenty = new KeyValuePair[20];//to store the key values after being sorted
        SortedArrayList<Long> restaurantIDs = new SortedArrayList<>();//to check the restaurantids are unique
        KeyValuePair<Long, Favourite>[] favouriteKV = favouriteHashMap.getAllKeyValues();//all the favourites
        MyArrayList<KeyValuePair<Integer,Favourite>> favouriteCount = new MyArrayList<>();//all the favourites with unique restaurant ids and how many reviews they customer has
        Heap<KeyValuePair<Integer,Favourite>> top20 = new Heap<KeyValuePair<Integer,Favourite>>(20,new ByRestaurantFavourite());//heap to get the top 20

        //adding the unique favourites and their count
        for(int i =0;i<favouriteKV.length;i++) {
            Long restaurantID = favouriteKV[i].getValue().getRestaurantID();
            if (restaurantIDs.findBinary(0, restaurantIDs.size(), restaurantID) == null) {
                favouriteCount.add(new KeyValuePair<Integer, Favourite>(getFavouritesByRestaurantID(restaurantID).length, getFavouritesByRestaurantID(restaurantID)[0]));
                restaurantIDs.addSorted(restaurantID);
                //System.out.println("new restaurant: "+restaurantID);
            }
        }

        //use the heap to get the top 20
        top20.topK(favouriteCount,tempTwenty,20,new ByRestaurantFavourite());

        //get all the customer ids of the top 20- it comes out backwards
        int j = tempTwenty.length-1;
        int i =0;
        while(j >= 0 && i<20) {
            if(tempTwenty[j]!=null) {
                topTwenty[i] = tempTwenty[j].getValue().getRestaurantID();
                i++;
            }
            j--;
        }
        return topTwenty;

    }
}

class ByFavouriteID implements Comparator<Favourite> {
    @Override
    public int compare(Favourite f1, Favourite f2) {
        return f1.getID().compareTo(f2.getID());
    }
}

class ByFavouriteDate implements Comparator<Favourite> {
    @Override
    public int compare(Favourite f1, Favourite f2) {
        if (f1.getDateFavourited().equals(f2.getDateFavourited())) {
            return f1.getID().compareTo(f2.getID());
        } else {
            return (-1) * f1.getDateFavourited().compareTo(f2.getDateFavourited());
        }
    }
}

class ByRestaurantDate implements Comparator<Favourite> {
    @Override
    public int compare(Favourite f1, Favourite f2) {
        if (f1.getDateFavourited().equals(f2.getDateFavourited())) {
            return f1.getRestaurantID().compareTo(f2.getRestaurantID());
        } else {
            return (-1) * f1.getDateFavourited().compareTo(f2.getDateFavourited());
        }
    }
}

class ByCustomerFavourite implements Comparator<KeyValuePair<Integer,Favourite>>{
    @Override
    public int compare(KeyValuePair<Integer,Favourite> kv1,KeyValuePair<Integer,Favourite> kv2) {
        if (kv1.getKey().equals(kv2.getKey())) {
            if (kv1.getValue().getDateFavourited().equals(kv2.getValue().getDateFavourited())) {
                return (-1)*kv1.getValue().getCustomerID().compareTo(kv2.getValue().getCustomerID());
            } else {
                return (-1)*kv1.getValue().getDateFavourited().compareTo(kv2.getValue().getDateFavourited());
            }
        } else {
            return kv1.getKey().compareTo(kv2.getKey());
        }
    }
}

class ByRestaurantFavourite implements Comparator<KeyValuePair<Integer,Favourite>>{
    @Override
    public int compare(KeyValuePair<Integer,Favourite> kv1,KeyValuePair<Integer,Favourite> kv2) {
        if (kv1.getKey().equals(kv2.getKey())) {
            if (kv1.getValue().getDateFavourited().equals(kv2.getValue().getDateFavourited())) {
                return (-1)*kv1.getValue().getRestaurantID().compareTo(kv2.getValue().getRestaurantID());
            } else {
                return (-1)*kv1.getValue().getDateFavourited().compareTo(kv2.getValue().getDateFavourited());
            }
        } else {
            return kv1.getKey().compareTo(kv2.getKey());
        }
    }
}



