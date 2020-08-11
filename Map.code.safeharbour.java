package com.example.safeharbour;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.android.gms.maps.model.LatLng
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.Application;
package com.codingwithmitch.googlemaps2018.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

//CONSTANTS

public class Constants {

    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSION_REQUEST_ENABLE_GPS - 9002;
    public static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION - 9003;
    public static final String MAPVIEW_BUNDLE_KEY - "MapViewbundlekey";

}

//MAIN ACTIVITY

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener
{

    private static final String TAG = "MainActivity";

    //widgets
    private ProgressBar mProgressBar;

    //vars
    private FirebaseFirestore mDb;
    private boolean mLocationPermissionGranted = false;
    private UserLocation mUserLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.progressBar);
       findViewById(R.id.fab_create_chatroom).setOnClickListener(this);
       
       mFusedLocationClient=LocationServices.getFusedLocationProviderClient(activity:this);

        mDb = FirebaseFirestore.getInstance();

        initSupportActionBar();
    }
    
    //KNOW THE USER'S LAST POSITION 
    
     private void getLastKnownLocation(){
        Log.d(TAG, msg:"getLastKnownLocation:called.");
        if(ActivityCompat.checkSelfPermission(context this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }
        mFusedLocationClient.getLastKnownLocation().addOnCompleteListener(new OnCompleteListener<Location>(){
            @Override
            public void OnComplete(@Nonnull Task<Location> task){
                if(task.isSuccessful()){
                    Location location = task.getResult();
                    Geopoint geoPoint = new Geopoint(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, msg:"onComplete:latitude:"+ geoPoint.getLatitude());
                    Log.d(TAG, msg:"onComplete:longitude:"+ geoPoint.getLongitude());
                    mUserLocation.setGeopoint(geoPoint);
                    mUserLocation.setTimestamp(null);
                    saveUserLocation();
                }
            }
        });
    }
    
   
   //KNOW IF THE USER HAS INSTALLED GOOGLE PLAY SERVICES AND HAS GEOLOCATION ENABLED
   
   private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getUserDetails();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    getUserDetails();
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }
        
    @Override
    protected void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                getUserDetails();
            } else {
                getLocationPermission();
            }
       
        }
       
    }
    
}
//CLASS USER CLIENT FOR USER CLASS AND USER LOCATION CLASS

public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}


//CLASS USER FOR USER LOCATION

public class User implements Parcelable{

    private String email;
    private String user_id;
    private String username;
    private String avatar;

    public User(String email, String user_id, String username, String avatar) {
        this.email = email;
        this.user_id = user_id;
        this.username = username;
        this.avatar = avatar;
    }

    public User() {

    }

    protected User(Parcel in) {
        email = in.readString();
        user_id = in.readString();
        username = in.readString();
        avatar = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }


//USER LOCATION

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;
public class UserLocation{
    private GeoPoint geo_point;
    private @ServerTimestamp String timestamp;
    private User user;
    public UserLocation(GeoPoint geo_point, String timestamp, User user){
        this.geo_point=geo_point;
        this.timestamp=timestamp;
        this.user=user;
    }
    public UserLocation(){

    }

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    @Override
    public String toString(){
        return "UserLocation{" +
                "geo_point=" +geo_point +
                ", timestamp='" + timestamp + '\'' +
                ", user=" +user +
                '}';
    }
}

//GENERATE A MARKER IN THE MAP

public class ClusterMarker implements ClusterItem{
    private LatLng position;
    private String title;
    private String snippet;
    private int iconPicture;
    private User user;
    public ClusterMarker(LatnLng position, String title, String snippet, int iconPicture, User user){
        this.position=position;
        this.title=title;
        this.snippet=snippet;
        this.iconPicture= iconPicture
        this.user=user;
    }
    public ClusterMarker(){
    }
    @Override
    public LatnLng getPosition(){
        this.position = position;
    }
    @Override
    public String gettitle(){
        return title;
    }
    @Override
    public void setTitle (String title){
        this.title=title;
    }
    @Override
    public String getSnippet(){
        return snippet;
    }
    public void setSnippet(String snippet){
        this. snippet=snippet;
    }
    public int getIconPicture(){
        return iconPicture;
    }
    public void setIconPicture (int iconPicture){
        this. iconPicture=iconPicture;
    }
    public void User

    public User getUser() {
        return user;
    }
    public void setUser (User user){
        this.user=user;
    }
}

//PROGRAM TO DISPLAY THE MARKER ON THE MAP

public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {
    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private final int markerWidth;
    private final int markerHeight;

    public MyClusterManagerRenderer(Context context, Google map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);

    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions){
        imageView.setImageResource(item.getIconPicture());
        Bitmap icon -iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptionFactory.fromBitmap(icon));

    }
    protected boolean ShouldRenderAsCluster(Cluster<ClusterMarker>cluster){
        return false;
    }



}

//CLASS USER RECYCLER ADAPTER FOR USER LIST FRAGMENT CLASS


public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder>{

    private ArrayList<User> mUsers = new ArrayList<>();


    public UserRecyclerAdapter(ArrayList<User> users) {
        this.mUsers = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ((ViewHolder)holder).username.setText(mUsers.get(position).getUsername());
        ((ViewHolder)holder).email.setText(mUsers.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView username, email;

        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
        }


    }

}


//CLASS USER LIST FRAGMENT

public class UserListFragment extends Fragment {

    private static final String TAG = "UserListFragment";

    //widgets
    private RecyclerView mUserListRecyclerView;


    //vars
    private ArrayList<User> mUserList = new ArrayList<>();
    private UserRecyclerAdapter mUserRecyclerAdapter;


    public static UserListFragment newInstance(){
        return new UserListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mUserList = getArguments().getParcelableArrayList(getString(R.string.intent_user_list));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_user_list, container, false);
        mUserListRecyclerView = view.findViewById(R.id.user_list_recycler_view);

        initUserListRecyclerView();
        return view;
    }


    private void initUserListRecyclerView(){
        mUserRecyclerAdapter = new UserRecyclerAdapter(mUserList);
        mUserListRecyclerView.setAdapter(mUserRecyclerAdapter);
        mUserListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}

/*This program is mainly used to import the map from google maps and to insert markers on it. 
After logging in (separate piece of the program) a warning appears asking to activate geolocation because without it the app would lose some of its functionality. 
Furthermore, it is useful for us to know where our user is when he reports a certain place in order not to have legal problems. 
Once geolocation is enabled, the map opens with all the integrated markers. 
In the program there are two methods for inserting new markers, one is used to locate the various rooms that will decide to download the app and the other, 
instead, is used by users to report any problems encountered in the room itself.
The first marker is entered directly from Firebase. 
In a nutshell, the owner of a place, at the time of registration, will write, in a special form, the name and the precise address of the place he manages. 
This information will be saved on Firebase in the dedicated section and then directly taken from the code, which will mark a marker on the map.
The second marker, on the other hand, can be entered directly by the user by simply clicking on the map.
So, if a user wants to report a place because it does not comply with the rules dictated by state institutions or for other reasons, 
just open the application (and after logging in and activating the geolocation) and click on the place to report. 
At that point a new marker will come out which will later be saved on firebase. Both markers can be controlled directly by application administrators.*/

