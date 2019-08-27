This filr has structure as following :
   FileName:
   		   File Info.
           ClassName:
                Class list and Info.
                     FunctionNames:




1. Client.java:
            class chatHandler:
                    
                    Contains method to handle the chat functionality of a client. 	
                    
                    Function: public void run()

                    	Thread function gets active as soon as user makes the account.
            
            class Client:
                    
                    Has main method wich establishes connection with server and then communicates with Server's thread for executing commands using socket,DataInputStream and DataOutputStream.
2.  User.java
		   
		    class User:

		            This class represents properties (variables of a user) and
		            contains getter and setter methods for getting and setting value to them
		            respectively.It extends to Group class to inherit properties groupName and chatFilePath.

		            Function: int getUserId
		            Function: void setUserId
		            Function: String getUserName
		            Function: void setUserName

3. Group.java

            class Group:

                    This class has group properties and their setters,getters methods.

                    Function: public String getGroup_name()
                    Function: String getFilePath()
                    Function: public void setFilePath(String path)                    
                    Function: setGroup_name(String group_name)

4. Server.java
            
            class Server:

                Has Main method and accets new connections from cleint and in return launched
                new thread clientHandler.

            class ClientHandler:

            	This class communicates as thread with client and executes all commands from client.

            	Function: public void run()
            	Function: String addUser(String uname)
            	Function: sendFile(String path)
            	Function: sendFileUDP(Stirng path)
            	Function: make_folder(String path)
            	Function: move_file(String p1,String p2)
            	Function: createGroup(String grpName);
            	Function: listGroup();
            	Function: joinGroup(String gname)
            	Function: leaveGroup();
            	Function: listDetail(String grpName)
            	Function: shareMsg(String grpName,String msg)
            	Function: String getFile(String path)