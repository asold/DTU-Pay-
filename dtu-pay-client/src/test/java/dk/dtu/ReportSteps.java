package dk.dtu;

import dk.dtu.adapters.CustomerAdapter;
import dk.dtu.adapters.MerchantAdapter;
import dk.dtu.adapters.PaymentAdapter;
import dk.dtu.adapters.ReportAdapter;
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

import static org.junit.Assert.*;

public class ReportSteps {

    private BankService bankService = new BankServiceService().getBankServicePort();

    private User bankCustomer;
    private User bankMerchant;

    private CustomerAdapter customerAdapter = new CustomerAdapter();
    private PaymentAdapter paymentAdapter = new PaymentAdapter();
    private MerchantAdapter merchantAdapter = new MerchantAdapter();
    private ReportAdapter reportAdapter = new ReportAdapter();

    private Customer dtuPayCustomer;
    private Merchant dtuPayMerchant = new Merchant();

    private String customerBankAccountNumber;
    private String merchantBankAccountNumber;

    private List<TokenResult> customerTokens;
    private PaymentResponse paymentResponse;

    private Payment payment;

    private TokenResult randomTokenFromCustomerList;

    private List<PaymentLog> receivedLogs;



    @Before
    public void beforeTests() {

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

    @Given("{int} payment between a customer and a merchant")
    public void paymentBetweenACustomerAndAMerchant(int arg0) throws BankServiceException_Exception, Exception {

        //@Before
        System.out.println("Clean up");
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
            System.out.println(e.getMessage());
        }


        //@Given("a customer with name {string}, last name {string}, and CPR {string}")
        String firstName = "Susan";
        String lastName = "Baldwin";
        String cpr = "250103-7220";

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

        //@And("the customer is registered with the bank with an initial balance of {int} kr")
        customerBankAccountNumber = bankService.createAccountWithBalance(this.bankCustomer, new BigDecimal(1000));
        dtuPayCustomer.setBankAccountNumber(customerBankAccountNumber);

        //@And("the customer is registered with Simple DTU Pay using their bank account")
        dtuPayCustomer.setId(customerAdapter.register(dtuPayCustomer));

        //@And("the customer has {int} valid token from DTU Pay")
        customerTokens = customerAdapter.getTokens(dtuPayCustomer.getId(), 1 ); // the customer class has a field for this ->
        dtuPayCustomer.setTokens(customerTokens);

        //@And("a merchant with name {string}, last name {string}, and CPR {string}")
        // Initialize the User model from the bank integration
        firstName = "Daniel";
        lastName = "Oliver";
        cpr = "241902-7253";
        bankMerchant = new User();
        bankMerchant.setFirstName(firstName);
        bankMerchant.setLastName(lastName);
        bankMerchant.setCprNumber(cpr);

        // Initialize the dtuPayCustomer model
        dtuPayMerchant = new Merchant();
        dtuPayMerchant.setFirstName(firstName);
        dtuPayMerchant.setLastName(lastName);
        dtuPayMerchant.setCpr(cpr);

        //@And("the merchant is registered with the bank with an initial balance of {int} kr")
        merchantBankAccountNumber = bankService.createAccountWithBalance(this.bankMerchant, new BigDecimal(1000));
        dtuPayMerchant.setBankAccountNumber(merchantBankAccountNumber);

        //@And("the merchant is registered with Simple DTU Pay using their bank account")
        // Register the merchant with DTU Pay
        String merchantId = merchantAdapter.register(dtuPayMerchant);
        dtuPayMerchant.setId(merchantId);

        //makes more payment
        for(int i = arg0 ; i > 0; i--){
            //@When("the merchant initiates a payment for {int} kr using the customer's token")
            randomTokenFromCustomerList = customerTokens.get(new Random().nextInt(customerTokens.size()));
            payment = new Payment(dtuPayMerchant.getId(), randomTokenFromCustomerList.tokenId(), new BigDecimal(10));

            paymentResponse = paymentAdapter.requestPayment(payment);

            //Get a new token
            customerTokens = customerAdapter.getTokens(dtuPayCustomer.getId(), 1 ); // the customer class has a field for this ->
            dtuPayCustomer.setTokens(customerTokens);

        }

        //@Then("the payment is successful")
        assertTrue(paymentResponse.successful());

        //@And("the balance of the customer at the bank is {int} kr")
        Account customerAccount = bankService.getAccount(customerBankAccountNumber);
        Assert.assertEquals(new BigDecimal(1000-(arg0*10)).setScale(4), customerAccount.getBalance().setScale(4));

        //@And("the balance of the merchant at the bank is {int} kr")
        Account merchantAccount = bankService.getAccount(merchantBankAccountNumber);
        Assert.assertEquals(new BigDecimal(1000+(arg0*10)).setScale(4), merchantAccount.getBalance().setScale(4));

        //@And("the customer's token is no longer valid")
        assertThrows("Invalid Token", Exception.class, () -> paymentAdapter.requestPayment(payment));

    }

    @When("the customer requests for a report")
    public void theCustomerRequestsForAReport() {
        receivedLogs = reportAdapter.getCustomerReport(dtuPayCustomer.getId());
    }

    @Then("the report with {int} payment\\(s) is generated successfully")
    public void theReportWithPaymentSIsGeneratedSuccessfully(int arg0) {
    assertEquals(receivedLogs.size(), arg0);
    }

    @And("the report contains the given {int} payment\\(s)")
    public void theReportContainsTheGivenPaymentS(int arg0) {
        for (int i = 0; i < arg0; i++) {
            PaymentLog log = receivedLogs.get(i);
            assertEquals(log.getCustomerId(), dtuPayCustomer.getId());
            assertEquals(log.getMerchantId(), dtuPayMerchant.getId());
            assertEquals(log.getAmount().setScale(4), new BigDecimal(10).setScale(4));
        }

   }
   //Scenario: Successful merchant report generation
    @When("the merchant requests for a report")
    public void theMerchantRequestsForAReport() {
        receivedLogs = reportAdapter.getMerchantReport(dtuPayMerchant.getId());
    }

    // Scenario: Successful manager report generation
    @When("the manager requests for a report")
    public void theManagerRequestsForAReport() {
        receivedLogs = reportAdapter.getManagerReport();

    }
    //Scenario: Report generation fails when initiated by an invalid merchant
    @Given("a merchant not registered in DTUPay")
    public void aMerchantNotRegisteredInDTUPay() {

    }




}
