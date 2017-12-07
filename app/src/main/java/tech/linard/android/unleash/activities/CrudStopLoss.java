package tech.linard.android.unleash.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import tech.linard.android.unleash.R;
import tech.linard.android.unleash.model.StopLoss;
import tech.linard.android.unleash.model.User;

public class CrudStopLoss extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CrudStopLoss.class.getSimpleName();
    Spinner mSpinner;
    Button mSave;
    Button mDelete;
    EditText mCotacao;
    EditText mQuantidade;
    StopLoss mStopLoss;
    boolean edit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crud_stop_loss);
        mCotacao = findViewById(R.id.edit_cotacao);
        mQuantidade = findViewById(R.id.edit_quantidade);
        mSave = findViewById(R.id.stop_loss_btn_save);
        mSave.setOnClickListener(this);
        mDelete = findViewById(R.id.stop_loss_btn_delete);
        mDelete.setOnClickListener(this);
        mDelete.setEnabled(false);


        mSpinner = (Spinner) findViewById(R.id.spinner_exchange);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.exchanges_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        Intent intent = this.getIntent();
        String id = intent.getStringExtra("id");
        String uuid = intent.getStringExtra("uuid");
        int exchangeId = intent.getIntExtra("exchangeId",0);
        double cotacaoBTC = intent.getDoubleExtra("cotacaoBTC",0.0);
        double quantidadeBTC = intent.getDoubleExtra("quantidadeBTC",0.0);

        mStopLoss = new StopLoss(id, uuid, exchangeId, cotacaoBTC, quantidadeBTC);
        edit = false;
        if (id != null) {
            edit = true;
            mDelete.setEnabled(true);
            preencheCampos(mStopLoss);
        }

    }

    private void preencheCampos(StopLoss mStopLoss) {
        mCotacao.setText(String.valueOf(mStopLoss.getCotacaoBTC()));
        mQuantidade.setText(String.valueOf(mStopLoss.getQuantidadeBTC()));
        mSpinner.setSelection(mStopLoss.getExchangeId());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stop_loss_btn_save:
                attemptSave();
                finish();
                break;
            case R.id.stop_loss_btn_delete:
                deleteFirebase();
                finish();
                break;
        }
    }

    private void attemptSave() {
        //Reset errors
        mCotacao.setError(null);
        mQuantidade.setError(null);
        Double cotacao = 0.0;
        Double quantidade = 0.0;

        // Store Values
        if (!mCotacao.getText().toString().isEmpty()) {
            try {
                cotacao = Double.parseDouble(String.valueOf(mCotacao.getText()));
            } catch (Exception e) {
                cotacao = 0.0;
            }
        }
        if (!mQuantidade.getText().toString().isEmpty()) {
            try {
                quantidade = Double.parseDouble(String.valueOf(mQuantidade.getText()));
            } catch (Exception e) {
                quantidade = 0.0;
            }
        }

        Boolean cancel = false;
        View focusView = null;

        // Check Cotação
        if (cotacao == 0.0) {
            cancel = true;
            mCotacao.setError(getString(R.string.erro_valor_zero));
            focusView = mCotacao;
        }
        if (quantidade == 0.0) {
            cancel = true;
            mQuantidade.setError(getString(R.string.erro_valor_zero));
            focusView = mQuantidade;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mStopLoss.setCotacaoBTC(cotacao);
            mStopLoss.setQuantidadeBTC(quantidade);

            if (edit) {
                updateInFireBase();
            } else {
                insertInFireBase();
            }
        }
    }

    private void updateInFireBase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("stop_loss").document(mStopLoss.getId())
                    .set(mStopLoss)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CrudStopLoss.this, "Registro Salvo", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CrudStopLoss.this, "Falha ao salvar Stop Loss", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void insertInFireBase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            mStopLoss.setUuid(firebaseUser.getUid());
            mStopLoss.setExchangeId(mSpinner.getSelectedItemPosition());

            db.collection("stop_loss")
                    .add(mStopLoss)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            mStopLoss.setId(documentReference.getId());
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference dr = db.collection("stop_loss").document(mStopLoss.getId());
                            dr.update("id", mStopLoss.getId());
                            Toast.makeText(CrudStopLoss.this, "Registro Salvo", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CrudStopLoss.this, "Falha ao salvar Stop Loss", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("stop_loss").document(mStopLoss.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CrudStopLoss.this, "Registro excluído", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

}
