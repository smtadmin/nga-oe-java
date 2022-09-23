package nga.oe.schema.exception;

public class UnexpectedException extends Exception {

	private static final long serialVersionUID = -7952648235876474196L;

	public UnexpectedException(String message, Exception e) {
		super(message, e);
	}

	public UnexpectedException(String message, Throwable t) {
		super(message, t);
	}
}
