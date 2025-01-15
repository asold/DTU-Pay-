package dk.dtu.core.reposotories;

import dk.dtu.core.models.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {

    private List<Customer> customers = new ArrayList<>();

    /**
     * Saves the given customer
     * @param customer the customer
     */
    public void save(Customer customer) {
        this.customers.add(customer);
    }
}
