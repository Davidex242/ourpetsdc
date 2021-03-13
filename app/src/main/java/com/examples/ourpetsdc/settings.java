package com.examples.ourpetsdc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.examples.ourpetsdc.launcher.login;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class settings extends AppCompatActivity {
    //Variaveis do layout
    private RadioButton rdbEn, rdbPt, rdbCn, rdbFr;
    private TextView txtUserName, txtEmailUser;
    private CircleImageView imgPerfil;
    private ImageView btnlogOut, btnedit, saveChanges;
    private CardView editCard;
    private EditText editName;

    //Variaveis do firebase
    private FirebaseAuth auth;
    private DatabaseReference mRef;
    private String mCurrentUser;
    private StorageTask<UploadTask.TaskSnapshot> uploadImage;
    private StorageReference mImageStorage;
    //Variaveis codigo
    private int state_edit = 1;
    private static final int GALLERY_PICK = 1;
    private String nome;
    private String thumb_image = "", image = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Link das variaveis
        rdbEn = findViewById(R.id.rdbEn);
        rdbPt = findViewById(R.id.rdbPt);
        rdbCn = findViewById(R.id.rdbCn);
        rdbFr = findViewById(R.id.rdbFr);
        txtEmailUser = findViewById(R.id.txtEmailUser);
        txtUserName = findViewById(R.id.txtUserName);
        imgPerfil = findViewById(R.id.imgPerfil);
        btnlogOut = findViewById(R.id.btnlogOut);
        editCard = findViewById(R.id.editCard);
        btnedit = findViewById(R.id.btnedit);
        saveChanges = findViewById(R.id.saveChanges);
        editName = findViewById(R.id.editName);
        //Buscar o id do utilizador
        auth = FirebaseAuth.getInstance();
        mCurrentUser = auth.getCurrentUser().getUid();

        //Referencia aos dados do utilizador
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mRef = FirebaseDatabase.getInstance().getReference().child("Utilizador").child(mCurrentUser);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nome = snapshot.child("nome").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String lingua = snapshot.child("lingua").getValue(String.class);
                String img = snapshot.child("thumb_imagem").getValue(String.class);
                txtUserName.setText(nome);
                txtEmailUser.setText(email);
                if (lingua.equals("pt")){
                    rdbPt.setChecked(true);
                    rdbPt.setText("Português");
                    rdbEn.setText("Inglês");
                    rdbCn.setText("Chinês");
                    rdbFr.setText("Francês");
                }else if(lingua.equals("en")){
                    rdbEn.setChecked(true);
                    rdbPt.setText("Portuguese");
                    rdbEn.setText("English");
                    rdbCn.setText("Chinese");
                    rdbFr.setText("French");
                }else if(lingua.equals("cn")){
                    rdbCn.setChecked(true);
                    rdbPt.setText("葡萄牙语");
                    rdbEn.setText("英语");
                    rdbCn.setText("中文");
                    rdbFr.setText("法文");

                }else if(lingua.equals("fr")){
                    rdbFr.setChecked(true);
                    rdbPt.setText("Portugais");
                    rdbEn.setText("Anglais");
                    rdbCn.setText("Chinois");
                    rdbFr.setText("Français");
                }
                if (img.equals("")){
                    //Se o img = "" , o codigo mete uma default
                    Glide.with(getApplicationContext()).load(R.drawable.ic_profile_user).into(imgPerfil);
                }else{
                    //Se o img != "" , o codigo faz download da img guardada no firebasestorage
                    Glide.with(getApplicationContext()).load(img).into(imgPerfil);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editName.setText(nome);
        //Guardar alteraçao do nome
        saveChanges.setOnClickListener(v -> {
            String novoNome = editName.getText().toString().trim();
            mRef.child("nome").setValue(novoNome);
            state_edit = 1;
            Glide.with(getApplicationContext()).load(R.drawable.ic_edit).into(btnedit);
            editCard.setVisibility(View.GONE);
        });
        //Funcao para alterar idioma
        alterarIdioma();

        //Funcao para acabar sessão
        btnlogOut.setOnClickListener(v-> {
            logout();
        });

        //Selecionar foto de perfir
        imgPerfil.setOnClickListener(v ->{

            abrirGaleria();

        });
        btnedit.setOnClickListener(v -> {
            //Imagem varia dependendo do valor na String State Edit
            if (state_edit == 1){
                state_edit = 2;
                Glide.with(getApplicationContext()).load(R.drawable.ic_cancel).into(btnedit);
                editCard.setVisibility(View.VISIBLE);
            }else{
                state_edit = 1;
                Glide.with(getApplicationContext()).load(R.drawable.ic_edit).into(btnedit);
                editCard.setVisibility(View.GONE);
            }


        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        editCard.setVisibility(View.GONE);
    }

    private void abrirGaleria() {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
    }

    private void logout() {
            auth.signOut();
            //Apos startActivity(intent), vai passar para outra actividade
            Intent intent = new Intent(settings.this, login.class);
            startActivity(intent);
    }

    private void alterarIdioma() {
        rdbPt.setOnClickListener(v -> {
            mRef.child("lingua").setValue("pt");
            rdbPt.setText("Português");
            rdbEn.setText("Inglês");
            rdbCn.setText("Chinês");
            rdbFr.setText("Francês");

        });
        rdbEn.setOnClickListener(v -> {
            mRef.child("lingua").setValue("en");
            rdbPt.setText("Portuguese");
            rdbEn.setText("English");
            rdbCn.setText("Chinese");
            rdbFr.setText("French");

        });
        rdbCn.setOnClickListener(v -> {
            mRef.child("lingua").setValue("cn");
            rdbPt.setText("葡萄牙语");
            rdbEn.setText("英语");
            rdbCn.setText("中文");
            rdbFr.setText("法文");

        });
        rdbFr.setOnClickListener(v -> {
            mRef.child("lingua").setValue("fr");
            rdbPt.setText("Portugais");
            rdbEn.setText("Anglais");
            rdbCn.setText("Chinois");
            rdbFr.setText("Français");

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {



                final Uri resultUri = result.getUri();

                final File thumb_filePath = new File(resultUri.getPath());

                //final Bitmap thumb_bitmap = new Compressor(this).setMaxWidth(200).setMaxHeight(200).setQuality(75).compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                //thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();
                final StorageReference filepath = mImageStorage.child("User_Images").child(mCurrentUser + ".jpg");
                //final StorageReference thump_filepath = mImageStorage.child("Forum_Images").child("Thumbs_Forum_Images").child(random() + "jpg");
                uploadImage = filepath.putFile(resultUri);

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
                            String mUri = downloadUri.toString();
                            HashMap update_hashmap = new HashMap<>();
                            update_hashmap.put("imagem", mUri);
                            update_hashmap.put("thumb_imagem", mUri);
                            FirebaseDatabase.getInstance().getReference("Utilizador").child(mCurrentUser).updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Glide.with(getBaseContext()).load(mUri).into(imgPerfil);
                                    }

                                }
                            });



                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}