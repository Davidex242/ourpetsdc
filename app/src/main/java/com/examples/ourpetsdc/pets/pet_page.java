package com.examples.ourpetsdc.pets;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examples.ourpetsdc.Firebase.Info_Vacina_Desp;
import com.examples.ourpetsdc.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class pet_page extends AppCompatActivity {
    //Recebe id do pet
    public static String PetID;
    String id_pet;
    //String id do user
    String userId;
    FirebaseAuth auth;
    DatabaseReference mRef;
    //Verificar a ligação a internet
    private static final String LOG_TAG = "CheckNetworkStatus";
    private NetworkChangeReceiver receiver;
    private boolean isConnected = false;
    private ConstraintLayout connectedNetwork;
    private ImageView NoConnectedNetwork;

    //Variaveis dos dados do pet
    private TextView petName, petDateBorn, petSex, petTail, petPelagem, petFormato, petChip, petRaca;
    private CircleImageView petImage;

    //Listagem de vacinas e tabs
    LinearLayout tab1, tab2;
    RecyclerView rc_desp, rc_vacinas;
    TabHost tabHost;

    //popUpInfo
    Dialog popUpInfo ;
    ImageView infoTable;
    TableLayout tableCaesPT, tableCaesEn, tableCaesFr, tableCaesCn;
    TableLayout tablegGatosPT, tablegGatosEn, tablegGatosFr, tablegGatosCn;

    //popUpRegistVacina
    Dialog popUpVac;
    ImageView createVaccine;
    Spinner vacina;
    RadioButton btnVac, btnDesp;

    //Editar chip
    EditText EditPetChip;
    ImageView btnSaveChip;
    String changesOk, changesNOk, chipValido;
    //vac
    EditText txtDataDia, /*txtDataMes, txtDataAno,*/ txthora, txtMin;
    DatePickerDialog.OnDateSetListener setListener;
    Button add;
    //Tipo de agendamento
    String tipo;
    String msgTxtData, msgTxtHora;
    String nome_pet;
    //Strings para tradução
    String escVac, escDesp, campObrig, suces, agend;
    String nome = "";
    final Calendar myCalendar = Calendar.getInstance();
    private ArrayList<Info_Vacina_Desp> vacs;
    private tag_vac adpaterVacs;
    private tag_desp adpaterDesp;
    private DatabaseReference mRefPet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_page);
        id_pet = getIntent().getStringExtra("petID");

       if (id_pet.equals("")){
            Intent intent = new Intent(pet_page.this, my_pets.class);
            startActivity(intent);
        }else{
            id_pet = "";
            id_pet = getIntent().getStringExtra("petID");
            Log.i("idPET", id_pet) ;
            auth = FirebaseAuth.getInstance();
            userId = auth.getCurrentUser().getUid();
            mRef =  FirebaseDatabase.getInstance().getReference().child("Utilizador").child(userId);
            tabHost = findViewById(R.id.tabHost);
            tabHost.setup();
            tab1 = findViewById(R.id.tab1);
            tab2 = findViewById(R.id.tab2);
            rc_vacinas = findViewById(R.id.rc_vacinas);
            rc_vacinas.setHasFixedSize(true);
            rc_vacinas.setLayoutManager(new LinearLayoutManager(this));
            rc_desp = findViewById(R.id.rc_desp);
            rc_desp.setHasFixedSize(true);
            rc_desp.setLayoutManager(new LinearLayoutManager(this));
            //Verificar a conexao a internet
            NoConnectedNetwork = findViewById(R.id.NoConnectedNetwork);
            connectedNetwork = findViewById(R.id.connectedNetwork);
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            receiver = new NetworkChangeReceiver();
            registerReceiver(receiver, filter);
            PetID = id_pet;
            //Variaveis dos dados do pet
            petName = findViewById(R.id.petName);
            petDateBorn = findViewById(R.id.petDateBorn);
            petSex = findViewById(R.id.petSex);
            petTail = findViewById(R.id.petTail);
            petPelagem = findViewById(R.id.petPelagem);
            petFormato = findViewById(R.id.petFormato);
            petChip = findViewById(R.id.petChip);
            petImage = findViewById(R.id.petImage);
            petRaca = findViewById(R.id.petRaca);
            //Editar chip number
            EditPetChip = findViewById(R.id.EditPetChip);
            btnSaveChip = findViewById(R.id.btnSaveChip);
            petChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    petChip.setVisibility(View.GONE);
                    EditPetChip.setVisibility(View.VISIBLE);
                    btnSaveChip.setVisibility(View.VISIBLE);
                }
            });
            btnSaveChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String chipChanged = EditPetChip.getText().toString().trim();
                    if (chipChanged.length()>15 || chipChanged.length() < 10){
                        EditPetChip.setError(chipValido);
                        EditPetChip.requestFocus();
                    }else {
                        mRefPet.child("animais").child(userId).child(id_pet).child("chip").setValue(chipChanged).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                petChip.setVisibility(View.VISIBLE);
                                EditPetChip.setVisibility(View.GONE);
                                btnSaveChip.setVisibility(View.GONE);
                                Toast.makeText(pet_page.this, changesOk, Toast.LENGTH_SHORT).show();
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                petChip.setVisibility(View.VISIBLE);
                                EditPetChip.setVisibility(View.GONE);
                                btnSaveChip.setVisibility(View.GONE);
                                Toast.makeText(pet_page.this, changesNOk, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

            });
            mRefPet = FirebaseDatabase.getInstance().getReference();
            mRefPet.child("animais").child(userId).child(id_pet).addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    nome_pet = snapshot.child("nomePet").getValue(String.class);
                    String data = snapshot.child("dataNasc").getValue(String.class);
                    String sex = snapshot.child("sexo").getValue(String.class);
                    String tail = snapshot.child("cauda").getValue(String.class);
                    String coat = snapshot.child("pelagem").getValue(String.class);
                    String format = snapshot.child("formato_pelo").getValue(String.class);
                    String chip = snapshot.child("chip").getValue(String.class);
                    String imagem = snapshot.child("imagem").getValue(String.class);
                    String raca = snapshot.child("raca").getValue(String.class);
                    String tipoAnimal = snapshot.child("tipoAnimal").getValue(String.class);
                    petName.setText(nome_pet);
                    petDateBorn.setText(data);
                    petSex.setText(sex);
                    petTail.setText(tail);
                    petPelagem.setText(coat);
                    petFormato.setText(format);
                    Glide.with(getApplicationContext()).load(imagem).into(petImage);
                    EditPetChip.setText(chip);
                    petChip.setText(chip);
                    if (tipoAnimal.equals("gato")){
                        Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.ic_cat);
                        petRaca.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                    }else{
                        Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.ic_dog__2);
                        petRaca.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                    }
                    petRaca.setText(raca);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String lingua = snapshot.child("lingua").getValue(String.class);
                    if (lingua.equals("pt")){
                        changesOk = "Alterações guardadas com sucesso";
                        changesNOk = "Ocorreu um erro";
                        chipValido = "Insira um chip válido";
                        TabHost.TabSpec spec = tabHost.newTabSpec("Vacinas");
                        spec.setContent(R.id.tab1);
                        spec.setIndicator("Vacinas");
                        tabHost.addTab(spec);
                        TabHost.TabSpec spec1 = tabHost.newTabSpec("Desparasitações");
                        spec1.setContent(R.id.tab2);
                        spec1.setIndicator("Desparasitações");
                        tabHost.addTab(spec1);
                    } else  if (lingua.equals("en")){
                        changesOk = "Changes saved successfully";
                        changesNOk = "An error has occurred";
                        chipValido = "Insert a valid chip";
                        TabHost.TabSpec spec = tabHost.newTabSpec("Vacinas");
                        spec.setContent(R.id.tab1);
                        spec.setIndicator("Vaccines");
                        tabHost.addTab(spec);
                        TabHost.TabSpec spec1 = tabHost.newTabSpec("Desparasitações");
                        spec1.setContent(R.id.tab2);
                        spec1.setIndicator("Deworming");
                        tabHost.addTab(spec1);
                    }else  if (lingua.equals("fr")){
                        changesOk = "Changements sauvegardés avec succès";
                        changesNOk = "Une erreur est survenue";
                        chipValido = "Insérez une puce valide";
                        TabHost.TabSpec spec = tabHost.newTabSpec("Vacinas");
                        spec.setContent(R.id.tab1);
                        spec.setIndicator("Vaccins");
                        tabHost.addTab(spec);
                        TabHost.TabSpec spec1 = tabHost.newTabSpec("Desparasitações");
                        spec1.setContent(R.id.tab2);
                        spec1.setIndicator("Vermifuge");
                        tabHost.addTab(spec1);
                    }else  if (lingua.equals("cn")){
                        changesOk = "更改已成功保存";
                        changesNOk = "发生了错误";
                        chipValido = "插入有效的筹码";
                        TabHost.TabSpec spec = tabHost.newTabSpec("Vacinas");
                        spec.setContent(R.id.tab1);
                        spec.setIndicator("疫苗");
                        tabHost.addTab(spec);
                        TabHost.TabSpec spec1 = tabHost.newTabSpec("Desparasitações");
                        spec1.setContent(R.id.tab2);
                        spec1.setIndicator("驱虫");
                        tabHost.addTab(spec1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //PopUpInfo
            infoTable = findViewById(R.id.infoTable);

            iniPopupInfo();
            infoTable.setOnClickListener(v->{
                popUpInfo.show();
            });
            //PopUP vAC
            createVaccine = findViewById(R.id.createVaccine);
            iniPopupRegist();
            createVaccine.setOnClickListener(v->{

                popUpVac.show();

            });
            //DatePicker
            txtDataDia.setOnClickListener(v->{
                DateDialog();
            });

            add.setOnClickListener(v->{

                //Receber dados Hora
                String hh = txthora.getText().toString().trim();
                String mm = txtMin.getText().toString().trim();
                int inHH = 0, inMM = 0;
                if (!hh.equals("")){
                    inHH = Integer.parseInt(hh);
                    if (inHH >= 25 || inHH <= 0){
                        txthora.requestFocus();
                    }
                }else {
                    txthora.requestFocus();
                }
                if (!mm.equals("")){
                    inMM = Integer.parseInt(mm);
                    if (inMM >= 61 || inMM <= 0){
                        txtMin.requestFocus();
                    }
                }else{
                    txtMin.requestFocus();
                }


                if (inHH <= 9){

                    hh = "0" + inHH;

                }
                if (inMM <= 9){

                    mm = "0" +inMM;

                }

                nome = vacina.getSelectedItem().toString().trim();
                String status = "-";
                String key = UUID.randomUUID().toString();
                if(nome.equals(escVac) || nome.equals(escDesp)){
                    vacina.requestFocus();
                }
                String data = txtDataDia.getText().toString();
                if (!nome.equals(escVac) && !data.equals("") && !nome.equals(escDesp) &&  inHH >= 1 && inHH <= 24 && inMM >= 1 && inMM <= 60){

                    //String data = dia + "/" + mes + "/" + ano;
                    String hora = hh + ":" + mm;
                    Info_Vacina_Desp info_vacinaDesp = new Info_Vacina_Desp(nome, data, tipo,status,key,nome_pet , hora);
                    FirebaseDatabase.getInstance().getReference().child("boletim").child(userId).child(id_pet).child(key).setValue(info_vacinaDesp).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            popUpVac.dismiss();
                            txthora.setText("");
                            txthora.setHint("hh");
                            txtMin.setText("");
                            txtMin.setHint("mm");

                            txtDataDia.setText("");
                            txtDataDia.setHint("");

                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                        }
                    });

                }

            });

            FirebaseDatabase.getInstance().getReference().child("boletim").child(userId).child(id_pet).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Lista os dados guardados na firebase do boletim de vacinas
                    vacs = new ArrayList<Info_Vacina_Desp>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Info_Vacina_Desp p = dataSnapshot1.getValue(Info_Vacina_Desp.class);
                        vacs.add(p);
                    }
                    adpaterDesp = new tag_desp(getBaseContext(), vacs);
                    rc_desp.setAdapter(adpaterDesp);
                    adpaterVacs = new tag_vac(getBaseContext(), vacs);
                    rc_vacinas.setAdapter(adpaterVacs);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }


    }
    public void DateDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
            myCalendar.getTime();
        }

    };

    private void updateLabel(){
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        txtDataDia.setText(sdf.format(myCalendar.getTime()));
    }


    private void iniPopupRegist() {

        popUpVac = new Dialog(this);
        popUpVac.setContentView(R.layout.popup_add_vaccine);
        popUpVac.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popUpVac.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popUpVac.getWindow().getAttributes().gravity = Gravity.TOP;

        //Vac
        vacina = popUpVac.findViewById(R.id.vacina);
        txtDataDia = popUpVac.findViewById(R.id.txtDataDia);
        txtDataDia.setFocusable(false);
        //txtDataMes = popUpVac.findViewById(R.id.txtDataMes);
        //txtDataAno = popUpVac.findViewById(R.id.txtDataAno);
        txthora = popUpVac.findViewById(R.id.txthora);
        txtMin = popUpVac.findViewById(R.id.txtMin);
        btnDesp =  popUpVac.findViewById(R.id.btnDesp);
        btnVac =  popUpVac.findViewById(R.id.btnVac);
        add = (Button) popUpVac.findViewById(R.id.add);
        //traducao das tabs

        btnDesp.setOnClickListener(v->{
            //Query para entrar child da firebase
            FirebaseDatabase.getInstance().getReference("Utilizador").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //String lingua recebe o valor que esta na child "lingua"
                            String lingua = snapshot.child("lingua").getValue(String.class);
                            //Verifica o idioma do utilizador idioma do utilizador
                            if (lingua.equals("en")){
                                tipo = "deworming";
                                ArrayAdapter<CharSequence> adapterDesp = ArrayAdapter.createFromResource(getApplicationContext(), R.array.desp_en, android.R.layout.simple_spinner_dropdown_item);
                                adapterDesp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                vacina.setAdapter(adapterDesp);
                            } else if (lingua.equals("pt")){
                                tipo = "desparazitação";
                                ArrayAdapter<CharSequence> adapterDesp = ArrayAdapter.createFromResource(getApplicationContext(), R.array.desp_pt, android.R.layout.simple_spinner_dropdown_item);
                                adapterDesp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                vacina.setAdapter(adapterDesp);
                            } else if (lingua.equals("cn")){
                                tipo = "降级";
                                ArrayAdapter<CharSequence> adapterDesp = ArrayAdapter.createFromResource(getApplicationContext(), R.array.desp_cn, android.R.layout.simple_spinner_dropdown_item);
                                adapterDesp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                vacina.setAdapter(adapterDesp);
                            }else if (lingua.equals("fr")){
                                tipo = "désescalade";
                                ArrayAdapter<CharSequence> adapterDesp = ArrayAdapter.createFromResource(getApplicationContext(), R.array.desp_fr, android.R.layout.simple_spinner_dropdown_item);
                                adapterDesp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                vacina.setAdapter(adapterDesp);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

        });
        btnVac.setOnClickListener(v->{

            FirebaseDatabase.getInstance().getReference("Utilizador").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String lingua = snapshot.child("lingua").getValue(String.class);
                            if (lingua.equals("en")){
                                tipo = "Vaccine";
                                msgTxtData = "Vaccine date";
                                msgTxtHora = "Vaccine time";
                                escDesp = "Choose deworming";
                                escVac = "Choose the vaccine";
                                campObrig = "Required field";
                                suces = "Success";
                                agend = "schedule";
                                FirebaseDatabase.getInstance().getReference().child("animais").child(userId).child(id_pet).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String tipoAnimal = dataSnapshot.child("tipoAnimal").getValue(String.class);

                                        if (tipoAnimal.equals("cao")){
                                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.vac_caes_en, android.R.layout.simple_spinner_dropdown_item);
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            vacina.setAdapter(adapter);
                                        }
                                        if (tipoAnimal.equals("gato")){

                                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.vac_gatos_en, android.R.layout.simple_spinner_dropdown_item);
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            vacina.setAdapter(adapter);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else if (lingua.equals("pt")){
                                tipo = "vacina";
                                msgTxtData = "Data da vacina";
                                msgTxtHora = "Hora da vacina";
                                escDesp = "Escolha a desparasitação";
                                escVac = "Escolha a vacina";
                                campObrig = "Campo obrigatório";
                                suces = "Sucesso";
                                agend = "Agendar";
                                FirebaseDatabase.getInstance().getReference().child("animais").child(userId).child(id_pet).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String tipoAnimal = dataSnapshot.child("tipoAnimal").getValue(String.class);

                                        if (tipoAnimal.equals("cao")){
                                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.vac_caes_pt, android.R.layout.simple_spinner_dropdown_item);
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            vacina.setAdapter(adapter);
                                        }
                                        if (tipoAnimal.equals("gato")){
                                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.vac_gatos_pt, android.R.layout.simple_spinner_dropdown_item);
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            vacina.setAdapter(adapter);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else if (lingua.equals("cn")){
                                tipo = "疫苗";
                                msgTxtData = "疫苗日期";
                                msgTxtHora = "疫苗接种时间";
                                escDesp = "选择驱虫";
                                escVac = "选择疫苗";
                                campObrig = "必填项目";
                                suces = "成功";
                                agend = "时间表";
                                FirebaseDatabase.getInstance().getReference().child("animais").child(userId).child(id_pet).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String tipoAnimal = dataSnapshot.child("tipoAnimal").getValue(String.class);

                                        if (tipoAnimal.equals("cao")){
                                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.vac_caes_cn, android.R.layout.simple_spinner_dropdown_item);
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            vacina.setAdapter(adapter);
                                        }
                                        if (tipoAnimal.equals("gato")){
                                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.vac_gatos_cn, android.R.layout.simple_spinner_dropdown_item);
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            vacina.setAdapter(adapter);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }else if (lingua.equals("fr")){
                                tipo = "vaccin";
                                msgTxtData = "Date du vaccin";
                                msgTxtHora = "Temps de vaccination";
                                escDesp = "Choisissez le vermifuge";
                                escVac = "Choisissez le vaccin\n";
                                campObrig = "Champs requis\n";
                                suces = "Succès";
                                agend = "Programme";
                                FirebaseDatabase.getInstance().getReference().child("animais").child(userId).child(id_pet).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String tipoAnimal = dataSnapshot.child("tipoAnimal").getValue(String.class);
                                        if (tipoAnimal.equals("cao")){
                                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.vac_caes_fr, android.R.layout.simple_spinner_dropdown_item);
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            vacina.setAdapter(adapter);
                                        }
                                        if (tipoAnimal.equals("gato")){
                                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.vac_gatos_fr, android.R.layout.simple_spinner_dropdown_item);
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            vacina.setAdapter(adapter);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        });
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String lingua = snapshot.child("lingua").getValue(String.class);
                if (lingua.equals("pt")){
                    add.setText("Adicionar");
                    txtDataDia.setHint("Data");
                    btnVac.setText("Vacinas");
                    btnDesp.setText("Desparasitações");
                } else  if (lingua.equals("en")){
                    add.setText("Add");
                    txtDataDia.setHint("Date");
                    btnVac.setText("Vaccines");
                    btnDesp.setText("Deworming");
                }else  if (lingua.equals("fr")){
                    add.setText("Ajouter");
                    txtDataDia.setHint("Date");
                    btnVac.setText("Vaccins");
                    btnDesp.setText("Vermifuge");
                }else  if (lingua.equals("cn")){
                    add.setText("加");
                    txtDataDia.setHint("日期");
                    btnVac.setText("疫苗");
                    btnDesp.setText("驱虫");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void iniPopupInfo() {

        popUpInfo = new Dialog(this);
        popUpInfo.setContentView(R.layout.infotable_popup);
        popUpInfo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popUpInfo.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popUpInfo.getWindow().getAttributes().gravity = Gravity.CENTER;
        //Layout com um planeamento de vacinas
        tableCaesPT = popUpInfo.findViewById(R.id.tableCaesPT);
        tableCaesEn = popUpInfo.findViewById(R.id.tableCaesEn);
        tableCaesFr = popUpInfo.findViewById(R.id.tableCaesFr);
        tableCaesCn = popUpInfo.findViewById(R.id.tableCaesCn);

        tablegGatosPT = popUpInfo.findViewById(R.id.tablegGatosPT);
        tablegGatosEn = popUpInfo.findViewById(R.id.tablegGatosEn);
        tablegGatosFr = popUpInfo.findViewById(R.id.tablegGatosFr);
        tablegGatosCn = popUpInfo.findViewById(R.id.tablegGatosCn);

       mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String lingua = snapshot.child("lingua").getValue(String.class);

                assert lingua != null;
                if(lingua.equals("en")){
                    mRefPet.child("animais").child(userId).child(id_pet).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String tipoAnimal = snapshot.child("tipoAnimal").getValue(String.class);
                            assert tipoAnimal != null;
                            if (tipoAnimal.equals("cao")){
                                tableCaesPT.setVisibility(View.GONE);
                                tableCaesEn.setVisibility(View.VISIBLE);
                                tableCaesFr.setVisibility(View.GONE);
                                tableCaesCn.setVisibility(View.GONE);

                                tablegGatosPT.setVisibility(View.GONE);
                                tablegGatosEn.setVisibility(View.GONE);
                                tablegGatosFr.setVisibility(View.GONE);
                                tablegGatosCn.setVisibility(View.GONE);

                            }else{
                                tableCaesPT.setVisibility(View.GONE);
                                tableCaesEn.setVisibility(View.GONE);
                                tableCaesFr.setVisibility(View.GONE);
                                tableCaesCn.setVisibility(View.GONE);

                                tablegGatosPT.setVisibility(View.GONE);
                                tablegGatosEn.setVisibility(View.VISIBLE);
                                tablegGatosFr.setVisibility(View.GONE);
                                tablegGatosCn.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }if(lingua.equals("pt")){
                    mRefPet.child("animais").child(userId).child(id_pet).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String tipoAnimal = snapshot.child("tipoAnimal").getValue(String.class);
                            if (tipoAnimal.equals("cao")){
                                tableCaesPT.setVisibility(View.VISIBLE);
                                tableCaesEn.setVisibility(View.GONE);
                                tableCaesFr.setVisibility(View.GONE);
                                tableCaesCn.setVisibility(View.GONE);
                            }else{
                                tablegGatosPT.setVisibility(View.VISIBLE);
                                tablegGatosEn.setVisibility(View.GONE);
                                tablegGatosFr.setVisibility(View.GONE);
                                tablegGatosCn.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(lingua.equals("cn")){
                    mRefPet.child("animais").child(userId).child(id_pet).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String tipoAnimal = snapshot.child("tipoAnimal").getValue(String.class);
                            if (tipoAnimal.equals("cao")){
                                tableCaesPT.setVisibility(View.GONE);
                                tableCaesEn.setVisibility(View.GONE);
                                tableCaesFr.setVisibility(View.GONE);
                                tableCaesCn.setVisibility(View.VISIBLE);

                                tablegGatosPT.setVisibility(View.GONE);
                                tablegGatosEn.setVisibility(View.GONE);
                                tablegGatosFr.setVisibility(View.GONE);
                                tablegGatosCn.setVisibility(View.GONE);

                            }else{
                                tableCaesPT.setVisibility(View.GONE);
                                tableCaesEn.setVisibility(View.GONE);
                                tableCaesFr.setVisibility(View.GONE);
                                tableCaesCn.setVisibility(View.GONE);

                                tablegGatosPT.setVisibility(View.GONE);
                                tablegGatosEn.setVisibility(View.GONE);
                                tablegGatosFr.setVisibility(View.GONE);
                                tablegGatosCn.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(lingua.equals("fr")){
                    mRefPet.child("animais").child(userId).child(id_pet).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String tipoAnimal = snapshot.child("tipoAnimal").getValue(String.class);
                            if (tipoAnimal.equals("cao")){
                                tableCaesPT.setVisibility(View.GONE);
                                tableCaesEn.setVisibility(View.GONE);
                                tableCaesFr.setVisibility(View.VISIBLE);
                                tableCaesCn.setVisibility(View.GONE);

                                tablegGatosPT.setVisibility(View.GONE);
                                tablegGatosEn.setVisibility(View.GONE);
                                tablegGatosFr.setVisibility(View.GONE);
                                tablegGatosCn.setVisibility(View.GONE);

                            }else{
                                tableCaesPT.setVisibility(View.GONE);
                                tableCaesEn.setVisibility(View.GONE);
                                tableCaesFr.setVisibility(View.GONE);
                                tableCaesCn.setVisibility(View.GONE);

                                tablegGatosPT.setVisibility(View.GONE);
                                tablegGatosEn.setVisibility(View.GONE);
                                tablegGatosFr.setVisibility(View.VISIBLE);
                                tablegGatosCn.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        //Verifica o estado da Internet
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