package com.example.datanotespickup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    EditText course ,subject;
    String noteName,noteUrl;
    TextView submit,textView2,textView3,textView4;
    public static List<DataModel> list = new ArrayList<>();
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        course= findViewById(R.id.course);
        subject= findViewById(R.id.subject);
        submit =findViewById(R.id.textView);
        textView2=findViewById(R.id.textView2);
        textView3=findViewById(R.id.textView3);
        textView4=findViewById(R.id.textView4);

        storage = FirebaseStorage.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();

        course.setError(null);
        subject.setError(null);

        submit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction()==MotionEvent.ACTION_UP)
                     submit.setTextColor(ColorStateList.valueOf(Color.BLACK));
                else if (motionEvent.getAction()==MotionEvent.ACTION_DOWN)
                    submit.setTextColor(ColorStateList.valueOf(Color.WHITE));
                return false;
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                if(sb.length()!=0)
                    sb.delete(0,sb.length()-1);
                textView2.setText("");
                textView4.setText("\nList Size: \n");
                //                textView2.setText("List will be shown here!!\n\n WAIT FOR SOMETIME\n\nIf nothing happens check for spelling mistake");
                if(course.getText().toString().isEmpty()) {
                    course.setError("Required!");
                    return;
                } else if (subject.getText().toString().isEmpty()) {
                    subject.setError("Required!");
                    return;
                }

                mStorageRef = storage.getReference().child("NotesDocument/"+course.getText().toString().trim()+"/"+subject.getText().toString().trim());
                mStorageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (final StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    noteName = item.getName().substring(0,item.getName().length()-4);
                                    noteUrl= uri.toString();
                                    list.add(new DataModel(noteName,noteUrl));
                                    Log.d(TAG,list.size()+"");
                                    textView4.setText("\nList Size: "+list.size());
//                                    Toast.makeText(MainActivity.this, "SIZE"+list.size(), Toast.LENGTH_SHORT).show();

//                                    Toast.makeText(MainActivity.this,   noteName+noteUrl, Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    list.clear();
                                    textView2.setText("");
                                    textView4.setText("\nList Size: \n");
                                    String error = e.getMessage();
                                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        list.clear();
                        textView2.setText("");
                        textView4.setText("\nList Size: \n");
                        String error = e.getMessage();
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(course.getText().toString().isEmpty()) {
                    course.setError("Required!");
                    return;
                } else if (subject.getText().toString().isEmpty()) {
                    subject.setError("Required!");
                    return;
                }
                if(sb.length()!=0)
                    sb.delete(0,sb.length()-1);
                textView2.setText("\nWait till you see \"Success!\" \n\nDON'T PRESS SUBMIT BUTTON AGAIN AND AGAIN \n\nAnd, check Cloud Firestore, if there is duplicacy of documents check both the documents and then remove one of them.\nList: \n");
                textView4.setText("\nList Size: \n");
                for(int i=0; i<list.size();i++) {

                    String str = (i + 1) + " " + list.get(i).getNoteName() + "\n";
                    sb.append(str);
                    textView4.setText(sb.toString());
                }
                addToFirestore();
            }
        });

    }

    private void addToFirestore(){

//        Toast.makeText(MainActivity.this, list.size()+"", Toast.LENGTH_SHORT).show();
//        sb.append("\nList:\n");
        for(int i=0; i<list.size();i++) {
//            Toast.makeText(this, "hey", Toast.LENGTH_SHORT).show();
//            String str= (i+1)+" "+list.get(i).getNoteName()+"\n";
//            sb.append(str);
//            textView4.setText(sb.toString());

            Map<String, Object> map1 = new HashMap<>();
            map1.put("name", course.getText().toString());
            final int finalI = i;
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
                                    map3.put("name", list.get(finalI).getNoteName());
                                    firebaseFirestore.collection("courses").document(course.getText().toString()).collection("subjects").document(subject.getText().toString()).collection("notes").document(list.get(finalI).getNoteName())
                                            .set(map3).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("name", list.get(finalI).getNoteUrl());
                                                firebaseFirestore.collection("courses").document(course.getText().toString()).collection("subjects").document(subject.getText().toString()).collection("notes").document(list.get(finalI).getNoteName()).collection("unit2").document(list.get(finalI).getNoteName())
                                                        .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
//                                                            list.clear();
                                                        }else{
                                                            list.clear();
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }else{
                                                list.clear();
                                                String error = task.getException().getMessage();
                                                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    list.clear();
                                    String error = task.getException().getMessage();
                                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        list.clear();
                        String error = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }
}

