package dk.dtu.core.services;

import dk.dtu.core.models.Customer;
import dk.dtu.core.reposotories.CustomerRepository;

import java.util.UUID;

public class CustomerService {

    private CustomerRepository customerRepository = new CustomerRepository();

    /**
     * Registers the given customer to DtuPay
     * @param customer the customer
     * @return generated customer ID
     */
    public String registerCustomer(Customer customer) {
        String uuid = UUID.randomUUID().toString();
        customer.setId(uuid);
        customerRepository.save(customer);
        return customer.getId();
    }
}
