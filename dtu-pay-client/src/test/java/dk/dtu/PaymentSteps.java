package dk.dtu;

import dk.dtu.core.models.Customer;
import dk.dtu.adapters.CustomerAdapter;
import dk.dtu.adapters.MerchantAdapter;
import dk.dtu.adapters.PaymentAdapter;
import dk.dtu.core.models.Merchant;
import dtu.ws.fastmoney.*;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;

public class PaymentSteps {

    private BankService bankService = new BankServiceService().getBankServicePort();

    private User bankCustomer;

    private CustomerAdapter customerAdapter = new CustomerAdapter();
    private PaymentAdapter paymentAdapter = new PaymentAdapter();
    private MerchantAdapter merchantAdapter = new MerchantAdapter();

    private Customer dtuPayCustomer;
    private Merchant dtuPayMerchant = new Merchant();

    private String customerBankAccountNumber;
    private String merchantBankAccountNumber;




    @Given("a customer with name {string}, last name {string}, and CPR {string}")
    public void aCustomerWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        // Initialize the User model from the bank integration
        bankCustomer = new User();
        bankCustomer.setFirstName(firstName);
        bankCustomer.setLastName(lastName);
        bankCustomer.setCprNumber(cpr);

        // Initialize the dtuPayCustomer model
        dtuPayCustomer = new Customer();
        dtuPayCustomer.setFirstName(firstName);
        dtuPayCustomer.setLastName(lastName);
        dtuPayCustomer.setCpr(cpr);
    }

    @And("the customer is registered with the bank with an initial balance of {int} kr")
    public void theCustomerIsRegisteredWithTheBankWithAnInitialBalanceOfKr(int balance) throws BankServiceException_Exception {
        customerBankAccountNumber = bankService.createAccountWithBalance(this.bankCustomer, new BigDecimal(balance));
        dtuPayCustomer.setBankAccountNumber(customerBankAccountNumber);
    }

    @And("the customer is registered with Simple DTU Pay using their bank account")
    public void theCustomerIsRegisteredWithSimpleDTUPayUsingTheirBankAccount() {
        // Register the customer with DTU Pay
        dtuPayCustomer.setId(customerAdapter.register(dtuPayCustomer));
    }

//    @And("the customer has a valid token from DTU Pay")
//    public void theCustomerHasAValidTokenFromDTUPay() {
//
//    }
//
//    @And("a merchant with name {string}, last name {string}, and CPR {string}")
//    public void aMerchantWithNameLastNameAndCPR(String arg0, String arg1, String arg2) {
//
//    }
//
//    @And("the merchant is registered with the bank with an initial balance of {int} kr")
//    public void theMerchantIsRegisteredWithTheBankWithAnInitialBalanceOfKr(int arg0) {
//
//    }
//
//    @And("the merchant is registered with Simple DTU Pay using their bank account")
//    public void theMerchantIsRegisteredWithSimpleDTUPayUsingTheirBankAccount() {
//
//    }
//
//    @When("the merchant initiates a payment for {int} kr using the customer's token")
//    public void theMerchantInitiatesAPaymentForKrUsingTheCustomerSToken(int arg0) {
//
//    }
//
//    @Then("the payment is successful")
//    public void thePaymentIsSuccessful() {
//
//    }
//
//    @And("the balance of the customer at the bank is {int} kr")
//    public void theBalanceOfTheCustomerAtTheBankIsKr(int arg0) {
//
//    }
//
//    @And("the balance of the merchant at the bank is {int} kr")
//    public void theBalanceOfTheMerchantAtTheBankIsKr(int arg0) {
//
//    }
//
//    @And("the customer's token is no longer valid")
//    public void theCustomerSTokenIsNoLongerValid() {
//    }



    @After
    public void cleanUp() {
        System.out.println("Clean up");
        try {
            Account customerAccount = bankService.getAccountByCprNumber(dtuPayCustomer.getCpr());
            if (customerAccount != null) {
                bankService.retireAccount(customerAccount.getId());
            }

            Account merchantAccount = bankService.getAccountByCprNumber(dtuPayMerchant.getCpr());
            if (merchantAccount != null) {
                bankService.retireAccount(merchantAccount.getId());
            }
        } catch (BankServiceException_Exception e) {
            throw new RuntimeException(e);
        }

    }
}
