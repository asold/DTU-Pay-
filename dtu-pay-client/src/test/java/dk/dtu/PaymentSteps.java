//package dk.dtu;
//
//import dk.dtu.core.services.CustomerService;
//import dk.dtu.core.services.MerchantService;
//import dk.dtu.core.services.PaymentService;
//import io.cucumber.java.en.And;
//import io.cucumber.java.en.Given;
//import io.cucumber.java.en.Then;
//import io.cucumber.java.en.When;
//
//
//public class PaymentSteps {
//
//    private CustomerService customerService = new CustomerService();
//    private PaymentService paymentService = new PaymentService();
//    private MerchantService merchantService = new MerchantService();
//
//
//
//    @Given("a customer with name {string}, last name {string}, and CPR {string}")
//    public void aCustomerWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
//
//
//    }
//
//    @And("the customer is registered with the bank with an initial balance of {int} kr")
//    public void theCustomerIsRegisteredWithTheBankWithAnInitialBalanceOfKr(int arg0) {
//
//    }
//
//    @And("the customer is registered with Simple DTU Pay using their bank account")
//    public void theCustomerIsRegisteredWithSimpleDTUPayUsingTheirBankAccount() {
//
//    }
//
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
//}
