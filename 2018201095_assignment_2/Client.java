import java.io.*; 
import java.net.*;
import java.util.Scanner;
  
// Client class 


class chatHandler extends Thread
{
	public String chatFilePath;
	public chatHandler(String path) {
		this.chatFilePath=path;
	}
	@Override
	public void run() {
		
		  File file=new File(chatFilePath);
		  long pointer = 0;
		  for (;;) {
		  
		 try {
		    long len = file.length();
		    if (len < pointer) {
		      // file was reset
		      pointer = len;
		    } else if (len > pointer) {
		      // Content was added
		      RandomAccessFile raf = new RandomAccessFile(file, "r");
		      raf.seek(pointer);
		      String line;
		      while ((line = raf.readLine()) != null) {
		    	  System.out.println("New message:"+line);
		      }
		      pointer = raf.getFilePointer();
		      raf.close();
		    }
		 }
		 catch(Exception e) {
			 e.printStackTrace();
		 }
		    try {
		      Thread.sleep(1000);
		    } catch (InterruptedException e) {
		      Thread.interrupted();
		      break;
		    }
		  }
		
		
	}
}
public class Client  
{ 
    public static void main(String[] args) throws IOException  
    { 
    	
    	boolean loggedIn=false;
    	String group_name="temp";
    	int user_id=0;
        try
        { 
            Scanner scn = new Scanner(System.in); 
              
            InetAddress ip = InetAddress.getByName("localhost"); 
      
            // establish the connection with server port 5056 
            Socket s = new Socket(ip, 5056); 
      
            // obtaining input and out streams 
            DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
      
            // the following loop performs the exchange of 
            // information between client and client handler 
            System.out.println(dis.readUTF());
            while (true)  
            {                
                if(!loggedIn)
                {
            		String toSend="create_user";
            		dos.writeUTF(toSend);
            		String received=dis.readUTF();
            		loggedIn=true;
            		System.out.println("Your credentials are:");
            		System.out.println("You are now logged in  :)");
            		System.out.println("User ID | User Name | Group ID");
            		String[] info=received.split(" ");
            		group_name=info[2];
            		loggedIn=true;
            		user_id=Integer.parseInt(info[0]);
            		
	        		 Thread ch=new chatHandler(info[3]);
	                 ch.setDaemon(true);
	                 ch.start();
	                 System.out.println("New chat started");
	                 System.out.println(received);
                }
                else
                {
                	System.out.print("Command:");
                	String tosend = scn.nextLine();
                	dos.writeUTF(tosend); 
                	// If client sends exit,close this connection
                	// and then break from the while loop 
	                if(tosend.equals("EXIT")) 
	                {
	                    System.out.println("Closing this connection : " + s); 
	                    s.close(); 
	                    System.out.println("Connection closed"); 
	                    break; 
	                }
	                if(tosend.contains("upload_udp"))
	                {
	                	String[] com=tosend.split(" ");
	                	
	                	String FileName=com[1];
	                	File file = new File(FileName);
	                    DatagramSocket ds = null;
	                    if (!file.exists()) 
	                    {
	                        System.out.println("File not Exist");
	                        continue;
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
	                        byte[] buffer = new byte[4096];         
	                        
	                        str = String.valueOf(fileSize);
	                        dp = new DatagramPacket(str.getBytes(), str.getBytes().length, serverAdd, 9999);
	                        ds.send(dp);
	                        
	                        str=group_name+"/"+user_id+"/"+FileName;
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
	                else if(tosend.contains("upload"))
	                {
	                	String[] com=tosend.split(" ");
	                	File f=new File(com[1]);
	                    if(f.exists())
	                    {
	                        System.out.println("Sending ....");
	                        FileInputStream fis = new FileInputStream(com[1]);
	                        long size=f.length();
	                        dos.writeUTF(group_name+"/"+user_id+"/"+com[1]);
	                        dos.flush();
	                        dos.writeUTF(Long.toString(size));
	                        dos.flush();
	                        byte[] buffer = new byte[4096];
	                        int bytesRead=0;
	                        while(size>0 && (bytesRead=fis.read(buffer)) > 0)
	                        {
	                            dos.write(buffer);
	                            size-=bytesRead;
	                        }
	                        fis.close();
	                        dos.flush();
	                    }
	                    else
	                    {
	                        System.out.println("File Not Found");
	                    }
	                }
	                else if(tosend.contains("move_file")){
	                	String[] com=tosend.split(" ");
	                	if(com.length!=3){
	                		System.out.println("Incorrect Format");
	                	}
	                }
	                else if(tosend.contains("get_file")) {
	                	//Thread.sleep(2000);
	                	String[] com=tosend.split(" ");
	                	long fileSize;
	                    long totalReadBytes = 0;
	                    String file_name="";
	                    byte[] buffer = new byte[10000];
	                    try {
	                        int nReadSize = 0;
	                        System.out.println("Waiting.....");
	                          
	                        DatagramSocket ds = new DatagramSocket(9999);
	                        FileOutputStream fos = null;       
	                        
	                        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
	                        ds.receive(dp);
	                        String str = new String(dp.getData()).trim();
	                        
	                        if(str.equals("start"))
	                        {
	                            System.out.println(str);
	                            dp = new DatagramPacket(buffer, buffer.length);
	                            // get file size
	                            ds.receive(dp);
	                            str = new String(dp.getData()).trim();
	                            fileSize = Long.parseLong(str);
	                            // get file name..
	                            dp = new DatagramPacket(buffer, buffer.length);
	                            ds.receive(dp);
	                            str = new String(dp.getData()).trim();
	                            file_name = str;
	                            File f=new File(file_name);
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
	                }
	                else if(tosend.contains("join_group")) {
	                	dos.writeUTF(Integer.toString(user_id));
	                	dos.writeUTF(group_name);
	                }
	                else if(tosend.contains("leaveGroup")) {
	                	dos.writeUTF(Integer.toString(user_id));
	                	dos.writeUTF(group_name);
	                }
                	String received = dis.readUTF(); 
                	System.out.println(received); 
                }
            }
              
            // closing resources 
            scn.close(); 
            dis.close(); 
            dos.close(); 
        }catch(Exception e){ 
            e.printStackTrace(); 
        }
    }
}