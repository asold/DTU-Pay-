package dk.dtu.businesslogic.repositories;

import dk.dtu.businesslogic.models.Customer;
import dk.dtu.businesslogic.models.Merchant;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Mihály Tass
 */
public class AccountRepository {

    private final Set<Customer> customers = new HashSet<>();
    private final Set<Merchant> merchants = new HashSet<>();

    public AccountRepository() {}

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void addMerchant(Merchant merchant) {
        merchants.add(merchant);
    }

    public void deleteCustomer(Customer customer) {
        customers.remove(customer); }

    public void deleteMerchant(Merchant merchant) {
        merchants.remove(merchant);}

    public Optional<Customer> getCustomerById(String id) {
        return customers.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public Optional<Merchant> getMerchantById(String id) {
        return merchants.stream().filter(m -> m.getId().equals(id)).findFirst();
    }
}
