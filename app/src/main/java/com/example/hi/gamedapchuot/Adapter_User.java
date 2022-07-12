package com.example.hi.gamedapchuot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter_User extends RecyclerView.Adapter<Adapter_User.adapterHoler1> {
    private List<User> mListUser;

    public Adapter_User(List<User> mListUser) {
        this.mListUser = mListUser;
    }

    @NonNull
    @Override
    public adapterHoler1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_higth_score, parent, false);
        return new adapterHoler1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterHoler1 holder, int position) {
        User user= mListUser.get(position);
        if(user==null)
            return;
        holder.txtName.setText(user.getName());
        holder.txtScore.setText(user.getScore()+"");
    }

    @Override
    public int getItemCount() {
        if(mListUser!=null)
            return mListUser.size();
        return 0;
    }

    public class adapterHoler1 extends RecyclerView.ViewHolder {
        TextView txtName, txtScore;

        public adapterHoler1(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name);
            txtScore = itemView.findViewById(R.id.txt_score);
        }
    }
}
