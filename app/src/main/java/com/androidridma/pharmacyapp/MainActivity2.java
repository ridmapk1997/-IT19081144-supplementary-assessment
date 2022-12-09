package com.androidridma.pharmacyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;


public class MainActivity2 extends AppCompatActivity {

    Button ch,up;
    ImageView img;
    EditText txtname,txtbrand,txtprice;
    Datas datas;
    StorageReference mStorageRef;
    DatabaseReference dbreff;
    private StorageTask uploadTask;
    public Uri imguri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mStorageRef= FirebaseStorage.getInstance().getReference("Images");
        dbreff= FirebaseDatabase.getInstance().getReference().child("Datas");

        ch=(Button)findViewById(R.id.btnchoose);
        up=(Button)findViewById(R.id.btnupload);
        img=(ImageView)findViewById(R.id.imgview);
        txtname=(EditText)findViewById(R.id.txtname);
        txtbrand=(EditText)findViewById(R.id.txtbrand);
        txtprice=(EditText)findViewById(R.id.txtprice);
        datas=new Datas();

        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Filechooser();
            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadTask!=null && uploadTask.isInProgress()){
                    Toast.makeText(MainActivity2.this,"Upload inprogress",Toast.LENGTH_LONG).show();

                }else {
                    Fileuploader();
                }
            }
        });
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void Fileuploader()
    {
        String imageid;
        imageid=System.currentTimeMillis()+"."+getExtension(imguri);
        datas.setName(txtname.getText().toString().trim());
        datas.setBrand(txtbrand.getText().toString().trim());
        datas.setImageid(imageid);
        int p=Integer.parseInt(txtprice.getText().toString().trim());
        datas.setPrice(p);
        dbreff.push().setValue(datas);

        StorageReference Ref=mStorageRef.child(imageid);

        uploadTask=Ref.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(MainActivity2.this,"Image Uploaded successfully",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });

    }

    private void Filechooser()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
              imguri=data.getData();
              img.setImageURI(imguri);
        }
    }
}