package com.thoughtsquare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import com.thoughtsquare.domain.Location;
import com.thoughtsquare.domain.User;
import com.thoughtsquare.intent.IntentActions;
import com.thoughtsquare.service.LocationService;
import com.thoughtsquare.utility.IntentBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class LocationAutoUpdaterTest{
    private User user;
    private LocationService locationService;
    private LocationAutoUpdater locationAutoUpdater;
    private Context context;
    private IntentBuilder intentBuilder;

    @Before
    public void setup() {
        user = mock(User.class);
        locationService = mock(LocationService.class);
        context = mock(Context.class);
        intentBuilder = mock(IntentBuilder.class);
        locationAutoUpdater = new LocationAutoUpdater(intentBuilder, context, user, locationService);
    }

    @Test
    public void shouldUpdateUsersLocationAndSendBroadcastWhenLocationIsFound() {
        android.location.Location location = mock(android.location.Location.class);
        Location foundLocation = mock(Location.class);
        when(locationService.findContainingLocation(location)).thenReturn(foundLocation);
        Intent intent = mock(Intent.class);
        when(intentBuilder.withAction(anyString())).thenReturn(intentBuilder);
        when(intentBuilder.withParcelable(anyString(), any(Parcelable.class))).thenReturn(intentBuilder);
        when(intentBuilder.build()).thenReturn(intent);

        locationAutoUpdater.onLocationChanged(location);

        verify(intentBuilder).withAction(IntentActions.LOCATION_UPDATED);
        verify(intentBuilder).withParcelable("location", foundLocation);
        InOrder inorder = inOrder(context, user);
        inorder.verify(context).sendBroadcast(intent); // this should run first to get quicker feedback
        inorder.verify(user).updateLocation(foundLocation);
    }

    @Test
    public void shouldNotUpdateUsersLocationWhenLocationIsNotKnown() {
        android.location.Location location = mock(android.location.Location.class);
        when(locationService.findContainingLocation(location)).thenReturn(null);

        locationAutoUpdater.onLocationChanged(location);

        verifyZeroInteractions(user);
        verifyZeroInteractions(context);
    }
}
