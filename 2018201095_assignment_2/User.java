import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class User extends Group
{
	int user_id;
	String user_name;
	Socket s;
	public Socket getSocket() {
		return s;	
	}
	public void setSocket(Socket sock) {
		this.s=sock;
	}
	List<String> files = new ArrayList<String>();
	public int getUser_id(){
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
}