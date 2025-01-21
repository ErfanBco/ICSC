package shady.bco.js;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class ParticipantsAdaptor extends RecyclerView.Adapter<ParticipantsAdaptor.MyViewHolder> {
//TODO Show a ProgressBar After Scrolling ant Waiting To Load
    private final Context context;
    private final String GroupName;
    private String[] ParticipantsList;
    private TextView textViewInfo;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewParticipant;
        public ImageButton imageButtonRemove;



        public MyViewHolder(View view) {
            super(view);
            textViewParticipant = view.findViewById(R.id.textViewP);
            imageButtonRemove=view.findViewById(R.id.imageButtonP);
        }
    }


    public ParticipantsAdaptor(Context context,String GroupName, String[] ParticipantsList,TextView textViewInfo) {
        this.context = context;
        this.GroupName = GroupName;
        this.ParticipantsList = ParticipantsList;
        this.textViewInfo = textViewInfo;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adaptor_participants, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
         final String Participant = ParticipantsList[position];

        if (android.os.Build.VERSION.SDK_INT < 21) {
            Log.d("Participant", "Under API21");
            holder.textViewParticipant.setTextColor(context.getResources().getColor(R.color.black));
        }
         holder.textViewParticipant.setText(Participant);
         holder.imageButtonRemove.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 SharedPreferences shPref = context.getSharedPreferences("Groups", Context.MODE_PRIVATE);
                 String input=null;
                 while (input==null) input = shPref.getString(GroupName,null);
                 String[] PNs=input.split(",");
                 SharedPreferences.Editor editor = shPref.edit();
                 for (int i=1;i<=PNs.length;i++){
                     if (PNs[i-1].equals(Participant)) {
                         Log.d("Participant","PhoneNumber : "+ParticipantsList[i-1]+" has been Deleted!");
                         ParticipantsList= GroupMenuActivity.removeTheElement(ParticipantsList,i-1);
                         break;
                     }
                 }
                 GroupMenuActivity.setInfo(ParticipantsList,textViewInfo);
                 editor.putString(GroupName,GroupMenuActivity.setGroupNames(ParticipantsList));
                 editor.apply();
                 ParticipantsAdaptor.this.notifyDataSetChanged();
             }
         });

    }


    @Override
    public int getItemCount() {
        return ParticipantsList.length;
    }
}
