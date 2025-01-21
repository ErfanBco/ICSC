package shady.bco.js;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class ChatAdaptor extends RecyclerView.Adapter<ChatAdaptor.MyViewHolder> {
//TODO Show a ProgressBar After Scrolling ant Waiting To Load
    private Context context;
    private List<ChatItems> ChatList;
    public int[] Colors={R.color.Messages_Person1,R.color.Messages_Person2,R.color.Messages_Person3,R.color.Messages_Person4,R.color.Messages_Person5};
    public String[] Months={"فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند"};
    public int ColorsIndex=1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewSender;
        public TextView textViewMessage;
        public TextView textViewTime;
        public TextView textViewNewMessages;
        public Chip chipDate;


        public MyViewHolder(View view) {
            super(view);
            textViewSender = view.findViewById(R.id.textViewSender);
            textViewMessage = view.findViewById(R.id.textViewMessage);
            textViewTime = view.findViewById(R.id.textViewTime);
            textViewNewMessages = view.findViewById(R.id.textViewNewMessages);
            chipDate = view.findViewById(R.id.chip4);
        }
    }


    public ChatAdaptor(Context context,List<ChatItems> ChatList) {
        this.context = context;
        this.ChatList = ChatList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adaptor_chat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ChatItems Message = ChatList.get(position);


        if (Message.getPerson()==null) holder.textViewSender.setText(Message.getAddress());
         else holder.textViewSender.setText(Message.getPerson());

         holder.textViewMessage.setText(Message.getMessage());
         holder.textViewTime.setText(getDate(Message.getDate(),"HH:mm"));
        ChatItems lastMessage=null;
         if (position+1<ChatList.size()) {
             lastMessage = ChatList.get(position + 1);
             String newDate = getDate(Message.getDate(), "yyyyMMdd");
             String oldDate = getDate(lastMessage.getDate(), "yyyyMMdd");
             Log.d("Adaptor","NewDate:"+newDate+"("+Message.getMessage()+")"+", OldDate:"+oldDate+"("+lastMessage.getMessage()+")");
             if (!(newDate.equals(oldDate))) {
                 setDate(holder,position,Message);
             }else holder.chipDate.setVisibility(View.GONE);
         }else{
             setDate(holder, position, Message);
         }
        holder.textViewMessage.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/bnazanin_bold.ttf"));
        holder.chipDate.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/bnazanin_bold.ttf"));
         Log.d("Adaptor","Sender:"+holder.textViewSender.getText().toString());
        Log.d("Adaptor","Message:"+holder.textViewMessage.getText().toString());
        Log.d("Adaptor","Time:"+holder.textViewTime.getText().toString());
        if (!MainActivity.showPNs){
            holder.textViewSender.setVisibility(View.GONE);
        }else holder.textViewSender.setVisibility(View.VISIBLE);




        if (Message.getIsRead() == 0){
            if (lastMessage != null){
                if (lastMessage.getIsRead() == 0) holder.textViewNewMessages.setVisibility(View.GONE);
                else holder.textViewNewMessages.setVisibility(View.VISIBLE);
            }else holder.textViewNewMessages.setVisibility(View.VISIBLE);
        }else holder.textViewNewMessages.setVisibility(View.GONE);


        //TODO each Phone Number, a different color
        /*if (ColorsIndex >= Colors.length) ColorsIndex=1;
        Log.d("Adaptor","Setting Color No."+String.valueOf(ColorsIndex));
        holder.textViewSender.setTextColor(context.getResources().getColor(Colors[ColorsIndex-1]));
        ColorsIndex=ColorsIndex+1;*/


    }


    private void setDate(MyViewHolder holder, int position, ChatItems Message) {
        Log.d("tst","im called on "+String.valueOf(position)+"value "+getDate(Message.getDate(), "MMM d"));
        holder.chipDate.setVisibility(View.VISIBLE);
        String[] date=getDate(Message.getDate(),"yyyy/M/d").split("/");
        int[] date2 = gregorian_to_jalali(Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]));
        String tst3 = String.valueOf(date2[2]) + " " + Months[date2[1]-1]+ " " + String.valueOf(date2[0]);
        Log.d("Adaptor", "Date:" + tst3);
        holder.chipDate.setText(tst3);
        Log.d("Adaptor","Chip Set");
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    public static int[] gregorian_to_jalali(int gy, int gm, int gd) {
        int[] out = {
                (gm > 2) ? (gy + 1) : gy,
                0,
                0
        };
        {
            int[] g_d_m = { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 };
            out[2] = 355666 + (365 * gy) + ((int) ((out[0] + 3) / 4)) - ((int) ((out[0] + 99) / 100)) + ((int) ((out[0] + 399) / 400)) + gd + g_d_m[gm - 1];
        }
        out[0] = -1595 + (33 * ((int) (out[2] / 12053)));
        out[2] %= 12053;
        out[0] += 4 * ((int) (out[2] / 1461));
        out[2] %= 1461;
        if (out[2] > 365) {
            out[0] += (int) ((out[2] - 1) / 365);
            out[2] = (out[2] - 1) % 365;
        }
        if (out[2] < 186) {
            out[1] = 1 + (int)(out[2] / 31);
            out[2] = 1 + (out[2] % 31);
        } else {
            out[1] = 7 + (int)((out[2] - 186) / 30);
            out[2] = 1 + ((out[2] - 186) % 30);
        }
        return out;
    }



    @Override
    public int getItemCount() {
        return ChatList.size();
    }
}
