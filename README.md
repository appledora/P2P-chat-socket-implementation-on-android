# Peer to Peer Chat

  

This is a peer to peer chat and file-sharing app, that connects two deivce on the same network using their IP address and designated port numbers.

  

## Getting Started

  
  

### Prerequisites

  

User's android version needs to be Lolipop(5.0) or higher to run this application. Also, he needs to give it the storage permission manually for now.

  

### Installing

  

Download it from Github and compile the project using Android Studio to run this project in an android device.

  

## Running the application

  

After the Application is launched user will bepresented with this screen where it will show the devices IP by default.

  

<p  align="center"  ><img  src="https://i.imgur.com/CZLDVKI.png"  width = "200"  height = "400"/></p>

I

This requires user to put the IP address and Port address of other peer and a port address on user's side to be opened for communication. After entering necessary information, user will be taken to next screen where he will be presented with the chat screen.

  

#### About Chat Screen

This screen presents with a simple Interface. Here user can see who he is connected to and if serversocket is working properly on users end. User can send files of any type clicking the clip button. Here he will have to select the file he wants to send and it is suggested to select file by going to the root location and then find it out and select.


<p  align="center"  ><img  src="https://i.imgur.com/mXTPAJi.png"  width = "200"  height = "400"/></p>

  

After selecting the file, he will be able to see its name on the screen. And also, if the selected file is an image file, both the sender and the receiver will be able to see a thumbnail of image in line.

<p  align="center"  ><img  src="https://i.imgur.com/wtvZ7xV.png"  width = "200"  height = "400"/></p>

User can also change the shade of background and the opposite side should receive the same color too.

<p  align="center"  ><img  src="https://i.imgur.com/YDOoKSU.png"  width = "200"  height = "400"/> <img  src="https://i.imgur.com/gTz7vQb.png"  width = "200"  height = "400"/></p>
The chat history and all the received file will be stored at the obb directory.
<p align="center"><img  src="https://i.imgur.com/eI4Tqep.png"  width = "200"  height = "400"/> 
</figure></p>

### Client Socket Code

    public class User extends AsyncTask<Void, Void, String> {  
      String msg;  
      
      User(String message) {  
	      msg = message;  
        }  
      
      @Override  
      protected String doInBackground(Void... voids) {  
      try {  
      String ipadd = serverIpAddress;  
                int portr = sendPort;  
                Socket clientSocket = new Socket(ipadd, portr);  
                OutputStream outToServer = clientSocket.getOutputStream();  
                PrintWriter output = new PrintWriter(outToServer);  
                output.println(msg);  
                output.flush();  
                clientSocket.close();  
                runOnUiThread(() -> sent.setEnabled(false)  
     );  
            } catch (Exception e) {  
      e.printStackTrace();  
            }  
      return msg;  
        }  
      
      protected void onPostExecute(String result) {  
      runOnUiThread(() -> sent.setEnabled(true));  
            Log.i(TAG, "on post execution result => " + result);  
            }  
     }  
      
    }

`

### Server Socket Code

      public void run() {  
      try {  
      ServerSocket initSocket = new ServerSocket(port);  
            initSocket.setReuseAddress(true);  
            TextView textView;  
            textView = activity.findViewById(R.id.textView);  
            textView.setText("Server Socket Started at IP: " + ownIp + " and Port: " + port);  
            textView.setBackgroundColor(Color.parseColor("#39FF14"));  
            System.out.println(TAG + "started");  
            while (!Thread.interrupted()) {  
      Socket connectSocket = initSocket.accept();  
                ReadFromClient handle = new ReadFromClient();  
                handle.execute(connectSocket);  
            }  
      initSocket.close();  
        } catch (IOException e) {  
      TextView textView;  
            textView = activity.findViewById(R.id.textView);  
            textView.setText("Server Socket initialization failed. Port already in use.");  
            textView.setBackgroundColor(Color.parseColor("#FF0800"));  
            e.printStackTrace();  
        }  
    }


Also don't forget to restart app if the messaging is not functioning properly.
## Built With

* <a href="https://docs.oracle.com/javase/7/docs/api/java/net/Socket.html">Java Socket</a>

## Authors

  

*  **NAZIA TASNIM**    [Appledora](https://github.com/appledora/)

*  **ISTIAK SHIHAB**   [istiakshihab](https://github.com/istiakshihab)

  

## Acknowledgments

  

* [Manug Gond](https://github.com/3ZadeSSG)

* [Quadflask](https://github.com/QuadFlask/colorpicker)

* [Glide](https://github.com/bumptech/glide)
