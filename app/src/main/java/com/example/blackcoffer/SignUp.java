package com.example.blackcoffer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    TextView login;
    TextInputLayout Username, Phone, Password;
    Button SignUp_btn;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

       SignUp_btn = findViewById(R.id.SignUp);
       login = findViewById(R.id.login_txt);
       Username = findViewById(R.id.username);
       Phone = findViewById(R.id.phone);
       Password = findViewById(R.id.password);

       SignUp_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {


               rootNode = FirebaseDatabase.getInstance();
               reference = rootNode.getReference("User");

               String username = Username.getEditText().getText().toString();
               String phone = Phone.getEditText().getText().toString();
               String password = Password.getEditText().getText().toString();

               Intent intent = new Intent(getApplicationContext(), Otp.class);
               intent.putExtra("phone", phone);
               startActivity(intent);

               HelperClass helperClass = new HelperClass(username, phone, password);
               reference.child(username).setValue(helperClass);

               Toast.makeText(SignUp.this, "Verify your phone", Toast.LENGTH_SHORT).show();

           }
       });

          login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, EditTag.class);
                startActivity(intent);
            }
        });
    }
}
