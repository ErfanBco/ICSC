package shady.bco.js;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupMenuActivity extends AppCompatActivity {
    ImageButton imageButtonAdd,imageButtonDelete,imageButtonRename;
    Button buttonRenameSubmit,buttonRenameCancel;
    EditText editTextRename;
    TextView textViewInfo;

    Intent intent;
    String oldGroupName,GroupName;
    SharedPreferences shPref;

    ParticipantsAdaptor participantsAdaptor;
    RecyclerView recyclerView;

    boolean shouldQuit=false;
    int[] DisplayMetrics;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu);

        DisplayMetrics=Data.getDisplayMetrics(getWindowManager());

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.toolbar3);
        View view = getSupportActionBar().getCustomView();
        imageButtonAdd=findViewById(R.id.buttonGMAdd);
        imageButtonDelete=findViewById(R.id.buttonGMDeleteGroup);
        imageButtonRename=findViewById(R.id.buttonGMChangeName);
        editTextRename=findViewById(R.id.editTextGMName);
        textViewInfo=findViewById(R.id.textViewGMInfo);
        buttonRenameSubmit=findViewById(R.id.buttonGMChaneNameSubmit);
        buttonRenameCancel=findViewById(R.id.buttonGMChaneNameCancel);
        recyclerView=findViewById(R.id.recyclerViewGM);
        intent=getIntent();
        GroupName=intent.getStringExtra("GroupName");
        editTextRename.setText(GroupName);
        if (android.os.Build.VERSION.SDK_INT < 21){
            editTextRename.setTextColor(getResources().getColor(R.color.black));
            textViewInfo.setTextColor(getResources().getColor(R.color.black));
            ((TextView)findViewById(R.id.textView2)).setTextColor(getResources().getColor(R.color.black));
        }
        Log.d("GroupMenu","GroupName:"+GroupName);
        shPref = getSharedPreferences("Groups", Context.MODE_PRIVATE);
        Refresh();

    }



    @Override
    protected void onPostResume() {
        super.onPostResume();
        Refresh();
    }

    private void Refresh() {
        String[] PNs=getPhoneNumbers();
        setInfo(PNs,textViewInfo);
        if (!PNs[0].equals("")) {
            //if Group is Not Empty
            findViewById(R.id.cardViewGroupMenu2).setVisibility(View.VISIBLE);
            participantsAdaptor = new ParticipantsAdaptor(this, GroupName, getPhoneNumbers(),textViewInfo);
            LinearLayoutManager layoutManager = new LinearLayoutManager(GroupMenuActivity.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(participantsAdaptor);
        }else findViewById(R.id.cardViewGroupMenu2).setVisibility(View.GONE);

    }

    public static void setInfo(String[] PNs,TextView textViewInfo) {
        int length=0;
        if (PNs.length==0 || !PNs[0].equals("")) length= PNs.length;
        textViewInfo.setText(String.valueOf(length)+" شماره تلفن");
    }


    @NonNull
    private String[] getPhoneNumbers() {
        String input=null;
        while (input==null) input=shPref.getString(GroupName,null);
        String[] PN=input.split(",");
        return PN;
    }

    @NonNull
    public static String[] getGroupNames(SharedPreferences shPref) {
        String input=null;
        while (input==null) input=shPref.getString("Names",null);
        Log.d("GroupMenu","input Names:"+input);
        return input.split(",");
    }

    @NonNull
    public static String[] getGroupShowPNs(SharedPreferences shPref) {
        String input=null;
        while (input==null) input=shPref.getString("ShowPN",null);
        Log.d("GroupMenu","input ShowPN:"+input);
        return input.split(",");
    }

    @NonNull
    public static String setGroupNames(String[] GroupNames) {
        String output="";
        for (int i = 1; i<= GroupNames.length; i++){
            if (i== GroupNames.length) output += GroupNames[i-1];
            else output += GroupNames[i-1]+",";
        }
        Log.d("GroupMenu","output Names:"+output);
        return output;
    }

    @NonNull
    public static String setGroupShowPNs(String[] GroupShowPNs) {
        String output="";
        for (int i = 1; i<= GroupShowPNs.length; i++){
            if (i== GroupShowPNs.length) output += GroupShowPNs[i-1];
            else output += GroupShowPNs[i-1]+",";
        }
        Log.d("GroupMenu","output ShowPN:"+output);
        return output;
    }

    public void Rename(View view) {
        Log.d("GroupMenu2","Rename() is Called");
        editTextRename.setEnabled(true);
        //editTextRename.requestFocus();
        buttonRenameSubmit.setVisibility(View.VISIBLE);
        buttonRenameCancel.setVisibility(View.VISIBLE);
    }

    public void Delete(View view) {
        Log.d("GroupMenu2","Delete() is Called");
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this);
        sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitle("آیا از حذف این گروه اطمینان دارید؟");
        sweetAlertDialog.setContentText("تمامی شماره های اضافه شده از دست خواهد رفت");
        sweetAlertDialog.setCancelButton("حذف", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                String[] GroupNames = getGroupNames(shPref);
                String[] GroupShowPNs = getGroupShowPNs(shPref);
                for (int i=1;i<=GroupNames.length;i++){
                    if (GroupNames[i-1].equals(GroupName)) {
                        Log.d("GroupMenu2","Group Name of "+GroupNames[i-1]+" has been Deleted!");
                        GroupNames= removeTheElement(GroupNames,i-1);
                        GroupShowPNs = removeTheElement(GroupShowPNs,i-1);
                        break;
                    }
                }
                String output = setGroupNames(GroupNames);
                String output2 = setGroupShowPNs(GroupShowPNs);
                Data.GROUP_NAME=null;
                SharedPreferences.Editor editor = shPref.edit();

                editor.putString("Names",output);
                editor.putString("ShowPN",output2);
                editor.remove(GroupName);
                String DefaultGroupName = null;
                while (DefaultGroupName == null) DefaultGroupName = shPref.getString("Default",null);
                if (GroupName.equals(DefaultGroupName)){
                    Log.d("GroupMenu2","Group We are Deleting is set as Default");
                    editor.remove("Default");
                    if (output.length() == 0) {
                        Log.d("GroupMenu2","and There is no Substitute Group to be set as Default instead");
                        editor.putString("Default", "");
                    } else {
                        String NewDefault;
                        if (!GroupNames[0].equals(GroupName)) NewDefault=GroupNames[0];
                        else NewDefault=GroupNames[1];
                        Log.d("GroupMenu2","Group "+NewDefault+" is the New Default Group");
                        editor.putString("Default",NewDefault);
                    }
                }
                editor.apply();
                finish();
            }
        });
        sweetAlertDialog.setConfirmButton("لغو", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) { sweetAlertDialog.dismiss(); }});
        sweetAlertDialog.show();
    }


    public void Add(View view) {
        Intent intent = new Intent(GroupMenuActivity.this,AddActivity.class);
        intent.putExtra("GroupName",GroupName);
        intent.putExtra("PhoneNumbers",getPhoneNumbers());
        startActivity(intent);
    }

    public void RenameCancel(View view) {
        Log.d("GroupMenu2","RenameCancel() Called");
        buttonRenameSubmit.setVisibility(View.INVISIBLE);
        buttonRenameCancel.setVisibility(View.INVISIBLE);
        editTextRename.clearFocus();
        editTextRename.setEnabled(false);
        editTextRename.setText(GroupName);
    }

    public void RenameSubmit(View view) {
        //ToDo We dont check for duplicate groups
        Log.d("GroupMenu2","RenameSubmit() Called");
        String[] GroupNames = getGroupNames(shPref);
        for (int i=1;i<=GroupNames.length;i++){
            if (GroupNames[i-1].equals(GroupName)) {
                //Checking If Group Name is Duplicate
                for (int x = 1; x <= GroupNames.length; x++) {
                    //Log.d("GroupMenu",String.valueOf(x)+":"+GroupNames[x-1]+","+String.valueOf(i)+":"+GroupNames[i-1]);
                    if (GroupNames[x - 1].equals(editTextRename.getText().toString())) {
                        Log.d("GroupMenu","New Name is duplicate");
                        editTextRename.setError(Html.fromHtml("<font color=\"#FFFFFF\">\"!این عنوان گروه تکراریست\"</font>"));
                        Snackbar.make(findViewById(R.id.ConstraintLayout3), "این عنوان گروه تکراریست!", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                }
                Log.d("GroupMenu", "Group Name of " + GroupName + " changed to " + editTextRename.getText().toString());
                GroupNames[i - 1] = editTextRename.getText().toString();
                oldGroupName = GroupName;
                GroupName = editTextRename.getText().toString();
                break;
            }
        }
        Data.GROUP_NAME=GroupName;
        SharedPreferences.Editor editor=shPref.edit();
        editor.remove("Names");
        editor.putString("Names",setGroupNames(GroupNames));
        String PN =null;
        while (PN==null) PN =shPref.getString(oldGroupName,null);
        editor.remove(oldGroupName);
        editor.putString(GroupName,PN);

        String DefaultGroupName = null;
        while (DefaultGroupName == null) DefaultGroupName = shPref.getString("Default",null);
        if (oldGroupName.equals(DefaultGroupName)){
            editor.remove("Default");
            editor.putString("Default",GroupName);
        }
        editor.apply();
        buttonRenameSubmit.setVisibility(View.INVISIBLE);
        buttonRenameCancel.setVisibility(View.INVISIBLE);
        editTextRename.setEnabled(false);
        editTextRename.clearFocus();
    }

    public static String[] removeTheElement(String[] arr, int index) {

        // If the array is empty
        // or the index is not in array range
        // return the original array
        if (arr == null
                || index < 0
                || index >= arr.length) {

            return arr;
        }

        // Create another array of size one less
        String[] anotherArray = new String[arr.length - 1];

        // Copy the elements from starting till index
        // from original array to the other array
        System.arraycopy(arr, 0, anotherArray, 0, index);

        // Copy the elements from index + 1 till end
        // from original array to the other array
        System.arraycopy(arr, index + 1,
                anotherArray, index,
                arr.length - index - 1);

        // return the resultant array
        return anotherArray;
    }

    public void Back2(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        if (shouldQuit) finish();
        else {
            shouldQuit=true;
            countDownTimer=new CountDownTimer(3000,1000) {
                @Override public void onTick(long millisUntilFinished) { }
                @Override
                public void onFinish() { shouldQuit=false; }
            }.start();
            Snackbar.make(findViewById(R.id.ConstraintLayout3),"برای خروج دوباره ضربه بزنید",Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        if (countDownTimer!=null) {
            Log.d("GroupMenu2","Timer Canceled");
            countDownTimer.cancel();
        }
        super.onDestroy();
    }
}