package Common;

import java.io.Serializable;

public interface FileDTO extends Serializable {

    public String getFileName();

    public String getOwner();

    public Boolean getPermission();

    public int getSize();
}
