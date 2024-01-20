package ru.clevertec.ecl.exception;

public class HouseNotFoundException extends RuntimeException {

    public HouseNotFoundException(String message) {
        super(message);
    }

    public HouseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
