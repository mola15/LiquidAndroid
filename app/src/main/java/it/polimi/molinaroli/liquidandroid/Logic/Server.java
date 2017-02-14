package it.polimi.molinaroli.liquidandroid.Logic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import it.polimi.molinaroli.liquidandroid.ResultActivity;

/**
 * The server creates the sockets and waits for messages form the clients,
 * handles the request and responds.
 */
public class Server {
    private ServerSocket mServerSocket;
    private int mLocalPort;
    private ArrayList<Socket> clients;
    private Context context;

    public Server(Context c){
        this.context = c;
        try {
            initializeServerSocket();
        } catch (IOException e) {
            Log.e("Server","Error server not started");
            e.printStackTrace();
        }
    }

    /**
     * starts the serversocket and stores the port
     * @throws IOException
     */
    public void initializeServerSocket() throws IOException {
        // Initialize a server socket on the next available port.
        setmServerSocket(new ServerSocket(0));
        // Store the chosen port.
        setmLocalPort(getmServerSocket().getLocalPort());
        clients = new ArrayList<>();
        Log.d("server","server port: "+ getmLocalPort());
    }
    public void startServer(){

        try {
            while(true) {
                Log.d("server" ,"server started");
                // bloccante finchè non avviene una connessione:
                Socket clientSocket = getmServerSocket().accept();
                //salva i client connessi
                getClients().add(clientSocket);
                Log.d("server","Connection accepted: "+ clientSocket);
                try {
                    new ServerThread(clientSocket);
                } catch(IOException e) {
                    clientSocket.close();
                }
            }
        }
        catch (IOException e) {
            System.err.println("Accept failed");
            System.exit(1);
        }
        //code to be executed only after a failure
        System.out.println("EchoMultiServer: closing...");
        try {
            getmServerSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerSocket getmServerSocket() {
        return mServerSocket;
    }

    public void setmServerSocket(ServerSocket mServerSocket) {
        this.mServerSocket = mServerSocket;
    }

    public int getmLocalPort() {
        return mLocalPort;
    }

    public void setmLocalPort(int mLocalPort) {
        this.mLocalPort = mLocalPort;
    }

    public ArrayList<Socket> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Socket> clients) {
        this.clients = clients;
    }

    /**
     * Server thread that it used to communicate with a single client
     * in that thread will be implemented communications methods
     * there will be one thread for each client
     */
    class ServerThread extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        public ServerThread(Socket s) throws IOException {
            socket = s;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
            out = new PrintWriter(new BufferedWriter(osw), true);
            start();
            Log.d("Server","ServerThread started" + s.toString());
        }
        public void run() {
            Context c = context;
            try {
                while (true) {
                    //legge dal buffer in ingresso (legge dal client)
                    String str = in.readLine();
                    Log.d("Server",str);
                    if (str.equals("END")) break;
                    //esegue quello che deve eseguire
                    //possibile caso di uscita
                    //metodo per scrivere sul buffer in uscita (scrive sul client)
                    Log.i("server client mess:", str);
                    Log.d("Server","building intent");

                    // Create the text message with a string
                    /* PROVA APERTURA BROWSER
                    Intent viewIntent = new Intent();
                    viewIntent.setAction(Intent.ACTION_VIEW);
                    viewIntent.setData(Uri.parse(str));
                    viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // Verify that the intent will resolve to an activity
                    if (viewIntent.resolveActivity(c.getPackageManager()) != null) {
                        c.startActivity(viewIntent);
                    } else{
                        Log.d("server","ramo else");
                    }
                    out.println(str);
                     END PROVA APERTURA BROWSER */
                    Intent cIntent = new Intent(c, ResultActivity.class);
                    cIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    cIntent.putExtra("INDIRIZZO",socket.getInetAddress().toString());
                    cIntent.putExtra("PORTA",socket.getPort());
                    c.startActivity(cIntent);
                    out.println("presa in carico");

                }

               //cose da farlgi fare in uscita
                Log.d("Server","ServerThread closing...");
            } catch (IOException e) {}
            try {
                socket.close();
            } catch(IOException e) {}
        }
    } // ServerThread

    public void stopServer(){
        try {
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
