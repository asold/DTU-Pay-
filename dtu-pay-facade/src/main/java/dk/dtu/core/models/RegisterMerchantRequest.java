package dk.dtu.core.models;

/**
 * Merchant Registration Request DTO
 *
 * @param firstName         Merchant's First Name
 * @param lastName          Merchant's Last Name
 * @param cpr               Merchant's CPR
 * @param bankAccountNumber Merchant's Bank Account Number
 * @author Simão Teixeira (s232431)
 */
public record RegisterMerchantRequest(
        String firstName,
        String lastName,
        String cpr,
        String bankAccountNumber) {
}
