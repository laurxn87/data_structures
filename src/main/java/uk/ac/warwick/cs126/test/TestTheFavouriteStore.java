package uk.ac.warwick.cs126.test;

import uk.ac.warwick.cs126.models.Favourite;
import uk.ac.warwick.cs126.stores.FavouriteStore;
import uk.ac.warwick.cs126.structures.Heap;
import uk.ac.warwick.cs126.structures.KeyValuePair;
import uk.ac.warwick.cs126.structures.MyArrayList;

import java.lang.reflect.Constructor;
import java.security.Key;
import java.util.Comparator;

public class TestTheFavouriteStore extends TestRunner {
    TestTheFavouriteStore(){
        System.out.println("\n[Testing FavouriteStore]");

        // Run tests, comment out if you want to omit a test, feel free to modify or add more.
        //testAddFavourite();
        //testAddFavourites();
        //testGetFavourite();
        //testGetFavourites();
        //testGetFavouritesByCustomerID();
        //testGetFavouritesByRestaurantID();
        testGetCommonFavouriteRestaurants();
       // testGetNotCommonFavouriteRestaurants();
        //testGetMissingFavouriteRestaurants();
        //testGetTopRestaurantsByFavouriteCount();
       // testGetTopCustomersByFavouriteCount();
        //testHeap();
    }

    private void testAddFavourite() {
        try {
            // Initialise new store
            FavouriteStore favouriteStore = new FavouriteStore();

            // Create a favourite object
            // Favourite(Long favouriteID,
            //           Long customerID,
            //           Long restaurantID,
            //           Date dateFavourited)
            Favourite favourite = new Favourite(
                    1112223334445556L,
                    1112223334445557L,
                    1112223334445558L,
                    parseDate("2020-04-30")
            );

            // Add to store
            boolean result = favouriteStore.addFavourite(favourite);

            if (result) {
                System.out.println("[SUCCESS]    FavouriteStore: testAddFavourite()");
            } else {
                System.out.println(" [FAILED]    FavouriteStore: testAddFavourite()");
            }
        } catch (Exception e) {
            System.out.println(" [FAILED]    FavouriteStore: testAddFavourite()");
            e.printStackTrace();
            System.out.println();
        }
    }

    private void testAddFavourites() {
        try {
            // Initialise new store
            FavouriteStore favouriteStore = new FavouriteStore();

            // Load test data from /data folder
            Favourite[] favourites = favouriteStore.loadFavouriteDataToArray(
                    loadData("/test-favourite/favourite-10.csv"));

            // Add to store to be processed, should return true as all the data is valid
            boolean result = favouriteStore.addFavourite(favourites);

            if (result) {
                System.out.println("[SUCCESS]    FavouriteStore: testAddFavourites()");
            } else {
                System.out.println(" [FAILED]    FavouriteStore: testAddFavourites()");
            }
        } catch (Exception e) {
            System.out.println(" [FAILED]    FavouriteStore: testAddFavourites()");
            e.printStackTrace();
            System.out.println();
        }
    }

    private void testGetFavourite() {
        try {
            // Initialise new store
            FavouriteStore favouriteStore = new FavouriteStore();

            // Load test data from /data folder
            Favourite[] favourites = favouriteStore.loadFavouriteDataToArray(
                    loadData("/test-favourite/favourite-10.csv"));

            // Add to store to be processed
            favouriteStore.addFavourite(favourites);

            // Should return true as the favourite with ID 9845217889252669 exists
            boolean result = favouriteStore.getFavourite(9845217889252669L) != null
                    && favouriteStore.getFavourite(9845217889252669L).getID().equals(9845217889252669L);

            if (result) {
                System.out.println("[SUCCESS]    FavouriteStore: testGetFavourite()");
            } else {
                System.out.println(" [FAILED]    FavouriteStore: testGetFavourite()");
            }
        } catch (Exception e) {
            System.out.println(" [FAILED]    FavouriteStore: testGetFavourite()");
            e.printStackTrace();
            System.out.println();
        }
    }

