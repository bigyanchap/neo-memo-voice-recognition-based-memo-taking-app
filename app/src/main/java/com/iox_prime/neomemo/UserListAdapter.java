package com.iox_prime.neomemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Author: Bigyan Chapagain
 * 5/21/2017.
 */

public class UserListAdapter extends ArrayAdapter<UserInfo> {
    Context context;
    public UserListAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<UserInfo> list) {
        super(context, resource,list);
        this.context=context;
    }
    //getView returns view
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("InflateParams") View view= LayoutInflater.from(context).inflate(R.layout.item_layout,null);
        TextView title,memo;
        title= (TextView) view.findViewById(R.id.textVent_title);
        memo= (TextView) view.findViewById(R.id.textVent_memo);

        final UserInfo info=getItem(position);
        title.setText(info.title);
        memo.setText(info.memo);
        view.findViewById(R.id.menu_btn).setTag(info.id+""); //get from menu
        Log.d("UserListAdapter", "getView: "+info.id);

        //delete from database
        /*view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListActivity)context).deleteUserInfo(info.id+"");
            }
        });*/

        //update/edit the content from database

        /*view.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,MainActivity.class);
                intent.putExtra("id",info.id);
                context.startActivity(intent);
            }
        });*/
        return view;
    }

}
