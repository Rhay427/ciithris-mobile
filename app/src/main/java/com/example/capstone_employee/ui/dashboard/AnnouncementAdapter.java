package com.example.capstone_employee.ui.dashboard;

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

public class AnnouncementAdapter extends ArrayAdapter<Announcement> {
    Context context;
    List<Announcement> announcementList;
    public AnnouncementAdapter(@NonNull Context context, List<Announcement> announcementList) {
        super(context, R.layout.announcement_layout, announcementList);
        this.context = context;
        this.announcementList = announcementList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcement_layout, null, true);
        TextView tv_date = view.findViewById(R.id.dateCreated);
        TextView tv_title = view.findViewById(R.id.txtTitle);
        TextView tv_author = view.findViewById(R.id.txtAuthor);
        TextView tv_desc = view.findViewById(R.id.txtDescription);
        tv_date.setText(announcementList.get(position).getDatestamp());
        tv_title.setText(announcementList.get(position).getTitle());
        tv_author.setText(announcementList.get(position).getAuthor());
        tv_desc.setText(announcementList.get(position).getDescription());
        return view;
    }
}
