package it.polimi.molinaroli.liquidandroid.Logic;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import static android.R.attr.id;

/**
 * The client connects to the server and send messages to it,
 */

public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client(InetAddress addr, int port) {
        try {
            socket = new Socket(addr, port);
            Log.d("Client", "Client started Client Socket:" + socket.toString());
        } catch (IOException e) {
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
            // in seguito ad ogni fallimento la socket deve essere chiusa, altrimenti
            // verrà chiusa dal metodo run() del thread
            try {
                socket.close();
            } catch (IOException e2) {
            }
        }
    }

    public void startClient() {
        try {
            for (int i = 0; i < 2; i++) {
                String mess = "prova";
                out.println(mess);
                Log.d("client", mess);
                //leggo dal server
                String str = in.readLine();
                System.out.println("Echo: " + str);
            }
            out.println("END");
        } catch (IOException e) {
        }
        try {
            System.out.println("Client closing...");
            socket.close();
        } catch (IOException e) {
        }
    }
}
