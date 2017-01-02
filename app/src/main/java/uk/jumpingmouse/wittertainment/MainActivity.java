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
import uk.jumpingmouse.wittertainment.adapter.CriticAdapter;
import uk.jumpingmouse.wittertainment.adapter.FilmAdapter;
import uk.jumpingmouse.wittertainment.data.Award;
import uk.jumpingmouse.wittertainment.data.Critic;
import uk.jumpingmouse.wittertainment.data.Film;

import java.util.ArrayList;
import java.util.List;

/**
 * The main activity.
 * @author Edmund Johnson
 */
public class MainActivity extends AppCompatActivity {

    // pseudo-username for use when the user is not logged in
    private static final String ANONYMOUS = "anonymous";

    // Logged in username
    private String mUsername;

    // Firebase variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReferenceFilms;
    private DatabaseReference mDatabaseReferenceAwards;
    private DatabaseReference mDatabaseReferenceCritics;
    private ChildEventListener mChildEventListenerFilms;
    private ChildEventListener mChildEventListenerAwards;
    private ChildEventListener mChildEventListenerCritics;

    // Adapters
    private FilmAdapter mFilmAdapter;
    private AwardAdapter mAwardAdapter;
    private CriticAdapter mCriticAdapter;

    // Screen fields - film
    private EditText mTxtFilmImdbId;
    private EditText mTxtFilmTitle;
    private Button mBtnSaveFilm;
    private Button mBtnToggleFilmList;
    private ListView mViewFilmList;

    // Screen fields - award
    private EditText mTxtAwardDate;
    private EditText mTxtAwardCategoryId;
    private EditText mTxtAwardFilmId;
    private EditText mTxtAwardCriticId;
    private EditText mTxtAwardCriticQuote;
    private Button mBtnSaveAward;
    private Button mBtnToggleAwardList;
    private ListView mViewAwardList;

    // Screen fields - critic
    private EditText mTxtCriticId;
    private EditText mTxtCriticName;
    private EditText mTxtCriticDescription;
    private Button mBtnSaveCritic;
    private Button mBtnToggleCriticList;
    private ListView mViewCriticList;

    //---------------------------------------------------------------------
    // Lifecycle methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReferenceRoot = mFirebaseDatabase.getReference();

        // Get references to the different sections of the database
        mDatabaseReferenceFilms = databaseReferenceRoot.child("films");
        mDatabaseReferenceAwards = databaseReferenceRoot.child("awards");
        mDatabaseReferenceCritics = databaseReferenceRoot.child("critics");

        // Initialise references to views

        // Film
        mTxtFilmImdbId = (EditText) findViewById(R.id.txtFilmImdbId);
        mTxtFilmTitle = (EditText) findViewById(R.id.txtFilmTitle);
        mBtnSaveFilm = (Button) findViewById(R.id.btnSaveFilm);
        mBtnToggleFilmList = (Button) findViewById(R.id.btnToggleFilmList);
        mViewFilmList = (ListView) findViewById(R.id.viewFilmList);

        // Award
        mTxtAwardDate = (EditText) findViewById(R.id.txtAwardDate);
        mTxtAwardCategoryId = (EditText) findViewById(R.id.txtAwardCategoryId);
        mTxtAwardFilmId = (EditText) findViewById(R.id.txtAwardFilmId);
        mTxtAwardCriticId = (EditText) findViewById(R.id.txtAwardCriticId);
        mTxtAwardCriticQuote = (EditText) findViewById(R.id.txtAwardCriticQuote);
        mBtnSaveAward = (Button) findViewById(R.id.btnSaveAward);
        mBtnToggleAwardList = (Button) findViewById(R.id.btnToggleAwardList);
        mViewAwardList = (ListView) findViewById(R.id.viewAwardList);

        // Critic
        mTxtCriticId = (EditText) findViewById(R.id.txtCriticId);
        mTxtCriticName = (EditText) findViewById(R.id.txtCriticName);
        mTxtCriticDescription = (EditText) findViewById(R.id.txtCriticDescription);
        mBtnSaveCritic = (Button) findViewById(R.id.btnSaveCritic);
        mBtnToggleCriticList = (Button) findViewById(R.id.btnToggleCriticList);
        mViewCriticList = (ListView) findViewById(R.id.viewCriticList);

        // Initialize film ListView and its adapter
        List<Film> films = new ArrayList<>();
        mFilmAdapter = new FilmAdapter(this, R.layout.list_item_film, films);
        mViewFilmList.setAdapter(mFilmAdapter);

        // Initialize award ListView and its adapter
        List<Award> awards = new ArrayList<>();
        mAwardAdapter = new AwardAdapter(this, R.layout.list_item_award, awards);
        mViewAwardList.setAdapter(mAwardAdapter);

        // Initialize critic ListView and its adapter
        List<Critic> critics = new ArrayList<>();
        mCriticAdapter = new CriticAdapter(this, R.layout.list_item_critic, critics);
        mViewCriticList.setAdapter(mCriticAdapter);

//        Spinner spinner = (Spinner) findViewById(R.id.listCategoryId);
//        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.award_categories, android.R.layout.simple_spinner_item);
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        spinner.setAdapter(adapter);

        // Clicking the Save Film button saves a film and clears the film fields
        mBtnSaveFilm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filmImdbId = mTxtFilmImdbId.getText().toString();
                String filmTitle = mTxtFilmTitle.getText().toString();

