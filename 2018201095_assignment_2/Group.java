import java.util.ArrayList;
import java.util.List;
public class Group{
	String group_name;
	List<User> users = new ArrayList<User>();
	String file_path;
	public String getGroup_name() {
		return group_name;
	}
	public String getFilePath() {
		return file_path;
	}
	public void setFilePath(String path) {
		file_path=path;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
}
