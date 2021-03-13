package com.examples.ourpetsdc.forum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.examples.ourpetsdc.Firebase.Comment;
import com.examples.ourpetsdc.Firebase.Like;
import com.examples.ourpetsdc.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class detailed_post extends AppCompatActivity {
    //Variaveis do layout
    ImageView imgPost, like_state;
    CircleImageView imgUserPost,imgCurrentUser;
    TextView txtPostDesc,txtPostDateName,txtPostTitle;
    EditText editTextComment;
    Button btnAddComment;
    RecyclerView rv_comment;
    LinearLayout openComment, like;
    ConstraintLayout addCommentField;

    //Like count --||-- Comment count
    private ImageView nbLikes, nbComments;
    private TextView countLikes, countComments;

    private int state_comment = 1;
    //recebe valor da key
    private String getKey;
    //Gerar keys
    private int stateLike;
    private String key = UUID.randomUUID().toString();
    //firebase variables
    private FirebaseAuth auth;
    private DatabaseReference mRefPost;
    private String mCurrentUser;
    FirebaseRecyclerAdapter<Comment, List_Comments> all_comments;

    //Verificar a ligação a internet
    private static final String LOG_TAG = "CheckNetworkStatus";
    private NetworkChangeReceiver receiver;
    private boolean isConnected = false;
    private RelativeLayout connectedNetwork;
    private ImageView NoConnectedNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_post);
        //Verificar a conexao a internet
        NoConnectedNetwork = findViewById(R.id.NoConnectedNetwork);
        connectedNetwork = findViewById(R.id.connectedNetwork);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);
        //get key value to string
        getKey = getIntent().getStringExtra("pub_id");

        nbLikes = findViewById(R.id.nbLikes);
        nbComments = findViewById(R.id.nbComments);
        countLikes = findViewById(R.id.countLikes);
        countComments = findViewById(R.id.countComments);


        like_state = findViewById(R.id.like_state);
        openComment = findViewById(R.id.openComment);
        addCommentField = findViewById(R.id.addCommentField);
        like = findViewById(R.id.like);
        imgPost =findViewById(R.id.post_detail_img);
        imgUserPost = findViewById(R.id.post_detail_user_img);
        imgCurrentUser = findViewById(R.id.post_detail_currentuser_img);
        txtPostTitle = findViewById(R.id.post_detail_title);
        txtPostDesc = findViewById(R.id.post_detail_desc);
        txtPostDateName = findViewById(R.id.post_detail_date_name);
        rv_comment = findViewById(R.id.rv_comment);
        rv_comment.setHasFixedSize(true);
        rv_comment.setLayoutManager(new LinearLayoutManager(this));
        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment_btn);
        btnAddComment.setOnClickListener(v->{

            publicarComentario();

        });

        //Firebase link
        auth = FirebaseAuth.getInstance();
        mCurrentUser = auth.getCurrentUser().getUid();
        mRefPost = FirebaseDatabase.getInstance().getReference().child("Pubs").child(getKey);
        mRefPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("title").getValue(String.class);
                String description = snapshot.child("description").getValue(String.class);
                String picture = snapshot.child("picture").getValue(String.class);
                String userPhoto = snapshot.child("userPhoto").getValue(String.class);
                String userId = snapshot.child("userId").getValue(String.class);
                long timestamp = snapshot.child("timestamp").getValue(long.class);
                txtPostTitle.setText(title);
                txtPostDesc.setText(description);
                Glide.with(getApplicationContext()).load(picture).into(imgPost);
                Glide.with(getApplicationContext()).load(userPhoto).into(imgUserPost);
                FirebaseDatabase.getInstance().getReference().child("Utilizador").child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nome = snapshot.child("nome").getValue(String.class);
                        String imagem = snapshot.child("imagem").getValue(String.class);
                        String timeago = timestampToString(timestamp);
                        Glide.with(getApplicationContext()).load(imagem).into(imgCurrentUser);
                        txtPostDateName.setText(timeago + " | " + nome);
                        String lingua = snapshot.child("lingua").getValue(String.class);
                        editTextComment.setText("");
                        if(lingua.equals("en")){
                            editTextComment.setHint("Comment...");
                        }if(lingua.equals("pt")){
                            editTextComment.setHint("Comentar...");
                        }
                        if(lingua.equals("cn")){
                            editTextComment.setHint("评论...");
                        }
                        if(lingua.equals("fr")){
                            editTextComment.setHint("Commentaire...");
                        }
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


        openComment.setOnClickListener(v ->{

            if (state_comment == 1){
                state_comment=2;
                addCommentField.setVisibility(View.VISIBLE);

            }else{
                state_comment=1;
                addCommentField.setVisibility(View.GONE);
            }


        });

            FirebaseDatabase.getInstance().getReference().child("likes").child(getKey).child(mCurrentUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        stateLike = 2;
                        like_state.setImageResource(R.drawable.ic_like);
                    }else {
                        stateLike = 1;
                        like_state.setImageResource(R.drawable.ic_like_1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        like.setOnClickListener(v ->{

            setLike();


        });

        //sistema de Comentarios
        FirebaseDatabase.getInstance().getReference().child("Comments").child(getKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size = (int) snapshot.getChildrenCount();
                if (size == 0) {
                    FirebaseDatabase.getInstance().getReference().child("Utilizador").child(mCurrentUser).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String lingua = snapshot.child("lingua").getValue(String.class);
                            editTextComment.setText("");
                            if(lingua.equals("en")){
                                countComments.setText("Comments");
                            }if(lingua.equals("pt")){
                                countComments.setText("Comentários");
                            }
                            if(lingua.equals("cn")){
                                countComments.setText("评论");
                            }
                            if(lingua.equals("fr")){
                                countComments.setText("Commentaires");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    countComments.setText(Integer.toString(size));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //sistema de likes
        FirebaseDatabase.getInstance().getReference().child("likes").child(getKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size = (int) snapshot.getChildrenCount();
                if (size == 0) {
                    FirebaseDatabase.getInstance().getReference().child("Utilizador").child(mCurrentUser).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String lingua = snapshot.child("lingua").getValue(String.class);
                            editTextComment.setText("");
                            if(lingua.equals("en")){
                                countLikes.setText("Likes");
                            }if(lingua.equals("pt")){
                                countLikes.setText("Gostos");
                            }
                            if(lingua.equals("cn")){
                                countLikes.setText("喜欢");
                            }
                            if(lingua.equals("fr")){
                                countLikes.setText("Aime");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    countLikes.setText(Integer.toString(size));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setLike() {
        if (stateLike==2){
            stateLike=1;
            like_state.setImageResource(R.drawable.ic_like_1);
            FirebaseDatabase.getInstance().getReference().child("likes").child(getKey).child(mCurrentUser).child("state").removeValue();
        }else{
            stateLike=2;
            long state = 1;
            Like like = new Like(state);
            like_state.setImageResource(R.drawable.ic_like);
            FirebaseDatabase.getInstance().getReference().child("likes").child(getKey).child(mCurrentUser).setValue(like);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Query allData = FirebaseDatabase.getInstance().getReference().child("Comments").child(getKey).orderByChild("timestamp");

        FirebaseRecyclerOptions<Comment> options =
                new FirebaseRecyclerOptions.Builder<Comment>()
                        .setQuery(allData, Comment.class)
                        .setLifecycleOwner(this)
                        .build();

        all_comments = new FirebaseRecyclerAdapter<Comment, detailed_post.List_Comments>(options) {
            @NonNull
            @Override
            public detailed_post.List_Comments onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new List_Comments(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.post_comment, parent, false));
            }
            @Override
            protected void onBindViewHolder(@NonNull final detailed_post.List_Comments holder, int i, @NonNull final Comment model) {
                final String keys = getRef(i).getKey();
                FirebaseDatabase.getInstance().getReference().child("Comments").child(getKey).child(keys).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String comentario = snapshot.child("content").getValue(String.class);
                        String nome = snapshot.child("uname").getValue(String.class);
                        String imagemUser = snapshot.child("uimg").getValue(String.class);
                        long timestamp = snapshot.child("timestamp").getValue(long.class);
                        String timeago = timestampToString(timestamp);
                        holder.comment_content.setText(comentario);
                        holder.comment_username.setText(nome);
                        holder.comment_date.setText(timeago);
                        Glide.with(getApplicationContext()).load(imagemUser).into(holder.comment_user_img);
                        String userdId = snapshot.child("uid").getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };
        rv_comment.setAdapter(all_comments);
    }


    public static class List_Comments extends RecyclerView.ViewHolder {

        View mView;
        TextView comment_username, comment_content, comment_date;
        CircleImageView comment_user_img;
        public List_Comments(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            comment_date = mView.findViewById(R.id.comment_date);
            comment_content = mView.findViewById(R.id.comment_content);
            comment_username = mView.findViewById(R.id.comment_username);
            comment_user_img = mView.findViewById(R.id.comment_user_img);
        }

    }

    private void publicarComentario() {

        long timestamp = 0;
        String content = editTextComment.getText().toString();
        FirebaseDatabase.getInstance().getReference().child("Utilizador").child(mCurrentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uname = snapshot.child("nome").getValue(String.class);
                String uimg = snapshot.child("imagem").getValue(String.class);
                Comment comment = new Comment(content, mCurrentUser, uimg, uname, timestamp);
                FirebaseDatabase.getInstance().getReference().child("Comments").child(getKey).child(key).setValue(comment);
                FirebaseDatabase.getInstance().getReference().child("Comments").child(getKey).child(key).child("timestamp").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseDatabase.getInstance().getReference().child("Utilizador").child(mCurrentUser).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                key = "";
                                key = UUID.randomUUID().toString();
                                String lingua = snapshot.child("lingua").getValue(String.class);
                                editTextComment.setText("");
                                if(lingua.equals("en")){
                                    editTextComment.setHint("Comment...");
                                }if(lingua.equals("pt")){
                                    editTextComment.setHint("Comentar...");
                                }
                                if(lingua.equals("cn")){
                                    editTextComment.setHint("评论...");
                                }
                                if(lingua.equals("fr")){
                                    editTextComment.setHint("Commentaire...");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;


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