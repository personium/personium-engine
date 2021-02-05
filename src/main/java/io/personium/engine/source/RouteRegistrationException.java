package io.personium.engine.source;

/**
 * Exception class for route registration
 */
@SuppressWarnings("serial")
public class RouteRegistrationException extends Exception{
   
    /** Raw exception */
    private Exception internalException = null;

    /**
     * Constructor of RouteRegistrationException
     * @param e raw exception
     */
    public RouteRegistrationException(final Exception e) {
        this.internalException = e;
    }

    /**
     * Getter of raw exception
     * @return raw exception
     */
    public Exception getInternalException() {
        return internalException;
    }
}
