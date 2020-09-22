package com.example.datanotespickup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
    EditText course ,subject, noteUrl, noteName;
    TextView submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        course= findViewById(R.id.course);
        subject= findViewById(R.id.subject);
        noteName= findViewById(R.id.noteName);
        noteUrl= findViewById(R.id.noteUrl);
        submit =findViewById(R.id.textView);
        getSupportActionBar().hide();

        course.setError(null);
        subject.setError(null);
        noteName.setError(null);
        noteUrl.setError(null);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(course.getText().toString().isEmpty())
                { course.setError("Required!");
                    return;}
                else if (subject.getText().toString().isEmpty())
                { subject.setError("Required!");
                    return;}
                else if (noteName.getText().toString().isEmpty())
                { noteName.setError("Required!");
                    return;}
                else if (noteUrl.getText().toString().isEmpty())
                { noteUrl.setError("Required!");
                    return;}
                Map<String, Object> map1 = new HashMap<>();
                map1.put("name", course.getText().toString());
                firebaseFirestore.collection("courses").document(course.getText().toString())
                        .set(map1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Map<String, Object> map2 = new HashMap<>();
                            map2.put("name", subject.getText().toString());

                            firebaseFirestore.collection("courses").document(course.getText().toString()).collection("subjects").document(subject.getText().toString())
                                    .set(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Map<String, Object> map3 = new HashMap<>();
                                        map3.put("name", noteName.getText().toString());
                                        firebaseFirestore.collection("courses").document(course.getText().toString()).collection("subjects").document(subject.getText().toString()).collection("notes").document(noteName.getText().toString())
                                                .set(map3).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("name", noteUrl.getText().toString());
                                                    firebaseFirestore.collection("courses").document(course.getText().toString()).collection("subjects").document(subject.getText().toString()).collection("notes").document(noteName.getText().toString()).collection("unit2").document(noteName.getText().toString())
                                                            .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(MainActivity.this, "done", Toast.LENGTH_SHORT).show();
                                                            }else{
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }else{
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                    }else{
                                        String error = task.getException().getMessage();
                                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}