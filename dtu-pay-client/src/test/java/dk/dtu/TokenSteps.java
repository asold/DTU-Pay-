package dk.dtu;

import dk.dtu.adapters.CustomerAdapter;
import dk.dtu.core.models.Customer;
import dtu.ws.fastmoney.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author Simao Teixeira (s232431)
 */
public class TokenSteps {

    private final CustomerAdapter customerAdapter = new CustomerAdapter();

    private Customer dtuPayCustomer;
    private String customerBankAccountNumber;
    private final BankService bank = new BankServiceService().getBankServicePort();
    private String errorMessageReturned;

    @Given("a customer of DTU pay")
    public void aCustomerOfDTUPay() throws BankServiceException_Exception {
        // Create Bank Account
        var bankCustomer = new User();
        bankCustomer.setFirstName("FirstName");
        bankCustomer.setLastName("LastName");
        bankCustomer.setCprNumber("170804-1435");
        customerBankAccountNumber = bank.createAccountWithBalance(bankCustomer, new BigDecimal(10));

        // Create DTUPay Customer
        dtuPayCustomer = new Customer(bankCustomer);
        dtuPayCustomer.setBankAccountNumber(customerBankAccountNumber);
        dtuPayCustomer.setId(customerAdapter.register(dtuPayCustomer));
    }

    @And("the customer has {int} valid tokens")
    public void theCustomerHasValidTokens(int arg0) {
    }

    @When("the customer requests {int} tokens")
    public void theCustomerRequestsTokens(int tokenAmount) {
        try {
            dtuPayCustomer.setTokens(customerAdapter.getTokens(dtuPayCustomer.getId(), tokenAmount));
        } catch (Exception e) {
            errorMessageReturned = e.getMessage();
        }
    }

    @Then("{int} tokens are generated for the customer")
    public void tokensAreGeneratedForTheCustomer(int tokenAmount) {
        Assert.assertEquals(tokenAmount, dtuPayCustomer.getTokens().size());
    }

    @And("the customer receives the error message {string}")
    public void theCustomerReceivesTheErrorMessage(String errorMessage) {
        Assert.assertEquals(errorMessage, errorMessageReturned);
    }

    @Given("a customer not registered in DTUPay")
    public void aCustomerNotRegisteredInDTUPay() {
        dtuPayCustomer = new Customer();
        dtuPayCustomer.setFirstName("FirstName");
        dtuPayCustomer.setLastName("LastName");
        dtuPayCustomer.setCpr("170804-1435");
        dtuPayCustomer.setId(UUID.randomUUID().toString());
    }

    @Before
    public void beforeTests() {
        System.out.println("Clean up before tests");
        try {
            Account customerAccount = bank.getAccountByCprNumber("170804-1435");
            if (customerAccount != null) {
                bank.retireAccount(customerAccount.getId());
            }
        } catch (BankServiceException_Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @After
    public void cleanUp() {
        System.out.println("Clean up after tests");
        try {
            if (customerBankAccountNumber != null) {
                bank.retireAccount(customerBankAccountNumber);
            }
        } catch (BankServiceException_Exception e) {
            throw new RuntimeException(e);
        }
    }
}
