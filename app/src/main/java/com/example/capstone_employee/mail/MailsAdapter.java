package com.example.capstone_employee.mail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.capstone_employee.R;

import java.util.List;

public class MailsAdapter extends ArrayAdapter<Mails> {
    Context context;
    List<Mails> mailsList;
    public MailsAdapter(@NonNull Context context, List<Mails> mailsList) {
        super(context, R.layout.mailstatus_layout,mailsList);
        this.context = context;
        this.mailsList = mailsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mailstatus_layout,null,true);
        TextView tv_title = view.findViewById(R.id.Title);
        TextView tv_status = view.findViewById(R.id.txtStatus);
        TextView tv_date = view.findViewById(R.id.date);
        tv_title.setText(mailsList.get(position).getTitle());
        tv_status.setText(mailsList.get(position).getStatus());
        tv_date.setText(mailsList.get(position).getDatestamp());
        return view;
    }
}
