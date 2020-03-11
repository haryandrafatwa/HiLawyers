package com.example.hilawyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.hilawyers.MainActivity;
import com.example.hilawyers.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextView tv_masuk;
    private FirebaseAuth mAuth;
    private EditText et_phonenumber, et_email, et_pass;
    private Button btnDaftar;
    private DatabaseReference userRefs;
    private ProgressDialog mDialog;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        initialize();

    }

    private void initialize() {

        et_email = findViewById(R.id.et_email_register);
        et_pass = findViewById(R.id.et_password_register);
        et_phonenumber = findViewById(R.id.et_phonenumber_register);
        btnDaftar = findViewById(R.id.btn_register);
        tv_masuk = findViewById(R.id.tv_login);
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        tv_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActivity(LoginActivity.class);
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerWithEmailandPassword();
            }
        });

        int tintColorDark = ContextCompat.getColor(getApplicationContext(), R.color.colorInputText);

        Drawable drawableVisibility = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_visibility);
        drawableVisibility = DrawableCompat.wrap(drawableVisibility);
        DrawableCompat.setTint(drawableVisibility.mutate(), tintColorDark);

        drawableVisibility.setBounds(0, 0, drawableVisibility.getIntrinsicWidth(), drawableVisibility.getIntrinsicHeight());

        et_pass.setCompoundDrawables(null, null, drawableVisibility, null);

        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!et_email.getText().toString().matches(emailPattern)) {
                    et_email.setTextColor(Color.RED);
                } else {
                    et_email.setTextColor(getResources().getColor(R.color.colorInputText));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_pass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (et_pass.getRight() - et_pass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        int tintColorDark = ContextCompat.getColor(getApplicationContext(), R.color.colorInputText);
                        int tintColorDrawable = ContextCompat.getColor(getApplicationContext(), R.color.colorInputText);

                        Drawable drawableVisibility = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_visibility);
                        drawableVisibility = DrawableCompat.wrap(drawableVisibility);
                        DrawableCompat.setTint(drawableVisibility.mutate(), tintColorDrawable);

                        Drawable drawableVisibilityOff = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_visibility_off);
                        drawableVisibilityOff = DrawableCompat.wrap(drawableVisibilityOff);
                        DrawableCompat.setTint(drawableVisibilityOff.mutate(), tintColorDrawable);

                        drawableVisibility.setBounds(0, 0, drawableVisibility.getIntrinsicWidth(), drawableVisibility.getIntrinsicHeight());
                        drawableVisibilityOff.setBounds(0, 0, drawableVisibilityOff.getIntrinsicWidth(), drawableVisibilityOff.getIntrinsicHeight());

                        if (et_pass.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                            et_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            DrawableCompat.setTint(drawableVisibility.mutate(), tintColorDark);
                            et_pass.setCompoundDrawables(null, null, drawableVisibility, null);
                        } else {
                            et_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            et_pass.setCompoundDrawables(null, null, drawableVisibilityOff, null);

                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void registerWithEmailandPassword() {

        final String email = et_email.getText().toString();
        final String phoneNumber = et_phonenumber.getText().toString();
        String pass = et_pass.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Data harus diisi", Toast.LENGTH_SHORT).show();
        } else {
            if (!pass.isEmpty()) {
                if (!phoneNumber.isEmpty()) {
                    mDialog.setTitle("Register");
                    mDialog.setCancelable(true);
                    mDialog.setMessage("Wait a minute .. ");
                    mDialog.show();

                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String currentUserID = mAuth.getCurrentUser().getUid();
                                userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID);
                                HashMap userMap = new HashMap();
                                userMap.put("phonenumber", phoneNumber);
                                userMap.put("email", email);

                                /*dummyDispPict.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        userRefs.child("displaypicture").setValue(uri.toString());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.d("DISPLAY PICTURE FAILED", "OMG");
                                    }
                                });*/

                                userRefs.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Daftar Berhasil", Toast.LENGTH_SHORT).show();
                                            mDialog.dismiss();
                                            setActivity(MainActivity.class);
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            mDialog.dismiss();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Phonenumber harus diisi!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Password harus diisi!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setActivity(Class activity) {
        Intent mainIntent = new Intent(RegisterActivity.this, activity);
        startActivity(mainIntent);
        finish();
    }

}