    private void testGetFavourites() {
        try {
            // Initialise new store
            FavouriteStore favouriteStore = new FavouriteStore();

            // Load test data from /data folder
            Favourite[] favourites = favouriteStore.loadFavouriteDataToArray(
                    loadData("/test-favourite/favourite-10.csv"));

            // Add to store to be processed
            favouriteStore.addFavourite(favourites);

            // Get favourites sorted by ID from store
            Favourite[] gotFavourites = favouriteStore.getFavourites();

            // Load manually sorted data from /data folder to verify with
            Favourite[] expectedfavourites = favouriteStore.loadFavouriteDataToArray(
                    loadData("/test-favourite/favourite-10-sorted-by-id.csv"));

            // Now we compare
            boolean result = true;
            if (gotFavourites.length == expectedfavourites.length) {
                for (int i = 0; i < expectedfavourites.length; i++) {
                    result = gotFavourites[i].getID().equals(expectedfavourites[i].getID());
                    if(!result){
                        break;
                    }
                }
            } else {
                result = false;
            }

            // Print if wrong
            if(!result){
                System.out.println("\n[Expected]");
                for (Favourite f: expectedfavourites){
                    System.out.println(f);
                }

                System.out.println("\n[Got]");
                if (gotFavourites.length == 0) {
                    System.out.println("You got nothing!");
                }

                for (Favourite f: gotFavourites){
                    System.out.println(f);
                }

                System.out.println();
            }

            if (result) {
                System.out.println("[SUCCESS]    FavouriteStore: testGetFavourites()");
            } else {
                System.out.println(" [FAILED]    FavouriteStore: testGetFavourites()");
            }
        } catch (Exception e) {
            System.out.println(" [FAILED]    FavouriteStore: testGetFavourites()");
            e.printStackTrace();
            System.out.println();
        }
    }

    private void testGetFavouritesByCustomerID() {
        try {
            //TODO
            FavouriteStore favouriteStore = new FavouriteStore();

            // Load test data from /data folder
            Favourite[] favourites = favouriteStore.loadFavouriteDataToArray(
                    loadData("/test-favourite/favourite-10.csv"));

            // Add to store to be processed
            favouriteStore.addFavourite(favourites);

            // Get favourites sorted by ID from store
            Favourite[] gotFavourites = favouriteStore.getFavouritesByRestaurantID(9899443627821832L);
            for(int i =0;i<gotFavourites.length;i++){
                System.out.println(gotFavourites[i]);
            }
            boolean result = false;

            if (result) {
                System.out.println("[SUCCESS]    FavouriteStore: testGetFavouritesByCustomerID()");
            } else {
                System.out.println(" [FAILED]    FavouriteStore: testGetFavouritesByCustomerID()");
            }
        } catch (Exception e) {
            System.out.println(" [FAILED]    FavouriteStore: testGetFavouritesByCustomerID()");
            e.printStackTrace();
            System.out.println();
        }
    }

    private void testGetFavouritesByRestaurantID() {
        try {
            //TODO
            FavouriteStore favouriteStore = new FavouriteStore();

            // Load test data from /data folder
            Favourite[] favourites = favouriteStore.loadFavouriteDataToArray(
                    loadData("/test-favourite/favourite-10.csv"));

            // Add to store to be processed
            favouriteStore.addFavourite(favourites);

            // Get favourites sorted by ID from store
            Favourite[] gotFavourites = favouriteStore.getFavouritesByRestaurantID(9899443627821832L);
            for(int i =0;i<gotFavourites.length;i++){
                System.out.println(gotFavourites[i]);
            }
            boolean result = false;

            if (result) {
                System.out.println("[SUCCESS]    FavouriteStore: testGetFavouritesByRestaurantID()");
            } else {
                System.out.println(" [FAILED]    FavouriteStore: testGetFavouritesByRestaurantID()");
            }
        } catch (Exception e) {
            System.out.println(" [FAILED]    FavouriteStore: testGetFavouritesByRestaurantID()");
            e.printStackTrace();
            System.out.println();
        }
    }

