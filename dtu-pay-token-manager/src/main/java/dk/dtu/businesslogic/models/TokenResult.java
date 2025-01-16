package dk.dtu.businesslogic.models;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record TokenResult(UUID tokenId) {
}
