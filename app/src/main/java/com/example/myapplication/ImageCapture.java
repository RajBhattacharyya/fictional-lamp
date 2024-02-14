package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCapture extends AppCompatActivity {
    public static final int REQUEST_CODE = 101;
    private ActivityResultLauncher<Intent> launcher;

    StorageReference storagereference;

    TextInputEditText editTextName;
    ImageView selectedImage;
    Button captureButton, galleryButton, submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_capture);
        selectedImage = findViewById(R.id.imageView);
        captureButton = findViewById(R.id.button2);
        galleryButton = findViewById(R.id.button3);
        editTextName = findViewById(R.id.editTextName);
        submitButton = findViewById(R.id.submit_button);
        storagereference = FirebaseStorage.getInstance().getReference();

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ImageCapture.this, "Gallery Button is Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageToFirebase();
            }
        });

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Bundle extras = data.getExtras();
                            if (extras != null) {
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                selectedImage.setImageBitmap(imageBitmap);
                                saveToFile(imageBitmap);
                            } else {
                                Toast.makeText(ImageCapture.this, "Failed to get image data", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ImageCapture.this, "Image capture failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        launcher.launch(intent);
    }

    private void saveToFile(Bitmap bitmap) {

    }

    private void uploadImageToFirebase() {
        String imageName = editTextName.getText().toString().trim();
        if (TextUtils.isEmpty(imageName)) {
            Toast.makeText(this, "Please enter a name for the image", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap imageBitmap = ((BitmapDrawable) selectedImage.getDrawable()).getBitmap();
        File imageFile = new File(getFilesDir(), imageName + ".jpg");
        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Uri uri = Uri.fromFile(imageFile);
            uploadToFirebase(imageFile.getName(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadToFirebase(String name, Uri uri) {
        StorageReference image = storagereference.child("users/" + name);
        image.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "Success: Uploaded Image url is" + uri.toString());
                    }
                });
                Toast.makeText(ImageCapture.this, "Image is uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ImageCapture.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFromFile() {
        File imageFile = new File(getFilesDir(), "captured_image.jpg");
        if (imageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            selectedImage.setImageBitmap(bitmap);
        }
    }
}
