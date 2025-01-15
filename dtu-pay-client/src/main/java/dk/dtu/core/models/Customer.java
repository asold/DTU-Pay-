package dk.dtu.core.models;

import org.jmolecules.ddd.annotation.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a customer entity within the application.
 *
 * @author
 *   Andrei Soldan s243873
 *   Maxim Zavidei s240
 *   Mihály Tass
 *   Karrar Adam
 */
@Entity
public final class Customer {

    private String Id;
    private String firstName;
    private String lastName;
    private String bankAccountNumber;
    private String cpr;
    private List<Token> tokens;

    public Customer(String id, String firstName, String lastName, String bankAccountNumber, String cpr) {
        this.Id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bankAccountNumber = bankAccountNumber;
        this.cpr = cpr;
        this.tokens = new ArrayList<Token>();
    }

    public Customer() {}

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(Id, customer.Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id);
    }
}


