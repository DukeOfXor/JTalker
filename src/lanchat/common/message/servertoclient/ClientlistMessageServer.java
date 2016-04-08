package lanchat.common.message.servertoclient;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientlistMessageServer implements Serializable{

  private static final long serialVersionUID = -103995469835311028L;
  private ArrayList<String> clientlist;

  public ClientlistMessageServer(ArrayList<String> clientlist) {
    setClientlist(clientlist);
  }

  public ArrayList<String> getClientlist() {
    return clientlist;
  }

  private void setClientlist(ArrayList<String> clientlist) {
    this.clientlist = clientlist;
  }
}