    private void testGetCommonFavouriteRestaurants() {
        try {
            //TODO
            FavouriteStore favouriteStore = new FavouriteStore();

            // Load test data from /data folder
            Favourite[] favourites = favouriteStore.loadFavouriteDataToArray(
                    loadData("/test-favourite/favourite-10.csv"));

            // Add to store to be processed
            favouriteStore.addFavourite(favourites);

            // Get favourites sorted by ID from store
            Long[] gotFavourites = favouriteStore.getCommonFavouriteRestaurants(6676894422433391L, 9842524693141867L);
            for (int i = 0; i < gotFavourites.length; i++) {
                System.out.println(gotFavourites[i]);
            }
            boolean result = false;

            if (result) {
                System.out.println("[SUCCESS]    FavouriteStore: testGetCommonFavouriteRestaurants()");
            } else {
                System.out.println(" [FAILED]    FavouriteStore: testGetCommonFavouriteRestaurants()");
            }
        } catch (Exception e) {
            System.out.println(" [FAILED]    FavouriteStore: testGetCommonFavouriteRestaurants()");
            e.printStackTrace();
            System.out.println();
        }
    }

    private void testGetMissingFavouriteRestaurants() {
        try {
            //TODO
            FavouriteStore favouriteStore = new FavouriteStore();

            // Load test data from /data folder
            Favourite[] favourites = favouriteStore.loadFavouriteDataToArray(
                    loadData("/test-favourite/favourite-10.csv"));

            // Add to store to be processed
            favouriteStore.addFavourite(favourites);

            // Get favourites sorted by ID from store
            Long[] gotFavourites = favouriteStore.getMissingFavouriteRestaurants(2649541714385159L, 9842524693141867L);
            for (int i = 0; i < gotFavourites.length; i++) {
                System.out.println(gotFavourites[i]);
            }
            boolean result = false;

            if (result) {
                System.out.println("[SUCCESS]    FavouriteStore: testGetMissingFavouriteRestaurants()");
            } else {
                System.out.println(" [FAILED]    FavouriteStore: testGetMissingFavouriteRestaurants()");
            }
        } catch (Exception e) {
            System.out.println(" [FAILED]    FavouriteStore: testGetMissingFavouriteRestaurants()");
            e.printStackTrace();
            System.out.println();
        }
    }

    private void testGetNotCommonFavouriteRestaurants() {
        try {
            //TODO
            FavouriteStore favouriteStore = new FavouriteStore();

            // Load test data from /data folder
            Favourite[] favourites = favouriteStore.loadFavouriteDataToArray(
                    loadData("/test-favourite/favourite-10.csv"));

            // Add to store to be processed
            favouriteStore.addFavourite(favourites);

            // Get favourites sorted by ID from store
            Long[] gotFavourites = favouriteStore.getNotCommonFavouriteRestaurants(2649541714385159L, 9842524693141867L);
            for (int i = 0; i < gotFavourites.length; i++) {
                System.out.println(gotFavourites[i]);
            }
            boolean result = false;

            if (result) {
                System.out.println("[SUCCESS]    FavouriteStore: testGetNotCommonFavouriteRestaurants()");
            } else {
                System.out.println(" [FAILED]    FavouriteStore: testGetNotCommonFavouriteRestaurants()");
            }
        } catch (Exception e) {
            System.out.println(" [FAILED]    FavouriteStore: testGetNotCommonFavouriteRestaurants()");
            e.printStackTrace();
            System.out.println();
        }
    }

