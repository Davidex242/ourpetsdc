package com.examples.ourpetsdc.launcher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.examples.ourpetsdc.MainActivity;
import com.examples.ourpetsdc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class login extends AppCompatActivity {
    //Variaveis layout
    private TextView gotoRegister, topText /*,forgotPassword*/;
    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private ProgressDialog progressDialog;
    //Variaveis de codigo
    private String idioma = "",loging, emailNaoVerificado, tentRestantes, tryAgain, changePassword, SentEmailChangePass,
            s, n, cancel, emailVerify, verifyConnect, campoObrigatorio;
    private int count = 3;
    //Firebase variaveis
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Firebase
        auth = FirebaseAuth.getInstance();

        //Link das variaveis com os objetos
        progressDialog = new ProgressDialog(this);
        topText = findViewById(R.id.topText);
        //forgotPassword = findViewById(R.id.forgotPassword);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        gotoRegister = findViewById(R.id.gotoRegister);
        gotoRegister.setOnClickListener(v -> {

                Intent intent = new Intent(login.this, registar.class);
                startActivity(intent);

                });
        idioma = verificarLinguagem(idioma);

        //Evento click do butao login
        btnLogin.setOnClickListener(v -> {

            final ConnectivityManager connMgr = (ConnectivityManager)
                    this.getSystemService(Context.CONNECTIVITY_SERVICE);
            final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifi.isConnectedOrConnecting() || mobile.isConnectedOrConnecting ()) {
                UserLogin();
            }else {
                Toast.makeText(this, verifyConnect, Toast.LENGTH_LONG).show();
            }

        });

        //Evento click para restaurar pass
        /*forgotPassword.setOnClickListener(v -> {

            final String email = inputEmail.getText().toString().trim();
            AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
            builder.setTitle(emailVerify).setMessage(email)
                    .setPositiveButton(s, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(login.this, SentEmailChangePass, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    })
                    .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            builder.create();
            builder.show();

        });*/
    }

    private void UserLogin() {

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (email.equals("")){

            Toast.makeText(this, campoObrigatorio, Toast.LENGTH_SHORT).show();
            return;

        }

        if (password.equals("")){

            Toast.makeText(this, campoObrigatorio, Toast.LENGTH_SHORT).show();
            return;

        }

        progressDialog.setMessage(loging);
        progressDialog.show();
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    progressDialog.dismiss();
                    Intent intent = new Intent(login.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    /*String current_user_id = auth.getCurrentUser().getUid();

                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    FirebaseDatabase.getInstance().getReference().child("Utilizador").child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Se pass e email estiverem ok, verifica o se a conta ja esta certificada
                            checkEmailVerification();

                        }
                    });*/

                }else{

                    progressDialog.dismiss();
                    count--;
                    Toast.makeText(login.this, tentRestantes + count, Toast.LENGTH_SHORT).show();


                    if (count == 0) {
                        inputEmail.setEnabled(false);
                        inputPassword.setEnabled(false);
                        btnLogin.setEnabled(false);
                        gotoRegister.setEnabled(false);
                        //txt_countDown.setVisibility(View.VISIBLE);
                        //forgotPassword.setVisibility(View.VISIBLE);
                        new CountDownTimer(30000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                count = 3;
                                //txt_countDown.setText(tryAgain + millisUntilFinished / 1000);
                                //forgotPassword.setText(changePassword);

                            }

                            public void onFinish() {
                                //txt_countDown.setVisibility(View.GONE);
                                //forgotPassword.setVisibility(View.GONE);
                                inputEmail.setEnabled(true);
                                inputPassword.setEnabled(true);
                                btnLogin.setEnabled(true);
                                gotoRegister.setEnabled(true);
                                recreate();
                                count = 3;
                            }

                        }.start();
                    }
                }
            }
        });

    }

    /*private void checkEmailVerification() {

        FirebaseUser firebaseUser = auth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();

        if (emailflag){

            Intent intent = new Intent(login.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);


        } else {
            Toast.makeText(this, emailNaoVerificado, Toast.LENGTH_SHORT).show();
            auth.signOut();
            progressDialog.dismiss();

        }

    }*/

    private String verificarLinguagem(String linguas) {
        //Vai buscar o idioma usado no telemovel
        String lingua = Locale.getDefault().getDisplayLanguage();
        if (lingua.equals("English")){
            //Menssagens
            loging = "Loging User";
            emailNaoVerificado= "Verify your email";
            tentRestantes = "Attempts remaining : ";
            tryAgain = "You can try again in : ";
            changePassword = "Change password";
            SentEmailChangePass = "We sent you a email to change your password";
            s = "yes";
            n = "no";
            cancel ="cancel";
            emailVerify = "Send for this email?";
            lingua="en";
            campoObrigatorio = "Field required";
            verifyConnect = "Check your Internet connection";
            inputPassword.setHint("Password");
            inputEmail.setHint("Email");
            gotoRegister.setText("Create account");
            btnLogin.setText("Login");
        }else if(lingua.equals("português")){
            //Menssagens
            loging = "Entrando";
            emailNaoVerificado= "Verifique o seu email";
            tentRestantes = "Tentativas restantes : ";
            tryAgain = "Pode voltar a tentar em : ";
            changePassword = "Mudar password";
            SentEmailChangePass = "Enviamos um email para poderes alterar a tua password";
            s = "sim";
            n = "não";
            cancel ="cancelar";
            emailVerify = "Enviar para este email?";
            lingua = "pt";
            campoObrigatorio = "Campo obrigatório";
            verifyConnect = "Verifique a sua conexão á Internet";
            inputPassword.setHint("Senha");
            inputEmail.setHint("Email");
            gotoRegister.setText("Criar conta");
            btnLogin.setText("Entrar");

        }else if(lingua.equals("简体中文")){

            //Menssagens
            loging = "进入";
            emailNaoVerificado= "查看你的电子邮件";
            tentRestantes = "剩余尝试：";
            tryAgain = "您可以在以下位置重试：";
            changePassword = "更改密码";
            SentEmailChangePass = "我们发送了一封电子邮件以更改您的密码";
            s = "是";
            n = "没有";
            cancel ="取消";
            emailVerify = "发送到此电子邮件？";
            lingua = "cn";
            campoObrigatorio = "必填项目";
            verifyConnect = "检查您的互联网连接";
            inputPassword.setHint("密码");
            inputEmail.setHint("电子邮件");
            gotoRegister.setText("创建一个帐户");
            btnLogin.setText("登录");

        }else if(lingua.equals("français")){
            //Menssagens
            loging = "Connexion";
            emailNaoVerificado= "Vérifiez votre email";
            tentRestantes = "Tentatives restantes:";
            tryAgain = "Vous pouvez réessayer dans:";
            changePassword = "Changer le mot de passe";
            SentEmailChangePass = "Nous vous avons envoyé un email pour changer votre mot de passe";
            s = "oui";
            n = "non";
            cancel ="annuler";
            emailVerify = "Envoyer pour cet e-mail?";
            lingua = "fr";
            campoObrigatorio = "Champ obligatoire";
            verifyConnect = "Vérifiez votre connection internet";
            inputPassword.setHint("Mot de passe");
            inputEmail.setHint("Email");
            gotoRegister.setText("Créer un compte");
            btnLogin.setText("Connexion");
        }
        linguas = lingua;
        return linguas;
    }
}