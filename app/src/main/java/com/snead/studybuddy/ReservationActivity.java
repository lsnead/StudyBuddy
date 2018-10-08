package com.snead.studybuddy;

import android.content.Intent;
import android.media.Image;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;


public class ReservationActivity extends AppCompatActivity {

    public static final String PLFCHECKED = "com.snead.studybuddy.plfchecked";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    //Calendar info

    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reservation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_markLocation) {
            Intent intent = new Intent(ReservationActivity.this, FloorActivity.class);
            intent.putExtra(PLFCHECKED, false);
            startActivity(intent);
        }
        if(id == R.id.action_signOut){
            startActivity(new Intent(ReservationActivity.this, MainActivity.class));
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String EXTRA_STUDENT_EMAIL = "com.snead.studybuddy.studentemail";
        DatabaseReference databaseStudentLocations;
        ArrayList<RoomReservation> reservations = new ArrayList();
        RoomReservation roomReservation;
        Boolean addSuccessful = false;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public String getDate(Calendar date){
            Pattern pattern = Pattern.compile("(\\[time=(\\d*),)");
            Matcher matcher = pattern.matcher(date.toString());
            String dateInMillis = "";
            if(matcher.find()){
                dateInMillis = matcher.group(2);
            }
            Long longDate = Long.parseLong(dateInMillis);

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date dateObject = new Date(longDate);


            return dateFormat.format(dateObject);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_reservation, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.reservation_section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            databaseStudentLocations = FirebaseDatabase.getInstance().getReference("RoomReservation");


            String studentEmail = getActivity().getIntent().getStringExtra(EXTRA_STUDENT_EMAIL);

            ImageView gsrImageView = (ImageView) rootView.findViewById(R.id.gsr_imageView);
            if(getArguments().getInt(ARG_SECTION_NUMBER)==1){
                gsrImageView.setImageResource(R.drawable.gsr1);
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==2){
                gsrImageView.setImageResource(R.drawable.gsr2);
            }
            else{
                gsrImageView.setImageResource(R.drawable.gsr3);
            }

            
            final TextView emailTextView = (TextView) rootView.findViewById(R.id.email_textView);
            emailTextView.setText(studentEmail);

            final TextView dayTextView = (TextView) rootView.findViewById(R.id.day_textView);

            final int[] reservationHour = {12};



            final TextView roomTextView = (TextView) rootView.findViewById(R.id.room_textView);
            //find out how to do dynamically
            if(getArguments().getInt(ARG_SECTION_NUMBER)==1){
                roomTextView.setText(R.string.reservation_tab_text_1);
            }
            else if (getArguments().getInt(ARG_SECTION_NUMBER)==2){
                roomTextView.setText(R.string.reservation_tab_text_2);
            }
            else{
                roomTextView.setText(R.string.reservation_tab_text_3);
            }

            final NumberPicker timePicker = (NumberPicker) rootView.findViewById(R.id.pm_picker);
            // Set value
            timePicker.setMaxValue(23);
            timePicker.setMinValue(1);
            timePicker.setValue(12);

            final Integer[] libraryHours = {0,1,2,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};

            timePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    reservationHour[0] = newVal;
                }
            });


            // Set wrap selector wheel
            timePicker.setWrapSelectorWheel(true);


            /* starts at current month from now */
            Calendar startDate = Calendar.getInstance();
            startDate.add(Calendar.MONTH, 0);

            /* ends after 1 month from now */
            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.MONTH, 2);



            HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(rootView, R.id.calendarView)
                    .range(startDate, endDate)
                    .datesNumberOnScreen(5)
                    .defaultSelectedDate(Calendar.getInstance())
                    .build();



            dayTextView.setText(getDate(horizontalCalendar.getDateAt(2)));
            final Calendar[] selectedDate = {horizontalCalendar.getDateAt(2)};


            horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
                @Override
                public void onDateSelected(Calendar date, int position) {
                    dayTextView.setText(getDate(date));
                    selectedDate[0] = date;
                }

                @Override
                public void onCalendarScroll(HorizontalCalendarView calendarView, int dx, int dy) {
                    //do something
                }

                @Override
                public boolean onDateLongClicked(Calendar date, int position) {
                    showReservationsDialog(date, roomTextView.getText().toString());
                    return true;
                }
            });


            final ArrayList<Integer> timesTaken = new ArrayList();
            final ArrayList<RoomReservation> reservationsByDate = getReservationsByDate(selectedDate[0], roomTextView.getText().toString());
            for(int i=0; i<reservationsByDate.size();i++){
                timesTaken.add(reservationsByDate.get(i).getTime());
            }



            Button reserve_button = (Button) rootView.findViewById(R.id.reserve_button);
            reserve_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                 if (Arrays.asList(libraryHours).contains(reservationHour[0]) == true) {
                            roomReservation = addRoomReservation(roomTextView.getText().toString(), emailTextView.getText().toString().trim(),
                                    dayTextView.getText().toString(), reservationHour[0]);

                    }
                 else{
                     Toast.makeText(getActivity(), "Sorry the library is closed at that time", Toast.LENGTH_LONG).show();
                 }
                }
            });

            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(addSuccessful){
                        Snackbar.make(view, "Update or delete your room reservation", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        showUpdateDialog(roomReservation.getId(), roomReservation.getRoom(),
                                roomReservation.getEmail(), roomReservation.getDate());
                    }
                    else{
                        Toast.makeText(getActivity(), "Please reserve a room first", Toast.LENGTH_LONG).show();
                    }

                }
            });


            return rootView;
        }

        //database crud
        private RoomReservation addRoomReservation(String room, String email, String date, int time) {
            //checking if the value is provided
            if (!TextUtils.isEmpty(email)) {

                final ArrayList<RoomReservation> reservationsByDate = getReservationsByDate(date, room);
                final ArrayList<Integer> timesTaken = new ArrayList<>();
                for(int i=0; i<reservationsByDate.size();i++){
                    timesTaken.add(reservationsByDate.get(i).getTime());
                }
                if(timesTaken.contains(time) == false){
                    //getting a unique id using push().getKey() method
                    //it will create a unique id and we will use it as the Primary Key for our student
                    String id = databaseStudentLocations.push().getKey();

                    //creating an RoomReservation Object
                    RoomReservation roomReservation = new RoomReservation(id, room, email, date, time);

                    //Saving the Reservation info
                    databaseStudentLocations.child(id).setValue(roomReservation);

                    //displaying a success toast
                    Toast.makeText(getActivity(), "Your room has been reserved", Toast.LENGTH_LONG).show();
                    addSuccessful = true;

                    return roomReservation;
                }
                else{
                    Toast.makeText(getActivity(), "Sorry this room is reserved at that time", Toast.LENGTH_LONG).show();
                    return null;
                }
            } else {
                //if the value is not given displaying a toast
                Toast.makeText(getActivity(), "Please enter an email", Toast.LENGTH_LONG).show();
                return null;
            }
        }
        private boolean updateRoomReservation(String reservationId, String room, String email, int time, String date){
            //getting the specified studentLocation reference
            DatabaseReference dR = FirebaseDatabase.getInstance().getReference("RoomReservation").child(reservationId);

            //updating studentLocation
            RoomReservation roomReservation = new RoomReservation(reservationId, room, email, date, time);
            dR.setValue(roomReservation);

            Toast.makeText(getActivity(), "Your reservation has been updated!", Toast.LENGTH_LONG).show();
            return true;
        }

        private boolean deleteRoomReservation(String id) {
            //getting the specified StudentLocation reference
            DatabaseReference dR = FirebaseDatabase.getInstance().getReference("RoomReservation").child(id);

            //removing StudentLocation
            dR.removeValue();

            Toast.makeText(getContext(), "Reservation Deleted", Toast.LENGTH_LONG).show();

            return true;
        }

        private ArrayList<RoomReservation> getReservationsByDate(Calendar date, String room){
            //only show reservations from selected date in list view
            ArrayList<RoomReservation> resbyRoom = new ArrayList();

            for(int i=0;i<reservations.size();i++) {

                if(reservations.get(i).getRoom().equals(room)){
                    resbyRoom.add(reservations.get(i));
                }
            }

            ArrayList<RoomReservation> reservationsByDate = new ArrayList();
            String dateString = getDate(date);
            for(int i=0; i<resbyRoom.size(); i++){
                if(resbyRoom.get(i).getDate().equals(dateString)){
                    reservationsByDate.add(resbyRoom.get(i));
                }
            }
            return reservationsByDate;
        }
        private ArrayList<RoomReservation> getReservationsByDate(String dateString, String room){
            //only show reservations from selected date/room in list view
            ArrayList<RoomReservation> resbyRoom = new ArrayList();


            for(int i=0;i<reservations.size();i++) {

                if(reservations.get(i).getRoom().equals(room)){
                    resbyRoom.add(reservations.get(i));
                }

            }
            ArrayList<RoomReservation> reservationsByDate = new ArrayList();
            for(int i=0; i<resbyRoom.size(); i++){
                if(resbyRoom.get(i).getDate().equals(dateString)){
                    reservationsByDate.add(resbyRoom.get(i));
                }
            }
            return reservationsByDate;
        }

        private void showReservationsDialog(Calendar date, String room) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.reservations_dialog, null);
            dialogBuilder.setView(dialogView);

            dialogBuilder.setTitle("Email                       Reservation Time");
            final AlertDialog b = dialogBuilder.create();
            b.show();


            ListView reservationsListView = (ListView) dialogView.findViewById(R.id.reservation_listView);

            //creating adapter
            ReservationList reservationAdapter = new ReservationList(getActivity(), getReservationsByDate(date, room));
            //attaching adapter to the listview
            reservationsListView.setAdapter(reservationAdapter);

        }

        private void showUpdateDialog(final String reservationId, final String room, final String email,
                                      final String date) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.update_reservation_dialog, null);
            dialogBuilder.setView(dialogView);


            Button updateButton = (Button) dialogView.findViewById(R.id.update_button);
            Button deleteButton = (Button) dialogView.findViewById(R.id.delete_button);
            final NumberPicker timePicker = (NumberPicker) dialogView.findViewById(R.id.timepicker);

            final ArrayList<RoomReservation> reservationsByDate = getReservationsByDate(date, room);
            final ArrayList<Integer> timesTaken = new ArrayList<>();
            for(int i=0; i<reservationsByDate.size();i++){
                timesTaken.add(reservationsByDate.get(i).getTime());
            }

            // Set value
            timePicker.setMaxValue(23);
            timePicker.setMinValue(1);
            timePicker.setValue(12);

            final Integer[] libraryHours = {0,1,2,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
            final int[] time = {0};

            timePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    time[0] = newVal;
                }
            });


            final AlertDialog b = dialogBuilder.create();
            b.show();

            //change some numbers to the word "occupied" -- check if occupied when reserving??
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Arrays.asList(libraryHours).contains(time[0]) == true) {
                        if(timesTaken.contains(time[0]) == false){
                            updateRoomReservation(reservationId, room, email, time[0], date);
                            b.dismiss();
                        }
                        else{
                            Toast.makeText(getActivity(), "Sorry this room is reserved at that time", Toast.LENGTH_LONG).show();
                        }

                    }
                    else{
                        Toast.makeText(getActivity(), "Sorry the library is closed at this time", Toast.LENGTH_LONG).show();
                    }
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteRoomReservation(reservationId);
                    b.dismiss();
                }
            });

        }

        @Override
        public void onStart() {
            super.onStart();
            //attaching value event listener
            databaseStudentLocations.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //clearing the previous reservation list
                    reservations.clear();

                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting roomReservation
                        RoomReservation roomRes = postSnapshot.getValue(RoomReservation.class);
                        //adding roomReservation  to the list
                        reservations.add(roomRes);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
