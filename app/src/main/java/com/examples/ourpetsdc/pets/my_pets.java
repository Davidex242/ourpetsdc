package com.examples.ourpetsdc.pets;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examples.ourpetsdc.Firebase.Info_Animal;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class my_pets extends AppCompatActivity {
    //Variaveis dos objetos do layout
    private RecyclerView rc_pets;

    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    DatabaseReference mUserRef;
    String userId;

    //Variaveis do layout popUpAnimal
    Dialog popAddPet ;
    ImageView popup_img, popup_add;
    ProgressBar popupClickProgress;
    private Uri pickedImgUri = null;
    String imagem;
    EditText txtNomePet, txtDataDia, txtDataMes, txtDataAno, txtNumChip;
    AutoCompleteTextView Raca_Animal;
    Spinner Tipo_Pelagem_Spinner, Tipo_Formato_Pelagem_Spinner, Tipo_Cauda_Spinner, Sexo_Animal_Spinner;
    RadioButton btnGato, btnCao;
    String userPhoto;
    private static final int PReqCode = 2 ;
    private static final int REQUESCODE = 1 ;
    private StorageTask<UploadTask.TaskSnapshot> uploadImage;
    //String tipo animal
    private String tipoAnimal;
    //Arrays com as raças de cães e gatos
    private String[]racas_cao_pt;
    private String[]racas_gatos_pt;
    private ArrayAdapter<String> adapter_raca_pt;
    String key_photo = UUID.randomUUID().toString();

    //Data Nascimento
    String dia, mes, ano;

    //Dados registo
    String raca, chip, pelagem, formato_pelo, cauda, sexo;
    final String key = UUID.randomUUID().toString();

    private FirebaseRecyclerAdapter<Info_Animal, List_Pets> meus_animais;
    //Strings para ter o valor das traduções
    String NP; //Nome pet
    String DN; //Data Nascimento
    String UF; //UploadFile
    String PN; //Pick name
    String PDN; //PICK date of birth
    String PIMG; //Pick img
    String registar_animal;
    String message;

    //Verificar a ligação a internet
    private static final String LOG_TAG = "CheckNetworkStatus";
    private NetworkChangeReceiver receiver;
    private boolean isConnected = false;
    private ConstraintLayout connectedNetwork;
    private ImageView NoConnectedNetwork;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pets);
        //Verificar a conexao a internet
        NoConnectedNetwork = findViewById(R.id.NoConnectedNetwork);
        connectedNetwork = findViewById(R.id.connectedNetwork);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);


        rc_pets = findViewById(R.id.rc_pets);
        rc_pets.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        rc_pets.setLayoutManager(mLayoutManager);
        //rc_pets.setLayoutManager(new LinearLayoutManager(my_pets.this));
        // ini

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Utilizador").child(userId);
        iniPopup();
        setupPopupImageClick();
        FloatingActionButton addAnimal = (FloatingActionButton) findViewById(R.id.addAnimal);
        addAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPet.show();
            }
        });
        //Verificar o idioma escolhido pelo utilizador
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lingua = dataSnapshot.child("lingua").getValue().toString().trim();

                if (lingua.equals("en")){
                    //SPINNERS
                    ArrayAdapter<CharSequence> adapter_Pelagem = ArrayAdapter.createFromResource(getApplicationContext(), R.array.pelagem_array_en, android.R.layout.simple_spinner_item);
                    adapter_Pelagem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Tipo_Pelagem_Spinner.setAdapter(adapter_Pelagem);

                    //Spinner TIPO FORMATO PELAGEM
                    ArrayAdapter<CharSequence> adapter_Formato_Pelagem = ArrayAdapter.createFromResource(getApplicationContext(), R.array.tipo_pelagem_array_en, android.R.layout.simple_spinner_item);
                    adapter_Formato_Pelagem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Tipo_Formato_Pelagem_Spinner.setAdapter(adapter_Formato_Pelagem);

                    //Spinner TIPO CAUDA
                    ArrayAdapter<CharSequence> Formato_Cauda = ArrayAdapter.createFromResource(getApplicationContext(), R.array.tipo_cauda_array_en, android.R.layout.simple_spinner_item);
                    Formato_Cauda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Tipo_Cauda_Spinner.setAdapter(Formato_Cauda);


                    //Spinner TIPO Sexo
                    ArrayAdapter<CharSequence> Sexo_Animal = ArrayAdapter.createFromResource(getApplicationContext(), R.array.gender_en, android.R.layout.simple_spinner_item);
                    Sexo_Animal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Sexo_Animal_Spinner.setAdapter(Sexo_Animal);

                    //TRADUÇÃO
                    NP = "Pet Name";
                    DN = "Date of birth";
                    UF = "Upload File";
                    PN = "Choice a name ";
                    PDN = "Pick a date of birth";
                    PIMG = "Pick an image";
                    registar_animal = "Adding new animal";
                    txtNomePet.setHint(NP);
                    //txtDataNascPet.setHint("dd/mm/YYYY");
                    //txtUploadFile.setText(UF);
                    //btnRegistPet.setText("REGISTER");
                    message = "Please verify all input fields!";
                    btnCao.setText("Dog");
                    btnGato.setText("Cat");
                    txtNumChip.setHint("Chip number");
                    Raca_Animal.setHint("Breed");
                }else if (lingua.equals("pt")){
                    ArrayAdapter<CharSequence> adapter_Pelagem = ArrayAdapter.createFromResource(getApplicationContext(), R.array.pelagem_array_pt, android.R.layout.simple_spinner_dropdown_item);
                    adapter_Pelagem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Tipo_Pelagem_Spinner.setAdapter(adapter_Pelagem);

                    //Spinner TIPO FORMATO PELAGEM
                    ArrayAdapter<CharSequence> adapter_Formato_Pelagem = ArrayAdapter.createFromResource(getApplicationContext(), R.array.tipo_pelagem_array_pt, android.R.layout.simple_spinner_dropdown_item);
                    adapter_Formato_Pelagem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Tipo_Formato_Pelagem_Spinner.setAdapter(adapter_Formato_Pelagem);

                    //Spinner TIPO CAUDA
                    ArrayAdapter<CharSequence> Formato_Cauda = ArrayAdapter.createFromResource(getApplicationContext(), R.array.tipo_cauda_array_pt, android.R.layout.simple_spinner_dropdown_item);
                    Formato_Cauda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Tipo_Cauda_Spinner.setAdapter(Formato_Cauda);

                    //Spinner TIPO Sexo
                    ArrayAdapter<CharSequence> Sexo_Animal = ArrayAdapter.createFromResource(getApplicationContext(), R.array.gender_pt, android.R.layout.simple_spinner_dropdown_item);
                    Sexo_Animal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Sexo_Animal_Spinner.setAdapter(Sexo_Animal);
                    message = "Preencha todos os campos";
                    NP = "Nome do animal";
                    DN = "Data de nascimento";
                    UF = "Carregar Ficheiro";
                    PN = "Escolha um nome ";
                    PDN = "Escolha a data de nascimento";
                    PIMG = "Escolha uma imagem";
                    registar_animal = "Adicionando novo animal";
                    txtNomePet.setHint(NP);
                    //txtDataNascPet.setHint("dd/mm/YYYY");
                    //txtUploadFile.setText(UF);
                    //btnRegistPet.setText("REGISTRAR");
                    btnCao.setText("Cão");
                    btnGato.setText("Gato");
                    txtNumChip.setHint("Numero chip");
                    Raca_Animal.setHint("Raça");
                } else if (lingua.equals("cn")){
                    //todo --> Translações e arrays dos animais em chines e do conteudo
                    //Spinner TIPO PELAGEM
                    ArrayAdapter<CharSequence> adapter_Pelagem = ArrayAdapter.createFromResource(getApplicationContext(), R.array.pelagem_array_cn, android.R.layout.simple_spinner_dropdown_item);
                    adapter_Pelagem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Tipo_Pelagem_Spinner.setAdapter(adapter_Pelagem);

                    //Spinner TIPO FORMATO PELAGEM
                    ArrayAdapter<CharSequence> adapter_Formato_Pelagem = ArrayAdapter.createFromResource(getApplicationContext(), R.array.tipo_pelagem_array_cn, android.R.layout.simple_spinner_dropdown_item);
                    adapter_Formato_Pelagem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Tipo_Formato_Pelagem_Spinner.setAdapter(adapter_Formato_Pelagem);

                    //Spinner TIPO CAUDA
                    ArrayAdapter<CharSequence> Formato_Cauda = ArrayAdapter.createFromResource(getApplicationContext(), R.array.tipo_cauda_array_cn, android.R.layout.simple_spinner_dropdown_item);
                    Formato_Cauda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Tipo_Cauda_Spinner.setAdapter(Formato_Cauda);

                    //Spinner Sexo
                    ArrayAdapter<CharSequence> Sexo_Animal = ArrayAdapter.createFromResource(getApplicationContext(), R.array.gender_cn, android.R.layout.simple_spinner_dropdown_item);
                    Sexo_Animal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Sexo_Animal_Spinner.setAdapter(Sexo_Animal);

                    NP = "动物名";
                    DN = "出生日期";
                    UF = "上传文件";
                    PN = "选择一个名字";
                    PDN = "选择您的出生日期";
                    PIMG = "选择一张图片";
                    registar_animal = "添加新动物";
                    txtNomePet.setHint(NP);
                    //txtDataNascPet.setHint("dd/mm/YYYY");
                    //txtUploadFile.setText(UF);
                    //btnRegistPet.setText("寄存器");
                    message = "填写所有输入字段";
                    btnCao.setText("狗");
                    btnGato.setText("猫");
                    txtNumChip.setHint("芯片编号");
                    Raca_Animal.setHint("品种");
                }else if (lingua.equals("fr")){
                    //Spinner TIPO PELAGEM
                    ArrayAdapter<CharSequence> adapter_Pelagem = ArrayAdapter.createFromResource(getApplicationContext(), R.array.pelagem_array_fr, android.R.layout.simple_spinner_dropdown_item);
                    adapter_Pelagem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Tipo_Pelagem_Spinner.setAdapter(adapter_Pelagem);

                    //Spinner TIPO FORMATO PELAGEM
                    ArrayAdapter<CharSequence> adapter_Formato_Pelagem = ArrayAdapter.createFromResource(getApplicationContext(), R.array.tipo_pelagem_array_fr, android.R.layout.simple_spinner_dropdown_item);
                    adapter_Formato_Pelagem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Tipo_Formato_Pelagem_Spinner.setAdapter(adapter_Formato_Pelagem);

                    //Spinner TIPO CAUDA
                    ArrayAdapter<CharSequence> Formato_Cauda = ArrayAdapter.createFromResource(getApplicationContext(), R.array.tipo_cauda_array_fr, android.R.layout.simple_spinner_dropdown_item);
                    Formato_Cauda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Tipo_Cauda_Spinner.setAdapter(Formato_Cauda);

                    //Spinner TIPO CAUDA
                    ArrayAdapter<CharSequence> Sexo_Animal = ArrayAdapter.createFromResource(getApplicationContext(), R.array.gender_fr, android.R.layout.simple_spinner_dropdown_item);
                    Sexo_Animal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Sexo_Animal_Spinner.setAdapter(Sexo_Animal);

                    NP = "Nom de l'animal";
                    DN = "Date de naissance";
                    UF = "Téléverser un fichier";
                    PN = "Choisissez un nom";
                    PDN = "Choisissez votre date de naissance";
                    PIMG = "Choisissez une image";
                    registar_animal = "Ajouter un nouvel animal";
                    txtNomePet.setHint(NP);
                    //txtDataNascPet.setHint("dd/mm/YYYY");
                    //txtUploadFile.setText(UF);
                    //btnRegistPet.setText("S'INSCRIRE");
                    message="Remplissez tous les champs de saisie";
                    btnCao.setText("chien");
                    btnGato.setText("chat");
                    txtNumChip.setHint("Numéro de puce");
                    Raca_Animal.setHint("Race");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setupPopupImageClick() {


        popup_img.setOnClickListener(new View.OnClickListener() {
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


        /*if (ContextCompat.checkSelfPermission(my_pets.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(my_pets.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(my_pets.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();

            }

            else
            {
                ActivityCompat.requestPermissions(my_pets.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }
        else*/
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

        if (resultCode == RESULT_OK && requestCode == REQUESCODE) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            popup_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            popup_img.setImageURI(pickedImgUri);

        }


    }

    private void iniPopup() {

        popAddPet = new Dialog(this);
        popAddPet.setContentView(R.layout.popup_add_animal);
        popAddPet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPet.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPet.getWindow().getAttributes().gravity = Gravity.TOP;

        // ini popup widgets
        txtNomePet  = popAddPet.findViewById(R.id.txtNomePet);
        txtDataDia  = popAddPet.findViewById(R.id.txtDataDia);
        txtDataMes  = popAddPet.findViewById(R.id.txtDataMes);
        txtDataAno  = popAddPet.findViewById(R.id.txtDataAno);
        txtNumChip  = popAddPet.findViewById(R.id.txtNumChip);

        Raca_Animal = popAddPet.findViewById(R.id.Raca_Animal);

        Tipo_Pelagem_Spinner  = popAddPet.findViewById(R.id.Tipo_Pelagem_Spinner);
        Tipo_Formato_Pelagem_Spinner = popAddPet.findViewById(R.id.Tipo_Formato_Pelagem_Spinner);
        Tipo_Cauda_Spinner = popAddPet.findViewById(R.id.Tipo_Cauda_Spinner);
        Sexo_Animal_Spinner = popAddPet.findViewById(R.id.Sexo_Animal_Spinner);
        btnGato = popAddPet.findViewById(R.id.btnGato);
        btnGato.setOnClickListener(v->{

            FirebaseDatabase.getInstance().getReference("Utilizador").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String lingua = snapshot.child("lingua").getValue().toString().trim();
                    if (lingua.equals("en")){
                        tipoAnimal = "gato";
                        racas_gatos_pt = getResources().getStringArray(R.array.gatos_en);
                        adapter_raca_pt = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, racas_gatos_pt);
                        Raca_Animal.setAdapter(adapter_raca_pt);
                        btnCao.setChecked(false);
                        btnGato.setChecked(true);
                    } else if (lingua.equals("pt")){
                        tipoAnimal = "gato";
                        racas_gatos_pt = getResources().getStringArray(R.array.gatos_pt);
                        adapter_raca_pt = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, racas_gatos_pt);
                        Raca_Animal.setAdapter(adapter_raca_pt);
                        btnCao.setChecked(false);
                        btnGato.setChecked(true);
                    } else if (lingua.equals("fr")){
                        tipoAnimal = "gato";
                        racas_gatos_pt = getResources().getStringArray(R.array.gatos_fr);
                        adapter_raca_pt = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, racas_gatos_pt);
                        Raca_Animal.setAdapter(adapter_raca_pt);
                        btnCao.setChecked(false);
                        btnGato.setChecked(true);
                    }else if (lingua.equals("cn")){
                        tipoAnimal = "gato";
                        racas_gatos_pt = getResources().getStringArray(R.array.gatos_cn);
                        adapter_raca_pt = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, racas_gatos_pt);
                        Raca_Animal.setAdapter(adapter_raca_pt);
                        btnCao.setChecked(false);
                        btnGato.setChecked(true);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        });
        btnCao = popAddPet.findViewById(R.id.btnCao);
        btnCao.setOnClickListener(v-> {

            FirebaseDatabase.getInstance().getReference("Utilizador").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String lingua = snapshot.child("lingua").getValue().toString().trim();
                    if (lingua.equals("en")){
                        tipoAnimal = "cao";

                        racas_cao_pt = getResources().getStringArray(R.array.caes_en);
                        adapter_raca_pt = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, racas_cao_pt);
                        Raca_Animal.setAdapter(adapter_raca_pt);
                        btnCao.setChecked(true);
                        btnGato.setChecked(false);
                    } else if (lingua.equals("pt")){
                        tipoAnimal = "cao";
                        racas_cao_pt = getResources().getStringArray(R.array.caes_pt);
                        adapter_raca_pt = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, racas_cao_pt);
                        Raca_Animal.setAdapter(adapter_raca_pt);
                        btnCao.setChecked(true);
                        btnGato.setChecked(false);
                    } else if (lingua.equals("fr")){
                        tipoAnimal = "cao";
                        racas_cao_pt = getResources().getStringArray(R.array.caes_fr);
                        adapter_raca_pt = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, racas_cao_pt);
                        Raca_Animal.setAdapter(adapter_raca_pt);
                        btnCao.setChecked(true);
                        btnGato.setChecked(false);
                    }else if (lingua.equals("cn")){
                        tipoAnimal = "cao";
                        racas_cao_pt = getResources().getStringArray(R.array.caes_cn);
                        adapter_raca_pt = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, racas_cao_pt);
                        Raca_Animal.setAdapter(adapter_raca_pt);
                        btnCao.setChecked(true);
                        btnGato.setChecked(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });

        popup_img = popAddPet.findViewById(R.id.popup_img);
        popup_add = popAddPet.findViewById(R.id.popup_add);
        popupClickProgress = popAddPet.findViewById(R.id.popup_progressBar);

        // Add post click Listener

        popup_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popup_add.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                // we need to test all input fields (Title and description ) and post image
                raca = Raca_Animal.getText().toString().trim();
                pelagem = Tipo_Pelagem_Spinner.getSelectedItem().toString().trim();
                formato_pelo = Tipo_Formato_Pelagem_Spinner.getSelectedItem().toString().trim();
                cauda = Tipo_Cauda_Spinner.getSelectedItem().toString().trim();
                sexo = Sexo_Animal_Spinner.getSelectedItem().toString().trim();

                //Recebe os valores das Edits e Spinners e guarda-os em strings
                 dia = txtDataDia.getText().toString().trim();
                 mes = txtDataMes.getText().toString().trim();
                 ano = txtDataAno.getText().toString().trim();
                int inAno= 0, inMes= 0, inDia = 0;
                if (!dia.equals("")){
                    inDia = Integer.parseInt(dia);
                    if (inDia >= 32 || inDia <= 0){
                        txtDataDia.requestFocus();
                    }
                }else{
                    txtDataDia.requestFocus();
                }
                if (!mes.equals("")){
                    inMes = Integer.parseInt(mes);
                    if (inMes >= 13 || inMes <= 0){
                        txtDataMes.requestFocus();
                    }
                }else {
                    txtDataMes.requestFocus();
                }
                if (!ano.equals("")){
                    inAno = Integer.parseInt(ano);
                    if (inAno >= 2019){
                        txtDataAno.requestFocus();
                    }
                }else {
                    txtDataAno.requestFocus();
                }
                if (!txtNomePet.getText().toString().isEmpty()
                        && !txtDataDia.getText().toString().isEmpty() && !txtDataMes.getText().toString().isEmpty()
                        && !txtDataAno.getText().toString().isEmpty()
                        && !raca.equals("") && !pelagem.equals("") && !formato_pelo.equals("") && !cauda.equals("")
                        && !sexo.equals("") && inDia >= 1 && inDia <= 31 && inMes >= 1 && inMes <= 12 && inAno <= 2020
                        && pickedImgUri != null) {

                    final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("pets").child(key_photo + ".jpg");
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
                                imagem = downloadUri.toString();
                                addAnimal();



                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });

                }
                else {
                    showMessage(message) ;
                    popup_add.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);

                }



            }
        });



    }

    private void showMessage(String message) {

        Toast.makeText(my_pets.this,message,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStart() {
        super.onStart();

        final Query allData = FirebaseDatabase.getInstance().getReference().child("animais").child(userId);
            FirebaseRecyclerOptions<Info_Animal> options =
                    new FirebaseRecyclerOptions.Builder<Info_Animal>()
                            .setQuery(allData, Info_Animal.class)
                            .setLifecycleOwner(this)
                            .build();

            meus_animais = new FirebaseRecyclerAdapter<Info_Animal, my_pets.List_Pets>(options) {
                @NonNull
                @Override
                public my_pets.List_Pets onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    return new my_pets.List_Pets(LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.row_pet, viewGroup, false));
                }

                @Override
                protected void onBindViewHolder(@NonNull final my_pets.List_Pets holder, final int position, @NonNull final Info_Animal model) {
                    final String key = getRef(position).getKey();
                    FirebaseDatabase.getInstance().getReference().child("animais").child(userId).child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String imagem = snapshot.child("imagem").getValue(String.class);
                            String nomePet = snapshot.child("nomePet").getValue(String.class);
                            holder.namePet.setText(nomePet);
                            Glide.with(getApplicationContext()).load(imagem).into(holder.imgPet);
                            holder.getAdapterPosition();
                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent openPub = new Intent(my_pets.this, pet_page.class);
                                    openPub.putExtra("petID", key);
                                    openPub.putExtra("value", "pet");
                                    startActivity(openPub);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            };
            rc_pets.setAdapter(meus_animais);

    }

    private void addAnimal() {
        String boletim = "";
        String nomePet = txtNomePet.getText().toString().trim();
        String DataNasc = dia + "/" + mes + "/" + ano;
        Toast.makeText(this, registar_animal, Toast.LENGTH_SHORT).show();
        Info_Animal info_animal = new Info_Animal(nomePet, DataNasc, tipoAnimal, raca, chip, pelagem, formato_pelo, cauda, sexo, boletim, imagem, key);
        FirebaseDatabase.getInstance().getReference().child("animais").child(userId).child(key).setValue(info_animal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                popAddPet.dismiss();
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });

    }

    public class List_Pets extends RecyclerView.ViewHolder {

        View mView;
        CircleImageView imgPet;
        TextView namePet;
        public List_Pets(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            imgPet = itemView.findViewById(R.id.imgPet);
            namePet = itemView.findViewById(R.id.namePet);
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