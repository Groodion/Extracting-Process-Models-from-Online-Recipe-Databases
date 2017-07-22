package ai4.master.project.apirequests.exceptions;


/**
 * An Expcetion when the response from chefkoch isn't as expected which means the API is currently offline.
 */
public class ServerOfflineException extends Exception {

    public ServerOfflineException(String message){
        super(message);
    }
}
