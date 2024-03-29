package com.example.shareer.ImageHandlerPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shareer.ListofPdf;
import com.example.shareer.MainActivity;
import com.example.shareer.R;
import com.example.shareer.User.ModelUser;
import com.example.shareer.User.UsersList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class ImageList extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private DatabaseReference databaseReference;
    private List<ImageUploadHandler> mUpload;
    List<ModelUser> usersList;
    EditText folderKey;
    Button searchImage;
    ImageView imageView;
    String userId="";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        getSupportActionBar().setTitle("Your Images");

        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        userId=getIntent().getStringExtra("imageUid");
        folderKey=findViewById(R.id.folderName);
        searchImage=findViewById(R.id.searchImage);

//        sharedPreferences=getSharedPreferences("MyImage", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor=sharedPreferences.edit();
//        editor.putString("imageId", userId);
//          editor.commit();
        userId=getIntent().getStringExtra("userUid");


        mRecyclerView=findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ImageList.this));
        mRecyclerView.setHasFixedSize(true);
        mUpload=new ArrayList<>();
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.clearDisappearingChildren();
                String folder_Name=folderKey.getText().toString().trim();
                databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Multiple").child(folder_Name).child("Images");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getChildren()!=null)
                        {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                            {
                                ImageUploadHandler upload=postSnapshot.getValue(ImageUploadHandler.class);
                                mUpload.add(upload);
                            }
                            mAdapter=new ImageAdapter(ImageList.this,mUpload);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        else
                        {
                            Toast.makeText(ImageList.this, "Please check your key", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ImageList.this, "Error:"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuLogout:
                mAuth.signOut();
                startActivity(new Intent(ImageList.this, MainActivity.class));
                finish();
                break;
            case R.id.viewpdfs:
                Intent intent=new Intent(ImageList.this, ListofPdf.class);
                startActivity(intent);

        }
        return true;
    }


}