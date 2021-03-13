package com.examples.ourpetsdc.launcher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.examples.ourpetsdc.Firebase.Info_User;
import com.examples.ourpetsdc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class registar extends AppCompatActivity {
    //Variaveis layout
    private TextView gotoLogin, topText;
    private EditText inputName, inputRepeatPassword, inputPassword, inputEmail;
    private Button btnRegister;
    private ProgressDialog progressDialog;
    //Variaveis de codigo
    private String idioma, EmailVerifyWarn, verifyConnect, EmailVerifyOk, EmailJaEmUso, registering, campoObrigatorio,
            passLenght;

    //Firebase variaveis
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);
        //Firebase
        auth = FirebaseAuth.getInstance();

        //fazer link das variaveis com os objetos
        progressDialog = new ProgressDialog(this);
        btnRegister = findViewById(R.id.btnRegister);
        inputName = findViewById(R.id.inputName);
        inputRepeatPassword = findViewById(R.id.inputRepeatPassword);
        inputPassword = findViewById(R.id.inputPassword);
        inputEmail = findViewById(R.id.inputEmail);
        gotoLogin = findViewById(R.id.gotoLogin);
        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registar.this, login.class));
            }
        });

        idioma = verificarLinguagem();

        btnRegister.setOnClickListener(v -> {
            final ConnectivityManager connMgr = (ConnectivityManager)
                    this.getSystemService(Context.CONNECTIVITY_SERVICE);
            final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifi.isConnectedOrConnecting() || mobile.isConnectedOrConnecting ()) {
                registerForm();
            }else {
                Toast.makeText(this, verifyConnect, Toast.LENGTH_LONG).show();
            }


        });
    }

    private void registerForm() {

        final String nome = inputName.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();
        final String confPassword = inputRepeatPassword.getText().toString().trim();
        final String email = inputEmail.getText().toString().trim();
        final String animais = "";
        final String thumb_imagem = "";
        final String imagem = "";
        final String clinica = "";
        final String status = "1"; // 1 = User normal; 2 = Premium; 3 = Vets; 4 = admin
        if (nome.equals("")){
            inputName.requestFocus();
            inputName.setError(campoObrigatorio);
        }

        if (email.equals("")){
            inputEmail.requestFocus();
            inputEmail.setError(campoObrigatorio);
        }

        if (password.equals("")){
            inputPassword.requestFocus();
            inputPassword.setError(campoObrigatorio);
        }
        if (confPassword.equals("")){
            inputRepeatPassword.requestFocus();
            inputRepeatPassword.setError(campoObrigatorio);
        }
        if (!confPassword.equals(password)){
            inputRepeatPassword.requestFocus();
            inputRepeatPassword.setError(campoObrigatorio);
        }
        if (!password.equals(confPassword)){
            inputPassword.requestFocus();
            inputPassword.setError(campoObrigatorio);
        }

        if (password.length() < 6){
            inputPassword.requestFocus();
            inputPassword.setError(passLenght);
        }

       /* if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError(EmailJaEmUso);
            return;
        }*/

        if (confPassword.equals(password) && !nome.equals("") && !email.equals("")){
            progressDialog.setMessage(registering);
            progressDialog.show();

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Info_User info_user = new Info_User(nome, imagem, thumb_imagem, email, idioma, animais, status, clinica);
                                FirebaseDatabase.getInstance().getReference("Utilizador").child(auth.getCurrentUser().getUid()).setValue(info_user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if(task.isSuccessful()){
                                            //sendEmailVerification();
                                            Intent intent = new Intent(registar.this, login.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            } else {

                                progressDialog.dismiss();
                                //If user already exists show a message
                                if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                    Toast.makeText(registar.this, EmailJaEmUso, Toast.LENGTH_SHORT).show();
                                } else{
                                    Toast.makeText(registar.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }

                            }
                        }
                    });
        }

    }
      //Envia email de verificação
    /*private void sendEmailVerification() {

        FirebaseUser firebaseUser = auth.getCurrentUser();
        //Verifica se o valor de utilizador não é igual a 0
        if(firebaseUser != null){
            //Envia email para verificar a conta
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        auth.signOut();
                        Intent intent = new Intent(registar.this, login.class);
                        startActivity(intent);
                        Toast.makeText(registar.this, EmailVerifyOk, Toast.LENGTH_SHORT).show();
                    } else {
                        // Alerta se não for enviado o email
                        Toast.makeText(registar.this, EmailVerifyWarn, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }*/

    private String verificarLinguagem() {
        //Verifica idioma do dispositivo
        String lingua = Locale.getDefault().getDisplayLanguage();
        if (lingua.equals("English")){
            //Menssagens
            EmailVerifyWarn = "Something went wrong, try again";
            EmailVerifyOk = "Registered Successfully, Verification email sent!";
            EmailJaEmUso = "This email are already registed";
            registering = "Registering new user";
            campoObrigatorio = "Field required";
            passLenght="Password minimum lenght is 6!";
            lingua="en";
            verifyConnect = "Check your Internet connection";

            inputPassword.setHint("Password");
            inputEmail.setHint("Email");
            inputRepeatPassword.setHint("Confirm password");
            inputName.setHint("Name");
            gotoLogin.setText("Back to login");
            btnRegister.setText("Sign Up");

        }else if(lingua.equals("português")){
            //Menssagens
            EmailVerifyWarn = "Ocorreu algum erro, tente novamente";
            EmailVerifyOk = "Registado com Sucesso, verifique o seu email!";
            EmailJaEmUso = "Email já está em uso";
            registering = "Registando novo utilizador";
            campoObrigatorio = "Campo obrigatório";
            passLenght="A senha tem que ter pelo menos 6 caracteres!";
            lingua = "pt";
            verifyConnect = "Verifique a sua conexão á Internet";
            inputPassword.setHint("Senha");
            inputEmail.setHint("Email");
            inputRepeatPassword.setHint("Confirmar senha");
            inputName.setHint("Nome");
            gotoLogin.setText("Voltar ao Login");
            btnRegister.setText("Registar");

        }else if(lingua.equals("简体中文")){

            //Menssagens
            EmailVerifyWarn = "发生错误，请重试";
            EmailVerifyOk = "注册成功，请检查您的电子邮件！";
            EmailJaEmUso = "电子邮件已在使用中";
            registering = "注册新用户";
            campoObrigatorio = "必填项目";
            passLenght="密码长度必须至少为6个字符！";
            lingua = "cn";
            verifyConnect = "检查您的互联网连接";
            inputPassword.setHint("密码");
            inputEmail.setHint("电子邮件");
            inputRepeatPassword.setHint("确认密码");
            inputName.setHint("名称");
            gotoLogin.setText("回到登入");
            btnRegister.setText("寄存器");

        }else if(lingua.equals("français")){
            //Menssagens
            EmailVerifyWarn = "Une erreur s'est produite, réessayez";
            EmailVerifyOk = "Enregistré avec succès, e-mail de vérification envoyé!";
            EmailJaEmUso = "Cet e-mail est déjà enregistré";
            registering = "Enregistrement d'un nouvel utilisateur";
            campoObrigatorio = "Champ obligatoire";
            passLenght="La longueur minimale du mot de passe est de 6!";
            lingua = "fr";
            verifyConnect = "Vérifiez votre connection internet";

            inputPassword.setHint("Mot de passe");
            inputEmail.setHint("Email");
            inputRepeatPassword.setHint("Confirmer le mot de passe");
            inputName.setHint("Nom");
            gotoLogin.setText("Retour à la connexion");
            btnRegister.setText("Inscription");
        }
        return lingua;
    }
}