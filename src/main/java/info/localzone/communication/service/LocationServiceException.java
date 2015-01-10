package info.localzone.communication.service;

public class LocationServiceException extends Exception{


	public LocationServiceException(String message) {
		super(message);
			}

	private static final long serialVersionUID = 5210382941967283835L;

	public LocationServiceException(String message, Throwable cause) {
		super(message, cause);
		
	}

}