    private void testGetTopCustomersByFavouriteCount() {
        try {
            //TODO
            FavouriteStore favouriteStore = new FavouriteStore();

            // Load test data from /data folder
            Favourite[] favourites = favouriteStore.loadFavouriteDataToArray(
                    loadData("/test-favourite/favourite-10.csv"));

            // Add to store to be processed
            favouriteStore.addFavourite(favourites);

            // Get favourites sorted by ID from store
            Long[] gotFavourites = favouriteStore.getTopCustomersByFavouriteCount();
            for (int i = 0; i < gotFavourites.length; i++) {
                System.out.println(gotFavourites[i]);
            }
            boolean result = false;

            if (result) {
                System.out.println("[SUCCESS]    FavouriteStore: testGetTopCustomersByFavouriteCount()");
            } else {
                System.out.println(" [FAILED]    FavouriteStore: testGetTopCustomersByFavouriteCount()");
            }
        } catch (Exception e) {
            System.out.println(" [FAILED]    FavouriteStore: testGetTopCustomersByFavouriteCount()");
            e.printStackTrace();
            System.out.println();
        }
    }

    private void testGetTopRestaurantsByFavouriteCount() {
        try {
            //TODO
            FavouriteStore favouriteStore = new FavouriteStore();

            // Load test data from /data folder
            Favourite[] favourites = favouriteStore.loadFavouriteDataToArray(
                    loadData("/test-favourite/favourite-10.csv"));

            // Add to store to be processed
            favouriteStore.addFavourite(favourites);

            // Get favourites sorted by ID from store
            Long[] gotFavourites = favouriteStore.getTopRestaurantsByFavouriteCount();
            for (int i = 0; i < gotFavourites.length; i++) {
                System.out.println(gotFavourites[i]);}
            boolean result = false;

            if (result) {
                System.out.println("[SUCCESS]    FavouriteStore: testGetTopRestaurantsByFavouriteCount()");
            } else {
                System.out.println(" [FAILED]    FavouriteStore: testGetTopRestaurantsByFavouriteCount()");
            }
        } catch (Exception e) {
            System.out.println(" [FAILED]    FavouriteStore: testGetTopRestaurantsByFavouriteCount()");
            e.printStackTrace();
            System.out.println();
        }
    }
    private void testHeap(){
       /* Heap<KeyValuePair<Integer,String>> heap = new Heap<KeyValuePair<Integer,String>>(5, new c());
        Heap<String> heap2 = new Heap<String>(5, new stringc());

        MyArrayList<KeyValuePair<Integer,String>> list = new MyArrayList<>();
        String[] words = {"offputting","zero","giggles","shoot","music","horrendous","ate","chimney","alright",
                "tense","missed","chicken","spare","middle","twenty","called","drink","back","rock","paper","scissors",
                "boy","minimum","maximum"};
        KeyValuePair<Integer,String>[] thing = new KeyValuePair[words.length];
       for(int i =0;i<words.length;i++){
           thing[i] = new KeyValuePair<>(words[i].length(),words[i]);
        }
        /*,"ate","chimney","alright",
        "tense","missed","chicken","spare","middle","twenty","called","drink","back","rock","paper","scissors",
        "boy","minimum","maximum"
        KeyValuePair<Integer,String>[] top5 = new KeyValuePair[5];
        String[] stuff = new String[5];
                heap.topK(thing, top5,5,new c());
        for(int i =0;i<top.length;i++){
            System.out.println(top[i].getValue() + " " + top[i].getKey());
        }
        for(int i =0;i<top5.length;i++){
            System.out.println(top5[i].getValue());
            //System.out.println(stuff[i]);
        }*/
    }

}
class c implements Comparator <KeyValuePair<Integer, String>> {
    @Override
    public int compare(KeyValuePair<Integer,String> kv1,KeyValuePair<Integer,String> kv2){
        if(kv1.getKey().equals(kv2.getKey())){
            return (-1)*kv1.getValue().compareTo(kv2.getValue());
        }else{
            return kv1.getKey().compareTo(kv2.getKey());
        }

    }
}
class stringc implements Comparator<String>{
    @Override
    public int compare(String s1, String s2){
        return (-1)*s1.compareTo(s2);
    }
}