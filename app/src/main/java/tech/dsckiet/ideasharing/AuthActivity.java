package tech.dsckiet.ideasharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1001;
    private static final String TAG = AuthActivity.class.getSimpleName();

//    TextView termsOfUseBtn, privacyPolicyBtn;
    CardView mGoogleSignInButton;

    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    RelativeLayout layout;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        progressDialog = new ProgressDialog(this, R.style.AlertDialogStyle);

        layout = findViewById(R.id.layout);

//        termsOfUseBtn = findViewById(R.id.termsofusebtn);
//
//        termsOfUseBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder alert = new AlertDialog.Builder(AuthActivity.this);
//                alert.setTitle("Terms of use");
//                alert.setMessage("Every landing page needs a small description after the big bold title, that's why we added this text here. Add here all the information that can make you or your product create the first impression.");
//                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                    }
//                });
//                alert.show();
//
//            }
//        });


//        mSignOutButton = findViewById(R.id.google_sign_out);
        mGoogleSignInButton = findViewById(R.id.google_sign_in);
        // Initializing Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        progressDialog.setMessage("logging in user..");
        progressDialog.show();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
//                            Toast.makeText(AuthActivity.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(R.id.layout), "User Logged In", Snackbar.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
//                            Toast.makeText(AuthActivity.this, "Could not login user", Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(R.id.layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });

    }

}