//////////////
//submit.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View view) {
////                submit.setTextColor(1);
//        if(course.getText().toString().isEmpty()) {
//        course.setError("Required!");
//        return;
//        } else if (subject.getText().toString().isEmpty()) {
//        subject.setError("Required!");
//        return;
//        }
//final StringBuilder sb = new StringBuilder();
//        mStorageRef = storage.getReference().child("NotesDocument/"+course.getText().toString().trim()+"/"+subject.getText().toString().trim());
//        mStorageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
//@Override
//public void onSuccess(ListResult listResult) {
//        for (final StorageReference item : listResult.getItems()) {
//        // All the items under listRef.
//        item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//@Override
//public void onSuccess(Uri uri) {
//        noteName = item.getName().substring(0,item.getName().length()-4);
//        noteUrl= uri.toString();
//        list.add(new DataModel(noteName,noteUrl));
//        Toast.makeText(MainActivity.this, list.size()+"", Toast.LENGTH_SHORT).show();
////                                    Toast.makeText(MainActivity.this,   noteName+noteUrl, Toast.LENGTH_LONG).show();
//        }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//@Override
//public void onComplete(@NonNull Task<Uri> task) {
//        for(int i=0; i<list.size();i++) {
//
//        String str= list.get(i).getNoteName()+"______"+list.get(i).getNoteUrl();
//        sb.append(str+"---------------------------------");
//        textView2.setText(sb.toString());
//
//        Map<String, Object> map1 = new HashMap<>();
//        map1.put("name", course.getText().toString());
//final int finalI = i;
//        firebaseFirestore.collection("courses").document(course.getText().toString())
//        .set(map1).addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        if(task.isSuccessful()){
//        Map<String, Object> map2 = new HashMap<>();
//        map2.put("name", subject.getText().toString());
//
//        firebaseFirestore.collection("courses").document(course.getText().toString()).collection("subjects").document(subject.getText().toString())
//        .set(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        if(task.isSuccessful()){
//        Map<String, Object> map3 = new HashMap<>();
//        map3.put("name", list.get(finalI).getNoteName());
//        firebaseFirestore.collection("courses").document(course.getText().toString()).collection("subjects").document(subject.getText().toString()).collection("notes").document(list.get(finalI).getNoteName())
//        .set(map3).addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        if(task.isSuccessful()){
//        Map<String, Object> map = new HashMap<>();
//        map.put("name", list.get(finalI).getNoteUrl());
//        firebaseFirestore.collection("courses").document(course.getText().toString()).collection("subjects").document(subject.getText().toString()).collection("notes").document(list.get(finalI).getNoteName()).collection("unit2").document(list.get(finalI).getNoteName())
//        .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        if(task.isSuccessful()){
//        Toast.makeText(MainActivity.this, "done", Toast.LENGTH_SHORT).show();
//        }else{
//        String error = task.getException().getMessage();
//        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
//        }
//        }
//        });
//        }else{
//        String error = task.getException().getMessage();
//        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
//        }
//        }
//        });
//
//
//        }else{
//        String error = task.getException().getMessage();
//        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
//        }
//        }
//        });
//
//
//        }else{
//        String error = task.getException().getMessage();
//        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
//        }
//        }
//        });
//
//        }
//        }
//        });
//        }
//        }
//        }).addOnFailureListener(new OnFailureListener() {
//@Override
//public void onFailure(@NonNull Exception e) {
//        list.clear();
//        String error = e.getMessage();
//        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
//        }
//        });
//
//
//
//
//
//        }
//        });

//////////////

//course= findViewById(R.id.course);
//        subject= findViewById(R.id.subject);
//        noteName= findViewById(R.id.noteName);
//        noteUrl= findViewById(R.id.noteUrl);
//        submit =findViewById(R.id.textView);
//        getSupportActionBar().hide();
//
//        course.setError(null);
//        subject.setError(null);
//        noteName.setError(null);
//        noteUrl.setError(null);
//
//        submit.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View view) {
//        if(course.getText().toString().isEmpty())
//        { course.setError("Required!");
//        return;}
//        else if (subject.getText().toString().isEmpty())
//        { subject.setError("Required!");
//        return;}
//        else if (noteName.getText().toString().isEmpty())
//        { noteName.setError("Required!");
//        return;}
//        else if (noteUrl.getText().toString().isEmpty())
//        { noteUrl.setError("Required!");
//        return;}
//        Map<String, Object> map1 = new HashMap<>();
//        map1.put("name", course.getText().toString());
//        firebaseFirestore.collection("courses").document(course.getText().toString())
//        .set(map1).addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        if(task.isSuccessful()){
//        Map<String, Object> map2 = new HashMap<>();
//        map2.put("name", subject.getText().toString());
//
//        firebaseFirestore.collection("courses").document(course.getText().toString()).collection("subjects").document(subject.getText().toString())
//        .set(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        if(task.isSuccessful()){
//        Map<String, Object> map3 = new HashMap<>();
//        map3.put("name", noteName.getText().toString());
//        firebaseFirestore.collection("courses").document(course.getText().toString()).collection("subjects").document(subject.getText().toString()).collection("notes").document(noteName.getText().toString())
//        .set(map3).addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        if(task.isSuccessful()){
//        Map<String, Object> map = new HashMap<>();
//        map.put("name", noteUrl.getText().toString());
//        firebaseFirestore.collection("courses").document(course.getText().toString()).collection("subjects").document(subject.getText().toString()).collection("notes").document(noteName.getText().toString()).collection("unit2").document(noteName.getText().toString())
//        .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        if(task.isSuccessful()){
//        Toast.makeText(MainActivity.this, "done", Toast.LENGTH_SHORT).show();
//        }else{
//        String error = task.getException().getMessage();
//        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
//        }
//        }
//        });
//        }else{
//        String error = task.getException().getMessage();
//        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
//        }
//        }
//        });
//
//
//        }else{
//        String error = task.getException().getMessage();
//        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
//        }
//        }
//        });
//
//
//        }else{
//        String error = task.getException().getMessage();
//        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
//        }
//        }
//        });
//        }
//        });
//        }
//        }