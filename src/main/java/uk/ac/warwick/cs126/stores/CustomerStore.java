package uk.ac.warwick.cs126.stores;

import uk.ac.warwick.cs126.interfaces.ICustomerStore;
import uk.ac.warwick.cs126.models.Customer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.AllPermission;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import org.apache.commons.io.IOUtils;

import javafx.scene.control.SortEvent;
import uk.ac.warwick.cs126.models.Review;
import uk.ac.warwick.cs126.structures.MyArrayList;
import uk.ac.warwick.cs126.structures.HashMap;
import uk.ac.warwick.cs126.structures.KeyValuePair;
import uk.ac.warwick.cs126.structures.SortedArrayList;


import uk.ac.warwick.cs126.structures.MySort;
import uk.ac.warwick.cs126.util.DataChecker;
import uk.ac.warwick.cs126.util.StringFormatter;

public class CustomerStore implements ICustomerStore {

    //private MyArrayList<Customer> customerArray;
    private DataChecker dataChecker;
    private HashMap<Long,Customer> customerHashMap;
    private SortedArrayList<Long> blacklistID;
    private MySort<Customer> customerSort;
    private static StringFormatter stringFormatter;

    public CustomerStore() {
        // Initialise variables here
        //customerArray = new MyArrayList<>();
        blacklistID = new SortedArrayList<>();
        customerHashMap = new HashMap<>();
        dataChecker = new DataChecker();
        customerSort = new MySort<>();
        stringFormatter = new StringFormatter();

    }

