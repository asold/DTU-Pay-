package dk.dtu;

import dk.dtu.adapters.CustomerAdapter;
import dk.dtu.adapters.MerchantAdapter;
import dk.dtu.adapters.PaymentAdapter;
import dk.dtu.core.models.*;
import dtu.ws.fastmoney.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

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

    private Payment payment;

    private TokenResult randomTokenFromCustomerList;
    private Exception exception;

    // Account

    private String dtuPayCustomerAccountRegisterResult;

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

    @And("the customer is registered with DTU Pay using their bank account")
    public void theCustomerIsRegisteredWithDTUPayUsingTheirBankAccount() throws Exception {
        // Register the customer with DTU Pay
        dtuPayCustomer.setId(customerAdapter.register(dtuPayCustomer));
    }

    @And("the customer has {int} valid token from DTU Pay")
    public void theCustomerHasAValidTokenFromDTUPay(int amount) throws Exception {
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

    @And("the merchant is registered with DTU Pay using their bank account")
    public void theMerchantIsRegisteredWithDTUPayUsingTheirBankAccount() throws Exception {
        // Register the merchant with DTU Pay
        String merchantId = merchantAdapter.register(dtuPayMerchant);
        dtuPayMerchant.setId(merchantId);
    }

    @When("the merchant initiates a payment for {int} kr using the customer's token")
    public void theMerchantInitiatesAPaymentForKrUsingTheCustomerSToken(int amount) throws Exception {
            try{
                randomTokenFromCustomerList = customerTokens.get(new Random().nextInt(customerTokens.size()));
                payment = new Payment(dtuPayMerchant.getId(), randomTokenFromCustomerList.tokenId(), new BigDecimal(amount));

                paymentResponse = paymentAdapter.requestPayment(payment);
            }catch (Exception e){
                paymentResponse = null;
                exception = e;
            }

    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertTrue(paymentResponse.successful());
    }

    @And("the balance of the customer at the bank is {int} kr")
    public void theBalanceOfTheCustomerAtTheBankIsKr(int balance) throws BankServiceException_Exception {
        Account customerAccount = bankService.getAccount(customerBankAccountNumber);
        Assert.assertEquals(new BigDecimal(balance).setScale(4), customerAccount.getBalance().setScale(4));
    }

    @And("the balance of the merchant at the bank is {int} kr")
    public void theBalanceOfTheMerchantAtTheBankIsKr(int balance) throws BankServiceException_Exception {
        Account merchantAccount = bankService.getAccount(merchantBankAccountNumber);
        Assert.assertEquals(new BigDecimal(balance).setScale(4), merchantAccount.getBalance().setScale(4));
    }

    @And("the customer's token is no longer valid")
    public void theCustomerSTokenIsNoLongerValid() throws Exception {
//        var response = paymentAdapter.requestPayment(payment);
        assertThrows("Invalid Token", Exception.class, () -> paymentAdapter.requestPayment(payment));
    }

    @Then("the payment is not successful")
    public void thePaymentIsNotSuccessful() {
        assertFalse(paymentResponse.successful());
    }

    @And("the customer has tokens, but they are invalid")
    public void theCustomerHasTokensButTheyAreInvalid() {
        customerTokens = List.of(
                new TokenResult(UUID.randomUUID()), // random token 1
                new TokenResult(UUID.randomUUID())  // random token 2
        );

        dtuPayCustomer.setTokens(customerTokens);
    }

    @And("the customer is not registered with the bank")
    public void theCustomerIsNotRegisteredWithTheBank() {
        customerBankAccountNumber = "unregistered-account";
        dtuPayCustomer.setBankAccountNumber(customerBankAccountNumber);
    }

    @And("an error message {string} is returned")
    public void anErrorMessageIsReturned(String expectedError) {
        assertNull(paymentResponse);
        assertThrows(expectedError, Exception.class, () -> paymentAdapter.requestPayment(payment));

    }

    @And("the merchant is not registered with the bank")
    public void theMerchantIsNotRegisteredWithTheBank() {
        merchantBankAccountNumber = "unregistered-account";
        dtuPayMerchant.setBankAccountNumber(merchantBankAccountNumber);
    }


    @And("the merchant is not registered with DTU Pay using their bank account")
    public void theMerchantIsNotRegisteredWithDTUPayUsingTheirBankAccount() {
        dtuPayMerchant.setId("unregistered-dtuPay-id");
    }

    @Before
    public void beforeTests() {
        System.out.println("Clean up PyamentSteps before");
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
    @After
    public void cleanUp() {
        System.out.println("Clean up PyamentSteps after");
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
