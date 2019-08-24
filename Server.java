import java.io.*; 
import java.util.*; 
import java.net.*; 
  
// Server class 
public class Server  
{ 
	public static List<User> users=new ArrayList<User>();
	public static List<Group> groups=new ArrayList<Group>();
	
    public static void main(String[] args) throws IOException  
    { 
        // server is listening on port 5056 
        ServerSocket ss = new ServerSocket(5056); 
          
        // running infinite loop for getting 
        // client request 
        while (true)  
        { 
            Socket s = null; 
              
            try 
            { 
                // socket object to receive incoming client requests 
                s = ss.accept(); 
                  
                System.out.println("A new client is connected : " + s); 
                  
                // obtaining input and out streams 
                DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
                  
                System.out.println("Assigning new thread for this client"); 
  
                // create a new thread object 
                Thread t = new ClientHandler(s, dis, dos); 
  
                // Invoking the start() method 
                t.start(); 
                  
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
        } 
    } 
} 
  
class ClientHandler extends Thread
{ 
    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 
    
    // Constructor 
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
    } 
    
    @Override
    public void run()  
    { 
        String received; 
        //String toreturn; 
        boolean loggedIn=false; 
    	try {
			dos.writeUTF("You are not logged in Please log in or create new account..");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        while(true)  
        {
            try 
            {
                // receive the answer from client 
                received = dis.readUTF(); 
                String[] req=received.split(" ");
                     
                if(req[0].equals("EXIT")) {
                	System.out.println("Closing this connection."); 
                    this.s.close(); 
                    System.out.println("Connection closed"); 
                    break;
                }
                // All commands executed here by server....
                switch(req[0])
                { 
                
                    case "create_user": 
                        String resp=addUser();
                        dos.writeUTF(resp);
                        dos.flush();
                        break;
                        
                        
                    case "upload":
                    	String path=dis.readUTF();
                    	int sz=Integer.parseInt(dis.readUTF());
                    	getFile(path,sz);
                    	String[] path_detail=path.split("/");
                    	System.out.println(path);
                    	int uid=Integer.parseInt(path_detail[1]);
                    	for(User u:Server.users) {
                    		if(u.getUser_id()==uid) {
                    			u.files.add(path);
                    			break;
                    		}
                    	}
                    	dos.writeUTF("Successfully uploaded");
                    	dos.flush();
                    	break;
                    	
                    	
                    case "upload_udp":
                    	String pat=getFileUDP();
                    	String[] pat_detail=pat.split("/");
                    	System.out.println(pat);
                    	for(String x:pat_detail)
                    		System.out.println(x);
                    	int uid1=Integer.parseInt(pat_detail[1]);
                    	for(User u:Server.users) {
                    		if(u.getUser_id()==uid1) {
                    			System.out.println("Path added...");
                    			u.files.add(pat);
                    			break;
                    		}
                    	}
                      	dos.writeUTF("Successfully uploaded by UDP");
                    	dos.flush();
                    	break;
                    	
                    	
                    case "create_folder":
                    	boolean res=make_folder(req[1]);
                    	if(res)
                    		dos.writeUTF("Folder added successfully");
                    	else
                    		dos.writeUTF("Folder creation Failed");
                    	break;
                    	
                    	
                    case "move_file":
                    	System.out.println(req[1]+" "+req[2]);
                    	if(req.length!=3 || !moveFile("ServerRoot/"+req[1], "ServerRoot/"+req[2])) {
                    		dos.writeUTF("Incorrect format of move_file command");
                    	}
                    	else {
                    		dos.writeUTF("File moved successfully");
                    	}
                    	dos.flush();
                    	break;
                    	
                    
                    case "create_group":
                    	boolean p=createGroup(req[1]);
                    	if(p){
                    		dos.writeUTF("Group created successfully");
                    	}
                    	else
                    		dos.writeUTF("Group cannot be created");
                    	dos.flush();
                    	break;
                    
                    
                    case "list_groups":
                    	String li=listGroups();
                    	dos.writeUTF(li);
                    	dos.flush();
                    	break;
                    
                    	
                    case "join_group":
                    	
                    	break;
                    
                    
                    case "leave_group":
                    	break;
                    
                    	
                    case "list_detail":
                    	System.out.println(req[1]);
                    	String detail=listDetail(req[1]);
                    	dos.writeUTF(detail);
                    	break;
                    	
                    	
                    case "share_msg":
                    	break;
                    
                    	
                    case "get_file":
                    	System.out.println(req[1]+" "+req[2]+" "+req[3]);
                    	File f=new File("ServerRoot/"+req[1]);
                    	if(f.exists()) {
                    		sendFile(req[1], req[2], Integer.parseInt(req[3]));
                    		dos.writeUTF("File Downloaded successfully");
                    	}
                    	else {
                    		dos.writeUTF("File does not exists..");
                    	}
                    	break;
                    default: 
                        dos.writeUTF("Invalid command"); 
                        break; 
                }
            }
            catch (IOException e) 
            { 
                e.printStackTrace(); 
            }
        } 
          
        try
        {
            // closing resources 
            this.dis.close(); 
            this.dos.close(); 
              
        }catch(IOException e){ 
            e.printStackTrace(); 
        }
    }
   
    private void sendFile(String path,String group_name,int user_id) {
		// TODO Auto-generated method stub
		
    	System.out.println("Downloading start.....");
    	String FileName="ServerRoot/"+path;
    	File file = new File(FileName);
    	String fname=file.getName();
        DatagramSocket ds = null;
        
        long fileSize = file.length();
        long totalReadBytes = 0;
        double startTime = 0;  
        try 
        {
            ds = new DatagramSocket();
            InetAddress serverAdd = InetAddress.getByName("localhost");
            startTime = System.currentTimeMillis();
            String str = "start";
            DatagramPacket dp = new DatagramPacket(str.getBytes(), str.getBytes().length, serverAdd, 9999);
            ds.send(dp);
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];         
            
            str = String.valueOf(fileSize);
            dp = new DatagramPacket(str.getBytes(), str.getBytes().length, serverAdd, 9999);
            ds.send(dp);
            
            str=group_name+"/"+user_id+"/"+"downloaded-"+fname;
            dp=new DatagramPacket(str.getBytes(), str.getBytes().length, serverAdd, 9999);
            ds.send(dp);
            
            while (true)
            {
                Thread.sleep(10);
                int readBytes = fis.read(buffer, 0, buffer.length);
                if (readBytes == -1)
                    break;
                dp = new DatagramPacket(buffer, readBytes, serverAdd, 9999);
                ds.send(dp);
                totalReadBytes += readBytes;
                System.out.println("In progress: " + totalReadBytes + "/"
                        + fileSize + " Byte(s) ("
                        + (totalReadBytes * 100 / fileSize) + " %)");
            }
            double endTime = System.currentTimeMillis();
            double diffTime = (endTime - startTime)/ 1000;;
            double transferSpeed = (fileSize / 1000)/ diffTime;
             
            System.out.println("time: " + diffTime+ " second(s)");
            System.out.println("Average transfer speed: " + transferSpeed + " KB/s");
             
            str = "end";
            dp = new DatagramPacket(str.getBytes(), str.getBytes().length, serverAdd, 9999);
            ds.send(dp);
            Thread.sleep(5000);
            System.out.println("Process Close");
            fis.close();
            ds.close();
  
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    	
	}

	private String listDetail(String grp) {
    	String detail="";
    	for(Group g:Server.groups) {
    		detail+="Group"+"\n";
    		if(g.getGroup_name().equals(grp))
    		{
	    		detail+="   "+g.getGroup_name()+"\n";
	    		for(User u:Server.users) {
	    			detail+="      User Name:"+u.getUser_name()+"\n";
	    			detail+="           Files"+"\n";
	    			for(String s:u.files) {
	    				detail+="             "+s+"\n";
	    			}
	    		}
    		}
    	}
    	return detail;
	}

	private boolean createGroup(String string) {
		// TODO Auto-generated method stub
    	System.out.println("Current number of groups:"+Server.groups.size());
    	for(Group g:Server.groups) {
    		System.out.println("Group name:"+g.getGroup_name());
    		if(g.getGroup_name().equals(string))
    			return false;
    	}
    	Group g=new Group();
    	g.setGroup_name(string);
    	Server.groups.add(g);
		return true;
	}
    private String listGroups() {
    	String li="";
    	for(Group g:Server.groups) {
    		li+=g.getGroup_name()+"\n";
    	}
    	return li;
    }
   
	private void getFile(String path,int filesize) throws IOException 
    {
		DataInputStream dis=new DataInputStream(s.getInputStream());
        System.out.println("Receiving file....");
        File f=new File("ServerRoot/"+path);
        f.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(f);
		byte[] buffer = new byte[4096];
		int read=0;
		int totalRead=0;
		int remaining=filesize;
        while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0)
        {
			totalRead += read;
			remaining -= read;
			fos.write(buffer, 0, read);
		}
		fos.close();
	}
    private boolean moveFile(String path1,String path2) throws IOException {
    	 File file = new File(path1); 
         if(file.renameTo(new File(path2))) 
         {  
             file.delete(); 
             return true;
         } 
         return false; 
    }
    private String getFileUDP() throws IOException
	{
    	System.out.println("Receiving file using UDP..");
		long fileSize;
        long totalReadBytes = 0;
        String file_name="";
        byte[] buffer = new byte[10000];
        try {
            int nReadSize = 0;
            System.out.println("Waitng.....");
              
            DatagramSocket ds = new DatagramSocket(9999);
            FileOutputStream fos = null;       
            
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            ds.receive(dp);
            String str = new String(dp.getData()).trim();
            
            if(str.equals("start"))
            {
                System.out.println(str);
                dp = new DatagramPacket(buffer, buffer.length);
                ds.receive(dp);
                str = new String(dp.getData()).trim();
                fileSize = Long.parseLong(str);
                
                dp = new DatagramPacket(buffer, buffer.length);
                ds.receive(dp);
                str = new String(dp.getData()).trim();
                file_name = str;
                File f=new File("ServerRoot/"+file_name);
                f.getParentFile().mkdirs();
                fos = new FileOutputStream(f);
                double startTime = System.currentTimeMillis(); 
                while (true)
                {
                    ds.receive(dp);
                    str = new String(dp.getData()).trim();
                    nReadSize = dp.getLength();
                    fos.write(dp.getData(), 0, nReadSize);
                    totalReadBytes+=nReadSize;
                    System.out.println("In progress: " + totalReadBytes + "/"
                            + fileSize + " Byte(s) ("
                            + (totalReadBytes * 100 / fileSize) + " %)");
                    if(totalReadBytes>=fileSize)
                        break;
                }
                double endTime = System.currentTimeMillis();
                double diffTime = (endTime - startTime)/ 1000;;
                double transferSpeed = (fileSize / 1000)/ diffTime;
                 
                System.out.println("time: " + diffTime+ " second(s)");
                System.out.println("Average transfer speed: " + transferSpeed + " KB/s");
                System.out.println("File transfer completed");
                Thread.sleep(5000);
                fos.close();
                ds.close();
            }
            else
            {
                System.out.println("Start Error");
                fos.close();
                ds.close();
            }
        } 
        catch (Exception e) {}
        return file_name;
    }
    
    private boolean make_folder(String folder)
    {
    	File newFolder = new File("ServerRoot/"+folder);
        boolean created =  newFolder.mkdirs();
    	return created;
    }
    
    public String randomString()
    {
    	 String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
         StringBuilder salt = new StringBuilder();
         Random rnd = new Random();
         while (salt.length()<10) 
         {
             int index = (int) (rnd.nextFloat() * SALTCHARS.length());
             salt.append(SALTCHARS.charAt(index));
         }
         String saltStr = salt.toString();
         return saltStr;
    }
    
	private String addUser() {
		// TODO Auto-generated method stub
		
		User u=new User();
		int id=Server.users.size()+1;
		u.setUser_id(id);
		String gname=randomString();
		String uname=randomString();
		u.setUser_name(uname);
		u.setGroup_name(gname);
		Group g=new Group();
		g.setGroup_name(gname);
		Server.users.add(u);
		Server.groups.add(g);
		return id+" "+uname+" "+gname;
	}
} 