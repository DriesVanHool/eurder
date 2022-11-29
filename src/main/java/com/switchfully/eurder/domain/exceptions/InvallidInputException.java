package com.switchfully.eurder.domain.exceptions;

import java.util.List;
import java.util.stream.Collectors;

public class InvallidInputException extends RuntimeException {
    public InvallidInputException(List<String> errors) {
        super("The following fields are invalid: " + String.join(", ", errors));
    }
}
