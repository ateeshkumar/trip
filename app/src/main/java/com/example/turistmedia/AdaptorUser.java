package com.example.turistmedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdaptorUser extends RecyclerView.Adapter<AdaptorUser.MyHolder>{
    Context context;

    public AdaptorUser(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    List<ModelUser>userList;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users,viewGroup);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String userImage = userList.get(position).image;
        String userName = userList.get(position).name;
        String userEmail = userList.get(position).email;

        holder.mAvartarTv.setText(userImage);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView mName,mEmail;
        ImageView mAvartarTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.nameTvt);
            mEmail = itemView.findViewById(R.id.emailTvt);
            mAvartarTv = itemView.findViewById(R.id.avtarIvt);
        }
    }

}
