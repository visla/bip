package bip.error;

public class ServiceNotAvailableException extends Exception {
    
    private static final long serialVersionUID = -2440159626870792897L;
    public String message;
    
    public ServiceNotAvailableException(String message) {
       this.message = message;
    }

    // Overrides Exception's getMessage()
    @Override
    public String getMessage(){
        return message;
    }
}