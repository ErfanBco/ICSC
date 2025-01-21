package shady.bco.js;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity {
    ChipGroup chipGroup;
    EditText editTextPhoneNumber,editTextGroupName;
    ProgressBar progressBar;
    List<Chip> chips;
    boolean isAddMode=true;
    boolean shouldQuit=false;
    CountDownTimer countDownTimer=null;
    Intent pickContact;
    SharedPreferences shPref;
    ActivityResultLauncher<Intent> ResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Casting();
        if (getIntent().getStringExtra("GroupName")!=null){
            Log.d("Add","Edit Mode");
            isAddMode=false;
            editTextGroupName.setText(getIntent().getStringExtra("GroupName"));
            editTextGroupName.setFocusable(false);
            String[] PNs=getIntent().getStringArrayExtra("PhoneNumbers");
            if (!PNs[0].equals("")) {
                //if the group is not Empty
                for (int i = 1; i <= PNs.length; i++) {
                    newChip(PNs[i - 1]);
                }
            }
        }else Log.d("Add","Add Mode");


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
            Snackbar.make(findViewById(R.id.ConstraintLayout),"برای خروج دوباره ضربه بزنید",Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        if (countDownTimer!=null) {
            Log.d("Add","Timer Canceled");
            countDownTimer.cancel();
        }
        super.onDestroy();
    }

    private void Casting() {
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.toolbar2);
        View view = getSupportActionBar().getCustomView();

        chipGroup = findViewById(R.id.chipGroup);
        shPref = getSharedPreferences("Groups", Context.MODE_PRIVATE);
        chips=new ArrayList<Chip>();
        progressBar=findViewById(R.id.progressBarAdd);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextGroupName = findViewById(R.id.editTextTextGroupName);


        final ViewTreeObserver observer= editTextPhoneNumber.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        findViewById(R.id.buttonContacts).getLayoutParams().height=editTextPhoneNumber.getHeight();
                        findViewById(R.id.buttonContacts).getLayoutParams().width=editTextPhoneNumber.getHeight();
                        findViewById(R.id.buttonContacts).requestLayout();
                    }
                });

        pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        ResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            ContentResolver cr = getContentResolver();
                            Cursor cursor = cr.query(data.getData(), null, null, null, null);
                            cursor.moveToFirst();
                            Log.d("Contact", "Cursor Count:" + String.valueOf(cursor.getColumnCount()));
                            if (cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)).equals("0")) {
                                Snackbar.make(findViewById(R.id.ConstraintLayout), "این مخاطب شماره تلفنی ندارد!", Snackbar.LENGTH_SHORT).show();
                                return;
                            }


                            String ContactID = cursor.getString(cursor.getColumnIndex("_id"));
                            Cursor cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + ContactID, null, null);
                            //cursor2.moveToFirst();


                            for (int x = 1; x <= cursor2.getCount(); x++) {
                                cursor2.moveToNext();
                                for (int i = 1; i <= cursor2.getColumnCount(); i++) {
                                    Log.d("Contact", String.valueOf(i) + "-" + cursor2.getColumnName(i - 1) + "," + cursor2.getString(i - 1));
                                }
                                if (cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) >= 0) {
                                    String PhoneNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    PhoneNumber = PhoneNumber.replaceAll("[^0-9]", "");
                                    if (isThePhoneNumberOK(PhoneNumber)) newChip(PhoneNumber);
                                    //editTextPhoneNumber.setText(PhoneNumber);
                                    //editTextPhoneNumber.setEnabled(false);
                                    Log.d("Contact", "PhoneNumber:" + PhoneNumber);
                                } else Log.d("Contact", "PhoneNumber,Failed");
                            /*for (int x=2;x<=15;x++) {
                                String PhoneNumber = cursor2.getString(cursor2.getColumnIndex("data" + String.valueOf(x)));
                                if (PhoneNumber !=null) {
                                    PhoneNumber = PhoneNumber.replaceAll("[^0-9]","");
                                    if (isThePhoneNumberOK(PhoneNumber)) newChip(PhoneNumber);
                                }
                            }*/
                            }
                        }
                    }
                });
    }

    public void ChooseContact(View view) {
        ResultLauncher.launch(pickContact);
    }


    public void Remove(View view) {
        editTextPhoneNumber.getText().clear();
    }

    public void Add(View view) {
        if (!isThePhoneNumberOK(editTextPhoneNumber.getText().toString())) return;
        newChip(editTextPhoneNumber.getText().toString());
        if (!editTextPhoneNumber.isEnabled()) editTextPhoneNumber.setEnabled(true);
        editTextPhoneNumber.getText().clear();
    }

    private boolean isThePhoneNumberOK(String PhoneNumber) {
        if (PhoneNumber.equals("")){
            editTextPhoneNumber.setError("لطفا شماره تلفنی وارد کنید!");
            Snackbar.make(findViewById(R.id.ConstraintLayout),Html.fromHtml("<font color=\"#FFFFFF\">\"!لطفا شماره تلفنی وارد کنید\"</font>"),Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (PhoneNumber.length() < 7){
            editTextPhoneNumber.setError(Html.fromHtml("<font color=\"#FFFFFF\">\"شماره تلفن حداقل باید هفت رقم داشته باشد!\"</font>"));
            Snackbar.make(findViewById(R.id.ConstraintLayout),"شماره تلفن حداقل باید هفت رقم داشته باشد!",Snackbar.LENGTH_SHORT).show();
            return false;
        }
        boolean isDuplicate=false;
        for (int i=1;i<=chips.size();i++){
            String Phone2 = chips.get(i - 1).getText().toString();
            if (Comparison(PhoneNumber,Phone2)) {
                isDuplicate=true;
                break;
            }
        }
        if (isDuplicate){
            editTextPhoneNumber.setError(Html.fromHtml("<font color=\"#FFFFFF\">\"!این شماره تلفن تکراری است\"</font>"));
            Snackbar.make(findViewById(R.id.ConstraintLayout),"این شماره تلفن تکراری است!",Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void newChip(String text) {
        Chip chip = new Chip(AddActivity.this);
        chipGroup.addView(chip);
        chip.getLayoutParams().width= ActionBar.LayoutParams.WRAP_CONTENT;
        chip.getLayoutParams().height= ActionBar.LayoutParams.WRAP_CONTENT;
        chip.setText(text);
        chips.add(chip);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip);
                chips.remove(chip);
            }
        });
    }

    public static boolean Comparison(String Phone1,String Phone2){
        if (Phone1.length()>10)
            Phone1= Phone1.substring(Phone1.length()-10,Phone1.length());
        if (Phone2.length()>10)
            Phone2= Phone2.substring(Phone2.length()-10,Phone2.length());
        Log.d("Comparison",Phone1+"("+ Phone1 +")"+ " VS "+Phone2+"("+ Phone2 +")");
        if (Phone1.equals(Phone2)) return true;
        return false;
    }

    public void Submit(View view) {
        Loading(view,true);
        SharedPreferences.Editor editor =shPref.edit();

        if (chips.size()<=0) {
            editTextPhoneNumber.setError(Html.fromHtml("<font color=\"#FFFFFF\">\"!لطفا شماره تلفنی وارد کنید\"</font>"));

            Snackbar.make(findViewById(R.id.ConstraintLayout),"لطفا شماره تلفنی وارد کنید!",Snackbar.LENGTH_SHORT).show();
            Loading(view,false);
            return;
        }

        if (isAddMode){
            if (editTextGroupName.getText().toString().equals("")){
                editTextGroupName.setError(Html.fromHtml("<font color=\"#FFFFFF\">\"لطفا عنوان گروه را وارد کنید!\"</font>"));
                Snackbar.make(findViewById(R.id.ConstraintLayout),"لطفا عنوان گروه را وارد کنید!",Snackbar.LENGTH_SHORT).show();
                Loading(view,false);
                return;
            }

            String input=null;
            while (input==null) input=shPref.getString("Names",null);
            if (input.length()==0) input=editTextGroupName.getText().toString();
            else {
                //Checking If Group Name is Duplicate
                String[] GroupNames=input.split(",");
                for (int i=1;i<=GroupNames.length;i++){
                    if (GroupNames[i-1].equals(editTextGroupName.getText().toString())){
                        editTextGroupName.setError(Html.fromHtml("<font color=\"#FFFFFF\">\"!این عنوان گروه تکراریست\"</font>"));
                        Snackbar.make(findViewById(R.id.ConstraintLayout),"این عنوان گروه تکراریست!",Snackbar.LENGTH_SHORT).show();
                        Loading(view,false);
                        return;
                    }
                }
                input += ","+editTextGroupName.getText().toString();
            }
            Log.d("Add2","New Names ShPref:"+input);
            editor.putString("Names",input);
        }

        String PhoneNumbers="";
        for (int i=1;i<=chips.size();i++){
            if (i==chips.size()) PhoneNumbers += chips.get(i-1).getText().toString();
            else PhoneNumbers += chips.get(i-1).getText().toString()+",";
        }

        String input2=null;
        while (input2==null) input2=shPref.getString("Default",null);
        if (input2.equals("")) editor.putString("Default",editTextGroupName.getText().toString());
        editor.putString(editTextGroupName.getText().toString(),PhoneNumbers);
        if (isAddMode){
            SMSBroadcastReceiver.createNotifChannel(this,editTextGroupName.getText().toString());
            String input3=null;
            while (input3==null) input3=shPref.getString("ShowPN",null);
            if (input3.equals("")) editor.putString("ShowPN","true");
            else editor.putString("ShowPN",input3+",true");
        }
        editor.apply();
        finish();
    }

    private void Loading(View view,boolean Load) {
        if (Load) {
            view.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            view.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void Back(View view) { finish(); }


}