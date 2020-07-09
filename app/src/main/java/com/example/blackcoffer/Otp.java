package com.example.blackcoffer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Otp extends AppCompatActivity {

    TextView Send;
    String verificationCodeBySystem;
    Button verify_btn;
    EditText phoneNoEnteredByTheOwner;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        Send = findViewById(R.id.send);
        Send.setPaintFlags(Send.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        verify_btn = findViewById(R.id.verify_btn);
        phoneNoEnteredByTheOwner = findViewById(R.id.verification_code_entered_by_user);
        progressBar = findViewById(R.id.progress_bar);


        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(Otp.this, "Account create successfully now login your account", Toast.LENGTH_SHORT).show();

                String code = phoneNoEnteredByTheOwner.getText().toString();

                if (code.isEmpty() || code.length() < 6) {

                    phoneNoEnteredByTheOwner.setError("Wrong OTP....");
                    phoneNoEnteredByTheOwner.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        });

        String phone = getIntent().getStringExtra("phone");

        sendVerificationCodeToUser(phone);
    }

    private void sendVerificationCodeToUser(String phone) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,// Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationCodeBySystem = s;

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.GONE);
                verifyCode(code);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

            Toast.makeText(Otp.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    };
        private void verifyCode(String codeByUser) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
            signInTheUserByCredentials(credential);
        }

        private void signInTheUserByCredentials(PhoneAuthCredential credential) {

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(Otp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(Otp.this, "Your Account has been created successfully!", Toast.LENGTH_SHORT).show();

                                //Perform Your required action here to either let the user sign In or do something required
                                Intent intent = new Intent(Otp.this, Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            } else {
                                Toast.makeText(Otp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
}