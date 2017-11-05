

/**
 * *
 * Irc class : simple implementation of a chat using JAVANAISE Contact:
 *
 * Authors:
 */
  
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import jvn.JvnException;

public class SpecIrc {

    ISentence sentence;

    /**
     * main method create a JVN object nammed IRC for representing the Chat
     * application
     *
     */
    public static void main(String argv[]) throws JvnException {
        
        new SpecIrc((ISentence) InvocationProxy.newInstance(new Sentence(),"IRC" )    );
            
    }


    public SpecIrc(ISentence sentence) {
        this.sentence = sentence;

        try {
            startSpec();
        } catch (InterruptedException ex) {
            Logger.getLogger(SpecIrc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startSpec() throws InterruptedException {
        int i = 0;
        String chaine= generate(4);
       
        while (i <= 500) {
          sleep(generateInt(1));
          
            String s = this.sentence.read();
            System.out.println("READ : " + s);

            i++;

            s = i + "_"+ chaine + "write";
            this.sentence.write(s);
            
            System.out.println("WRITE");
            System.out.println(s);
        }
    }
    
    
    public String generate(int length)
{
	    String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"; 
          
	    String pass = "";
	    for(int x=0;x<length;x++)
	    {
	       int i = (int)Math.floor(Math.random() * 62);  
	       pass += chars.charAt(i);
	    }
	    System.out.println(pass);
	    return pass;
}


public int generateInt(int length)
{
	    String chars = "123456789"; 
	    String pass = "";
	    for(int x=0;x<length;x++)
	    {
	       int i = (int)Math.floor(Math.random() * 9);  
	       pass += chars.charAt(i);
	    }
	    System.out.println(pass);
	    return (Integer.parseInt(pass))*100;
}

}