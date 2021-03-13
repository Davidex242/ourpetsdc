package com.examples.ourpetsdc.forum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.examples.ourpetsdc.Firebase.Post;
import com.examples.ourpetsdc.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class forum extends AppCompatActivity {
    private static final int PReqCode = 2 ;
    private static final int REQUESCODE = 1 ;
    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    String userId;
    Dialog popAddPost ;
    ImageView popupUserImage,popupPostImage,popupAddBtn;
    TextView popupTitle,popupDescription;
    ProgressBar popupClickProgress;
    private Uri pickedImgUri = null;
    String picture;
    String userPhoto;
    private RecyclerView listPosts;
    FirebaseRecyclerAdapter<Post, forum.List_Posts> all_post;
    //String para gerar valor aleatorio para chave de cod_pub
    private String key = UUID.randomUUID().toString();
    private String key_photo = UUID.randomUUID().toString();
    private StorageTask<UploadTask.TaskSnapshot> uploadImage;

    //Verificar a ligação a internet
    private static final String LOG_TAG = "CheckNetworkStatus";
    private NetworkChangeReceiver receiver;
    private boolean isConnected = false;
    private ConstraintLayout connectedNetwork;
    private ImageView NoConnectedNetwork;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        //Verificar a conexao a internet
        NoConnectedNetwork = findViewById(R.id.NoConnectedNetwork);
        connectedNetwork = findViewById(R.id.connectedNetwork);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);


        listPosts = findViewById(R.id.listPosts);
        listPosts.setHasFixedSize(true);
        listPosts.setLayoutManager(new LinearLayoutManager(forum.this));
        // ini

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();

        // ini popup
        iniPopup();
        setupPopupImageClick();
        FloatingActionButton addPost = (FloatingActionButton) findViewById(R.id.addPost);
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });


    }
    private void setupPopupImageClick() {


        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // here when image clicked we need to open the gallery
                // before we open the gallery we need to check if our app have the access to user files
                // we did this before in register activity I'm just going to copy the code to save time ...

                checkAndRequestForPermission();


            }
        });



    }


    private void checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(forum.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(forum.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(forum.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();

            }

            else
            {
                ActivityCompat.requestPermissions(forum.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }
        else
            // everything goes well : we have permission to access user gallery
            openGallery();

    }





    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }



    // when user picked an image ...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            popupPostImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            popupPostImage.setImageURI(pickedImgUri);

        }


    }






    private void iniPopup() {

        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        // ini popup widgets
        popupUserImage = popAddPost.findViewById(R.id.popup_user_image);
        popupPostImage = popAddPost.findViewById(R.id.popup_img);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        popupClickProgress = popAddPost.findViewById(R.id.popup_progressBar);

        // load Current user profile photo
        FirebaseDatabase.getInstance().getReference().child("Utilizador").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String lingua = snapshot.child("lingua").getValue(String.class);

                        if(lingua.equals("en")){
                            popupTitle.setHint("Title");
                            popupDescription.setHint("Description");
                        }if(lingua.equals("pt")){
                            popupTitle.setHint("Titulo");
                            popupDescription.setHint("Descrição");
                        }
                        if(lingua.equals("cn")){
                            popupTitle.setHint("标题");
                            popupDescription.setHint("描述");
                        }
                        if(lingua.equals("fr")){
                            popupTitle.setHint("Titre");
                            popupDescription.setHint("La description");
                        }

                String image = snapshot.child("imagem").getValue(String.class);
                Glide.with(getApplicationContext()).load(image).into(popupUserImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        // Add post click Listener

        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                // we need to test all input fields (Title and description ) and post image

                if (!popupTitle.getText().toString().isEmpty()
                        && !popupDescription.getText().toString().isEmpty()
                        && pickedImgUri != null ) {
                    final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("blog_images").child(key_photo + ".jpg");
                    //final StorageReference thump_filepath = mImageStorage.child("Forum_Images").child("Thumbs_Forum_Images").child(random() + "jpg");
                    uploadImage = filepath.putFile(pickedImgUri);

                    uploadImage.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull final Task<Uri> task) {
                            if (task.isSuccessful()) {
                                final Uri downloadUri = task.getResult();
                                picture = downloadUri.toString();
                                addPost();



                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });
                    //everything is okey no empty or null value
                    // TODO Create Post Object and add it to firebase database
                    // first we need to upload post Image
                    // access firebase storage
                    /*StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                    final StorageReference imageFilePath = storageReference.child(key_photo+ "jpg");
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    picture = uri.toString();
                                    // Add post to firebase database

                                    addPost();



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // something goes wrong uploading picture

                                    showMessage(e.getMessage());
                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                    popupAddBtn.setVisibility(View.VISIBLE);



                                }
                            });


                        }
                    });*/








                }
                else {
                    showMessage("Please verify all input fields and choose Post Image") ;
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);

                }



            }
        });



    }

    private void addPost() {
        long timestamp = 0;
        String title = popupTitle.getText().toString();
        String description = popupDescription.getText().toString();
        FirebaseDatabase.getInstance().getReference().child("Utilizador").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userPhoto = snapshot.child("imagem").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String data = sdf.format(new Date());
        Post info_pub = new Post(title, description, picture, userId, userPhoto, timestamp, data);
        FirebaseDatabase.getInstance().getReference().child("Pubs").child(key).setValue(info_pub);

        // add post data to firebase database

        FirebaseDatabase.getInstance().getReference().child("Pubs").child(key).child("timestamp").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference().child("Utilizador").child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String lingua = snapshot.child("lingua").getValue(String.class);

                        if(lingua.equals("en")){
                            showMessage("Published successfully");
                            popupPostImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            popupPostImage.setImageResource(R.drawable.ic_gallery);
                            popupTitle.setText("");
                            popupTitle.setHint("Title");
                            popupDescription.setText("");
                            popupDescription.setHint("Description");
                            popupClickProgress.setVisibility(View.INVISIBLE);
                            popupAddBtn.setVisibility(View.VISIBLE);
                            picture = "";
                            pickedImgUri.equals("");
                            popAddPost.dismiss();
                        }if(lingua.equals("pt")){
                            showMessage("Publicado com sucesso");
                            popupPostImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            popupPostImage.setImageResource(R.drawable.ic_gallery);
                            popupTitle.setText("");
                            popupTitle.setHint("Titulo");
                            popupDescription.setText("");
                            popupDescription.setHint("Descrição");
                            popupClickProgress.setVisibility(View.INVISIBLE);
                            popupAddBtn.setVisibility(View.VISIBLE);
                            picture = "";
                            pickedImgUri.equals("");
                            popAddPost.dismiss();
                        }
                        if(lingua.equals("cn")){
                            showMessage("成功发布");
                            popupPostImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            popupPostImage.setImageResource(R.drawable.ic_gallery);
                            popupTitle.setText("");
                            popupTitle.setHint("标题");
                            popupDescription.setText("");
                            popupDescription.setHint("描述");
                            popupClickProgress.setVisibility(View.INVISIBLE);
                            popupAddBtn.setVisibility(View.VISIBLE);
                            picture = "";
                            pickedImgUri.equals("");
                            popAddPost.dismiss();
                        }
                        if(lingua.equals("fr")){
                            showMessage("Publié avec succès");
                            popupPostImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            popupPostImage.setImageResource(R.drawable.ic_gallery);
                            popupTitle.setText("");
                            popupTitle.setHint("Titre");
                            popupDescription.setText("");
                            popupDescription.setHint("La description");
                            popupClickProgress.setVisibility(View.INVISIBLE);
                            popupAddBtn.setVisibility(View.VISIBLE);
                            picture = "";
                            pickedImgUri.equals("");
                            popAddPost.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });





    }


    private void showMessage(String message) {

        Toast.makeText(forum.this,message,Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        final Query allData = FirebaseDatabase.getInstance().getReference().child("Pubs").orderByChild("timestamp");

            FirebaseRecyclerOptions<Post> options =
                    new FirebaseRecyclerOptions.Builder<Post>()
                            .setQuery(allData, Post.class)
                            .setLifecycleOwner(this)
                            .build();

            all_post = new FirebaseRecyclerAdapter<Post, forum.List_Posts>(options) {
                @NonNull
                @Override
                public forum.List_Posts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    return new forum.List_Posts(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.post_item, parent, false));
                }
                @Override
                protected void onBindViewHolder(@NonNull final forum.List_Posts holder, int i, @NonNull final Post model) {
                    final String key = getRef(i).getKey();
                    FirebaseDatabase.getInstance().getReference().child("Pubs").child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String title = snapshot.child("title").getValue(String.class);
                            String picture = snapshot.child("picture").getValue(String.class);
                            String mCurrentUser = snapshot.child("userId").getValue(String.class);
                            //final long timestamp = snapshot.child("timestamp").getValue(long.class);
                            Glide.with(getApplicationContext()).load(picture).into(holder.row_post_img);
                            holder.setRowTitle(title);
                            //holder.setNome(mCurrentUser);
                            holder.getAdapterPosition();
                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent openPub = new Intent(forum.this, detailed_post.class);
                                    openPub.putExtra("pub_id", key);
                                    openPub.putExtra("value", "pub");
                                    startActivity(openPub);
                                }
                            });

                            FirebaseDatabase.getInstance().getReference().child("Utilizador").child(mCurrentUser).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String nome = snapshot.child("nome").getValue(String.class);
                                    String thumb_imagem = snapshot.child("thumb_imagem").getValue(String.class);
                                    Glide.with(getApplicationContext()).load(thumb_imagem).into(holder.row_post_profile_img);
                                    //holder.setNome(nome);
                                    String lingua = snapshot.child("lingua").getValue(String.class);
                                    /*if (lingua.equals("pt")){
                                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                                        String timeago = getTimeAgo.getTimeAgoPt(timestamp, lost_pets.this);
                                        holder.setTimeAgo(timeago);
                                    }else if(lingua.equals("en")){
                                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                                        String timeago = getTimeAgo.getTimeAgo(timestamp, lost_pets.this);
                                        holder.setTimeAgo(timeago);
                                    }else if(lingua.equals("fr")){
                                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                                        String timeago = getTimeAgo.getTimeAgoFr(timestamp, lost_pets.this);
                                        holder.setTimeAgo(timeago);
                                    }else if(lingua.equals("cn")){
                                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                                        String timeago = getTimeAgo.getTimeAgoCn(timestamp, lost_pets.this);
                                        holder.setTimeAgo(timeago);
                                    }*/
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            };
        listPosts.setAdapter(all_post);
    }

    public class List_Posts extends RecyclerView.ViewHolder {

        View mView;
        ImageView row_post_img;
        CircleImageView row_post_profile_img;
        TextView row_post_title;

        // RecyclerView rc_comments;
        public List_Posts(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            row_post_img = mView.findViewById(R.id.row_post_img);
            row_post_profile_img = mView.findViewById(R.id.row_post_profile_img);
            row_post_title = mView.findViewById(R.id.row_post_title);

        }
        public void setRowTitle(String title){

            TextView post_title = mView.findViewById(R.id.row_post_title);
            post_title.setText(title);

        }
    }

    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG, "onDestory");
        super.onDestroy();

        unregisterReceiver(receiver);

    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            Log.v(LOG_TAG, "Receieved notification about network status");
            isNetworkAvailable(context);

        }


        private boolean isNetworkAvailable(final Context context) {
            ConnectivityManager connectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            if (!isConnected) {
                                Log.v(LOG_TAG, "Now you are connected to Internet!");
                                connectedNetwork.setVisibility(View.VISIBLE);
                                NoConnectedNetwork.setVisibility(View.GONE);
                                isConnected = true;
                                return true;
                            }
                        }
                    }
                }
            }
            Log.v(LOG_TAG, "You are not connected to Internet!");
            isConnected = false;
            connectedNetwork.setVisibility(View.GONE);
            NoConnectedNetwork.setVisibility(View.VISIBLE);
            return false;
        }
    }

}