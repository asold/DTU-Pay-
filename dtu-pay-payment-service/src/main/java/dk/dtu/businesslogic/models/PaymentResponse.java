package dk.dtu.businesslogic.models;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;
/**
 * @author  Andrei Soldan 243873
 */
@ValueObject
public record PaymentResponse(UUID paymentId, boolean successful){ }
