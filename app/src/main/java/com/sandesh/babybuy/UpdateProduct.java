package com.sandesh.babybuy;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateProduct extends AppCompatActivity {

    ImageView updateImage;
    Button updateButton,prdlocation;
    EditText updateDesc, updateTitle, updatePrice;
    String title, desc, price;
    String imageUrl;
    TextView locationTxt;
    String key, oldImageURL;
    Uri uri;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String lat,lang,address;
    Boolean isPurchased= false;
    CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        updateButton = findViewById(R.id.updateButton);
        prdlocation = findViewById(R.id.prdlocation);
        updateDesc = findViewById(R.id.updateDesc);
        updateImage = findViewById(R.id.updateImage);
        updatePrice = findViewById(R.id.updatePrice);
        updateTitle = findViewById(R.id.updateTitle);
        locationTxt = findViewById(R.id.locationTxt);
        checkBox = findViewById(R.id.checkBox);
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            updateImage.setImageURI(uri);
                        } else {
                            Toast.makeText(UpdateProduct.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        Bundle bundle = getIntent().getExtras();


       // uri = Uri.parse(bundle.getString("Image"));
        if (bundle != null){
            Glide.with(UpdateProduct.this).load(bundle.getString("Image")).into(updateImage);
            updateTitle.setText(bundle.getString("Title"));
            updateDesc.setText(bundle.getString("Description"));
            updatePrice.setText(bundle.getString("Price"));
            locationTxt.setText(bundle.getString("address"));
            key = bundle.getString("Key");
            lat=bundle.getString("lat");
            lang = bundle.getString("lang");
            oldImageURL = bundle.getString("Image");
            isPurchased = bundle.getBoolean("isPurchased");
        }
        if(isPurchased){
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("BabyBuy").child(key);
        address = bundle.getString("address");

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked()){isPurchased=true;
                }else{
                    isPurchased=false;
                }
            }
        });

        prdlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateProduct.this, MapActivity.class);
                Double latitude, longitude;

                if (bundle != null) {
                    Bundle params = new Bundle();
                    latitude = Double.valueOf(lat);
                    longitude = Double.valueOf(lang);
                    params.putDouble("latitude", latitude);
                    params.putDouble("longitude", longitude);
                    params.putString("page", "updatePage");
                    intent.putExtras(params);
                }
                startActivityForResult(intent,1010);
            }
        });



        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                Intent intent = new Intent(UpdateProduct.this,DashActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1010&& resultCode == 201){
            locationTxt.setText(data.getStringExtra("address"));
            lat = data.getStringExtra("latitude");
            lang = data.getStringExtra("longitude");
            address = data.getStringExtra("address");
        }
    }
    public void saveData(){
        storageReference = FirebaseStorage.getInstance().getReference().child("Images").child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProduct.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageUrl = urlImage.toString();
                updateData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }
    public void updateData(){
        title = updateTitle.getText().toString().trim();
        desc = updateDesc.getText().toString().trim();
        price = updatePrice.getText().toString();
        String userId = FirebaseAuth.getInstance().getUid();
        DataClass dataClass = new DataClass(userId,title, desc, price, imageUrl,lat,lang,address,isPurchased);

        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                    reference.delete();
                    Toast.makeText(UpdateProduct.this,"Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProduct.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}