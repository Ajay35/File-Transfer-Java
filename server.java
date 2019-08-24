import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class server extends Thread 
{	
	private List<Group> groups = new ArrayList<Group>();
	private List<User> users= new ArrayList<User>();
	private ServerSocket ss;
	private DatagramSocket ds;
    public server(int port)
    {
        try 
        {
		    ss = new ServerSocket(port);
        } 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
	}
	
    public void run() 
    {
        while (true) 
        {
            try
            {
                // get new socket for new client and launch thread for providing service
				Socket clientSock = ss.accept();
				// execute commands here..
				login(clientSock);
				//getFileUDP(clientSock);
				//getFile(clientSock);
            } 
            catch (IOException e) 
            {
				e.printStackTrace();
			}
		}
	}
    
    private void login(Socket clientSock) throws IOException
    {
    	while(true)
    	{
    		DataOutputStream dos=new DataOutputStream(clientSock.getOutputStream());
    		DataInputStream dis=new DataInputStream(clientSock.getInputStream());
    		String res=dis.readUTF();
    		
    		String[] req=res.split(" "); 
    		if(req[0]=="LOGIN")
    			dos.writeUTF("SUCCESS");
    		else
    			dos.writeUTF("FAILED");
    	}
    }
    
    
    private String list_groups()
    {
    	String gl="";
    	for(Group i:groups)
    	{
    		gl+=i.getGroup_id()+"\n";
    	}
    	return gl;
    }
    private void addUser(String name,int id,int gid)
	{
		User u=new User();
		u.setUser_id(id);
		u.setUser_name(name);
		u.setGroup_id(gid);
		users.add(u);
	}
    /*private boolean login(int id,String name){
    	
    	for(User p:users) {
    		if(p.getUser_id()==id && p.getUser_name()==name)
    			return true;
    	}
    	return false;
    }*/
    private void create_group(String name)
    {
    	int id=groups.size();
    	Group g=new Group();
    	g.setGroup_id(id+1);
    	groups.add(g);
    }
    
   
	private void getFileUDP(Socket clientSock) throws IOException
	{
		System.out.println("Receiving file using UDP..");
		long fileSize;
        long totalReadBytes = 0;
         
        byte[] buffer = new byte[10000];
        try {
            int nReadSize = 0;
            System.out.println("Waitng.....");
              
            DatagramSocket ds = new DatagramSocket(9999);
            FileOutputStream fos = null;       
            
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            ds.receive(dp);
            String str = new String(dp.getData()).trim();
             
            if (str.equals("start"))
            {
                System.out.println(str);
                dp = new DatagramPacket(buffer, buffer.length);
                ds.receive(dp);
                str = new String(dp.getData()).trim();
                fileSize = Long.parseLong(str);
                
                dp = new DatagramPacket(buffer, buffer.length);
                ds.receive(dp);
                str = new String(dp.getData()).trim();
                String file_name = str;
                fos = new FileOutputStream("ServerRoot/"+file_name);
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
        System.out.println("Process Close");
    }
    public static void main(String[] args)
    {
        if(args.length==0)
        {
            System.out.println("Enter port number");
            return;
        }
        int port=Integer.parseInt(args[0]);
		server fs = new server(port);
		fs.start();
	}
}