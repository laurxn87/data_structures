package uk.ac.warwick.cs126.stores;

import uk.ac.warwick.cs126.interfaces.IRestaurantStore;
import uk.ac.warwick.cs126.models.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;


import org.apache.commons.io.IOUtils;

import uk.ac.warwick.cs126.structures.MyArrayList;
import uk.ac.warwick.cs126.structures.HashMap;
import uk.ac.warwick.cs126.structures.KeyValuePair;
import uk.ac.warwick.cs126.structures.SortedArrayList;

import uk.ac.warwick.cs126.util.ConvertToPlace;
import uk.ac.warwick.cs126.util.HaversineDistanceCalculator;
import uk.ac.warwick.cs126.structures.MySort;
import uk.ac.warwick.cs126.util.DataChecker;
import uk.ac.warwick.cs126.util.StringFormatter;

public class RestaurantStore implements IRestaurantStore {

    // private MyArrayList<Restaurant> restaurantArray;
    private DataChecker dataChecker;
    // private HaversineDistanceCalculator distanceCalculator;
    private ConvertToPlace convertToPlace;
    private HashMap<Long, Restaurant> restaurantHashMap;
    private SortedArrayList<Long> blacklistID;
    private MySort<Restaurant> restaurantSort;
    private MySort<RestaurantDistance> restaurantDistanceSort;
    //sort out this comparator thing


    private static StringFormatter stringFormatter;


    public RestaurantStore() {
        // Initialise variables here
        //restaurantArray = new MyArrayList<>();
        dataChecker = new DataChecker();
        convertToPlace = new ConvertToPlace();
        restaurantHashMap = new HashMap<>();
        blacklistID = new SortedArrayList<>();
        restaurantSort = new MySort<>();
        restaurantDistanceSort = new MySort<>();
        //distanceCalculator = new HaversineDistanceCalculator();
        stringFormatter = new StringFormatter();


    }

    public Restaurant[] loadRestaurantDataToArray(InputStream resource) {
        Restaurant[] restaurantArray = new Restaurant[0];

        try {
            byte[] inputStreamBytes = IOUtils.toByteArray(resource);
            BufferedReader lineReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int lineCount = 0;
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (!("".equals(line))) {
                    lineCount++;
                }
            }
            lineReader.close();

            Restaurant[] loadedRestaurants = new Restaurant[lineCount - 1];

            BufferedReader csvReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            String row;
            int restaurantCount = 0;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            csvReader.readLine();
            while ((row = csvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split(",");

                    Restaurant restaurant = new Restaurant(
                            data[0],
                            data[1],
                            data[2],
                            data[3],
                            Cuisine.valueOf(data[4]),
                            EstablishmentType.valueOf(data[5]),
                            PriceRange.valueOf(data[6]),
                            formatter.parse(data[7]),
                            Float.parseFloat(data[8]),
                            Float.parseFloat(data[9]),
                            Boolean.parseBoolean(data[10]),
                            Boolean.parseBoolean(data[11]),
                            Boolean.parseBoolean(data[12]),
                            Boolean.parseBoolean(data[13]),
                            Boolean.parseBoolean(data[14]),
                            Boolean.parseBoolean(data[15]),
                            formatter.parse(data[16]),
                            Integer.parseInt(data[17]),
                            Integer.parseInt(data[18]));

                    loadedRestaurants[restaurantCount++] = restaurant;
                }
            }
            csvReader.close();

            restaurantArray = loadedRestaurants;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return restaurantArray;
    }

    public boolean addRestaurant(Restaurant restaurant) {
        // TODO   
        //valid? 
        //is the ID already there-yes=blacklist and remove the current restaurant
        //blacklisted?
        restaurant.setID(dataChecker.extractTrueID(restaurant.getRepeatedID()));
        if(dataChecker.isValid(restaurant)==true){
            if(restaurantHashMap.find(restaurant.getID())==false){
                if(blacklistID.findBinary(0, blacklistID.size(),restaurant.getID())==null){
                    restaurantHashMap.add(restaurant.getID(), restaurant);
                    return true;
                }else{
                return false;
                }
            }else{
                blacklistID.addSorted(restaurant.getID());
                restaurantHashMap.remove(restaurant.getID());
                return false;
            }
        }
        return false;
    }

    public boolean addRestaurant(Restaurant[] restaurants) {
        // TODO-this seems sketchy
        if(restaurants == null){
            return false;
        }else{
       for(int i =0;i<restaurants.length;i++){
           addRestaurant(restaurants[i]);
        }
        return true;
        }
    }

    public Restaurant getRestaurant(Long id) {
        // TODO

        if(restaurantHashMap.find(id)==true){
            return restaurantHashMap.get(id);
        }
        return null;
    }

    public Restaurant[] getRestaurants() {
        // TODO

        KeyValuePair<Long, Restaurant>[] restaurantKV = restaurantHashMap.getAllKeyValues();
        if(restaurantKV.length>0){
        Restaurant[] allRestaurants = new Restaurant[restaurantKV.length];
        Restaurant[] sortedRestaurants = new Restaurant[restaurantKV.length];
        for(int i=0;i<restaurantKV.length;i++){
            allRestaurants[i] = restaurantKV[i].getValue();
        }
        restaurantSort.mergeSort(allRestaurants, sortedRestaurants, allRestaurants.length, new ByRestaurantID());
        return sortedRestaurants;
        }else{
            return new Restaurant[0];
        }
    }

