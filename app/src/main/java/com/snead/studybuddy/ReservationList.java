package com.snead.studybuddy;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class ReservationList extends ArrayAdapter<RoomReservation> {

    private Activity context;
    List<RoomReservation> reservations;

    public ReservationList(Activity context, List<RoomReservation> reservations) {
        super(context, R.layout.reservations_dialog, reservations);
        this.context = context;
        this.reservations = reservations;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.reservation_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.name_textView);
        TextView textViewTime = (TextView) listViewItem.findViewById(R.id.time_textView);


        RoomReservation reservation = reservations.get(position);
        textViewName.setText(reservation.getEmail());
        textViewTime.setText(String.valueOf(reservation.getTime()));

        return listViewItem;
    }

}
