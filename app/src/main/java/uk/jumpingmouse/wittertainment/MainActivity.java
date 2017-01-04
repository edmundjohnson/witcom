package uk.jumpingmouse.wittertainment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

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

    // Use this rather than the automatically-generated static import
    private static final int RC_SIGN_IN = 1;

    // pseudo-username for use when the user is not logged in
    private static final String ANONYMOUS = "anonymous";

    // Logged in username
    private String mUsername;

    // Firebase Auth variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

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
    private Button mBtnShowFilm;
    private LinearLayout mLayoutFilm;
    private EditText mTxtFilmImdbId;
    private EditText mTxtFilmTitle;
    private EditText[] mEditTextsFilm;
    private Button mBtnSaveFilm;
    private ListView mViewFilmList;

    // Screen fields - award
    private Button mBtnShowAward;
    private LinearLayout mLayoutAward;
    private EditText mTxtAwardDate;
    private EditText mTxtAwardCategoryId;
    private EditText mTxtAwardFilmId;
    private EditText mTxtAwardCriticId;
    private EditText mTxtAwardCriticReview;
    private EditText[] mEditTextsAward;
    private Button mBtnSaveAward;
    private ListView mViewAwardList;

    // Screen fields - critic
    private Button mBtnShowCritic;
    private LinearLayout mLayoutCritic;
    private EditText mTxtCriticId;
    private EditText mTxtCriticName;
    private EditText mTxtCriticDescription;
    private EditText[] mEditTextsCritic;
    private Button mBtnSaveCritic;
    private ListView mViewCriticList;

    //---------------------------------------------------------------------
    // Lifecycle methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReferenceRoot = mFirebaseDatabase.getReference();

        // Get references to the different sections of the database
        mDatabaseReferenceFilms = databaseReferenceRoot.child("films");
        mDatabaseReferenceAwards = databaseReferenceRoot.child("awards");
        mDatabaseReferenceCritics = databaseReferenceRoot.child("critics");

        // Initialise references to views

        mBtnShowFilm = (Button) findViewById(R.id.btnShowFilm);
        mBtnShowAward = (Button) findViewById(R.id.btnShowAward);
        mBtnShowCritic = (Button) findViewById(R.id.btnShowCritic);

        // Add Film
        mLayoutFilm = (LinearLayout) findViewById(R.id.layoutFilm);
        mTxtFilmImdbId = (EditText) findViewById(R.id.txtFilmImdbId);
        mTxtFilmTitle = (EditText) findViewById(R.id.txtFilmTitle);
        mEditTextsFilm = new EditText [] { mTxtFilmImdbId, mTxtFilmTitle };
        mBtnSaveFilm = (Button) findViewById(R.id.btnSaveFilm);
        mViewFilmList = (ListView) findViewById(R.id.viewFilmList);

        // Add Award
        mLayoutAward = (LinearLayout) findViewById(R.id.layoutAward);
        mTxtAwardDate = (EditText) findViewById(R.id.txtAwardDate);
        mTxtAwardCategoryId = (EditText) findViewById(R.id.txtAwardCategoryId);
        mTxtAwardFilmId = (EditText) findViewById(R.id.txtAwardFilmId);
        mTxtAwardCriticId = (EditText) findViewById(R.id.txtAwardCriticId);
        mTxtAwardCriticReview = (EditText) findViewById(R.id.txtAwardCriticReview);
        mEditTextsAward = new EditText [] { mTxtAwardDate, mTxtAwardCategoryId,
                mTxtAwardFilmId, mTxtAwardCriticId, mTxtAwardCriticReview };
        mBtnSaveAward = (Button) findViewById(R.id.btnSaveAward);
        mViewAwardList = (ListView) findViewById(R.id.viewAwardList);

        // Add Critic
        mLayoutCritic = (LinearLayout) findViewById(R.id.layoutCritic);
        mTxtCriticId = (EditText) findViewById(R.id.txtCriticId);
        mTxtCriticName = (EditText) findViewById(R.id.txtCriticName);
        mTxtCriticDescription = (EditText) findViewById(R.id.txtCriticDescription);
        mEditTextsCritic = new EditText [] { mTxtCriticId, mTxtCriticName, mTxtCriticDescription };
        mBtnSaveCritic = (Button) findViewById(R.id.btnSaveCritic);
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

        // Toggle button listeners

        // Clicking the Show Film button hides the other entities
        mBtnShowFilm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showView(mLayoutFilm);
                hideView(mLayoutAward);
                hideView(mLayoutCritic);
            }
        });

        // Clicking the Show Award button hides the other entities
        mBtnShowAward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideView(mLayoutFilm);
                showView(mLayoutAward);
                hideView(mLayoutCritic);
            }
        });

        // Clicking the Show Critic button hides the other entities
        mBtnShowCritic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideView(mLayoutFilm);
                hideView(mLayoutAward);
                showView(mLayoutCritic);
            }
        });

        // Save button listeners

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
                    Task<Void> task = mDatabaseReferenceFilms.push().setValue(film);
                    task.addOnCompleteListener(getPushCompleteListener(mEditTextsFilm));
                } else {
                    displayErrorMessage(getString(R.string.invalid_field));
                }
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
                String criticReview = mTxtAwardCriticReview.getText().toString();

                if (Award.isFieldsValid(awardDate, categoryId, filmId, criticId, criticReview)) {
                    // Create an award object based on the screen fields
                    Award award = Award.newInstance(awardDate, categoryId, filmId, criticId, criticReview);

                    // Add the message to the "messages" part of the database
                    // Use "push()" so that a new Push ID is generated for the message
                    Task<Void> task = mDatabaseReferenceAwards.push().setValue(award);
                    task.addOnCompleteListener(getPushCompleteListener(mEditTextsAward));
                } else {
                    displayErrorMessage(getString(R.string.invalid_field));
                }
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
                    Task<Void> task = mDatabaseReferenceCritics.push().setValue(critic);
                    task.addOnCompleteListener(getPushCompleteListener(mEditTextsCritic));

                } else {
                    displayErrorMessage(getString(R.string.invalid_field));
                }
            }
        });

        // Authorisation

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    String displayName = user.getDisplayName();

                    // If the displayName was null, iterate the provider-specific data
                    // and set with the first non-null value
