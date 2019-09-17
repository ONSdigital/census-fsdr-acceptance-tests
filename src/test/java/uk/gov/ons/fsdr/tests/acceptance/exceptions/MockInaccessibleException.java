package uk.gov.ons.fsdr.tests.acceptance.exceptions;

public class MockInaccessibleException extends RuntimeException {
  public MockInaccessibleException(String reason) {
    super(reason);
  }
}
