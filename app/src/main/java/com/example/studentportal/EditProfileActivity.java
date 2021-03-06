package com.example.studentportal;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.studentportal.utils.Config;
import com.example.studentportal.utils.SpManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private EditText ed_name, ed_batch, ed_email, ed_phone, ed_blood, ed_occupation;
    private RadioGroup ed_gender;
    private RadioButton ed_male, ed_female;
    private ImageView ed_image;
    private Button ed_update;
    private ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    Bitmap bitmap;
    private String g = "";

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle("Profile Update");

        ed_image = findViewById(R.id.edit_profile_image);
        ed_name = findViewById(R.id.edit_profile_name);
        ed_batch = findViewById(R.id.edit_batch);
        ed_email = findViewById(R.id.edit_email);
        ed_phone = findViewById(R.id.edit_phone);
        ed_blood = findViewById(R.id.edit_blood_group);
        ed_occupation = findViewById(R.id.edit_occupation);
        ed_gender = findViewById(R.id.edit_gender);
        ed_male = findViewById(R.id.radio_male);
        ed_female = findViewById(R.id.radio_female);
        ed_update = findViewById(R.id.edit_profile_btn);
        progressBar = findViewById(R.id.edit_progressbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference(Config.fireFolder);

        String userId = firebaseUser.getUid();

        if(firebaseUser.getPhotoUrl() != null){
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(ed_image);
        }

        firestore.collection(Config.fireFolder).document(userId)
                .addSnapshotListener(this, (documentSnapshot,e)->{
                    int b = documentSnapshot.getLong(Config.fireBatch).intValue();
                    ed_name.setHint(documentSnapshot.getString(Config.fireName));
                    ed_batch.setHint(String.valueOf(b));
                    ed_email.setHint(documentSnapshot.getString(Config.fireMail));
                    ed_phone.setHint(documentSnapshot.getString(Config.firePhone));
                    ed_blood.setHint(documentSnapshot.getString(Config.fireBlood));
                    ed_occupation.setHint(documentSnapshot.getString(Config.fireOccupation));

                    if(documentSnapshot.getString(Config.fireGender).equals("Male")){
                        ed_male.setChecked(true);
                        g = "male";
                    }
                    else if(documentSnapshot.getString(Config.fireGender).equals("Female")){
                        ed_female.setChecked(true);
                        g = "female";
                    }

                });
        pickImage();

        ed_update.setOnClickListener(new View.OnClickListener() {

            String gender;

            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                ed_update.setEnabled(false);

                String name = ed_name.getText().toString().trim();
                String batch = ed_batch.getText().toString().trim();
                String phone = ed_phone.getText().toString().trim();
                String email = ed_email.getText().toString().trim();
                String blood = ed_blood.getText().toString().trim();
                String occupation = ed_occupation.getText().toString().trim();

                String userId = firebaseUser.getUid();

                if (!name.isEmpty()) {
                    firestore.collection(Config.fireFolder).document(userId).update(Config.fireName, name);
                }

                if (!batch.isEmpty()) {
                    int intBatch = Integer.parseInt(batch);
                    firestore.collection(Config.fireFolder).document(userId).update(Config.fireBatch, intBatch);
                }
                if (!email.isEmpty()) {
                    firestore.collection(Config.fireFolder).document(userId).update(Config.fireMail, email);
                }

                if (!phone.isEmpty()) {
                    firestore.collection(Config.fireFolder).document(userId).update(Config.firePhone, phone);
                }
                if (!blood.isEmpty()) {
                    firestore.collection(Config.fireFolder).document(userId).update(Config.fireBlood, blood);
                }
                if (!occupation.isEmpty()) {
                    firestore.collection(Config.fireFolder).document(userId).update(Config.fireOccupation, occupation);
                }

                if (isGenderSelected()) {
                    firestore.collection(Config.fireFolder).document(userId).update(Config.fireGender, gender);
                }

                if(bitmap != null){
                    handleUpload(bitmap);

                } else{
                    firebaseUser.reload();

                    if(!name.isEmpty() || !batch.isEmpty() || !email.isEmpty() || isGenderSelected()
                        || !phone.isEmpty() || !blood.isEmpty() || !occupation.isEmpty()){
                        Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(EditProfileActivity.this, "No Changes Found!", Toast.LENGTH_SHORT).show();
                    }

                    startActivity(new Intent(EditProfileActivity.this,ProfileActivity.class));
                    finish();
                }

            }

            public boolean isGenderSelected() {
                if (ed_gender.getCheckedRadioButtonId() == -1) {
                    return false;
                } else {
                    if (ed_gender.getCheckedRadioButtonId() == R.id.radio_male && g.equals("female")){
                        gender = "Male";
                        return true;
                    }
                    else if (ed_gender.getCheckedRadioButtonId() == R.id.radio_female && g.equals("male")){
                        gender = "Female";
                        return true;
                    }
                    else if(ed_gender.getCheckedRadioButtonId() == R.id.radio_female && g.equals("")){
                        gender = "Female";
                        return true;
                    }
                    else if(ed_gender.getCheckedRadioButtonId() == R.id.radio_male && g.equals("")){
                        gender = "Male";
                        return true;
                    }
                    else return false;
                }
            }

        });

    }

    private void pickImage() {
        ed_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chooseImageFromGallery();
            }
        });
    }

    private void chooseImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult.launch(intent);

    }

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {

                        Intent data = result.getData();
                        Uri imageUri = Objects.requireNonNull(data).getData();

                        int orientation = getOrientation(EditProfileActivity.this, imageUri); //get orientation in degree

                        try {
                            bitmap = rotateImage(EditProfileActivity.this, imageUri,orientation);
                            ed_image.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
    );

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child(Config.StorageProfileFolder).child(userId + ".jpeg");

        reference.putBytes(byteArrayOutputStream.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         getDownloadUrl(reference);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setUserProfileUri(uri);
                String profile_img_url = uri.toString();
                firestore.collection(Config.fireFolder).document(firebaseUser.getUid()).update(Config.fireProfileImageUrl, profile_img_url);
                SpManager.saveString(EditProfileActivity.this, SpManager.PREF_USER_PROFILE_IMAGE, profile_img_url);
            }
        });
    }

    private void setUserProfileUri(Uri uri) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        firebaseUser.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                        firebaseUser.reload();
                        startActivity(new Intent(EditProfileActivity.this,ProfileActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Profile Picture Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Bitmap rotateImage(Context context, Uri uri, int orientation) throws IOException {

        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        return bitmap;
    }

    public static int getOrientation(Context context, Uri uri) {

        int rotate = 0;

        try {

            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");

            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

            FileInputStream input = new FileInputStream(fileDescriptor);

            File tempFile = File.createTempFile("exif", "tmp");

            FileOutputStream output = new FileOutputStream(tempFile.getPath());

            int read;

            byte[] bytes = new byte[4096];

            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }

            input.close();
            output.close();

            ExifInterface exif = new ExifInterface(tempFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

        } catch (Exception e) {
            Log.d("TAG", e.getLocalizedMessage());
        }

        return rotate;
    }
    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.reset_login_info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent i;
        if(item.getItemId() == R.id.menu_reset_mail){
            i = new Intent(this, ResetLoginEmailActivity.class);
            startActivity(i);
        }
        if(item.getItemId() == R.id.menu_reset_pass){
            firebaseAuth.sendPasswordResetEmail(firebaseUser.getEmail()).addOnSuccessListener(unused -> {
                Toast.makeText(EditProfileActivity.this,
                        "A Link is sent to your Email to Reset the Password", Toast.LENGTH_SHORT).show();

            }).addOnFailureListener(e -> {
                Toast.makeText(EditProfileActivity.this,
                        "Reset Link Can't be Sent due to "
                        + e.getMessage(), Toast.LENGTH_SHORT).show();

            });
        }
        return super.onOptionsItemSelected(item);
    }


}