//                    for (UserInfo userInfo : user.getProviderData()) {
//
//                        // Id of the provider (ex: google.com)
//                        String providerId = userInfo.getProviderId();
//
//                        // UID specific to the provider
//                        String uid = userInfo.getUid();
//
//                        // Name, email address, and profile photo Url
//                        String name = userInfo.getDisplayName();
//                        String email = userInfo.getEmail();
//
//                        if (displayName == null && userInfo.getDisplayName() != null) {
//                            displayName = userInfo.getDisplayName();
//                        }
//                    }
                    onSignedInInitialise(displayName);
                } else {
                    // user is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            // Firebase UI 0.6.0
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                    // Firebase UI 1.0.1
                                    //Arrays.asList(
                                    //    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    //    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

    }

    /**
     * Check if the user has hit back button from the sign-in screen.
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the returned data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Are we returning from the sign-in screen?
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                //IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
                displayInfoMessage(getString(R.string.sign_in_ok));
            } else if (resultCode == RESULT_CANCELED) {
                // The user cancelled sign-in, e.g. they hit the back button
                displayInfoMessage(getString(R.string.sign_in_cancelled));
                // finish the activity
                finish();
            }
        }
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

        mFilmAdapter.clear();
        mAwardAdapter.clear();
        mCriticAdapter.clear();

        detachDatabaseReadListener(mDatabaseReferenceFilms, mChildEventListenerFilms);
        detachDatabaseReadListener(mDatabaseReferenceAwards, mChildEventListenerAwards);
        detachDatabaseReadListener(mDatabaseReferenceCritics, mChildEventListenerCritics);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // sign out
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

        mFilmAdapter.clear();
        mAwardAdapter.clear();
        mCriticAdapter.clear();

        detachDatabaseReadListenerFilms();
        detachDatabaseReadListenerAwards();
        detachDatabaseReadListenerCritics();
    }

    //---------------------------------------------------------------------
    // Database listeners

    /**
     * Return a listener for completion of a push task for an entity.
     * @param editTextFields the EditText fields containing the entity info. These fields are
     *                       cleared on successful completion of the task.
     */
    private OnCompleteListener<Void> getPushCompleteListener(@NonNull final EditText[] editTextFields) {

        return new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    displayInfoMessage(getString(R.string.save_successful));
                    clearFields(editTextFields);
                } else {
                    String errorMessage;
                    try {
                        errorMessage = task.getResult().toString();
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                    }
                    displayErrorMessage(getString(R.string.error_while_saving, errorMessage));
                }
            }
        };
    }

    /**
     * Clear a set of on-screen EditText fields.
     */
    private void clearFields(@NonNull final EditText[] editTextFields) {
        for (EditText editTextField : editTextFields) {
            editTextField.setText("");
        }
    }

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
     * Sets the visibility of a view to VISIBLE.
     * @param view the view
     */
    private void showView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Sets the visibility of a view to GONE.
     * @param view the view
     */
    private void hideView(View view) {
        view.setVisibility(View.GONE);
    }

//    /**
//     * Toggles the visibility of a view between VISIBLE and GONE.
//     * @param view the view
//     */
//    private void toggleVisibility(View view) {
//        if (view.getVisibility() == View.VISIBLE) {
//            hideView(view);
//        } else {
//            showView(view);
//        }
//    }

    private void displayInfoMessage(String message) {
        Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void displayErrorMessage(String message) {
        //Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
        //toast.setGravity(Gravity.CENTER, 0, 0);
        //toast.show();

        displayErrorDialog(this, getString(R.string.error_title), message);
    }

    /**
     * Displays an alert dialog.
     * @param context the context
     * @param title the title of the dialog
     * @param message the text of the dialog
     */
    private static void displayErrorDialog(@Nullable Context context, @NonNull String title,
                                           @Nullable String message) {
        if (context != null) {
            new AlertDialog.Builder(context)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        }
    }

}
