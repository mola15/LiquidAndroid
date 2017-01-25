package it.polimi.molinaroli.liquidandroid.Logic;

import android.content.Context;
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

/**
 * The server creates the sockets and waits for messages form the clients,
 * handles the request and responds.
 */
public class Server {
    private ServerSocket mServerSocket;
    private int mLocalPort;
    private ArrayList<Socket> clients;
    private NsdHelper helper;
    private Context c;


    public Server(Context c){
        this.c = c;
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
            try {
                while (true) {
                    //legge dal buffer in ingresso (legge dal client)
                    String str = in.readLine();
                    if (str.equals("END")) break;
                    //esegue quello che deve eseguire
                    //possibile caso di uscita
                    //metodo per scrivere sul buffer in uscita (scrive sul client)
                    out.println(str);
                }
               //cose da farlgi fare in uscita
                Log.d("Server","ServerThread closing...");
            } catch (IOException e) {}
            try {
                socket.close();
            } catch(IOException e) {}
        }
    } // ServerThread
}
