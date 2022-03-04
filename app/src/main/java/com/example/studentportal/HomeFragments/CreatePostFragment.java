package com.example.studentportal.HomeFragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.studentportal.Config;
import com.example.studentportal.R;
import com.example.studentportal.modelClasses.PostModelClass;
import com.example.studentportal.modelClasses.UserPostModel;
import com.example.studentportal.utils.SpManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class CreatePostFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;
    private EditText postEditText;
    private String strPost, userId, postDateTime;
    private Button postButton;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ImageView creatorImg, postImg;
    private TextView addImg, removeImg;
    private ProgressBar progressBar;
    private Bitmap bitmap = null;
    private Uri imageUri;
    private String imageUrl = "";

    private UserPostModel postModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        creatorImg = view.findViewById(R.id.postCreatorImg);
        postImg = view.findViewById(R.id.upPostImage);
        addImg = view.findViewById(R.id.upAddImage);
        removeImg = view.findViewById(R.id.upRemoveImg);
        postEditText = view.findViewById(R.id.postSpaceId);
        postButton = view.findViewById(R.id.postButton);
        progressBar = view.findViewById(R.id.upPostProgressBar);

        postDateTime = DateFormat.getDateTimeInstance()
                .format(Calendar.getInstance().getTime());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();



        if(firebaseUser.getPhotoUrl() != null){
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(creatorImg);
        }

        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageFromGallery();
            }
        });

        removeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmap != null){
                    bitmap = null;
                    postImg.setImageBitmap(bitmap);
                    postImg.setVisibility(View.INVISIBLE);
                }
                else{
                    Toast.makeText(getActivity(), "No Image to Remove", Toast.LENGTH_SHORT).show();
                }
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                strPost = postEditText.getText().toString().trim();

                if(!strPost.isEmpty() || bitmap != null){
                    PostModelClass model = new PostModelClass(
                            strPost,
                            userId,
                            postDateTime,
                            imageUrl
                    );

                if(bitmap != null){
                    uploadImageToFirestore(bitmap,model);
                }
                else {
                    createPostModel(model);
                    databaseReference.child(Config.USER_POSTS).child(postModel.getId()).setValue(postModel);
                }

//                storageReference.child(Config.StoragePostFolder).child(userId)
//                        .child(postDateTime + ".jpeg")
//                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        imageUri = uri;
//                        imageUrl = imageUri.toString();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                });



//                    databaseReference.child("Post Data")
//                            .child(userId)
//                            .child(postDateTime).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()){
//                                postEditText.setText("");
//                                postImg.setImageBitmap(null);
//                                postImg.setVisibility(View.GONE);
//                                Toast.makeText(getActivity(), "Post Created Successfully", Toast.LENGTH_LONG).show();
//                                progressBar.setVisibility(View.GONE);
//                                startActivity(new Intent(getActivity(), HomeActivity.class));
//                            }
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
//                            progressBar.setVisibility(View.GONE);
//
//                        }
//                    });
                } else {
                    Toast.makeText(getActivity(), "Write Something or Add Image to Post!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

            }

        });

        return view;
    }

    private void createPostModel(PostModelClass model) {
        postModel = new UserPostModel();
        String id = databaseReference.child(Config.USER_POSTS).push().getKey();

        postModel.setId(id);
        postModel.setBatch(SpManager.getString(requireContext(),SpManager.PREF_BATCH));
        postModel.setUser_id(SpManager.getString(requireContext(),SpManager.PREF_USER_ID));

        postModel.setUser_profile_img(SpManager.getString(requireContext(),SpManager.PREF_USER_PROFILE_IMAGE));

        postModel.setUser_name(SpManager.getString(requireContext(),SpManager.PREF_USER_NAME));

        postModel.setPost(model.getPostText());
        postModel.setImage_url(imageUrl);
        postModel.setCreated_at(String.valueOf(System.currentTimeMillis()/1000));
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
                        imageUri = Objects.requireNonNull(data).getData();

                        int orientation = getOrientation(getActivity(), imageUri); //get orientation in degree

                        try {
                            bitmap = rotateImage(getActivity(), imageUri,orientation);
                            postImg.setVisibility(View.VISIBLE);
                            postImg.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
    );

    private void uploadImageToFirestore(Bitmap bitmap,PostModelClass model) {


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child(Config.StoragePostFolder)
                .child(userId).child(postDateTime + ".jpeg");

        reference.putBytes(byteArrayOutputStream.toByteArray())
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("img_url", "onSuccess: ");

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("img_url", "onSuccess: url: "+uri.toString());
                            String url = uri.toString();
                            imageUrl = url;

                            createPostModel(model);
                            databaseReference.child(Config.USER_POSTS).child(postModel.getId()).setValue(postModel);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d("img_url", "onFailure: "+exception.getLocalizedMessage());
                        }
                    });

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Toast.makeText(getActivity(), "Inside imageUrl assign part", Toast.LENGTH_SHORT).show();
//                Uri imgUri = uri;
//                imageUrl = imgUri.toString();
//            }
//        });
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


}
