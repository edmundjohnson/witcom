package uk.jumpingmouse.wittertainment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import uk.jumpingmouse.wittertainment.adapter.AwardAdapter;
import uk.jumpingmouse.wittertainment.data.Award;

import java.util.ArrayList;
import java.util.List;

/**
 * The main activity.
 * @author Edmund Johnson
 */
public class MainActivity extends AppCompatActivity {

    // pseudo-username for use when the user is not logged in
    private static final String ANONYMOUS = "anonymous";

    // Firebase variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReferenceAwards;
    private ChildEventListener mChildEventListenerAwards;

    // Adapter
    private AwardAdapter mAwardAdapter;

    // Screen fields
    private EditText mTxtAwardDate;
    private EditText mTxtCategoryId;
    private EditText mTxtFilmId;
    private EditText mTxtCriticId;
    private EditText mTxtCriticQuote;
    private Button mBtnSaveAward;
    private Button mBtnToggleAwardList;
    private ListView mViewAwardList;

    // Logged in username
    private String mUsername;

    //---------------------------------------------------------------------
    // Lifecycle methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReferenceRoot = mFirebaseDatabase.getReference();

        // Get a reference to the "awards" portion of the database
        mDatabaseReferenceAwards = databaseReferenceRoot.child("awards");

        // Initialise references to views

        mTxtAwardDate = (EditText) findViewById(R.id.txtAwardDate);
        mTxtCategoryId = (EditText) findViewById(R.id.txtCategoryId);
        mTxtFilmId = (EditText) findViewById(R.id.txtFilmId);
        mTxtCriticId = (EditText) findViewById(R.id.txtCriticId);
        mTxtCriticQuote = (EditText) findViewById(R.id.txtCriticQuote);
        mBtnSaveAward = (Button) findViewById(R.id.btnSaveAward);
        mBtnToggleAwardList = (Button) findViewById(R.id.btnToggleAwardList);
        mViewAwardList = (ListView) findViewById(R.id.viewAwardList);

        // Initialize award ListView and its adapter
        List<Award> awards = new ArrayList<>();
        mAwardAdapter = new AwardAdapter(this, R.layout.list_item_award, awards);
        mViewAwardList.setAdapter(mAwardAdapter);

//        Spinner spinner = (Spinner) findViewById(R.id.listCategoryId);
//        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.award_categories, android.R.layout.simple_spinner_item);
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        spinner.setAdapter(adapter);


        // Clicking the Save Award button saves an award and clears the award fields
        mBtnSaveAward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String awardDate = mTxtAwardDate.getText().toString();
                String categoryId = mTxtCategoryId.getText().toString();
                String filmId = mTxtFilmId.getText().toString();
                String criticId = mTxtCriticId.getText().toString();
                String criticQuote = mTxtCriticQuote.getText().toString();

                if (Award.isFieldsValid(awardDate, categoryId, filmId, criticId, criticQuote)) {
                    // Create an award object based on the screen fields
                    Award award = Award.newInstance(awardDate, categoryId, filmId, criticId, criticQuote);

                    // Add the message to the "messages" part of the database
                    // Use "push()" so that a new Push ID is generated for the message
                    mDatabaseReferenceAwards.push().setValue(award);
                    displayMessage(getString(R.string.save_successful));
                    clearAwardFields();
                } else {
                    displayMessage(getString(R.string.invalid_field));
                }
            }
        });

        // Clicking the Toggle Award List button changes the visibility of the award list
        mBtnToggleAwardList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVisibility(mViewAwardList);
            }
        });

        // TODO Authorisation stuff

        onSignedInInitialise("Edmund");

    }

    private void clearAwardFields() {
        mTxtAwardDate.setText("");
        mTxtCategoryId.setText("");
        mTxtFilmId.setText("");
        mTxtCriticId.setText("");
        mTxtCriticQuote.setText("");
    }


    //---------------------------------------------------------------------
    // Security methods

    private void onSignedInInitialise(String username) {
        mUsername = username;
        attachDatabaseReadListenerAwards();
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        mAwardAdapter.clear();
        detachDatabaseReadListenerAwards();
    }

    //---------------------------------------------------------------------
    // Database listeners

    private void attachDatabaseReadListenerAwards() {
        if (mChildEventListenerAwards == null) {
            mChildEventListenerAwards = new ChildEventListener() {
                // This is called for each existing child when the listener is attached
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Award award = dataSnapshot.getValue(Award.class);
                    mAwardAdapter.add(award);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                // Called if an error occurs while attempting a database operation
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };

            mDatabaseReferenceAwards.addChildEventListener(mChildEventListenerAwards);
        }
    }

    private void detachDatabaseReadListenerAwards() {
        if (mChildEventListenerAwards != null) {
            mDatabaseReferenceAwards.removeEventListener(mChildEventListenerAwards);
            mChildEventListenerAwards = null;
        }
    }

    //---------------------------------------------------------------------

    /**
     * Toggles the visibility of a view between VISIBLE and GONE.
     * @param view the view
     */
    private void toggleVisibility(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void displayMessage(String message) {
        Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

}
