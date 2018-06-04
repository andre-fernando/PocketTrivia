package com.andre_fernando.pockettrivia.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.andre_fernando.pockettrivia.R;
import com.andre_fernando.pockettrivia.helpers.SharedPreferenceHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

@SuppressWarnings("WeakerAccess")
public class SignInFragment extends Fragment {
    private GoogleSignInClient signInClient;
    private Unbinder unbinder;
    private SharedPreferenceHelper helper;
    private boolean isLoggedIn;
    private final int RC_SIGN_IN = 52;

    @BindView(R.id.et_sign_in_username) EditText et_username;
    @BindView(R.id.bt_sign_in_save) Button bt_save_username;
    @BindView(R.id.bt_sign_in_google) SignInButton bt_google_sign_in;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        //Bind views with ButterKnife
        unbinder = ButterKnife.bind(this, view);

        //Get SharedPreference Helper
        helper = new SharedPreferenceHelper(getContext());

        //region Google Sign In
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(Objects.requireNonNull(getContext()), gso);

        //endregion Google Sign In

        return view;
    }


    //region UI Methods
    private void updateUI(){
        if (isLoggedIn){
            String username = helper.getUserName();
            et_username.setText(username);
            et_username.setEnabled(false);
            bt_save_username.setText(R.string.log_out);
            bt_google_sign_in.setEnabled(false);
        } else {
            et_username.setText("");
            et_username.setEnabled(true);
            bt_save_username.setText(R.string.bt_save);
            bt_google_sign_in.setEnabled(true);
        }

    }

    @OnClick(R.id.bt_sign_in_save)
    public void onClickSaveUsername(){
        if (isLoggedIn){
            isLoggedIn = false;
            helper.removeUsername();
            updateUI();
        } else {
            String username = et_username.getText().toString();
            if (username.length() > 0){
                helper.setUserName(username);
                isLoggedIn=true;
                updateUI();
            }
        }
    }
    //endregion UI Methods

    //region Google Sign In Methods

    @OnClick(R.id.bt_sign_in_google)
    public void onClickSignInGoogle(){
        Intent signIn = signInClient.getSignInIntent();
        startActivityForResult(signIn, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            GoogleSignInAccount account = task.getResult();
            String username = account.getDisplayName();
            if (username != null && username.length()>0){
                helper.setUserName(username);
                isLoggedIn=true;
                updateUI();
            }
        }
    }
    //endregion Google Sign In Methods

    //region Lifecycle Methods

    @Override
    public void onStart() {
        super.onStart();
        isLoggedIn = helper.isLoggedIn();
        updateUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }
    //endregion Lifecycle Methods
}
