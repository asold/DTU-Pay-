package dk.dtu;

import dk.dtu.core.models.*;
import dk.dtu.adapters.CustomerAdapter;
import dk.dtu.adapters.MerchantAdapter;
import dk.dtu.adapters.PaymentAdapter;
import dtu.ws.fastmoney.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class PaymentSteps {

    private BankService bankService = new BankServiceService().getBankServicePort();

    private User bankCustomer;
    private User bankMerchant;

    private CustomerAdapter customerAdapter = new CustomerAdapter();
    private PaymentAdapter paymentAdapter = new PaymentAdapter();
    private MerchantAdapter merchantAdapter = new MerchantAdapter();

    private Customer dtuPayCustomer;
    private Merchant dtuPayMerchant = new Merchant();

    private String customerBankAccountNumber;
    private String merchantBankAccountNumber;

    private List<TokenResult> customerTokens;
    private PaymentResponse paymentResponse;

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

    @And("the customer has {int} valid token from DTU Pay")
    public void theCustomerHasAValidTokenFromDTUPay(int amount) {
        customerTokens = customerAdapter.getTokens(dtuPayCustomer.getId(), amount ); // the customer class has a field for this ->
        dtuPayCustomer.setTokens(customerTokens);
    }

    @And("a merchant with name {string}, last name {string}, and CPR {string}")
    public void aMerchantWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        // Initialize the User model from the bank integration
        bankMerchant = new User();
        bankMerchant.setFirstName(firstName);
        bankMerchant.setLastName(lastName);
        bankMerchant.setCprNumber(cpr);

        // Initialize the dtuPayCustomer model
        dtuPayMerchant = new Merchant();
        dtuPayMerchant.setFirstName(firstName);
        dtuPayMerchant.setLastName(lastName);
        dtuPayMerchant.setCpr(cpr);
    }

    @And("the merchant is registered with the bank with an initial balance of {int} kr")
    public void theMerchantIsRegisteredWithTheBankWithAnInitialBalanceOfKr(int balance) throws BankServiceException_Exception {
        merchantBankAccountNumber = bankService.createAccountWithBalance(this.bankMerchant, new BigDecimal(balance));
        dtuPayMerchant.setBankAccountNumber(merchantBankAccountNumber);
    }

    @And("the merchant is registered with Simple DTU Pay using their bank account")
    public void theMerchantIsRegisteredWithSimpleDTUPayUsingTheirBankAccount() {
        // Register the merchant with DTU Pay
        String merchantId = merchantAdapter.register(dtuPayMerchant);
        dtuPayMerchant.setId(merchantId);
    }

    @When("the merchant initiates a payment for {int} kr using the customer's token")
    public void theMerchantInitiatesAPaymentForKrUsingTheCustomerSToken(int amount) {
        TokenResult randomTokenFromCustomerList = customerTokens.get(new Random().nextInt(customerTokens.size()));
        Payment payment = new Payment(dtuPayMerchant.getId(), randomTokenFromCustomerList.tokenId(), new BigDecimal(amount));

        paymentResponse = paymentAdapter.requestPayment(payment);
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertTrue(paymentResponse.successful());
    }
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


    @Before
    public void beforeTests() {
        System.out.println("Clean up");
        try {
            Account customerAccount = bankService.getAccountByCprNumber("250103-7220");
            if (customerAccount != null) {
                bankService.retireAccount(customerAccount.getId());
            }

            Account merchantAccount = bankService.getAccountByCprNumber("241902-7253");
            if (merchantAccount != null) {
                bankService.retireAccount(merchantAccount.getId());
            }
        } catch (BankServiceException_Exception e) {
//            throw new RuntimeException(e);
            System.out.println(e.getMessage());
        }
    }
    @After
    public void cleanUp() {
        try {
            if (customerBankAccountNumber != null) {
                bankService.retireAccount(customerBankAccountNumber);
            }

            if (merchantBankAccountNumber != null) {
                bankService.retireAccount(merchantBankAccountNumber);
            }
        } catch (BankServiceException_Exception e) {
            throw new RuntimeException(e);
        }

    }

}
