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
    private String merchantCpr;


    @Before("@Report")
    public void beforeTests() throws Exception {
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

        String customerFirstName = "Susan";
        String customerLastName = "Baldwin";
        String customerCpr = "250103-7220";

        // Initialize the User model from the bank integration
        bankCustomer = new User();
        bankCustomer.setFirstName(customerFirstName);
        bankCustomer.setLastName(customerLastName);
        bankCustomer.setCprNumber(customerCpr);

        // Initialize the dtuPayCustomer model
        dtuPayCustomer = new Customer();
        dtuPayCustomer.setFirstName(customerFirstName);
        dtuPayCustomer.setLastName(customerLastName);
        dtuPayCustomer.setCpr(customerCpr);

        customerBankAccountNumber = bankService.createAccountWithBalance(this.bankCustomer, new BigDecimal(1000));
        dtuPayCustomer.setBankAccountNumber(customerBankAccountNumber);

        dtuPayCustomer.setId(customerAdapter.register(dtuPayCustomer));

        customerTokens = customerAdapter.getTokens(dtuPayCustomer.getId(), 2 );
        dtuPayCustomer.setTokens(customerTokens);

        // Initialize the User model from the bank integration
        String merchantFirstName = "Daniel";
        String merchantLastName = "Oliver";
        merchantCpr = "241902-7253";
        bankMerchant = new User();
        bankMerchant.setFirstName(merchantFirstName);
        bankMerchant.setLastName(merchantLastName);
        bankMerchant.setCprNumber(merchantCpr);

        // Initialize the dtuPayCustomer model
        dtuPayMerchant = new Merchant();
        dtuPayMerchant.setFirstName(merchantFirstName);
        dtuPayMerchant.setLastName(merchantLastName);
        dtuPayMerchant.setCpr(merchantCpr);

        merchantBankAccountNumber = bankService.createAccountWithBalance(this.bankMerchant, new BigDecimal(1000));
        dtuPayMerchant.setBankAccountNumber(merchantBankAccountNumber);

        // Register the merchant with DTU Pay
        String merchantId = merchantAdapter.register(dtuPayMerchant);
        dtuPayMerchant.setId(merchantId);
    }

    @After("Report")
    public void cleanUp() {
        try {
            if (customerBankAccountNumber != null) {
                bankService.retireAccount(customerBankAccountNumber);
            }

            if (merchantBankAccountNumber != null) {
                bankService.retireAccount(merchantBankAccountNumber);
            }
        } catch (BankServiceException_Exception e) {
            System.err.println(e.getMessage());
        }

    }

    @Given("{int} payment between a customer and a merchant")
    public void paymentBetweenACustomerAndAMerchant(int numberOfPaymentsToMake) throws BankServiceException_Exception, Exception {

        //makes more payment
        for(int i = 0 ; i < numberOfPaymentsToMake; i++){
            randomTokenFromCustomerList = customerTokens.get(new Random().nextInt(customerTokens.size()));
            customerTokens.remove(randomTokenFromCustomerList);
            payment = new Payment(dtuPayMerchant.getId(), randomTokenFromCustomerList.tokenId(), new BigDecimal(10));

            paymentResponse = paymentAdapter.requestPayment(payment);
        }

    }

    @When("the customer requests for a report")
    public void theCustomerRequestsForAReport() throws InterruptedException {
        receivedLogs = reportAdapter.getCustomerReport(dtuPayCustomer.getId());
    }

    @Then("the report with {int} payment\\(s) is generated successfully")
    public void theReportWithPaymentSIsGeneratedSuccessfully(int numberOfPaymentsInTheReport) {
    assertEquals(numberOfPaymentsInTheReport, receivedLogs.size());
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
    public void theMerchantRequestsForAReport() throws InterruptedException {
        receivedLogs = reportAdapter.getMerchantReport(dtuPayMerchant.getId());
    }

    // Scenario: Successful manager report generation
    @When("the manager requests for a report")
    public void theManagerRequestsForAReport() {
        receivedLogs = reportAdapter.getManagerReport();

    }

}
