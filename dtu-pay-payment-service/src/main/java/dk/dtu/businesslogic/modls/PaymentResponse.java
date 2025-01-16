package dk.dtu.businesslogic.modls;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record PaymentResponse(UUID paymentId, boolean successful){ }
