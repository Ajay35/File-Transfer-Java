import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.io.File;
import java.util.Scanner;
public class client 
{	
    private Socket s;
    private DatagramSocket ds;
    public client(String host, int port)
    {
        try
        {
            s=new Socket(host,port);
			//sendFile();
            //sendFileUDP();
           
        }
        catch (Exception e)
        {
			e.printStackTrace();
		}		
	}
    public Socket getSocket(){
    	return s;
    }
    public void commands() throws IOException
    {   
    	System.out.println("Ready to process commands");        
    }
    public void sendFile() throws IOException 
    {
        System.out.println("Enter file name to upload");
        Scanner scanner = new Scanner(System. in);
        String file_name =scanner.nextLine();
        scanner.close();
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        File f=new File(file_name);
        if(f.exists())
        {
            System.out.println("Sending ....");
            FileInputStream fis = new FileInputStream(file_name);
            long size=f.length();
            dos.writeUTF(file_name);
            dos.flush();
            dos.writeUTF(Long.toString(size));
            dos.flush();
            byte[] buffer = new byte[4096];
            while(fis.read(buffer) > 0)
            {
                dos.write(buffer);
            }
            fis.close();
            dos.close();	
            System.out.println("File uploaded successfully..");
        }
        else
        {
            System.out.println("File Not Found");
        }
	}
	public void sendFileUDP() throws IOException
	{
		System.out.println("Enter file name:");
		Scanner sc=new Scanner(System.in);
		String FileName=sc.nextLine();
		sc.close();
        File file = new File(FileName);
        DatagramSocket ds = null;
        if (!file.exists()) 
        {
            System.out.println("File not Exist");
            System.exit(0);
        }
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
            byte[] buffer = new byte[10000];         
            
            str = String.valueOf(fileSize);
            dp = new DatagramPacket(str.getBytes(), str.getBytes().length, serverAdd, 9999);
            ds.send(dp);
            
            str=FileName;
            dp=new DatagramPacket(str.getBytes(), str.getBytes().length, serverAdd, 9999);
            ds.send(dp);
            
            while (true)
            {
                Thread.sleep(10);
                int readBytes = fis.read(buffer, 0, buffer.length);
                if (readBytes == -1)
                    break;
                dp = new DatagramPacket(buffer, readBytes, serverAdd, 9999); // *
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

    public static void main(String[] args) throws IOException 
    {
        if(args.length==0)
        {
            System.out.println("Enter port number");
            return;
        }
        int port=Integer.parseInt(args[0]);
        client fc=new client("localhost", port);
        System.out.println("Are you existing User or want to create new Account");
        System.out.println("Choose 1 for New User");
        System.out.println("Choose 2 for Existing User");
        Scanner sc=new Scanner(System.in);
        int ch=sc.nextInt();
        if(ch==1)
        {
        	System.out.println("Type your name");
        	String uname=sc.next();
        	String create="CREATE_USER"+" "+uname;
        	String glist="GROUP_LIST";
        	DataOutputStream dos=new DataOutputStream(fc.getSocket().getOutputStream());
        	dos.writeUTF(glist);
        	DataInputStream dis=new DataInputStream(fc.getSocket().getInputStream());
        	String result1=dis.readUTF();
        	System.out.println("Groups Available:");
        	System.out.println(result1);
        	dos.flush();
        	System.out.println("Select group from list or create new group ID");
        	int gid=sc.nextInt();
        	dos.writeUTF(create+gid);
        	dos.close();
        	String res=dis.readUTF();
        	dis.close();
        	if(res=="SUCCESS")
        	{
        		//process commands here
        	}
        }
        else
        {
        	System.out.println("Type your ID and Username to login ");        	
        	int id=Integer.parseInt(sc.next());
        	String uname=sc.next();
        	String login="LOGIN"+" "+id+" "+uname;
        	DataOutputStream dos = new DataOutputStream(fc.getSocket().getOutputStream());
        	dos.writeUTF(login);
        	DataInputStream dis=new DataInputStream(fc.getSocket().getInputStream());
        	String res=dis.readUTF();
        	dis.close();
        	if(res=="SUCCESS")
        	{
        		fc.commands();
        	}
        	else
        	{
        		System.out.println("Login Failed.Try Again.");
        	}
        }
             
  	}
}