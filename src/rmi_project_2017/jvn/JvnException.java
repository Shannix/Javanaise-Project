/***
 * JAVANAISE API
 * Contact: 
 *
 * Authors: 
 */

package rmi_project_2017.jvn;

/**
 * Interface of a JVN Exception. 
 */

public class JvnException extends Exception {
	String message;
  
	public JvnException() {
	}
	
	public JvnException(String message) {
		this.message = message;
	}	
  
	public String getMessage(){
		return message;
	}
}
