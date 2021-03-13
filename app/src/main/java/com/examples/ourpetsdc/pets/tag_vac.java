package com.examples.ourpetsdc.pets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.examples.ourpetsdc.Firebase.Info_Vacina_Desp;
import com.examples.ourpetsdc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.examples.ourpetsdc.pets.pet_page.PetID;


public class tag_vac extends RecyclerView.Adapter<tag_vac.AdapterVacs> {
    private int is =1;
    private Context context;
    ArrayList<Info_Vacina_Desp> tag_vacs;
    FirebaseAuth mAuth;
    String date, hora;
    public tag_vac(Context ctx , ArrayList<Info_Vacina_Desp> g) {
        context = ctx;
        tag_vacs = g;
    }
    @NonNull
    @Override
    public tag_vac.AdapterVacs onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new tag_vac.AdapterVacs(LayoutInflater.from(context).inflate(R.layout.list_vacinas, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final tag_vac.AdapterVacs holder, final int i) {
        if (tag_vacs.get(i).getTipo().equals("Vaccine") || tag_vacs.get(i).getTipo().equals("vacina") || tag_vacs.get(i).getTipo().equals("疫苗") || tag_vacs.get(i).getTipo().equals("vaccin")) {
            holder.txtTipo.setText(tag_vacs.get(i).getTipo());
            holder.txtDataVac.setText(tag_vacs.get(i).getData());
            holder.txtHoraVac.setText(tag_vacs.get(i).getHora());
            holder.txtNomeVac.setText(tag_vacs.get(i).getNome());
            holder.rl_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (is == 1){
                        is = 2;
                        holder.rl_moreData.setVisibility(View.VISIBLE);
                        holder.img_info.setImageResource(R.drawable.ic_up_arrow);
                    }else if (is == 2){
                        is = 1;
                        holder.img_info.setImageResource(R.drawable.ic_down_arrow);
                        holder.rl_moreData.setVisibility(View.GONE);
                    }
                }
            });
            if (tag_vacs.get(i).getStatus().equals("-")){
                holder.chkBoxVacina.setChecked(false);
            }else{
                holder.chkBoxVacina.setChecked(true);
            }
            final DatabaseReference RefStatus =   FirebaseDatabase.getInstance().getReference().child("boletim").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(PetID).child(tag_vacs.get(i).getKey());
            holder.chkBoxVacina.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tag_vacs.get(i).getStatus().equals("-")){
                        holder.chkBoxVacina.setChecked(true);
                        RefStatus.child("status").setValue("true");
                    }else {
                        holder.chkBoxVacina.setChecked(false);
                        RefStatus.child("status").setValue("-");
                    }
                }
            });

            holder.editChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.rl_moreData.setVisibility(View.VISIBLE);
                    holder.txtDataVac.setVisibility(View.INVISIBLE);
                    holder.txtHoraVac.setVisibility(View.INVISIBLE);
                    //hora = holder.txtHoraVac.getText().toString().trim();
                    //date = holder.txtDataVac.getText().toString().trim();
                    holder.txtEditHoraVac.setVisibility(View.VISIBLE);
                    holder.txtEditDataVac.setVisibility(View.VISIBLE);
                    /*holder.txtEditDataVac.setHint("");
                    holder.txtEditDataVac.setHint("dd/mm/YYYY");*/
                    //holder.txtEditHoraVac.setHint("hh:mm");
                    holder.imgCanceChanges.setVisibility(View.VISIBLE);
                    holder.imgSaveChanges.setVisibility(View.VISIBLE);
                    holder.editChange.setVisibility(View.GONE);
                }
            });
            holder.imgSaveChanges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Recebe os valores das Edits e Spinners e guarda-os em strings
                    String dia = holder.txtDataDia.getText().toString().trim();
                    String mes = holder.txtDataMes.getText().toString().trim();
                    String ano = holder.txtDataAno.getText().toString().trim();
                    int inAno= 0, inMes= 0, inDia = 0;
                    if (!dia.equals("")){
                        inDia = Integer.parseInt(dia);
                        if (inDia >= 32 || inDia <= 0){
                            holder.txtDataDia.requestFocus();
                        }
                    }else{
                        holder.txtDataDia.requestFocus();
                    }
                    if (!mes.equals("")){
                        inMes = Integer.parseInt(mes);
                        if (inMes >= 13 || inMes <= 0){
                            holder.txtDataMes.requestFocus();
                        }
                    }else {
                        holder.txtDataMes.requestFocus();
                    }
                    if (!ano.equals("")){
                        inAno = Integer.parseInt(ano);
                        if (inAno >= 2019){
                            holder.txtDataAno.requestFocus();
                        }
                    }else {
                        holder.txtDataAno.requestFocus();
                    }

                    //Receber dados Hora
                    String hh = holder.txtHora.getText().toString().trim();
                    String mm = holder.txtMin.getText().toString().trim();
                    int inHH = 0, inMM = 0;
                    if (!hh.equals("")){
                        inHH = Integer.parseInt(hh);
                        if (inHH >= 25 || inHH <= 0){
                            holder.txtHora.requestFocus();
                        }
                    }else {
                        holder.txtHora.requestFocus();
                    }
                    if (!mm.equals("")){
                        inMM = Integer.parseInt(mm);
                        if (inMM >= 61 || inMM <= 0){
                            holder.txtMin.requestFocus();
                        }
                    }else{
                        holder.txtMin.requestFocus();
                    }

                    if (inHH <= 9){

                        hh = "0" + inHH;

                    }
                    if (inMM <= 9){

                        mm = "0" +inMM;

                    }
                    if (inDia <= 9){

                        dia = "0" +inDia;

                    }

                    if (inMes <= 9){

                        mes = "0" +inMes;

                    }


                    if ( inDia >= 1 && inDia <= 31 && inMes >= 1 && inMes <= 12 && inAno >= 2020 && inHH >= 1 && inHH <= 24 && inMM >= 1 && inMM <= 60 ){
                        String data = dia + "/" + mes + "/" + ano;
                        String hour = hh + ":" + mm;
                        Map updateMap = new HashMap();
                        updateMap.put("data", data);
                        updateMap.put("hora", hour);
                        FirebaseDatabase.getInstance().getReference().child("boletim").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(PetID).child(tag_vacs.get(i).getKey()).updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()){
                                    holder.rl_moreData.setVisibility(View.VISIBLE);
                                    holder.txtDataVac.setEnabled(false);
                                    holder.txtHoraVac.setEnabled(false);
                                    holder.txtHoraVac.setVisibility(View.VISIBLE);
                                    holder.txtDataVac.setVisibility(View.VISIBLE);

                                    holder.txtEditHoraVac.setVisibility(View.GONE);
                                    holder.txtEditDataVac.setVisibility(View.GONE);
                                    holder.imgCanceChanges.setVisibility(View.GONE);
                                    holder.imgSaveChanges.setVisibility(View.GONE);
                                    holder.editChange.setVisibility(View.VISIBLE);
                                }else {

                                }
                            }
                        });
                    }

                }
            });
            holder.imgCanceChanges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.rl_moreData.setVisibility(View.VISIBLE);
                    holder.txtDataVac.setEnabled(false);
                    holder.txtHoraVac.setEnabled(false);
                    holder.txtHoraVac.setVisibility(View.VISIBLE);
                    holder.txtDataVac.setVisibility(View.VISIBLE);

                    holder.txtEditHoraVac.setVisibility(View.GONE);
                    holder.txtEditDataVac.setVisibility(View.GONE);
                    holder.imgCanceChanges.setVisibility(View.GONE);
                    holder.imgSaveChanges.setVisibility(View.GONE);
                    holder.editChange.setVisibility(View.VISIBLE);
                }
            });

        }else{
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }
    @Override
    public int getItemCount() {
        return tag_vacs.size();
    }
    public class AdapterVacs extends RecyclerView.ViewHolder {
        EditText txtDataVac, txtHoraVac, txtTipo, txtNomeVac;
        CheckBox chkBoxVacina;
        RelativeLayout rl_moreData, rl_plus;
        ImageView editChange, imgCanceChanges, imgSaveChanges, img_info;
        RelativeLayout txtEditDataVac, txtEditHoraVac;

        EditText txtDataDia, txtDataMes, txtDataAno;

        EditText txtHora, txtMin;

        public AdapterVacs(@NonNull View itemView) {
            super(itemView);
            imgSaveChanges = itemView.findViewById(R.id.imgSaveChanges);
            imgSaveChanges.setVisibility(View.GONE);
            imgCanceChanges = itemView.findViewById(R.id.imgCanceChanges);
            imgCanceChanges.setVisibility(View.GONE);
            editChange = itemView.findViewById(R.id.editChange);
            img_info = itemView.findViewById(R.id.img_info);
            txtEditDataVac = itemView.findViewById(R.id.txtEditDataVac);
            txtEditHoraVac = itemView.findViewById(R.id.txtEditHoraVac);
            txtDataVac = itemView.findViewById(R.id.txtDataVac);
            txtDataVac.setEnabled(false);
            txtHoraVac = itemView.findViewById(R.id.txtHoraVac);
            txtHoraVac.setEnabled(false);
            txtTipo = itemView.findViewById(R.id.txtTipo);
            txtTipo.setEnabled(false);
            txtNomeVac = itemView.findViewById(R.id.txtNomeVac);
            txtNomeVac.setEnabled(false);
            chkBoxVacina = itemView.findViewById(R.id.chkBoxVacina);
            rl_moreData = itemView.findViewById(R.id.rl_moreData);
            rl_plus = itemView.findViewById(R.id.rl_plus);
            rl_moreData.setVisibility(View.GONE);

            //Data
            txtDataDia = itemView.findViewById(R.id.txtDataDia);
            txtDataMes = itemView.findViewById(R.id.txtDataMes);
            txtDataAno = itemView.findViewById(R.id.txtDataAno);

            //Horas
            txtHora = itemView.findViewById(R.id.txtHora);
            txtMin = itemView.findViewById(R.id.txtMin);
            FirebaseDatabase.getInstance().getReference("Utilizador")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String lingua = snapshot.child("lingua").getValue(String.class);
                    if (lingua.equals("en")){
                        chkBoxVacina.setText("Fulfilled");
                    }else if (lingua.equals("pt")){
                        chkBoxVacina.setText("Realizada");
                    }else if (lingua.equals("cn")){
                        chkBoxVacina.setText("已完成");
                    }else if (lingua.equals("fr")){
                        chkBoxVacina.setText("Rempli");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}
