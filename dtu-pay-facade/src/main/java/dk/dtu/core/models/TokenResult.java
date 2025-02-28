package dk.dtu.core.models;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

/**
 * Models a token value object needed by a customer to make a payment.
 *
 * @author Mihály Tass s243573
 */
@ValueObject

public record TokenResult(UUID tokenId) { }
