package com.rishabh.hashcontact;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewFiled extends RecyclerView.Adapter<RecyclerViewFiled.FieldHolder> {
    ArrayList<Map<String,String>> contacts=new ArrayList<>();

int lastPosition=-1;

    Context context;

    public RecyclerViewFiled(ArrayList<Map<String, String>> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }
    public Map<String,String> retrieveData()
    {
        Map<String,String > map=new HashMap<>();
        for(int i=0;i<contacts.size();i++)
        {
            String hint= contacts.get(i).get("detailname");
            String detail=contacts.get(i).get("detail");
            map.put(hint,detail);

        }
        return map;

    }

    @NonNull
    @Override
    public FieldHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.fields_single,parent,false);
        RecyclerViewFiled.FieldHolder  fieldHolder=new RecyclerViewFiled.FieldHolder(v,new MyCustomEditTextListener());



        return fieldHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FieldHolder holder, final int position) {



        if(contacts.get(position).get("detailname")!="Photo") {
            Log.d("meme", String.valueOf(contacts));
            Log.d("memer", String.valueOf(contacts.get(position).get("detail")));
            holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
            holder.textView.setText(contacts.get(holder.getAdapterPosition()).get("detailname"));
            holder.editText.getBackground().setAlpha(50);

            holder.editText.setHint(contacts.get(holder.getAdapterPosition()).get("detailname"));
            holder.editText.setText(contacts.get(holder.getAdapterPosition()).get("detail"));
            holder.editText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    final View view = LayoutInflater.from(context).inflate(R.layout.deletedialog, null);
                    builder.setView(view);
                    final Dialog dialog=builder.create();

                    dialog.setContentView(R.layout.deletedialog);
                    dialog.getWindow().getAttributes().windowAnimations=R.style.MyAnimation_Window;
                    dialog.getWindow().setBackgroundDrawableResource(R.color.trans);
                    // (0x80000000, PorterDuff.Mode.MULTIPLY);
                    dialog.show();

                    ImageButton addButton =dialog.findViewById(R.id.delete);
                    TextView cancelButton = (TextView) dialog.findViewById(R.id.cancel);

                    addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(contacts.get(holder.getAdapterPosition()).get("detailname").equals("Name")||contacts.get(holder.getAdapterPosition()).get("detailname").equals("Photo")) {
                                Toast.makeText(context,"Mandatory Field!Cannot be deleted.",Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                            else
                            {
                                FirebaseDatabase.getInstance().getReference().child(Profile.getCurrentProfile().getId()).child("Personal").child(contacts.get(holder.getAdapterPosition()).get("detailname")).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialog.dismiss();

                                        Toast.makeText(context, "Field deleted successfully", Toast.LENGTH_LONG).show();



                                    }
                                });
                            }

                        }
                    });

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });



                    return true;

                }
            });
            if (lastPosition == getItemCount() - 1)
                lastPosition = -1;
            Animation animation = AnimationUtils.loadAnimation(context,
                    (position > lastPosition) ? R.anim.layout_animation
                            : R.anim.item_animation_fall_down);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }



    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class FieldHolder extends RecyclerView.ViewHolder{
        TextInputEditText editText;
        TextView textView;
        public MyCustomEditTextListener myCustomEditTextListener;



        public FieldHolder(@NonNull View itemView,MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            editText=itemView.findViewById(R.id.fieldtitle);
            textView=itemView.findViewById(R.id.detailnam);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.editText.addTextChangedListener(myCustomEditTextListener);



        }
    }
    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            Map<String,String > ma=new HashMap<>();
            ma.put("detailname",contacts.get(position).get("detailname"));
            ma.put("detail",charSequence.toString());
            contacts.set(position, ma);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

}
