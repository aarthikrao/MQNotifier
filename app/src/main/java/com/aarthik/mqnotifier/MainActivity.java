package com.aarthik.mqnotifier;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {
    private final static String QUEUE_NAME = "hello";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    ///    Context context = getApplicationContext();
    TextView DisplayText;
    ConnectionFactory factory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        connectAndListen();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectAndListen();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void setText(final TextView text, final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
                sendNotification("Hello", value);
            }
        });
    }

    private void connectAndListen() {
        prefs = getSharedPreferences("MQNotifier", MODE_PRIVATE);
        DisplayText = (TextView) findViewById(R.id.displayText);
        factory = new ConnectionFactory();
        String host = prefs.getString("mqip", "192.168.1.7");
        factory.setHost(host);
        String port = prefs.getString("mqport", "5672");
        factory.setPort(5672);
        String username = prefs.getString("username", "admin");
        factory.setUsername(username);
        String password = prefs.getString("password", "admin");
        factory.setPassword(password);
        Log.i("Submit", " Credentials : " + host + " " + String.valueOf(port) + " " + username + " " + password + " ");
        factory.setAutomaticRecoveryEnabled(true);
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(prefs.getString("queuename", "hello"), false, false, false, null);
//            channel.exchangeDeclare("logs","fanout");
            Log.i("", "Channel declared");
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
//                    Toast.makeText(context,message, Toast.LENGTH_LONG);
                    Log.i(message, message);
                    setText(DisplayText, message);
                    Log.i("", "In the method");
                }
            };
            channel.basicConsume(QUEUE_NAME, true, consumer);

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendNotification(String title, String text) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text).setVibrate(new long[]{500, 500, 500, 500, 500})
                        .setLights(Color.RED, 500, 500)
                        .setSound(uri);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        synchronized (mNotificationManager) {
            mNotificationManager.notify(1, mBuilder.build());
        }
    }
}

