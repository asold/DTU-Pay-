package dk.dtu.businesslogic.models;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

/**
 * Public model of a Token sent to the customer
 *
 * @param tokenId
 * @author Simão Teixeira (s232431)
 */
@ValueObject
public record TokenResult(UUID tokenId) {
}