    public Customer[] loadCustomerDataToArray(InputStream resource) {
        Customer[] customerArray = new Customer[0];

        try {
            byte[] inputStreamBytes = IOUtils.toByteArray(resource);
            BufferedReader lineReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int lineCount = 0;
            String line;
            while ((line=lineReader.readLine()) != null) {
                if (!("".equals(line))) {
                    lineCount++;
                }
            }
            lineReader.close();

            Customer[] loadedCustomers = new Customer[lineCount - 1];

            BufferedReader csvReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int customerCount = 0;
            String row;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            csvReader.readLine();
            while ((row = csvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split(",");

                    Customer customer = (new Customer(
                            Long.parseLong(data[0]),
                            data[1],
                            data[2],
                            formatter.parse(data[3]),
                            Float.parseFloat(data[4]),
                            Float.parseFloat(data[5])));

                    loadedCustomers[customerCount++] = customer;
                }
            }
            csvReader.close();

            customerArray = loadedCustomers;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return customerArray;
    }

    public boolean addCustomer(Customer customer) {
        // TODO
        if(dataChecker.isValid(customer)==true){
            if(customerHashMap.find(customer.getID())==false){
                if(blacklistID.findBinary(0, blacklistID.size(), customer.getID())==null){
                    //is valid, not in hashmap, not in blacklist-add
                    //System.out.println("is valid, not in hashmap, not in blacklist");
                    customerHashMap.add(customer.getID(),customer);
                    return true;
                }else{
                    //is valid, not in hashmap, in blacklist-dont add
                    //System.out.println("is valid, not in hashmap, in blacklist");
                    return false;
                }
            }else{
                //is valid, in hashmap-add to blacklisted ids
               // System.out.println("is valid, in hashmap");
                blacklistID.addSorted(customer.getID());
               customerHashMap.remove(customer.getID());
                return false;
            }
        }
        //System.out.println("not valid");
        return false;
    }

    public boolean addCustomer(Customer[] customers) {
        // TODO-WHAT AM I SUPPOSED TO ACTUALLY RETURN
        if(customers == null) {
            return false;
        }else{
       for(int i=0;i<customers.length;i++){
           addCustomer(customers[i]);
        }
        return true;
    }
    }

    public Customer getCustomer(Long id) {
        // TODO
        //System.out.println("trying to get a customer"+ id);
        //fix the hashmap code or just check it 
      if(customerHashMap.find(id)==true){
            return customerHashMap.get(id);
        }
        return null;
        //System.out.println(customerHashMap.get(id));
        //return customerHashMap.get(id);

    }

    public Customer[] getCustomers() {
        // TODO-check for zero case
        //System.out.println("trying to get some customers");
        KeyValuePair<Long,Customer>[] customerKV = customerHashMap.getAllKeyValues();
        if(customerKV.length>0){
        Customer [] allCustomers = new Customer[customerKV.length];
        Customer [] sortedCustomers = new Customer [customerKV.length];
        for(int i =0;i<customerKV.length;i++){
            allCustomers[i] = customerKV[i].getValue();
        }

        customerSort.mergeSort(allCustomers, sortedCustomers, allCustomers.length, new ByCustomerID());       
        return sortedCustomers;
        }else {
            return new Customer[0];
        }

    }

    public Customer[] getCustomers(Customer[] customers) {
        // TODO
        //array to return 
       Customer [] sortedCustomers = new Customer [customers.length];

        customerSort.mergeSort(customers, sortedCustomers, customers.length, new ByCustomerID());
        return sortedCustomers;
    }

    public Customer[] getCustomersByName() {
        // TODO-check for zero case
        Customer [] allCustomers = getCustomers();
        Customer [] sortedCustomers = new Customer [allCustomers.length];

        
        customerSort.mergeSort(allCustomers, sortedCustomers, allCustomers.length, new ByCustomerName() );       
        return sortedCustomers;
    }

    public Customer[] getCustomersByName(Customer[] customers) {
        // TODO
        Customer [] sortedCustomers = new Customer [customers.length];
        customerSort.mergeSort(customers, sortedCustomers, customers.length, new ByCustomerName());
        return sortedCustomers;

    }

    public Customer[] getCustomersContaining(String searchTerm) {
        // TODO
        if (searchTerm != "") {
            Customer[] customers = getCustomers();
            MyArrayList<Customer> customerList = new MyArrayList<>();
            String searchTermConvertedFaster = stringFormatter.convertAccentsFaster(searchTerm).toLowerCase();
            for (int i = 0; i < customers.length; i++) {
                // System.out.println(reviews[i]);
                //String[] review = reviews[i].getReview().replaceAll("(?:[^a-zA-Z -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
                //i could make into a sortedarraylist but that doubles space
                //  for (int j = 0; j < review.length; j++) {
                if (customers[i].getFirstName().toLowerCase().contains(searchTermConvertedFaster)||customers[i].getLastName().toLowerCase().contains(searchTermConvertedFaster)) {
                    customerList.add(customers[i]);
                    //break;//hopefully only adds each review once
                }
                // }
            }
            if (customerList.size() > 0) {
                Customer[] customersContaining = new Customer[customerList.size()];
                Customer[] sortedCustomers = new Customer[customerList.size()];
                for(int i =0;i<customerList.size();i++){
                    customersContaining[i] = customerList.get(i);
                }
                return customerSort.mergeSort(customersContaining,sortedCustomers, customersContaining.length, new ByCustomerName());

            } else return new Customer[0];
        } else {
            return new Customer[0];
        }
    }

}
//COMPARATORS
class ByCustomerID implements Comparator<Customer> {

    @Override
    public int compare(Customer c1, Customer c2) {
        return Long.compare(c1.getID(), c2.getID());
    }
}

class ByCustomerName implements Comparator<Customer> {
    @Override
    public int compare(Customer c1, Customer c2) {
        if (c1.getLastName().equalsIgnoreCase(c2.getLastName())) {
            if (c1.getFirstName().equals(c2.getFirstName())) {
                return c1.getID().compareTo(c2.getID());
            } else {
                return c1.getFirstName().compareToIgnoreCase(c2.getFirstName());
            }
        } else {
            return c1.getLastName().compareToIgnoreCase(c2.getLastName());
        }
    }
}