    public Restaurant[] getRestaurants(Restaurant[] restaurants) {
        // TODO-which ids do i use 
        //array to return 
       Restaurant [] sortedRestaurants = new Restaurant [restaurants.length];
        restaurantSort.mergeSort(restaurants, sortedRestaurants, restaurants.length, new ByRestaurantID());
        return sortedRestaurants;
    }

    public Restaurant[] getRestaurantsByName() {
        // TODO
    KeyValuePair<Long, Restaurant>[] restaurantKV = restaurantHashMap.getAllKeyValues();
    Restaurant[] allRestaurants = new Restaurant[restaurantKV.length];
    Restaurant[] sortedRestaurants = new Restaurant[restaurantKV.length];
    for(int i=0;i<restaurantKV.length;i++){
        allRestaurants[i] = restaurantKV[i].getValue();
    }
    restaurantSort.mergeSort(allRestaurants, sortedRestaurants, allRestaurants.length, new ByName());
    return sortedRestaurants;

    }

    public Restaurant[] getRestaurantsByDateEstablished() {
        // TODO
        KeyValuePair<Long,Restaurant>[] restaurantKV  = restaurantHashMap.getAllKeyValues();
        Restaurant [] allRestaurants = new Restaurant [restaurantKV.length];
        Restaurant [] sortedRestaurants = new Restaurant [restaurantKV.length];
        for(int i=0;i<restaurantKV.length;i++){
            allRestaurants[i] = restaurantKV[i].getValue();
        }
        restaurantSort.mergeSort(allRestaurants, sortedRestaurants, allRestaurants.length, new ByDate());

        return sortedRestaurants;

    }

    public Restaurant[] getRestaurantsByDateEstablished(Restaurant[] restaurants) {

        // TODO
        Restaurant [] sortedRestaurants = new Restaurant [restaurants.length];
        restaurantSort.mergeSort(restaurants, sortedRestaurants, restaurants.length, new ByDate());
        return sortedRestaurants;

    }

    public Restaurant[] getRestaurantsByWarwickStars() {
        // TODO
        KeyValuePair<Long,Restaurant>[] restaurantKV  = restaurantHashMap.getAllKeyValues();
        Restaurant [] allRestaurants = new Restaurant [restaurantKV.length];
        Restaurant [] sortedRestaurants = new Restaurant [restaurantKV.length];
        for(int i=0;i<restaurantKV.length;i++){
            allRestaurants[i] = restaurantKV[i].getValue();
        }
        restaurantSort.mergeSort(allRestaurants, sortedRestaurants, allRestaurants.length, new ByWarwickStars());

        return sortedRestaurants;

    }

    public Restaurant[] getRestaurantsByRating(Restaurant[] restaurants) {

        // TODO
        Restaurant [] sortedRestaurants = new Restaurant [restaurants.length];
        restaurantSort.mergeSort(restaurants, sortedRestaurants, restaurants.length, new ByRating());
        return sortedRestaurants;


    }

    public RestaurantDistance[] getRestaurantsByDistanceFrom(float latitude, float longitude) {
        // TODO

        KeyValuePair<Long,Restaurant>[] restaurantKV  = restaurantHashMap.getAllKeyValues();
        RestaurantDistance [] allRestaurants = new RestaurantDistance [restaurantKV.length];
        RestaurantDistance [] sortedRestaurants = new RestaurantDistance [restaurantKV.length];
        //add restaurants to allRestaurants array 
        for(int i=0;i<restaurantKV.length;i++){
            float restLat = restaurantKV[i].getValue().getLatitude();
            float restLong =  restaurantKV[i].getValue().getLongitude();
            float distance = HaversineDistanceCalculator.inKilometres(latitude, longitude, restLat, restLong);
            RestaurantDistance restaurant = new RestaurantDistance(restaurantKV[i].getValue(), distance);
            allRestaurants[i] = restaurant;
        }
        restaurantDistanceSort.mergeSort(allRestaurants, sortedRestaurants,allRestaurants.length, new ByDistance());
        return sortedRestaurants;


    }

    public RestaurantDistance[] getRestaurantsByDistanceFrom(Restaurant[] restaurants, float latitude, float longitude) {
        // TODO
        RestaurantDistance[] allRestaurants = new RestaurantDistance[restaurants.length];
        RestaurantDistance [] sortedRestaurants = new RestaurantDistance [restaurants.length];
        //add restaurants to allRestaurants array 
        for(int i=0;i<restaurants.length;i++){
            float restLat = restaurants[i].getLatitude();
            float restLong =  restaurants[i].getLongitude();
            float distance = HaversineDistanceCalculator.inKilometres(latitude, longitude, restLat, restLong);
            RestaurantDistance restaurant = new RestaurantDistance(restaurants[i], distance);
            allRestaurants[i] = restaurant;
        }
        restaurantDistanceSort.mergeSort(allRestaurants, sortedRestaurants,allRestaurants.length, new ByDistance());
        return sortedRestaurants;

    }

