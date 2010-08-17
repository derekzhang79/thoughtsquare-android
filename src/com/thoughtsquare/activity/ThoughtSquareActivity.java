package com.thoughtsquare.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.thoughtsquare.R;
import com.thoughtsquare.domain.Location;
import com.thoughtsquare.domain.UserProvider;
import com.thoughtsquare.utility.AHTTPClient;
import com.thoughtsquare.utility.Config;
import com.thoughtsquare.utility.ConfigLoader;

import static android.preference.PreferenceManager.*;
import static com.thoughtsquare.Preferences.DEFAULT;
import static com.thoughtsquare.Preferences.DISPLAY_NAME;

public class ThoughtSquareActivity extends Activity {
    private static final int REGISTER_ACTIVITY = 0;
    private static final int UPDATE_LOCATION_ACTIVITY = 1;
    private UserProvider userProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Config config = new ConfigLoader().getConfig(this);
        userProvider = new UserProvider(getDefaultSharedPreferences(this), new AHTTPClient(), config);

        Button updateLocation = (Button) findViewById(R.id.update_location);
        updateLocation.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getContext(), UpdateLocationActivity.class);
                startActivityForResult(i, UPDATE_LOCATION_ACTIVITY);
            }
        });

        if (!userProvider.doesUserExist()) {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivityForResult(i, REGISTER_ACTIVITY);
        } else {
            greetUser(userProvider.getUser().getDisplayName());
        }
    }

    private Context getContext() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras = intent.getExtras();
        switch (requestCode) {
            case REGISTER_ACTIVITY:
                greetUser(extras.getString("displayName"));
                break;
            case UPDATE_LOCATION_ACTIVITY:
                TextView currentLocation = (TextView)findViewById(R.id.current_location);
                Location location = extras.getParcelable("location");
                currentLocation.setText(location.getTitle());
                userProvider.getUser().updateLocation(location);
                break;
        }
    }

    private void greetUser(String displayName) {

        TextView textView = (TextView) findViewById(R.id.welcome_label);
        textView.setText("Hello " + displayName);
    }


}