                if (Film.isFieldsValid(filmImdbId, filmTitle)) {
                    // Create an award object based on the screen fields
                    Film film = Film.newInstance(filmImdbId, filmTitle);

                    // Add the message to the "messages" part of the database
                    // Use "push()" so that a new Push ID is generated for the message
                    mDatabaseReferenceFilms.push().setValue(film);
                    displayMessage(getString(R.string.save_successful));
                    clearFieldsFilm();
                } else {
                    displayMessage(getString(R.string.invalid_field));
                }
            }
        });

        // Clicking the Toggle Film List button changes the visibility of the film list
        mBtnToggleFilmList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVisibility(mViewFilmList);
            }
        });

        // Clicking the Save Award button saves an award and clears the award fields
        mBtnSaveAward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String awardDate = mTxtAwardDate.getText().toString();
                String categoryId = mTxtAwardCategoryId.getText().toString();
                String filmId = mTxtAwardFilmId.getText().toString();
                String criticId = mTxtAwardCriticId.getText().toString();
                String criticQuote = mTxtAwardCriticQuote.getText().toString();

                if (Award.isFieldsValid(awardDate, categoryId, filmId, criticId, criticQuote)) {
                    // Create an award object based on the screen fields
                    Award award = Award.newInstance(awardDate, categoryId, filmId, criticId, criticQuote);

                    // Add the message to the "messages" part of the database
                    // Use "push()" so that a new Push ID is generated for the message
                    mDatabaseReferenceAwards.push().setValue(award);
                    displayMessage(getString(R.string.save_successful));
                    clearFieldsAward();
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

        // Clicking the Save Critic button saves a critic and clears the critic fields
        mBtnSaveCritic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String criticId = mTxtCriticId.getText().toString();
                String criticName = mTxtCriticName.getText().toString();
                String criticDescription = mTxtCriticDescription.getText().toString();

                if (Critic.isFieldsValid(criticId, criticName, criticDescription)) {
                    // Create an award object based on the screen fields
                    Critic critic = Critic.newInstance(criticId, criticName, criticDescription);

                    // Add the message to the "messages" part of the database
                    // Use "push()" so that a new Push ID is generated for the message
                    mDatabaseReferenceCritics.push().setValue(critic);
                    displayMessage(getString(R.string.save_successful));
                    clearFieldsCritic();
                } else {
                    displayMessage(getString(R.string.invalid_field));
                }
            }
        });

        // Clicking the Toggle Critic List button changes the visibility of the critic list
        mBtnToggleCriticList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVisibility(mViewCriticList);
            }
        });

        // TODO Authorisation stuff

        onSignedInInitialise("Edmund");

    }

    /**
     * Clear the on-screen film fields.
     */
    private void clearFieldsFilm() {
        mTxtFilmImdbId.setText("");
        mTxtFilmTitle.setText("");
    }

    /**
     * Clear the on-screen award fields.
     */
    private void clearFieldsAward() {
        mTxtAwardDate.setText("");
        mTxtAwardCategoryId.setText("");
        mTxtAwardFilmId.setText("");
        mTxtAwardCriticId.setText("");
        mTxtAwardCriticQuote.setText("");
    }

    /**
     * Clear the on-screen critic fields.
     */
    private void clearFieldsCritic() {
        mTxtCriticId.setText("");
        mTxtCriticName.setText("");
        mTxtCriticDescription.setText("");
    }

    //---------------------------------------------------------------------
    // Security methods

    private void onSignedInInitialise(String username) {
        mUsername = username;
        attachDatabaseReadListenerFilms();
        attachDatabaseReadListenerAwards();
        attachDatabaseReadListenerCritics();
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        mAwardAdapter.clear();
        detachDatabaseReadListenerFilms();
        detachDatabaseReadListenerAwards();
        detachDatabaseReadListenerCritics();
    }

    //---------------------------------------------------------------------
    // Database listeners

    /**
     * Attach a read listener to the films section of the database.
     */
    private void attachDatabaseReadListenerFilms() {
        if (mChildEventListenerFilms == null) {
            mChildEventListenerFilms = new ChildEventListener() {
                // This is called for each existing child when the listener is attached
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Film film = dataSnapshot.getValue(Film.class);
                    mFilmAdapter.add(film);
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

            mDatabaseReferenceFilms.addChildEventListener(mChildEventListenerFilms);
        }
    }

    /**
     * Detach the read listener from the films section of the database.
     */
    private void detachDatabaseReadListenerFilms() {
        detachDatabaseReadListener(mDatabaseReferenceFilms, mChildEventListenerFilms);
    }

    /**
     * Attach a read listener to the awards section of the database.
     */
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

    /**
     * Detach the read listener from the awards section of the database.
     */
    private void detachDatabaseReadListenerAwards() {
        detachDatabaseReadListener(mDatabaseReferenceAwards, mChildEventListenerAwards);
    }

    /**
     * Attach a read listener to the critic section of the database.
     */
    private void attachDatabaseReadListenerCritics() {
        if (mChildEventListenerCritics == null) {
            mChildEventListenerCritics = new ChildEventListener() {
                // This is called for each existing child when the listener is attached
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Critic critic = dataSnapshot.getValue(Critic.class);
                    mCriticAdapter.add(critic);
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

            mDatabaseReferenceCritics.addChildEventListener(mChildEventListenerCritics);
        }
    }

    /**
     * Detach the read listener from the critic section of the database.
     */
    private void detachDatabaseReadListenerCritics() {
        detachDatabaseReadListener(mDatabaseReferenceCritics, mChildEventListenerCritics);
    }

    /**
     * Detaches a child event listener from a section of the database.
     * @param databaseReference a reference to a section of the database
     * @param childEventListener the child event listener
     */
    private void detachDatabaseReadListener(DatabaseReference databaseReference,
                                            ChildEventListener childEventListener) {
        if (childEventListener != null) {
            databaseReference.removeEventListener(childEventListener);
            childEventListener = null;
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
