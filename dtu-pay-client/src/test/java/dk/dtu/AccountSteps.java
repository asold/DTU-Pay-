package dk.dtu;

import dk.dtu.adapters.CustomerAdapter;
import dk.dtu.adapters.MerchantAdapter;
import dk.dtu.core.models.Customer;
import dk.dtu.core.models.Merchant;
import dtu.ws.fastmoney.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AccountSteps {

    private final BankService bankService = new BankServiceService().getBankServicePort();

    private User bankCustomer;
    private User bankMerchant;

    private final CustomerAdapter customerAdapter = new CustomerAdapter();
    private final MerchantAdapter merchantAdapter = new MerchantAdapter();

    private Customer dtuPayCustomer;
    private Merchant dtuPayMerchant = new Merchant();

    private String customerBankAccountNumber;
    private String merchantBankAccountNumber;

    private String deregisterCustomerIdResult;
    private String deregisterMerchantIdResult;

    private Exception registrationexception;


    @Given("a accountTest customer with name {string}, last name {string}, and CPR {string}")
    public void aAccountTestCustomerWithNameLastNameAndCpr(String firstName, String lastName, String cpr) {
        // Initialize the User model from the bank integration
        bankCustomer = new User();
        bankCustomer.setFirstName(firstName == null || firstName.isEmpty() ? "testFirstName" : firstName);
        bankCustomer.setLastName(lastName == null || lastName.isEmpty() ? "testLastName" : lastName);
        bankCustomer.setCprNumber(cpr == null || cpr.isEmpty() ? "testCprNumber" : cpr);

        // Initialize the dtuPayCustomer model
        dtuPayCustomer = new Customer();
        dtuPayCustomer.setFirstName(firstName);
        dtuPayCustomer.setLastName(lastName);
        dtuPayCustomer.setCpr(cpr);
    }

    @Given("the accountTest customer is registered with the bank with an initial balance of {int} kr")
    public void theAccountTestCustomerIsRegisteredWithTheBankWithAnInitialBalanceOfKr(Integer balance) throws BankServiceException_Exception {
        customerBankAccountNumber = bankService.createAccountWithBalance(this.bankCustomer, new BigDecimal(balance));
        dtuPayCustomer.setBankAccountNumber(customerBankAccountNumber);
    }

    @When("the accountTest customer registers with DTUPay")
    public void theAccountTestCustomerRegistersWithDtuPay() {
        // Register the customer with DTU Pay
        try {
            dtuPayCustomer.setId(customerAdapter.register(dtuPayCustomer));
        } catch (Exception e) {
            registrationexception = e;
        }

    }

    @Then("the customer registration is successful")
    public void theCustomerRegistrationIsSuccessful() {
        assertNotNull(dtuPayCustomer.getId());
    }

    @And("the accountTest customer is registered with DTUPay")
    public void theAccountTestCustomerIsRegisteredWithDTUPay() {
        try {
            dtuPayCustomer.setId(customerAdapter.register(dtuPayCustomer));
        } catch (Exception e) {
            registrationexception = e;
        }

    }

    @When("the accountTest customer deregisters from DTUPay")
    public void theAccountTestCustomerDeregistersFromDTUPay() {
        deregisterCustomerIdResult = customerAdapter.deregister(dtuPayCustomer.getId());
    }

    @Then("the customer account deregistration is successful")
    public void theCustomerAccountDeregistrationIsSuccessful() {
        assertEquals(dtuPayCustomer.getId(), deregisterCustomerIdResult);
    }

    @Given("a accountTest merchant with name {string}, last name {string}, and CPR {string}")
    public void aAccountTestMerchantWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        // Initialize the User model from the bank integration
        bankMerchant = new User();
        bankMerchant.setFirstName(firstName == null || firstName.isEmpty() ? "testFirstName" : firstName);
        bankMerchant.setLastName(lastName == null || lastName.isEmpty() ? "testLastName" : lastName);
        bankMerchant.setCprNumber(cpr == null || cpr.isEmpty() ? "testCprNumber" : cpr);

        // Initialize the dtuPayCustomer model
        dtuPayMerchant = new Merchant();
        dtuPayMerchant.setFirstName(firstName);
        dtuPayMerchant.setLastName(lastName);
        dtuPayMerchant.setCpr(cpr);

    }

    @And("the accountTest merchant is registered with the bank with an initial balance of {int} kr")
    public void theAccountTestMerchantIsRegisteredWithTheBankWithAnInitialBalanceOfKr(int balance) throws BankServiceException_Exception {
        merchantBankAccountNumber = bankService.createAccountWithBalance(this.bankMerchant, new BigDecimal(balance));
        dtuPayMerchant.setBankAccountNumber(merchantBankAccountNumber);

    }

    @When("the accountTest merchant registers in DTU Pay")
    public void theAccountTestMerchantRegistersInDTUPay() {
        try {
            dtuPayMerchant.setId(merchantAdapter.register(dtuPayMerchant));
        } catch (Exception e) {
            registrationexception = e;
        }

    }

    @Then("the merchant registration is successful")
    public void theMerchantRegistrationIsSuccessful() {
        assertNotNull(dtuPayMerchant.getId());
    }

    @And("the accountTest merchant is registered with DTUPay")
    public void theAccountTestMerchantIsRegisteredWithDTUPay() {
        try {
            dtuPayMerchant.setId(merchantAdapter.register(dtuPayMerchant));
        } catch (Exception e) {
            registrationexception = e;
        }

    }

    @When("the accountTest merchant deregisters from DTUPay")
    public void theAccountTestMerchantDeregistersFromDTUPay() {
        deregisterMerchantIdResult = merchantAdapter.deregister(dtuPayMerchant.getId());
    }

    @Then("the merchant account deregistration is successful")
    public void theMerchantAccountDeregistrationIsSuccessful() {
        assertEquals(dtuPayMerchant.getId(), deregisterMerchantIdResult);
    }

    @Then("the accountTest customer registration fails")
    public void theAccountTestCustomerRegistrationFails() {
        assertNotNull(registrationexception);
    }

    @And("the error message {string} is returned")
    public void theErrorMessageIsReturned(String msg) {
        assertEquals(msg, registrationexception.getMessage());
    }

    @And("the accountTest customer is not registered with the bank")
    public void theAccountTestCustomerIsNotRegisteredWithTheBank() {
        dtuPayCustomer.setBankAccountNumber("");
    }


    @Then("the accountTest merchant registration fails")
    public void theAccountTestMerchantRegistrationFails() {
        assertNotNull(registrationexception);
    }

    @And("the accountTest merchant is not registered with the bank")
    public void theAccountTestMerchantIsNotRegisteredWithTheBank() {
        dtuPayMerchant.setBankAccountNumber("");
    }


    @Before("@Account")
    public void beforeTests() {
        System.out.println("Clean up");
        try{
            Account emptyCprAccount = bankService.getAccountByCprNumber("testCprNumber");
            if (emptyCprAccount != null) {
                bankService.retireAccount(emptyCprAccount.getId());
            }
        }catch(BankServiceException_Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            Account customerAccount = bankService.getAccountByCprNumber("250103-7220");
            if (customerAccount != null) {
                bankService.retireAccount(customerAccount.getId());
            }
        }catch(BankServiceException_Exception e) {
            System.err.println(e.getMessage());
        }
        try{
            Account merchantAccount = bankService.getAccountByCprNumber("241902-7253");
            if (merchantAccount != null) {
                bankService.retireAccount(merchantAccount.getId());
            }
        }
        catch (BankServiceException_Exception e) {
            System.err.println(e.getMessage());
        }
    }
    @After("@Account")
    public void cleanUp() {
        try {
            if (customerBankAccountNumber != null && !customerBankAccountNumber.equals("unregistered-account")) {
                bankService.retireAccount(customerBankAccountNumber);
            }

            if (merchantBankAccountNumber != null && !merchantBankAccountNumber.equals("unregistered-account")) {
                bankService.retireAccount(merchantBankAccountNumber);
            }
        } catch (BankServiceException_Exception e) {
            throw new RuntimeException(e);
        }

    }
}
