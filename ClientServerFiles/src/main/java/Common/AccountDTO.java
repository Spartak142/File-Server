package Common;

import java.io.Serializable;

public interface AccountDTO extends Serializable {
   
   //Returns a username
    public String getUserName();
    
    //Returns a password
    public String getPassword();
    
}
