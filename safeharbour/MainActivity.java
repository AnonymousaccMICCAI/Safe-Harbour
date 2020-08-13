package com.example.safeharbour;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final String  COMENT_KEY = "coment";
    public static final String POSITIVITY_KEY = "pos";
    public static final String TAG = "messaggio";

    TextView mCommentoTextView;

    //
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference mDocRef = db.document("SampleData/report");
    private CollectionReference systemRef = mDocRef.collection("user_report");
    private FirebaseAnalytics mAnalytics= FirebaseAnalytics.getInstance(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCommentoTextView = (TextView) findViewById(R.id.textView);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        //Log.d("tag", "onCreate: " + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail() + firebaseAuth.getCurrentUser().getDisplayName());

    };
    
    //load data on database
    @SuppressLint("InvalidAnalyticsName")
    public void loadData (View view) {
        // register the interaction and send to analythics
        Bundle bundle = new Bundle();
        bundle.putInt("Load Data",view.getId()); ;
        String btnName = "Load Data";
        mAnalytics.logEvent(btnName,bundle);


        EditText positivityView = (EditText) findViewById(R.id.positivityTXT);
        EditText comentView = (EditText) findViewById(R.id.commentoTXT);
        String positivityText = coordinatesView.getText().toString();
        String comentText = comentView.getText().toString();


        if (coordinatesText.isEmpty() || comentText.isEmpty()) { return; }
        HashMap<String, Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put(COMENT_KEY, comentText);
        dataToSave.put(POSITIVITY_KEY, coordinatesText);

        systemRef.add(dataToSave);


    };

//gets data from the database
        public void fetchdata (View view){

            systemRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    String data = "";

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String commentTxt = documentSnapshot.getString(COMENT_KEY);
                        String coordinatesTxt = documentSnapshot.getString(POSITIVITY_KEY);
                        data += "\"" + commentTxt + "\" --" + coordinatesTxt + "\n\n";
                        //.toObject(geopoint.class) transform the object in geopoint
                    }
                        mCommentoTextView.setText(data);


                    Log.d(TAG,"Documenti sono stati presi");
                };
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Documenti non sono stati presi");
                };
            });

        }

// logout from the app
    
    public void logout(final View view) {
        FirebaseAuth.getInstance().signOut();

        GoogleSignIn.getClient(this,new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                .signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(view.getContext(),login.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Signout Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // onclick of mapactivity
        public void AddComment(View retu){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
    
    // go to map
    public void Retrn2map(View retu){
        startActivity(new Intent(getApplicationContext(),MapsActivity.class));
    }


    }




