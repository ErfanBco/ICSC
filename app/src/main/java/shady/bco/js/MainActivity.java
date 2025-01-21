package shady.bco.js;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
    TextView textViewNoGroup;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Typeface typefaceEntezar;

    ChatAdaptor chatAdaptor;
    List<ChatItems> chatList = new ArrayList<>();
    int SMSes_start_index =1;
    int SMSes_end_index =0;
    int SMSes_count;
    int SMSes_pattern=40;
    public static boolean showPNs=true;
    boolean shouldQuit=false;
    CountDownTimer countDownTimer;
    MenuItem[] menuItems;

    String VALUE_NO_GROUP="شما هیچ گروه ایجاد نکرده اید برای ایجاد گروه اینجا را کلیک کنید";
    String VALUE_NO_SMS="هیچ پیامی یافت نشد!";
    int[] NewMessagesCountImages={R.drawable.one,R.drawable.two,R.drawable.three,R.drawable.four,R.drawable.five,R.drawable.six,R.drawable.seven,R.drawable.eight,R.drawable.nine,R.drawable.ten};
    int NewMessagesCountImagesIndex=0;
    ImageView imageViewNewMessages;

    SharedPreferences shPref;
    BroadcastReceiver broadcastReceiver;
    FloatingActionButton floatingActionButton;
    boolean ShouldUnCheckDefault =true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Done Add Runtime Permission For Receiving SMS
        //TODO Night Theme
        //Done Notifications
        //TODO Notifications Action
        //TODO Icon Next to drawer showing unread messages count
        //Done fix things for phones persian as language
        //Done Loading SMSes Level By Level
        //Done Hide Phone Numbers Option
        //Done Chats Scroller
        //Done Date TO shamsi
        //ToDo Header
        //Done Splash Screen
        //ToDo پیام های مانیتورینگ فاوا-ICT SMS Mon
        //ToDo layouts and Sizes
        //Done Ask Are You Sure? When Deleting the Group
        //Done Participants RecyclerView Size
        //Done NestedScrollView for ChipGroup
        //Done NewMessagesCountImages be turned to white
        //Done multiple contacts
        //Done save state of selected group
        //ToDo set Package Name
        //ToDo Evaluate More on phones which have persian as language

        Casting();

        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                    Log.d("Main2","an SMS Received");

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
                            String input = null;
                            while (input == null) {
                                input = shPref.getString(toolbar.getTitle().toString(), null);
                            }
                            Log.d("Main2", "Group's PhoneNumbers:" + input);
                            String[] PhoneNumbers = input.split(",");
                            boolean ShouldHave = false;
                            for (int x = 1; x <= PhoneNumbers.length; x++) {
                                if (AddActivity.Comparison(Sender, PhoneNumbers[x - 1]))
                                    ShouldHave = true;
                            }
                            if (!ShouldHave) continue;
                            Collections.reverse(chatList);
                            chatList.add(new ChatItems(smsBody,Sender,null,msg[i].getTimestampMillis(),0));
                            Collections.reverse(chatList);
                            imageViewNewMessages.setVisibility(View.VISIBLE);
                            chatAdaptor.notifyDataSetChanged();
                            if (textViewNoGroup.getText().toString().equals(VALUE_NO_SMS)) textViewNoGroup.setVisibility(View.GONE);
                            if (floatingActionButton.getVisibility()==View.VISIBLE) {
                                if (NewMessagesCountImagesIndex<=NewMessagesCountImages.length-1) {
                                    imageViewNewMessages.setImageResource(NewMessagesCountImages[NewMessagesCountImagesIndex]);
                                    NewMessagesCountImagesIndex++;
                                }else imageViewNewMessages.setImageResource(NewMessagesCountImages[9]);
                            }
                        }
                    }
                }

            }
        };
        registerReceiver(broadcastReceiver,new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }





    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d("Main","Main Started!");
        ShouldUnCheckDefault =true;
        getGroups();
    }
    public static String Comparison2(String Phone){
        String output="";
        /*if (Phone.charAt(0)=='9' && Phone.charAt(1) == '8'){
            //The Number Has 98 at the Beginning
            output += Telephony.Sms.ADDRESS + "=" + Phone;//One Number With +98
            output += " OR "+Telephony.Sms.ADDRESS + "=" + Phone.substring(2); //One Without 98
            if (Phone.charAt(2)=='0') output += " OR "+Telephony.Sms.ADDRESS + "=" + Phone.substring(3); //One Without 980
            else output += " OR "+Telephony.Sms.ADDRESS + "=" + "0"+Phone.substring(2); //One With 0 but Without 98

        }else if (Phone.charAt(0)=='0' && Phone.charAt(1)=='9'){
            //The Number Has 09 at the Beginning
            output += Telephony.Sms.ADDRESS + "=" + Phone;//One Number With 09
        }else{

        }*/
        /*if (Phone.charAt(0)=='+' && Phone.charAt(1) == '9' && Phone.charAt(2)=='8') {
            output +=  Phone;//One Number With +98
        } else if (Phone.charAt(0)=='9' && Phone.charAt(1) == '8'){
            if (Phone.charAt(2)=='0') output +="+98"+Phone.substring(3);
            else output += "+98"+Phone.substring(2);
        } else if (Phone.charAt(0) == '0'){
            output +="+98"+Phone.substring(1);
        }else if (Phone.charAt(0) == '9' && !(Phone.charAt(1) == '8')){
            output +="+98"+Phone;
        }*/
        Phone = Phone.replaceAll("/+","");
        if (Phone.charAt(0)=='9' && Phone.charAt(1) == '8') Phone = Phone.substring(2);
        if (Phone.charAt(0)=='0') Phone = Phone.substring(1);
        return Phone;
    }
    private void getSMSes(String GroupName) {
        ProgressBar progressBar =findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        Log.d("Main2", "getSMSes() is Called");
        Log.d("Main2", "showPNs:"+String.valueOf(showPNs));
        String input = null;
        while (input == null) {
            Log.d("Main2", "getting Group's PhoneNumbers");
            input = shPref.getString(GroupName, null);
        }
        if (input.equals("")){
            chatList.clear();
            chatAdaptor.notifyDataSetChanged();
            textViewNoGroup.setVisibility(View.VISIBLE);
            textViewNoGroup.setText("هیج شماره تلفنی اضافه نشده است!");
            Log.d("Main2", "No PhoneNumber");
            progressBar.setVisibility(View.GONE);
            return;
        }else  textViewNoGroup.setVisibility(View.GONE);
        Log.d("Main2", "Group's PhoneNumbers:" + input);
        String[] PhoneNumbers = input.split(",");
        String Selection = "(";
        for (int x = 1; x <= PhoneNumbers.length; x++) {
            if (x == 1) Selection += Telephony.Sms.ADDRESS + " like " +"'%"+ Comparison2(PhoneNumbers[x - 1])+"%'";
            else Selection += " OR " + Telephony.Sms.ADDRESS + " like "+"'%" + Comparison2(PhoneNumbers[x - 1])+"%'";
        }
        Selection += ") AND "+Telephony.Sms.TYPE+"=1";

        //Log.d("Main2", "Selection:" + Selection);
        Cursor SMSes = getContentResolver().query(Uri.parse("content://sms/"), null, Selection, null, null);
        SMSes_count = SMSes.getCount();
        SMSes_end_index=Math.min(SMSes_end_index+SMSes_pattern, SMSes_count);
        Log.d("Main2","Range is From "+String.valueOf(SMSes_start_index)+" to "+String.valueOf(SMSes_end_index));
        SMSes.move(SMSes_start_index-1);
        for (int i = SMSes_start_index; i <= SMSes_end_index; i++) {
            SMSes.moveToNext();
                /*boolean ShouldHave = false;
                for (int x = 1; x <= PhoneNumbers.length; x++) {
                    if (AddActivity.Comparison(SMSes.getString(SMSes.getColumnIndex(Telephony.Sms.ADDRESS)), PhoneNumbers[x - 1]) && SMSes.getInt(SMSes.getColumnIndex(Telephony.Sms.TYPE)) == 1)
                        ShouldHave = true;
                }
                if (!ShouldHave) continue;*/
            Log.d("Main2", "New SMS");
            String Person = null;
            String Selection2 =ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + SMSes.getString(SMSes.getColumnIndex(Telephony.Sms.ADDRESS))+"'";

            Log.d("Main2","getting SMS Contact Name");
            for (int x=2;x<=15;x++)
                Selection2 += " OR  data"+String.valueOf(x)+" = '" + SMSes.getString(SMSes.getColumnIndex(Telephony.Sms.ADDRESS))+"'";
            if (SMSes.getString(SMSes.getColumnIndex(Telephony.Sms.PERSON)) != null) {
                Selection2 += " OR "+ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + SMSes.getString(SMSes.getColumnIndex(Telephony.Sms.PERSON));
            }
            //Log.d("Main2","Selection2:"+Selection2);
            Cursor cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, Selection2, null, null);
            if (cursor2.getCount()>0) {
                cursor2.moveToFirst();
                Person = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                /*for (int z = 1; z <= cursor2.getColumnCount(); z++) {
                    Log.d("Contact", String.valueOf(z) + "-" + cursor2.getColumnName(z - 1) + "," + cursor2.getString(z - 1));
                }*/
            }
            cursor2.close();

                if (SMSes.getInt(SMSes.getColumnIndex(Telephony.Sms.READ))==0) {
                    ContentValues values = new ContentValues();
                    values.put(Telephony.Sms.READ, true);
                    String Selection3 ="_id=" +SMSes.getString(SMSes.getColumnIndex(Telephony.Sms._ID));
                    Log.d("Main2","Selection3:"+Selection3);
                    int result =getContentResolver().update(Uri.parse("content://sms/"), values, Selection3, null);
                    Log.d("Main2","Set as Read:"+String.valueOf(result));
                }
            chatList.add(new ChatItems(SMSes.getString(SMSes.getColumnIndex(Telephony.Sms.BODY)), SMSes.getString(SMSes.getColumnIndex(Telephony.Sms.ADDRESS)), Person, SMSes.getLong(SMSes.getColumnIndex(Telephony.Sms.DATE)),SMSes.getInt(SMSes.getColumnIndex(Telephony.Sms.READ))));
            /*String tst = "";
            for (int x = 1; x <= SMSes.getColumnCount(); x++) {
                tst += SMSes.getColumnName(x - 1) + ":" + SMSes.getString(x - 1) + "|";
            }
            Log.d("SMSes", tst);*/
        }
        SMSes.close();
        recyclerView.setAdapter(chatAdaptor);
        ((LinearLayoutManager)(recyclerView.getLayoutManager())).scrollToPositionWithOffset(SMSes_start_index-2,1);
        Log.d("Main2", "Auto Scrolled to "+String.valueOf(SMSes_start_index));
        SMSes_start_index=SMSes_end_index+1;
        if (chatList.size()==0){
            Log.d("Main2", "No Message Was Found!");
            textViewNoGroup.setVisibility(View.VISIBLE);
            textViewNoGroup.setText(VALUE_NO_SMS);
        }else textViewNoGroup.setVisibility(View.GONE);
        SharedPreferences.Editor editor = shPref.edit();
        editor.remove("Notif-"+GroupName);
        editor.apply();
        Log.d("Main2", "Notif Old Messages Reset");
        progressBar.setVisibility(View.GONE);
        Log.d("Main2", "getSMSes() is Finished");
    }





    private void Casting() {
        Log.d("tst","Casting is there");
        toolbar = findViewById(R.id.Toolbar);
        typefaceEntezar = Typeface.createFromAsset(getAssets(), "fonts/entezar.ttf");
        setToolbarTitle("");
        setSupportActionBar(toolbar);
        imageViewNewMessages=findViewById(R.id.imageViewNM);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,GroupMenuActivity.class);
                intent.putExtra("GroupName",toolbar.getTitle().toString());
                startActivity(intent);
            }
        });
        textViewNoGroup = findViewById(R.id.textViewNoGroup);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton=findViewById(R.id.floatingActionButton2);
        shPref = getSharedPreferences("Groups", Context.MODE_PRIVATE);
        ActionBarDrawerToggle Toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.OPEN,R.string.CLOSE);
        Toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        chatAdaptor = new ChatAdaptor(this, chatList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(chatAdaptor);

        drawerLayout.addDrawerListener(Toggle);
        Toggle.syncState();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (recyclerView.canScrollVertically(1)) floatingActionButton.setVisibility(View.VISIBLE);
                else{
                    NewMessagesCountImagesIndex=0;
                    imageViewNewMessages.setImageBitmap(null);
                    imageViewNewMessages.setVisibility(View.GONE);
                    floatingActionButton.setVisibility(View.GONE);
                }
                if (!recyclerView.canScrollVertically(-1) && SMSes_count>SMSes_end_index) getSMSes(toolbar.getTitle().toString());
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.getLayoutManager().scrollToPosition(0);
                NewMessagesCountImagesIndex=0;
                imageViewNewMessages.setImageBitmap(null);
                imageViewNewMessages.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.GONE);
            }
        });
    }


    private void getGroups() {
        Log.d("Main2","getGroups() is Called");
        Menu menu = navigationView.getMenu();
        menu.clear();
        SubMenu subMenu2 = menu.addSubMenu(null);
        MenuItem menuItemAddGroup = subMenu2.add("ایجاد گروه").setIcon(R.drawable.add2_vector);
        menuItemAddGroup.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AddGroup(null);
                return false;
            }
        });
        String names = shPref.getString("Names", null);
        Log.d("Main2","Group Names : "+names);
        if (names == null){
            toolbar.setClickable(false);
            textViewNoGroup.setVisibility(View.VISIBLE);
            textViewNoGroup.setText(VALUE_NO_GROUP);
            chatList.clear();
            chatAdaptor.notifyDataSetChanged();
            toolbar.setTitle("");
            SharedPreferences.Editor editor = shPref.edit();
            editor.putString("Names","");
            editor.putString("Default","");
            editor.putString("ShowPN","");
            editor.apply();
            Log.d("Main2","Shared Preferences Created.");
            return;
        }else {
            toolbar.setClickable(true);
            textViewNoGroup.setVisibility(View.GONE);
            Log.d("Main2","Shared Preferences Exist!");
        }
        if (names.equals("")){
            toolbar.setClickable(false);
            textViewNoGroup.setVisibility(View.VISIBLE);
            textViewNoGroup.setText(VALUE_NO_GROUP);
            chatList.clear();
            chatAdaptor.notifyDataSetChanged();
            toolbar.setTitle("");
            Log.d("Main2","NO Group Exists!");
            return;
        }else{
            toolbar.setClickable(true);
            textViewNoGroup.setVisibility(View.GONE);
        }
        String[] names2 = names.split(",");
        menuItems=new MenuItem[names2.length];

        SubMenu subMenu = menu.addSubMenu("  گروه ها");
        subMenu.setHeaderIcon(R.drawable.add2_icon);
        String ChoosenGroupName = "";
        String input = null;
        while (input == null) input = shPref.getString("Default", null);
        String input2 = null;
        while (input2 == null) input2 = shPref.getString("ShowPN", null);
        String[] ShowPNs=input2.split(",");

            for (int i = 1; i <= names2.length; i++) {
                menuItems[i - 1] = subMenu.add(names2[i - 1]);
                menuItems[i - 1] = menuItems[i - 1].setActionView(R.layout.action_layout);
                ImageButton imageButtonHide =menuItems[i - 1].getActionView().findViewById(R.id.imageButtonHide);
                if (ShowPNs[i-1].equals("true")) imageButtonHide.setAlpha(1f);
                else imageButtonHide.setAlpha(0.2f);
                RatingBar ratingBar = menuItems[i - 1].getActionView().findViewById(R.id.ratingBar);
                if (input.equals(menuItems[i - 1].getTitle().toString())) {
                    ratingBar.setRating(1);
                    if (Data.GROUP_NAME==null || Data.GROUP_NAME.equals(input)) {
                        ChoosenGroupName = menuItems[i - 1].getTitle().toString();
                        menuItems[i - 1].setChecked(true);
                        Log.d("Main2", "Loaded Default Group: " + menuItems[i - 1].getTitle().toString());
                        setToolbarTitle(menuItems[i - 1].getTitle().toString());
                        Data.GROUP_NAME=menuItems[i - 1].getTitle().toString();
                        if (ShowPNs[i-1].equals("true")) showPNs=true;
                        else showPNs=false;
                        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }
                }else if (Data.GROUP_NAME!= null && Data.GROUP_NAME.equals(menuItems[i - 1].getTitle().toString())){
                    ChoosenGroupName = menuItems[i - 1].getTitle().toString();
                    menuItems[i - 1].setChecked(true);
                    Log.d("Main2", "Loaded Previous Group: " + menuItems[i - 1].getTitle().toString());
                    setToolbarTitle(menuItems[i - 1].getTitle().toString());
                    Data.GROUP_NAME=menuItems[i-1].getTitle().toString();
                    if (ShowPNs[i-1].equals("true")) showPNs=true;
                    else showPNs=false;
                    toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                final int x = i;
                ratingBar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Log.d("Main", "New Default Group is " + menuItems[x - 1].getTitle().toString());
                        for (int i = 1; i <= menuItems.length; i++) {
                            RatingBar ratingBarTMP = menuItems[i - 1].getActionView().findViewById(R.id.ratingBar);
                            if (ratingBarTMP.getNumStars() >= 1 && i != x) {
                                Log.d("Main", "tst: " + String.valueOf(i));
                                ratingBarTMP.setRating(0);
                            } else if (i == x) ratingBarTMP.setRating(1);
                        /*if (ratingBarTMP==ratingBar) {
                            //ratingBarTMP.setRating(0);
                            ratingBarTMP.setRating(1);
                        }else ratingBarTMP.setRating(0);*/
                        }
                        SharedPreferences.Editor editor = shPref.edit();
                        editor.putString("Default", menuItems[x - 1].toString());

                        editor.apply();
                        return false;
                    }
                });
                imageButtonHide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = shPref.edit();
                        if (imageButtonHide.getAlpha()==1f){
                           imageButtonHide.setAlpha(0.2f);
                           ShowPNs[x-1]="false";

                       }else{
                           imageButtonHide.setAlpha(1f);
                            ShowPNs[x-1]="true";
                        }
                        String output=ShowPNs[0];
                        for (int z=2;z<=ShowPNs.length;z++) output +=","+ShowPNs[z-1];
                        editor.putString("ShowPN",output);
                        editor.apply();

                        if (menuItems[x-1].isChecked()) {
                            Log.d("Main2","Reload Initiated to Apply Change of showPNs");
                            if (ShowPNs[x-1].equals("true")) showPNs=true;
                            else showPNs=false;
                            chatList.clear();
                            SMSes_end_index = 0;
                            SMSes_start_index = 1;
                            getSMSes(menuItems[x-1].getTitle().toString());
                        }
                        drawerLayout.close();
                    }
                });
            }
            subMenu.setGroupCheckable(menuItems[0].getGroupId(), true, true);
            if (!ChoosenGroupName.equals("")) {
                chatList.clear();
                SMSes_end_index = 0;
                SMSes_start_index = 1;
                getSMSes(ChoosenGroupName);
            }
        Log.d("Main2","getGroups() is finished");
    }

    private void setToolbarTitle(String Title) {
        toolbar.setTitle(Title);
        //if (Fa.IsTheWordEnglish(Title)) toolbar.setTitleTextAppearance(this,R.style.EnglishToolbar);
        //else toolbar.setTitleTextAppearance(this,R.style.FarsiToolbar);
        toolbar.setTitleTextAppearance(this,R.style.FarsiToolbar);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getActionView()==null) return false;
        Log.d("Main","Another Group has been Chosen");
        if (item.isChecked()) {
            drawerLayout.close();
            return false;
        }
        if (ShouldUnCheckDefault) {
            ShouldUnCheckDefault =false;
            for (int i=1; i <= menuItems.length;i++) {
                menuItems[i-1].setChecked(false);
            }
        }
        setToolbarTitle(item.getTitle().toString());
        Data.GROUP_NAME=item.getTitle().toString();
        if (item.getActionView().findViewById(R.id.imageButtonHide).getAlpha()==1f) showPNs=true;
        else showPNs=false;
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        chatList.clear();
        SMSes_end_index=0;
        SMSes_start_index=1;
        getSMSes(item.getTitle().toString());
        drawerLayout.close();
        return true;
    }

    public void AddGroup(View view) {
        startActivity(new Intent(MainActivity.this,AddActivity.class));
    }




    @Override
    public void onBackPressed() {
        if (shouldQuit) {
            //finishAffinity();
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", 1234567);
            try {startActivity(smsIntent);}
            catch (Exception e) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("smsto:" + Uri.encode("1234567")));
                    startActivity(intent);
                }
                catch (Exception e1) {}
            }
        }
        else {
            shouldQuit=true;
            countDownTimer=new CountDownTimer(3000,1000) {
                @Override public void onTick(long millisUntilFinished) { }
                @Override
                public void onFinish() { shouldQuit=false; }
            }.start();
            Snackbar.make(findViewById(R.id.ConstraintLayout2),"برای خروج دوباره ضربه بزنید",Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        if (countDownTimer!=null) {
            Log.d("Main2","Timer Canceled");
            countDownTimer.cancel();
        }
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}