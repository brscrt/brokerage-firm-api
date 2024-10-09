package com.brscrt.brokerage.exception.unchecked;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ApiRuntimeExceptionTest {

    private static final String TEST_MESSAGE = "This is a test message.";
    private static final Throwable TEST_CAUSE = new Throwable("This is a test cause.");

    static Stream<Class<? extends ApiRuntimeException>> exceptionProvider() {
        return Stream.of(
                ApiRuntimeException.class,
                DataSourceException.class,
                UnauthorizedException.class
        );
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    void givenMessage_whenCreatingException_thenMessageIsSet(Class<? extends ApiRuntimeException> exceptionClass)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Given - a test message to initialize the exception
        String message = TEST_MESSAGE;

        // When - creating an instance of ApiRuntimeException with the test message
        ApiRuntimeException exception = exceptionClass.getConstructor(String.class).newInstance(message);

        // Then - verifying that the message is correctly set and the cause is null
        assertEquals(message, exception.getMessage(), "The exception message should" +
                " match the provided message.");
        assertNull(exception.getCause(), "The exception cause should be null.");
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    void givenCause_whenCreatingException_thenCauseIsSet(Class<? extends ApiRuntimeException> exceptionClass)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Given - a test cause to initialize the exception
        Throwable cause = TEST_CAUSE;

        // When - creating an instance of ApiRuntimeException with the test cause
        ApiRuntimeException exception = exceptionClass.getConstructor(Throwable.class).newInstance(cause);

        // Then - verifying that the cause is correctly set and the message is derived from the cause
        assertEquals(cause, exception.getCause(), "The exception cause should match the provided cause.");
        assertEquals(cause.toString(), exception.getMessage(), "The exception message should" +
                " be the string representation of the cause.");
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    void givenMessageAndCause_whenCreatingException_thenMessageAndCauseAreSet(
            Class<? extends ApiRuntimeException> exceptionClass)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Given - a test message and a test cause to initialize the exception
        String message = TEST_MESSAGE;
        Throwable cause = TEST_CAUSE;

        // When - creating an instance of ApiRuntimeException with the test message and cause
        ApiRuntimeException exception = exceptionClass.getConstructor(String.class, Throwable.class)
                .newInstance(message, cause);

        // Then - verifying that both the message and the cause are correctly set
        assertEquals(message, exception.getMessage(), "The exception message should" +
                " match the provided message.");
        assertEquals(cause, exception.getCause(), "The exception cause should match the provided cause.");
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    void givenException_whenCallingToString_thenReturnsExpectedString(
            Class<? extends ApiRuntimeException> exceptionClass)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Given - a test message to initialize the exception
        String message = TEST_MESSAGE;

        // When - creating an instance of ApiRuntimeException with the test message
        ApiRuntimeException exception = exceptionClass.getConstructor(String.class)
                .newInstance(message);

        // Then - verifying that the toString method returns the expected string
        String expectedToString = exceptionClass.getSimpleName() + ": " + message;
        assertEquals(expectedToString, exception.toString(), "The toString method should" +
                " return the expected string representation.");
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    void givenExceptionWithCause_whenCallingToString_thenReturnsExpectedString(
            Class<? extends ApiRuntimeException> exceptionClass)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Given - a test message and a test cause to initialize the exception
        String message = TEST_MESSAGE;
        Throwable cause = TEST_CAUSE;

        // When - creating an instance of ApiRuntimeException with the test message and cause
        ApiRuntimeException exception = exceptionClass.getConstructor(String.class, Throwable.class)
                .newInstance(message, cause);

        // Then - verifying that the toString method returns the expected string
        String expectedToString = exceptionClass.getSimpleName() + ": " + message + " Cause: " + cause.getMessage();
        assertEquals(expectedToString, exception.toString(), "The toString method should" +
                " return the expected string representation.");
    }
}