    public Restaurant[] getRestaurantsContaining(String searchTerm) {
        // TODO

        if (searchTerm != "") {
            Restaurant[] restaurants = getRestaurants();
            MyArrayList<Restaurant> restaurantsContainingList = new MyArrayList<>();
            String searchTermConvertedFaster = stringFormatter.convertAccentsFaster(searchTerm).toLowerCase();
            for (int i = 0; i < restaurants.length; i++) {
                // System.out.println(reviews[i]);
                //String[] review = reviews[i].getReview().replaceAll("(?:[^a-zA-Z -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
                //i could make into a sortedarraylist but that doubles space
                //  for (int j = 0; j < review.length; j++) {
                String name = restaurants[i].getName().toLowerCase();
                String cuisine = restaurants[i].getCuisine().toString().toLowerCase();
                String place = convertToPlace.convert(restaurants[i].getLatitude(),restaurants[i].getLongitude()).getName().toLowerCase();
                if (name.contains(searchTermConvertedFaster)||cuisine.contains(searchTermConvertedFaster)||place.contains(searchTermConvertedFaster)) {
                    restaurantsContainingList.add(restaurants[i]);
                    //break;//hopefully only adds each review once
                }
                // }
            }
            if (restaurantsContainingList.size() > 0) {
                Restaurant[] restaurantsContaining = new Restaurant[restaurantsContainingList.size()];
                Restaurant[] sortedRestaurants = new Restaurant[restaurantsContainingList.size()];
                for(int i =0;i<restaurantsContainingList.size();i++){
                    restaurantsContaining[i] = restaurantsContainingList.get(i);
                }
                return restaurantSort.mergeSort(restaurantsContaining,sortedRestaurants,restaurantsContaining.length, new ByName());

            } else return new Restaurant[0];
        } else {
            return new Restaurant[0];
        }

    }




//COMPARATORS
class ByName implements Comparator<Restaurant>{
    @Override
    public int compare(Restaurant r1, Restaurant r2){
        if(r1.getName().equalsIgnoreCase(r2.getName())){
            DataChecker dc = new DataChecker();
            return dc.extractTrueID(r1.getRepeatedID()).compareTo(dc.extractTrueID(r2.getRepeatedID()));
        }else{
            return r1.getName().compareToIgnoreCase(r2.getName());
        }
    }
}

class ByRestaurantID implements Comparator<Restaurant>{
    @Override
    public int compare(Restaurant r1, Restaurant r2){
        DataChecker dc = new DataChecker();
        return dc.extractTrueID(r1.getRepeatedID()).compareTo(dc.extractTrueID(r2.getRepeatedID()));
    }
}

class ByDate implements Comparator<Restaurant>{
    @Override
    public int compare(Restaurant r1, Restaurant r2){
        if(r1.getDateEstablished().equals(r2.getDateEstablished())){
            if(r1.getName().equals(r2.getName())){
                DataChecker dc = new DataChecker();
                return dc.extractTrueID(r1.getRepeatedID()).compareTo(dc.extractTrueID(r2.getRepeatedID()));
            }else{
                return r1.getName().compareTo(r2.getName());
            }
        }else{
            return r1.getDateEstablished().compareTo(r2.getDateEstablished());
        }
    }
}

class ByWarwickStars implements Comparator<Restaurant>{
    @Override
    public int compare(Restaurant r1, Restaurant r2){
        if(r1.getWarwickStars()==(r2.getWarwickStars())){
            if(r1.getName().equals(r2.getName())){
                DataChecker dc = new DataChecker();
                return dc.extractTrueID(r1.getRepeatedID()).compareTo(dc.extractTrueID(r2.getRepeatedID()));
            }else{
                return r1.getName().compareTo(r2.getName());
            }
        }else{
            return r1.getWarwickStars()-r2.getWarwickStars();
        }
    }
}

class ByRating implements Comparator<Restaurant>{
    @Override
    public int compare(Restaurant r1, Restaurant r2){
        if(r1.getCustomerRating()==r2.getCustomerRating()){
            if(r1.getName().equals(r2.getName())){
                DataChecker dc = new DataChecker();
                return dc.extractTrueID(r1.getRepeatedID()).compareTo(dc.extractTrueID(r2.getRepeatedID()));
            }else{
                return r1.getName().compareTo(r2.getName());
            }
        }
        else{
            if(r1.getCustomerRating()<r2.getCustomerRating()){
                return -1;
            }else{
                return 1;
            }
        }
    }
}

class ByDistance implements Comparator<RestaurantDistance> {
    @Override
    public int compare(RestaurantDistance r1, RestaurantDistance r2) {
        if (r1.getDistance() == r2.getDistance()) {
            DataChecker dc = new DataChecker();
            return dc.extractTrueID(r1.getRestaurant().getRepeatedID()).compareTo(dc.extractTrueID((r2.getRestaurant().getRepeatedID())));
        } else {
            if (r1.getDistance() < r2.getDistance()) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
}




