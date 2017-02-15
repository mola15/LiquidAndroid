package it.polimi.molinaroli.liquidandroid.Logic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    String mCurrentPhotoPath;


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
                    if (str.equals("END")) break; //esce dal while
                    else if (str.equals("IMAGE")){
                        //caso che gestisce l'arrivo di una immagine
                        //devo leggere tutta la immagine

                        /*METODO FUNZIONANTE CON LA BITMAP
                        InputStream in = socket.getInputStream();
                        DataInputStream input = new DataInputStream(in);
                        byte[] data;//String read = input.readLine();
                        int len= input.readInt();
                        data = new byte[len];
                        if (len > 0) {
                            input.readFully(data, 0, data.length);
                        }
                        //a questo punto dentro data dovrebbe esserci la bitmap
                        //dopo dovrebbe esserci end e finisce chiude entrambi i thread
                        //ora dovrei mandarla alla activity posso startarla da qui ??
                        Log.d("server","lettura immagine in corso");
                        FINE METODO FUNZIONANTE CON LA BITMAP*/
                        try {
                            File file = createImageFile();
                            Log.d("server","file creato");
                            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                            byte[] bytes;
                            FileOutputStream fos = null;
                            try {
                                Log.d("server","leggo il file");
                                bytes = (byte[]) ois.readObject();
                                fos = new FileOutputStream(file);
                                fos.write(bytes);

                                //a questo punto dovrei avere il file con dentro tutto
                                Intent i = new Intent(context,ResultActivity.class);
                                i.putExtra("IMAGE",mCurrentPhotoPath);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Log.d("server","intento apertura immagine");
                                context.startActivity(i);

                            } catch (ClassNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Log.d("server","errore col file");

                            } finally {
                                if (fos != null) {
                                    fos.close();
                                    Log.d("server","file chiuso");
                                }
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        /*
                        Intent i = new Intent(context,ResultActivity.class);
                        //i.putExtra("IMAGE",data); CON LA BITMAP
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Log.d("server","intento apertura immagine");
                        context.startActivity(i);
                        */
                    }else {
                        int rport = Integer.valueOf(str); // leggo la porta bisogna vedere se è giusto
                        //per ora gestisce tutto il resto
                        //esegue quello che deve eseguire
                        //possibile caso di uscita
                        //metodo per scrivere sul buffer in uscita (scrive sul client)
                        Log.i("server client mess:", str);
                        Log.d("Server", "building intent");

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
                        cIntent.putExtra("INDIRIZZO", socket.getInetAddress().toString());
                        cIntent.putExtra("PORTA", rport);
                        Log.d("server",socket.getInetAddress().toString());
                        c.startActivity(cIntent);
                        out.println("presa in carico");
                    }

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

        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = image.getAbsolutePath();
            return image;
        }
}
