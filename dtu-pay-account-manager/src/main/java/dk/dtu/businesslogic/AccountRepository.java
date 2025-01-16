package dk.dtu.businesslogic;

import dk.dtu.models.Merchant;

import java.util.HashSet;
import java.util.Set;

public class AccountRepository {

    private Set<Customer> customers = new HashSet<>();
    private Set<Merchant> merchants = new HashSet<>();

    public AccountRepository() {}

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void addMerchant(Merchant merchant) {
        merchants.add(merchant);
    }
}
