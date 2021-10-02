package dts.errors;

public class ItemNotFound extends ResourceNotFound {
	
	private static final long serialVersionUID = 1L;
	
	public ItemNotFound(String itemNotFoundMsg) {
		super(itemNotFoundMsg);
	}

	public ItemNotFound(Throwable cause) {
		super(cause);
	}

	public ItemNotFound(String message, Throwable cause) {
		super(message, cause);
	}

}
