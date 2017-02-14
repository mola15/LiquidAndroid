package it.polimi.molinaroli.liquidandroid.Logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import static xdroid.toaster.Toaster.toast;


import it.polimi.molinaroli.liquidandroid.MainActivity;

import static android.R.attr.id;

/**
 * The client connects to the server and send messages to it,
 */

public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Context context;
    private int flag; //serve a lanciare il tipo giusto di client 0 CLIENT NORMALE, 1 CLIENT CHE RIPORTA INDIETRO UN DATO A CHI L'HA RICHIESTO



    public Client(InetAddress addr, int port, Context c, int flag) {
        this.flag = flag;
        this.context = c;
        try {
            socket = new Socket(addr, port);
            Log.d("Client", "Client started Client Socket:" + socket.toString());
        } catch (IOException e) {
            Log.d("Client","socket non creata");
        }
        Log.e("Client","socket non creata");
        // Se la creazione della socket fallisce non è necessario fare nulla
        try {
            //entro qui quando è connesso alla socket
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            in = new BufferedReader(isr);
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
            out = new PrintWriter(new BufferedWriter(osw), true);

            //lancio diversi tipi di client
            switch (flag){
                case 0:  startClient(); break;
                case 1: break;
            }
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

    public void startClient() {
        try {

            //prova mando intento per aprire google.it

                String mess = "http://google.it";
                out.println(mess);
                Log.d("client", mess);
                //leggo dal server
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

    public void startReturnClient() {
        try {

            //prova mando intento per aprire google.it

            // mando indietro la foto e poi chiudo la connessione
            String str = in.readLine();
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
}
