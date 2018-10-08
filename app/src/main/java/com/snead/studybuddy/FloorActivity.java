package com.snead.studybuddy;

import android.content.ClipData;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
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
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FloorActivity extends AppCompatActivity {



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
    public static final String STUDENTEMAIL = "com.snead.studybuddy.studentemail";
    private static final String EXTRA_STUDENT_EMAIL = "com.snead.studybuddy.studentemail";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor);

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
        getMenuInflater().inflate(R.menu.menu_floor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reserve) {
            Intent intent = new Intent(FloorActivity.this, ReservationActivity.class);
            intent.putExtra(STUDENTEMAIL, getIntent().getStringExtra(EXTRA_STUDENT_EMAIL));
            startActivity(intent);
        }
        if(id == R.id.action_signOut){
            startActivity(new Intent(FloorActivity.this, MainActivity.class));
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
        private static final String EXTRA_PLF_CHECKED = "com.snead.studybuddy.plfchecked";
        private static final String EXTRA_STUDENT_NAME = "com.snead.studybuddy.studentname";
        DatabaseReference databaseStudentLocations;
        ArrayList<StudentLocation> studentLocations = new ArrayList();
        StudentLocation studentLocation;
        Boolean addSuccessful = false;
        Boolean deleteSuccessful = false;
        Boolean updateSuccessful = false;
        private Spinner departmentSpinner;
        private ArrayAdapter<String> departmentAdapter;
        private String[] departments = {"Physics", "Psychology", "Sociology", "Accounting",
                "Political Science", "Computer Science", "Math & Statistics", "Writing Center",
                "Economics", "Japanese", "Finance", "Classics", "Career Services", "Biology", "Chinese",
                "French", "Italian", "Spanish", "Logic"};
        ArrayList<String> departmentList = new ArrayList();
        float updatedX; //find better solution to this problem
        float updatedY;
        //private static final String EXTRA_OCATION_ID = "com.db.lsnead18.artistdatabaseexample.locationid";
        float globalX =0;
        float globalY =0;

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

        private final class dragTouchListener implements View.OnTouchListener {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    ClipData clipData = ClipData.newPlainText("", "");
                    v.startDrag(clipData, shadowBuilder, v, 0);
                    return true;
                } else {
                    return false;
                }
            }

        }


        private class dropListener implements View.OnDragListener {

            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                int dragAction = dragEvent.getAction();
                View dragView = (View) dragEvent.getLocalState();
                if (dragAction == DragEvent.ACTION_DRAG_EXITED) {
                    //no action required
                } else if (dragAction == DragEvent.ACTION_DRAG_ENTERED) {
                    //no action required
                } else if (dragAction == DragEvent.ACTION_DRAG_ENDED) {
                    if (dropEventNotHandled(dragEvent)) {
                        dragView.setVisibility(View.VISIBLE);
                    }
                } else if (dragAction == DragEvent.ACTION_DROP) {
                     globalX = dragEvent.getX();
                     globalY = dragEvent.getY();
                    dragView.setX(globalX);
                    dragView.setY(globalY);
                    dragView.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                }
                return true;
            }

            private boolean dropEventNotHandled(DragEvent dragEvent) {
                return !dragEvent.getResult();
            }

        }

        public void setupDragView(View v, float x, float y){
            ImageView imageView = (ImageView) v.findViewById(R.id.floorPlan_imageView);

            RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);

            //show up on correct floors
            ArrayList<StudentLocation> floor1 = new ArrayList();
            ArrayList<StudentLocation> floor2 = new ArrayList();
            ArrayList<StudentLocation> floor3 = new ArrayList();


            for(int i=0;i<studentLocations.size();i++) {

                if(studentLocations.get(i).getFloor() == 1){
                    floor1.add(studentLocations.get(i));
                }
                else if(studentLocations.get(i).getFloor() == 3){
                    floor3.add(studentLocations.get(i));
                }
                else {
                    floor2.add(studentLocations.get(i));
                }
            }


            if(getArguments().getInt(ARG_SECTION_NUMBER)==1){
                imageView.setImageResource(R.mipmap.library1);
                for (int i = 0; i < floor1.size(); i++) {
                    ImageView image = new ImageView(getActivity());
                    image.setBackgroundResource(R.drawable.pin3);
                    relativeLayout.addView(image);
                    image.setX(floor1.get(i).getX());
                    image.setY(floor1.get(i).getY());
                    final StudentLocation student = floor1.get(i);
                    image.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            Toast.makeText(getActivity(), student.getName() + ": " + student.getMessage(), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    });
                }
            }
            else if (getArguments().getInt(ARG_SECTION_NUMBER)==3){
                imageView.setImageResource(R.mipmap.library3);
                for (int i = 0; i < floor3.size(); i++) {

                    ImageView image = new ImageView(getActivity());
                    image.setBackgroundResource(R.drawable.pin3);
                    relativeLayout.addView(image);
                    image.setX(floor3.get(i).getX());
                    image.setY(floor3.get(i).getY());
                    final StudentLocation student = floor3.get(i);
                    image.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            Toast.makeText(getActivity(), student.getName() + ": " + student.getMessage(), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    });

                }
            }
            else{
                imageView.setImageResource(R.mipmap.library2);
                for (int i = 0; i < floor2.size(); i++) {
                    ImageView image = new ImageView(getActivity());
                    image.setBackgroundResource(R.drawable.pin3);
                    relativeLayout.addView(image);
                    image.setX(floor2.get(i).getX());
                    image.setY(floor2.get(i).getY());
                    final StudentLocation student = floor2.get(i);
                    image.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            Toast.makeText(getActivity(), student.getName() + ": " + student.getMessage(), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    });
                }
           }

            final ImageView pin = (ImageView) v.findViewById(R.id.pin_imageView);

            pin.setOnTouchListener(new dragTouchListener());
            imageView.setOnDragListener(new dropListener());

            pin.setX(x);
            pin.setY(y);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_floor, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.floor_section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            Button mark_button = (Button) rootView.findViewById(R.id.submitMessage_button);
            final EditText nameEditText = (EditText) rootView.findViewById(R.id.name_editText);
            final EditText messageEditText = (EditText) rootView.findViewById(R.id.message_editText);
            final TextView timeTextView = (TextView) rootView.findViewById(R.id.progess_textView);
            final ImageView pin = (ImageView) rootView.findViewById(R.id.pin_imageView);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.floorPlan_imageView);
            final SeekBar seekBarTime = (SeekBar) rootView.findViewById(R.id.occupation_seekBar);

            databaseStudentLocations = FirebaseDatabase.getInstance().getReference("StudentLocations");

            final Boolean plfChecked = getActivity().getIntent().getExtras().getBoolean(EXTRA_PLF_CHECKED);
            if(plfChecked){
                seekBarTime.setProgress(2);
                timeTextView.setText(String.valueOf(2));
            }

            nameEditText.setText(getActivity().getIntent().getStringExtra(EXTRA_STUDENT_NAME));

            setupDragView(rootView, 0, 0);

            //initially set name to name from email (get from intent)
            final String name = nameEditText.getText().toString();
            final String[] message = {messageEditText.getText().toString().trim()};
            final int[] time = {Integer.parseInt(timeTextView.getText().toString())};


            seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    timeTextView.setText(String.valueOf(i));
                    time[0] = Integer.parseInt(timeTextView.getText().toString());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            TextView messageTextView = (TextView) rootView.findViewById(R.id.message_textView);

            Collections.addAll(departmentList, departments);

            departmentSpinner = (Spinner) rootView.findViewById(R.id.department_spinner);
            setupDepartmentSpinnerAdapter();
            departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    //save into intent
                    departmentAdapter.notifyDataSetChanged();
                    departmentSpinner.setSelection(i);
                    message[0] = adapterView.getItemAtPosition(i).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    departmentSpinner.setSelection(0, true);
                }

            });


            if(plfChecked){
                messageEditText.setVisibility(View.INVISIBLE);
                messageTextView.setVisibility(View.INVISIBLE);
                departmentSpinner.setVisibility(View.VISIBLE);
            }
            else{
                messageEditText.setVisibility(View.VISIBLE);
                messageTextView.setVisibility(View.VISIBLE);
                departmentSpinner.setVisibility(View.INVISIBLE);
            }

            mark_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(plfChecked){
                        studentLocation = addStudentLocation(nameEditText.getText().toString().trim(), message[0],
                                globalX, globalY, time[0], getArguments().getInt(ARG_SECTION_NUMBER));
                    }else{
                        studentLocation = addStudentLocation(nameEditText.getText().toString().trim(), messageEditText.getText().toString().trim(),
                                globalX, globalY, time[0], getArguments().getInt(ARG_SECTION_NUMBER));
                    }
                    if(deleteSuccessful){
                        nameEditText.setText("");
                        messageEditText.setText("");
                    }

                }
            });

            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(addSuccessful){
                        Snackbar.make(view, "Update, delete, or suspend your pin!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        showUpdateDeleteDialog(studentLocation.getId(), studentLocation.getName(),
                                studentLocation.getMessage(), getArguments().getInt(ARG_SECTION_NUMBER), studentLocation.getOccupiedTime());
                        //setupDragView(rootView, updatedX, updatedY);
                    }
                    else{
                        Toast.makeText(getActivity(), "Please mark your location first", Toast.LENGTH_LONG).show();
                    }

                }
            });


            return rootView;
        }

        private void setupDepartmentSpinnerAdapter() {
            departmentAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, departmentList);
            departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            departmentSpinner.setAdapter(departmentAdapter);
            departmentAdapter.notifyDataSetChanged();

        }

        private void setupDepartmentSpinnerListener() {
            departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    //save into intent
                    departmentAdapter.notifyDataSetChanged();
                    departmentSpinner.setSelection(i);
                    Toast.makeText(getActivity(), adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    departmentSpinner.setSelection(0, true);
                }

            });
        }

        //database crud

        private StudentLocation addStudentLocation(String name, String message, float x, float y, int occupationTime, int floor) {
            //checking if the value is provided
            if (!TextUtils.isEmpty(name)) {

                //getting a unique id using push().getKey() method
                //it will create a unique id and we will use it as the Primary Key for our student
                String id = databaseStudentLocations.push().getKey();

                //creating an StudentLocation Object
                StudentLocation studentLocation = new StudentLocation(id, name, message, x, y, occupationTime, floor);

                //Saving the StudentLocation
                databaseStudentLocations.child(id).setValue(studentLocation);

                addSuccessful = true;

                //displaying a success toast
                Toast.makeText(getActivity(), "We marked your spot!", Toast.LENGTH_LONG).show();


                return studentLocation;
            } else {
                //if the value is not given displaying a toast
                Toast.makeText(getActivity(), "Please enter a name", Toast.LENGTH_LONG).show();
                addSuccessful = false;
                return null;
            }
        }

        private boolean updateStudentLocation(String studentLocationId, String name, String message,
                                           float x, float y, int time, int floor){
            //getting the specified studentLocation reference
            DatabaseReference dR = FirebaseDatabase.getInstance().getReference("StudentLocations").child(studentLocationId);

            //updating studentLocation
            StudentLocation studentLocation = new StudentLocation(studentLocationId, name, message, x, y, time, floor);
            dR.setValue(studentLocation);
            updatedX = studentLocation.getX();
            updatedY = studentLocation.getY();
            Toast.makeText(getActivity(), "Your pin has been updated!", Toast.LENGTH_LONG).show();
            setupDragView(getView(), x, y);
            return true;
        }
        private boolean deleteStudentLocation(String id) {
            //getting the specified StudentLocation reference
            DatabaseReference dR = FirebaseDatabase.getInstance().getReference("StudentLocations").child(id);

            //removing StudentLocation
            dR.removeValue();

            Toast.makeText(getContext(), "Location Deleted", Toast.LENGTH_LONG).show();

            return true;
        }

        //This dialog is shown when the floating action button is clicked
        private void showUpdateDeleteDialog(final String studentLocationId, final String name,
                                            final String message, final int floor, final int time) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.update_location_dialog, null);
            dialogBuilder.setView(dialogView);

            final EditText nameEditText = (EditText) dialogView.findViewById(R.id.name_editText);
            final EditText messageEditText = (EditText) dialogView.findViewById(R.id.message_editText);
            final SeekBar seekBarTime = (SeekBar) dialogView.findViewById(R.id.occupation_seekBar);
            final TextView timeTextView = (TextView) dialogView.findViewById(R.id.progess_textView);
            final Button buttonUpdate = (Button) dialogView.findViewById(R.id.update_button);
            final Button buttonDelete = (Button) dialogView.findViewById(R.id.delete_button);
            final ImageView pin = (ImageView) dialogView.findViewById(R.id.pin_imageView);

            dialogBuilder.setTitle(name); //learn how to center
            final AlertDialog b = dialogBuilder.create();
            b.show();

            setupDragView(dialogView, globalX, globalY);

            nameEditText.setText(name);
            messageEditText.setText(message);

            seekBarTime.setProgress(time);
            timeTextView.setText(String.valueOf(time));


            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = nameEditText.getText().toString().trim();
                    String message = messageEditText.getText().toString().trim();
                    int time = seekBarTime.getProgress();
                    if (!TextUtils.isEmpty(name)) {
                       updateStudentLocation(studentLocationId, name, message, globalX, globalY, time, floor);
                       updateSuccessful = true;
                       b.dismiss();
                    }
                }
            });


            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   deleteSuccessful = deleteStudentLocation(studentLocationId);
                   b.dismiss();
                }
            });


            seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    timeTextView.setText(String.valueOf(i));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

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

                    //clearing the previous list
                    studentLocations.clear();

                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting roomReservation
                        StudentLocation studentL = postSnapshot.getValue(StudentLocation.class);
                        //adding roomReservation  to the list
                        studentLocations.add(studentL);
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
