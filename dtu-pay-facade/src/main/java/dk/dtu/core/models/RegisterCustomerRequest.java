package dk.dtu.core.models;

/**
 * Customer Registration Request DTO
 *
 * @param firstName         Customer's First Name
 * @param lastName          Customer's Last Name
 * @param cpr               Customer's CPR
 * @param bankAccountNumber Customer's Bank Account Number
 * @author Simão Teixeira (s232431)
 */
public record RegisterCustomerRequest(
        String firstName,
        String lastName,
        String cpr,
        String bankAccountNumber) {
}
