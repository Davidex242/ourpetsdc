package com.examples.ourpetsdc;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.examples.ourpetsdc.launcher.login;
import com.examples.ourpetsdc.pets.my_pets;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity{

    //Variaveis layout
    private CardView myPetsCard, ForumCard, VetsCard, SettingsCards, publicity_card;
    private CircleImageView imgPerfil;
    private TextView txtUserName, txtEmailUser, myPets, forum, vet, settings;
    //Firebase
    private FirebaseAuth auth;
    private String mCurrentUser;
    private DatabaseReference mRef;

    //Adds
    private Uri add1, add2, add3;
    private FirebaseStorage storage;
    private ImageView imageView;
    private ViewPager viewPager;
    private Dialog popUpAdds;
    private WebView webView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JodaTimeAndroid.init(this);
        //Variavel link objetos
        imgPerfil = findViewById(R.id.imgPerfil);
        txtEmailUser = findViewById(R.id.txtEmailUser);
        txtUserName = findViewById(R.id.txtUserName);
        myPetsCard = findViewById(R.id.myPetsCard);
        ForumCard = findViewById(R.id.ForumCard);
        VetsCard = findViewById(R.id.VetsCard);
        SettingsCards = findViewById(R.id.SettingsCards);
        publicity_card = findViewById(R.id.publicity_card);

        //Textview para traduzir
        myPets = findViewById(R.id.myPets);
        forum = findViewById(R.id.forum);
        vet = findViewById(R.id.vet);
        settings = findViewById(R.id.settings);

        //Vai buscar a sessao currente
        auth = FirebaseAuth.getInstance();

        //Se não tiver nenhuma sessao iniciada vai para o login
        if (auth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, login.class));
        }else{
            mCurrentUser = auth.getCurrentUser().getUid();
            mRef = FirebaseDatabase.getInstance().getReference().child("Utilizador").child(mCurrentUser);
            StorageReference reference1 = FirebaseStorage.getInstance().getReferenceFromUrl("gs://ourpetsdc-610f5.appspot.com/adds/1234.PNG");
            reference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //Glide.with(MainActivity.this).load(uri).into(imgAdd);
                    add1 = uri;
                }
            });
            StorageReference reference2 = FirebaseStorage.getInstance().getReferenceFromUrl("gs://ourpetsdc-610f5.appspot.com/adds/color2.PNG");
            reference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //Glide.with(MainActivity.this).load(uri).into(imgAdd);
                    add2 = uri;
                }
            });

            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String nome = snapshot.child("nome").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String lingua = snapshot.child("lingua").getValue(String.class);
                    String img = snapshot.child("thumb_imagem").getValue(String.class);
                    txtUserName.setText(nome);
                    txtEmailUser.setText(email);
                    //Verifica o Idioma do Utilizador
                    if (lingua.equals("pt")){
                        //message = "Escolha uma foto de perfil";
                        myPets.setText("Meus Pets");
                        vet.setText("Veterinários ");
                        forum.setText("Animais Perdidos");
                        //txt_add.setText("Perfil");
                        settings.setText("Definições");
                    } else  if (lingua.equals("en")){
                       // message = "Choose a profile photo";
                        myPets.setText("My Pets");
                        vet.setText("Veterinarians");
                        forum.setText("Lost Animals");
                        //txt_add.setText("Profile");
                        settings.setText("Settings");
                    }else  if (lingua.equals("fr")){
                        //message = "Choisissez une photo de profil";
                        myPets.setText("Mes animaux");
                        vet.setText("Vétérinaires");
                        forum.setText("Animaux perdus");
                        //txt_add.setText("Profil");
                        settings.setText("Définitions");
                    }else  if (lingua.equals("cn")){
                        //message = "选择个人资料照片";
                        myPets.setText("我的宠物");
                        vet.setText("兽医");
                        forum.setText("迷路的动物");
                        //txt_add.setText("个人资料");
                        settings.setText("定义");
                    }
                    if (img.equals("")){
                        Glide.with(getApplicationContext()).load(R.drawable.ic_profile_user).into(imgPerfil);
                    }else{
                        Glide.with(getApplicationContext()).load(img).into(imgPerfil);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            SettingsCards.setOnClickListener(v -> {

                Intent intent = new Intent(MainActivity.this, settings.class);
                startActivity(intent);
            });
            VetsCard.setOnClickListener(v->{
                //Abre o google maps, com a pesquisa dos vets
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/vet"));
                startActivity(intent);

            });
            ForumCard.setOnClickListener(v->{
                //Abre a atividade Forum
                Intent intent = new Intent(MainActivity.this, com.examples.ourpetsdc.forum.forum.class);
                startActivity(intent);

            });

            myPetsCard.setOnClickListener(v->{
                //Abre a atividade my_pets
                Intent intent = new Intent(MainActivity.this, my_pets.class);
                startActivity(intent);

            });

            //Adds
            viewPager = (ViewPager) findViewById(R.id.imgAdd);
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
            viewPager.setAdapter(viewPagerAdapter);
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new ViewPagerTimer(),2000,3000);

        }

    }

    public class  ViewPagerTimer extends TimerTask{


        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() == 0){
                            viewPager.setCurrentItem(1);
                        //webView.loadUrl("https://www.royalcanin.com/pt");
                    }else if(viewPager.getCurrentItem() == 1){

                        viewPager.setCurrentItem(2);
                    }else if(viewPager.getCurrentItem() == 2){
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }
}