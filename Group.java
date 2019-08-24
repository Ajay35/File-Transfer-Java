import java.util.ArrayList;
import java.util.List;
public class Group{
	String group_name;
	List<User> users = new ArrayList<User>();
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
}
