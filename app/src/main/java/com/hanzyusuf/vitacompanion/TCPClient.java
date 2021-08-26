package com.hanzyusuf.vitacompanion;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPClient {

    private TCPClientListener listener;

    public interface TCPClientListener {
        void onResponse(String response);
    }

    public TCPClient(TCPClientListener listener) {
        this.listener = listener;
    }

    public void sendData(String host, int port, String data) {
        new SendCommand(listener).execute(host, String.valueOf(port), data);
    }

    private static class SendCommand extends AsyncTask<String, Void, String> {

        TCPClientListener listener;

        public SendCommand(TCPClientListener listener){
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            try {
                Socket socket = new Socket(params[0],Integer.parseInt(params[1]));
                socket.setSoTimeout(2000);
                OutputStream out = socket.getOutputStream();
                out.write((params[2]+"\n").getBytes());
                Log.d("TEST",params[2]);
                InputStream in = socket.getInputStream();
                byte buf[] = new byte[1024];
                int nbytes;
                while ((nbytes = in.read(buf)) != -1) {
                    sb.append(new String(buf, 0, nbytes));
                }
                socket.close();
                SharedPreferencesManager.setLastIP(params[0]);
            } catch(Exception e) {
                e.printStackTrace();
                if(e.getMessage() != null)
                    return e.getMessage();
                return "Unknown error occurred!";
            }
            return sb.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            if(listener!=null)
                listener.onResponse(result);
        }
    }

}