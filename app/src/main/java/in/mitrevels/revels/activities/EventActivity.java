package in.mitrevels.revels.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import in.mitrevels.revels.R;
import in.mitrevels.revels.models.FavouritesModel;
import in.mitrevels.revels.models.events.EventModel;
import in.mitrevels.revels.receivers.NotificationReceiver;
import io.realm.Realm;

public class EventActivity extends AppCompatActivity {

    private boolean isFavourited;
    private CoordinatorLayout coordinatorLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private int primaryColor, darkColor;
    private LinearLayout headerLayout;
    private ImageView logo;
    private AppBarLayout appBarLayout;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton favFab;
    private Realm mRealm;
    private final int CREATE_NOTIFICATION = 0;
    private final int CANCEL_NOTIFICATION = 1;
    private String title;
    private String id;
    private String catID;
    private String venue;
    private String date;
    private String day;
    private String startTime;
    private String endTime;
    private String teamOf;
    private String contactName;
    private String contactNumber;
    private String category;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.event_collapsing_toolbar);
        appBarLayout = (AppBarLayout)findViewById(R.id.event_app_bar_layout);

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.event_coordinator_layout);
        headerLayout = (LinearLayout)findViewById(R.id.event_header_layout);

        logo = (ImageView)findViewById(R.id.event_cat_logo);

        fabMenu = (FloatingActionMenu)findViewById(R.id.event_fab_menu);
        fabMenu.setClosedOnTouchOutside(true);

        favFab = (FloatingActionButton)findViewById(R.id.fav_fab);

        mRealm = Realm.getDefaultInstance();

        isFavourited = getIntent().getBooleanExtra("Favourite", false);

        id = getIntent().getStringExtra("Event ID");
        title = getIntent().getStringExtra("Event Name");
        date = getIntent().getStringExtra("Event Date");
        day = getIntent().getStringExtra("Event Day");
        startTime = getIntent().getStringExtra("Event Start Time");
        endTime = getIntent().getStringExtra("Event End Time");
        venue = getIntent().getStringExtra("Event Venue");
        teamOf = getIntent().getStringExtra("Team Of");
        category = getIntent().getStringExtra("Event Category");
        catID = getIntent().getStringExtra("Category ID");
        contactNumber = getIntent().getStringExtra("Contact Number");
        contactName = getIntent().getStringExtra("Contact Name");
        description = getIntent().getStringExtra("Event Description");

        Boolean enableFavourite = getIntent().getBooleanExtra("enableFavourite", true);
        if (!enableFavourite) fabMenu.setVisibility(View.GONE);

        if (title!=null && !title.equals(""))
            collapsingToolbarLayout.setTitle(title);
        else  collapsingToolbarLayout.setTitle("");

        TextView eventDate = (TextView)findViewById(R.id.event_date_text_view);
        if (date!=null) eventDate.setText(date);

        TextView eventTime = (TextView)findViewById(R.id.event_time_text_view);
        if (startTime!=null && endTime!=null) eventTime.setText(startTime+" - "+endTime);

        TextView eventVenue = (TextView)findViewById(R.id.event_venue_text_view);
        if (venue!=null) eventVenue.setText(venue);

        TextView eventTeamOf = (TextView)findViewById(R.id.event_team_of_text_view);
        if (teamOf!=null) eventTeamOf.setText(teamOf);

        TextView eventCategory = (TextView)findViewById(R.id.event_category_text_view);
        if (category!=null) eventCategory.setText(category);

        TextView eventContactNumber = (TextView)findViewById(R.id.event_contact_number_text_view);
        if (contactNumber!=null) eventContactNumber.setText(contactNumber);

        TextView eventContactName = (TextView)findViewById(R.id.event_contact_name_text_view);
        if (contactName!=null) eventContactName.setText(contactName);

        TextView eventDescription = (TextView)findViewById(R.id.event_description_text_view);
        if (description!=null) eventDescription.setText(description);

        Bitmap bitmap = null;

        if (getIntent().getIntExtra("Category Logo", 0) == 0) {
            logo.setImageResource(R.drawable.tt_aero);
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tt_aero);
        }
        else{
            logo.setImageResource(R.drawable.tt_kraftwagen);
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tt_kraftwagen);
        }

        // Code for dynamic colouring of toolbar, status bar and navigation bar

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {

            @Override
            public void onGenerated(Palette palette) {
                primaryColor = palette.getDominantColor(ContextCompat.getColor(EventActivity.this, R.color.teal));

                int a = Color.alpha(primaryColor);
                int r = Math.round(Color.red(primaryColor) * 0.8f);
                int g = Math.round(Color.green(primaryColor) * 0.8f);
                int b = Math.round(Color.blue(primaryColor) * 0.8f);
                darkColor = Color.argb(a, Math.min(r,255), Math.min(g,255), Math.min(b,255));

                collapsingToolbarLayout.setContentScrimColor(primaryColor);
                collapsingToolbarLayout.setStatusBarScrimColor(darkColor);
                headerLayout.setBackgroundColor(primaryColor);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.setStatusBarColor(darkColor);
                    window.setNavigationBarColor(primaryColor);
                }
            }
        });

        if (mRealm.where(FavouritesModel.class).equalTo("id", id).findAll().size() != 0){
            favFab.setImageResource(R.drawable.ic_fav_deselected);
            favFab.setColorNormal(ContextCompat.getColor(this, R.color.grey));
            favFab.setColorPressed(ContextCompat.getColor(this, R.color.grey_medium));
            favFab.setColorRipple(ContextCompat.getColor(this, R.color.grey_dark));
            favFab.setLabelText(getString(R.string.remove_from_favourites));
        }
        else{
            favFab.setImageResource(R.drawable.ic_fav_selected);
            favFab.setColorNormal(ContextCompat.getColor(this, R.color.red));
            favFab.setColorPressed(ContextCompat.getColor(this, R.color.red_medium));
            favFab.setColorRipple(ContextCompat.getColor(this, R.color.red_dark));
            favFab.setLabelText(getString(R.string.add_to_favourites));
        }

        favFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrRemoveFavourite();
            }
        });

    }

    private void addOrRemoveFavourite(){
        if (mRealm == null) return;

        mRealm.beginTransaction();
        if (mRealm.where(FavouritesModel.class).equalTo("id", id).findAll().size() == 0){
            favFab.setImageResource(R.drawable.ic_fav_deselected);
            favFab.setLabelText(getString(R.string.remove_from_favourites));
            favFab.setColorNormal(ContextCompat.getColor(this, R.color.grey));
            favFab.setColorPressed(ContextCompat.getColor(this, R.color.grey_medium));
            favFab.setColorRipple(ContextCompat.getColor(this, R.color.grey_dark));

            FavouritesModel favourite = new FavouritesModel();
            favourite.setId(id);
            favourite.setEventName(title);
            favourite.setCatID(catID);
            favourite.setCatName(category);
            favourite.setDate(date);
            favourite.setDay(day);
            favourite.setVenue(venue);
            favourite.setStartTime(startTime);
            favourite.setEndTime(endTime);
            favourite.setDescription(description);
            favourite.setParticipants(teamOf);
            favourite.setContactName(contactName);
            favourite.setContactNumber(contactNumber);
            mRealm.copyToRealm(favourite);

            Snackbar.make(coordinatorLayout, title+" added to favourites!", Snackbar.LENGTH_SHORT).show();
            editNotification(CREATE_NOTIFICATION);
        }
        else{
            favFab.setImageResource(R.drawable.ic_fav_selected);
            favFab.setColorNormal(ContextCompat.getColor(this, R.color.red));
            favFab.setColorPressed(ContextCompat.getColor(this, R.color.red_medium));
            favFab.setColorRipple(ContextCompat.getColor(this, R.color.red_dark));
            favFab.setLabelText(getString(R.string.add_to_favourites));

            mRealm.where(FavouritesModel.class).equalTo("id", id).findAll().deleteAllFromRealm();

            Snackbar.make(coordinatorLayout, title+" removed from favourites!", Snackbar.LENGTH_SHORT).show();
            editNotification(CANCEL_NOTIFICATION);
        }
        mRealm.commitTransaction();
    }

    public void editNotification(int operation){
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("eventName", title);
        intent.putExtra("startTime", startTime);
        intent.putExtra("eventVenue", venue);
        intent.putExtra("eventID", id);

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, Integer.parseInt(catID+id+"0"), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, Integer.parseInt(catID+id+"1"), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (operation==CREATE_NOTIFICATION){
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
            Date d = null;

            try {
                d = sdf.parse(startTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int eventDate = 7 + Integer.parseInt(day);   //event dates start from 8th March

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(d);

            calendar1.set(Calendar.MONTH, Calendar.MARCH);
            calendar1.set(Calendar.YEAR, 2017);
            calendar1.set(Calendar.DATE, eventDate);
            calendar1.set(Calendar.SECOND, 0);

            long eventTimeInMillis = calendar1.getTimeInMillis();
            calendar1.set(Calendar.HOUR_OF_DAY, calendar1.get(Calendar.HOUR_OF_DAY)-1);

            Calendar calendar2 = Calendar.getInstance();

            if(calendar2.getTimeInMillis() <= eventTimeInMillis)
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent1);

            if (calendar2.get(Calendar.MONTH) == calendar1.get(Calendar.MONTH) && calendar2.get(Calendar.DATE) < calendar1.get(Calendar.DATE)){
                calendar2.set(Calendar.SECOND, 0);
                calendar2.set(Calendar.MINUTE, 0);
                calendar2.set(Calendar.HOUR, 0);
                calendar2.set(Calendar.AM_PM, Calendar.AM);
                calendar2.set(Calendar.MONTH, Calendar.MARCH);
                calendar2.set(Calendar.YEAR, 2017);
                calendar2.set(Calendar.DATE, eventDate);

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), pendingIntent2);
            }

        }
        else if (operation==CANCEL_NOTIFICATION){
            alarmManager.cancel(pendingIntent1);
            alarmManager.cancel(pendingIntent2);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //This enables shared element transition on Up Navigation
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (fabMenu.isOpened()) fabMenu.close(true);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            super.onBackPressed();
        else{
            finish();
            overridePendingTransition(R.anim.scale_up, R.anim.slide_out_right);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
        mRealm = null;
    }
}
