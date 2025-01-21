package shady.bco.js;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.core.app.NotificationCompat;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Log.d("Broadcast","an SMS Received");

            Bundle mBundle = intent.getExtras();
            SmsMessage[] msg;
            String Sender;

            if (mBundle != null) {
                Object[] mPdus = (Object[]) mBundle.get("pdus");
                msg = new SmsMessage[mPdus.length];

                for (int i = 0; i < mPdus.length; i++) {
                    msg[i] = SmsMessage.createFromPdu((byte[]) mPdus[i]);
                    Sender = msg[i].getOriginatingAddress();
                    String smsBody = msg[i].getMessageBody();

                    Log.d("Broadcast", "an SMS Received :" + smsBody);
                    SharedPreferences shPref = context.getSharedPreferences("Groups", Context.MODE_PRIVATE);
                    String[] GroupNames = GroupMenuActivity.getGroupNames(shPref);
                    for (int x = 1; x <= GroupNames.length; x++) {
                        String input = null;
                        while (input == null) input = shPref.getString(GroupNames[x - 1], null);
                        String[] PNs = input.split(",");
                        for (int z = 1; z <= PNs.length; z++) {
                            if (AddActivity.Comparison(Sender, PNs[z - 1])) {
                                Log.d("Broadcast", "SMS Belongs to Group " + GroupNames[x - 1]);
                                String OldNotifs = shPref.getString("Notif-"+GroupNames[x-1],null);
                                String NewNotifs="";
                                if (OldNotifs==null) {
                                    NewNotifs=smsBody;
                                    Log.d("Broadcast", "No Old Unseen Messages");
                                }
                                else {
                                    NewNotifs += OldNotifs+","+smsBody;
                                    Log.d("Broadcast", "Old Unseen Messages Joined");
                                }
                                SharedPreferences.Editor editor = shPref.edit();
                                editor.putString("Notif-"+GroupNames[x-1],NewNotifs);
                                editor.apply();
                                NotifMaker(context, GroupNames[x - 1],x, NewNotifs.split(","));
                                break;
                            }
                        }
                    }
                }

            }

        }

    }
    public void NotifMaker(Context context,String GroupName,int ID,String[] Messages){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (int i=1;i<=Messages.length;i++) inboxStyle.addLine(Messages[i-1]);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,GroupName).setSmallIcon(R.drawable.message_vector).setContentTitle(GroupName).setStyle(inboxStyle);
        /*Intent intent=new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_ONE_SHOT);
        builder.addAction(R.drawable.add2_vector,null,pendingIntent);*/
        notificationManager.notify(ID,builder.build());

        Log.d("Broadcast","a Notification for "+GroupName+" Group, With ID of "+String.valueOf(ID)+" has Been Created");
    }

    public static void createNotifChannel(Context context,String ChannelName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int offerChannelImportance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notifChannel = new NotificationChannel(ChannelName, ChannelName, offerChannelImportance);
            notifChannel.enableVibration(true);
            notifChannel.enableLights(true);
            notifChannel.setLightColor(Color.WHITE);

            notificationManager.createNotificationChannel(notifChannel);
            Log.d("Broadcast",ChannelName+" Notification Channel Has been Created");

        }

    }


    public int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss").format(now));
        return id;
    }

}
