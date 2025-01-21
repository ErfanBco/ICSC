package shady.bco.js;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final View decorView=getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        final AlphaAnimation alphaAnimation=new AlphaAnimation(0F,1F);
        alphaAnimation.setDuration(3000);
        alphaAnimation.setFillAfter(true);
        findViewById(R.id.textViewSplashScreen).startAnimation(alphaAnimation);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            RequestPermission(1);
        }else {
            checkNewMessages();
        }




    }
    private void checkNewMessages(){
        MoveToMainAct();

    }




    private void MoveToMainAct() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent StartApp = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(StartApp);

            }
        },3000);
    }

    private void RequestPermission(int RequestCode) {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS,Manifest.permission.READ_CONTACTS,Manifest.permission.RECEIVE_SMS}, RequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            MoveToMainAct();
        }else{
            Snackbar snackbar =Snackbar.make(findViewById(R.id.ConstraintLayoutSplash),"این مجوزها برای استفاده از این سامانه ضرروی میباشد",Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("نمایش دوباره", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequestPermission(1);
                }
            }).show();
            TextView snackbarTextView = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            TextView actionTextView = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_action);
            snackbarTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bnazanin_bold.ttf"));
            actionTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/btitr.ttf"));
        }
    }
}