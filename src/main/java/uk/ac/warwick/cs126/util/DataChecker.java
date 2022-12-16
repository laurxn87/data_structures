package uk.ac.warwick.cs126.util;

import uk.ac.warwick.cs126.interfaces.IDataChecker;

//import uk.ac.warwick.cs126.structures.MySort;

import uk.ac.warwick.cs126.models.Customer;
import uk.ac.warwick.cs126.models.Restaurant;
import uk.ac.warwick.cs126.models.Favourite;
import uk.ac.warwick.cs126.models.Review;

//import java.util.Date;

public class DataChecker implements IDataChecker {

    public DataChecker() {
        // Initialise things here
    }

    public Long extractTrueID(String[] repeatedID) {
        // TODO
        // check array has exactly 3 elements if not return null if so
        if (repeatedID.length == 3) {
            //check each has 16 characters and check all 3 permutations
            if(repeatedID[0].length()==16&&repeatedID[0].equals(repeatedID[1])){
                return Long.parseLong(repeatedID[0]);
            }
            else if(repeatedID[1].length()==16&&repeatedID[1].equals(repeatedID[2])){
                return Long.parseLong(repeatedID[1]);
            }
            else if(repeatedID[2].length()==16&&repeatedID[2].equals(repeatedID[0])){
                return Long.parseLong(repeatedID[2]);
            }else{
                return null;
            }
            
        } else {
            return null;
        }

    }

    public boolean isValid(Long inputID) {
        // TODO

        String stringID = Long.toString(inputID);
        int[] digits = new int[10];
        if (stringID.length() == 16) {
            for (int i = 0; i < stringID.length(); i++){
                char c = stringID.charAt(i);
                switch (c) {
                case '1':
                    digits[0]++;
                    break;
                case '2':
                    digits[1]++;
                    break;
                case '3':
                    digits[2]++;
                    break;
                case '4':
                    digits[3]++;
                    break;
                case '5':
                    digits[4]++;
                    break;
                case '6':
                    digits[5]++;
                    break;
                case '7':
                    digits[6]++;
                    break;
                case '8':
                    digits[7]++;
                    break;
                case '9':
                    digits[8]++;
                    break;
                default:
                //if there is a 0 or any other character
                    return false;
                }
            }
            for (int i = 0; i < digits.length; i++) {
                if (digits[i] > 3) {
                    return false;
                }
            }
            return true;

        }
        return false;
    }

    public boolean isValid(Customer customer) {
        // TODO
        if (customer != null && customer.getFirstName() != null && customer.getLastName() != null
                && customer.getID() != null && isValid(customer.getID()) == true && customer.getDateJoined() != null
                && customer.getLongitude() != 0 && customer.getLatitude() != 0 && isValid(customer.getID()) == true) {
            return true;
        }
        return false;
    }

    public boolean isValid(Restaurant restaurant) {
        // TODO-left out latitude and longitude becuase they can be 0
        if (restaurant != null && extractTrueID(restaurant.getRepeatedID()) != null
                && isValid(extractTrueID(restaurant.getRepeatedID())) == true && restaurant.getStringID() != null
                && restaurant.getName() != null && restaurant.getOwnerFirstName() != null
                && restaurant.getOwnerLastName() != null && restaurant.getCuisine() != null
                && restaurant.getEstablishmentType() != null && restaurant.getPriceRange() != null
                && restaurant.getDateEstablished() != null && restaurant.getLastInspectedDate() != null
                && restaurant.getLastInspectedDate().compareTo(restaurant.getDateEstablished()) >= 0
                && (restaurant.getFoodInspectionRating() == 0 || restaurant.getFoodInspectionRating() == 1
                        || restaurant.getFoodInspectionRating() == 2 || restaurant.getFoodInspectionRating() == 3
                        || restaurant.getFoodInspectionRating() == 4 || restaurant.getFoodInspectionRating() == 5)
                && (restaurant.getWarwickStars() == 0 || restaurant.getWarwickStars() == 1
                        || restaurant.getWarwickStars() == 2 || restaurant.getWarwickStars() == 3)
                && (restaurant.getCustomerRating() == 0
                        || (restaurant.getCustomerRating() >= 1.0f && restaurant.getCustomerRating() <= 5.0f))) {
            return true;
        }
        return false;

    }

    public boolean isValid(Favourite favourite) {
        // TODO
        if (favourite!= null && favourite.getID() != null && isValid(favourite.getID()) == true && favourite.getCustomerID() != null
                && isValid(favourite.getCustomerID()) == true && favourite.getRestaurantID() != null
                && isValid(favourite.getRestaurantID()) == true && favourite.getDateFavourited() != null) {
            return true;
        }
        return false;
    }

    public boolean isValid(Review review) {
        // TODO-not sure about the rating
        if (review!=null && review.getID() != null && isValid(review.getID()) == true && review.getCustomerID() != null
                && isValid(review.getCustomerID()) == true && review.getRestaurantID() != null
                && isValid(review.getRestaurantID()) == true && review.getDateReviewed() != null
                && review.getReview() != null) {
            return true;
        }
        return false;
    }
}