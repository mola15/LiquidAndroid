package it.polimi.molinaroli.liquidandroid.Logic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import static xdroid.toaster.Toaster.toast;


import it.polimi.molinaroli.liquidandroid.MainActivity;
import xdroid.toaster.Toaster;

import static android.R.attr.id;

/**
 * The client connects to the server and send messages to it,
 */

public class Client {
    private int serverPort; //porta del server locale serve per aprire un eventuale connessione di ritorno sulla serversocket
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Context context;
    private int flag; //serve a lanciare il tipo giusto di client 0 CLIENT NORMALE, 1 CLIENT CHE RIPORTA INDIETRO UN DATO A CHI L'HA RICHIESTO
    private String data;
    private int code;

    /**
     * client generico che passa l'intento così com è al server
     * convertendolo per mandarlo con la socket
     * @param addr
     * @param port
     * @param c
     * @param i
     */
    public Client(InetAddress addr, int port, Context c, Intent i) {
        this.context = c;
        try {
            socket = new Socket(addr, port);
            Log.d("Client", "Client started Client Socket:" + socket.toString());
        } catch (IOException e) {
            Log.d("Client","socket non creata");
        }
        // Se la creazione della socket fallisce non è necessario fare nulla
        try {
            //entro qui quando è connesso alla socket
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            in = new BufferedReader(isr);
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
            out = new PrintWriter(new BufferedWriter(osw), true);

            Log.e("client","lancio startclient");
            startClient(i);

        } catch (IOException e1) {
            // in seguito ad ogni fallimento la socket deve essere chiusa, altrimenti
            // verrà chiusa dal metodo run() del thread
            Log.e("client","IO exception di comunicazione");
            Toaster.toast("IO exception di comunicazione");
            try {
                socket.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        } catch (NullPointerException e3){
            e3.printStackTrace();
            //non c'è la serversocket
            Log.e("client","null pointer");
            CharSequence text = "Server non più disponibile";
            Toaster.toast(text);


        }
    }

    /**
     * Client to ask the server to take a picture with the camera
     * @param addr
     * @param port
     * @param c
     * @param serverPort
     */
    public Client(InetAddress addr, int port, Context c, int serverPort) {
        this.serverPort = serverPort;
        this.context = c;
        try {
            socket = new Socket(addr, port);
            Log.d("Client", "Client started Client Socket:" + socket.toString());
        } catch (IOException e) {
            Log.d("Client","socket non creata");
        }
        // Se la creazione della socket fallisce non è necessario fare nulla
        try {
            //entro qui quando è connesso alla socket
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            in = new BufferedReader(isr);
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
            out = new PrintWriter(new BufferedWriter(osw), true);


                startClient();

        } catch (IOException e1) {
            try {
                socket.close();
            } catch (IOException e2) {
            }
        } catch (NullPointerException e3){
            CharSequence text = "Server non più disponibile";
            toast(text);


        }
    }

    /**
     * client to send back the image file
     * @param addr
     * @param port
     * @param c
     * @param f
     */
    public Client(InetAddress addr, int port, Context c, String f) {
        this.context = c;
        try {
            Log.d("returnClient",addr.getHostAddress());
            Log.d("returnClient","" + port);

            socket = new Socket(addr, port);
            Log.d("Client", "Client started Client Socket:" + socket.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Client","socket non creata");
        }
        // Se la creazione della socket fallisce non è necessario fare nulla
        try {
            //entro qui quando è connesso alla socket
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            in = new BufferedReader(isr);
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
            out = new PrintWriter(new BufferedWriter(osw), true);


            startReturnClient(f);

        } catch (IOException e1) {
            // in seguito ad ogni fallimento la socket deve essere chiusa, altrimenti
            // verrà chiusa dal metodo run() del thread
            try {
                socket.close();
            } catch (IOException e2) {
            }
        } catch (NullPointerException e3){
            //non c'è la serversocket

            CharSequence text = "Server non più disponibile";
            toast(text);


        }
    }

    /**
     * starts the client specifying the return port of its server to get data back
     */
    public void startClient() {
        try {
                out.println(serverPort);
                String str = in.readLine();
                System.out.println("server: " + str);
            //chiudo la socket e il thread del server
            out.println("END");
        } catch (IOException e) {
            Log.d("Client","errore di rete");
        }
        try {
            System.out.println("Client closing...");
            socket.close();
        } catch (IOException e) {
        }
    }


    /**
     * starts the client and passes the intent to be forwarded
     * @param i
     */
    public void startClient(Intent i) {
        try {
            out.println("INTENT");
            out.println(IntentConverter.intentToJSON(i).toString());
            Log.d("client","intento spedito");
            in.readLine(); //mi conferma che ha ricevuto l'intento
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println("END"); //chiedo di chiudere la connessione

        try {
            System.out.println("Client closing...");
            socket.close();
        } catch (IOException e) {
        }
    }


    /**
     * returns the image taken from the camera to the caller
     * @param file
     */
    public void startReturnClient(String file) {
        try {
            out.println("IMAGE");

            Log.d("returnClient","scrittaimmagine");

            File f = new File(file);
            byte[] bytes = new byte[(int) f.length()];
            Log.d("filel", ""+ file.length());
            BufferedInputStream bis;

                bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytes, 0, bytes.length);

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(bytes);
                oos.flush();

            out.println("END");
            Log.d("returnClient","scrittaend");

        } catch (IOException e) {
            Log.d("Client","errore di rete");
        }
        try {
            System.out.println("Client closing...");
            socket.close();
        } catch (IOException e) {
        }
    }
}
