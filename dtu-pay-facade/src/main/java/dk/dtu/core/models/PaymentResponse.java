package dk.dtu.core.models;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record PaymentResponse(UUID paymentId, boolean successful){ }
