package dk.dtu.core.models;

import org.jmolecules.ddd.annotation.Entity;
import java.util.Objects;

@Entity
public final class Merchant {

    private String Id;
    private String firstName;
    private String lastName;
    private String bankAccountNumber;
    private String cpr;

    public Merchant(String id, String firstName, String lastName, String bankAccountNumber, String cpr) {
        this.Id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bankAccountNumber = bankAccountNumber;
        this.cpr = cpr;
    }

    public Merchant() {}

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
        Merchant customer = (Merchant) o;
        return Objects.equals(Id, customer.Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id);
    }
}


