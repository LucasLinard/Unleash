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

public class CrudStopLoss extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CrudStopLoss.class.getSimpleName();
    Spinner mSpinner;
    Button mSave;
    EditText mCotacao;
    EditText mQuantidade;
    StopLoss mStopLoss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crud_stop_loss);
        mCotacao = findViewById(R.id.edit_cotacao);
        mQuantidade = findViewById(R.id.edit_quantidade);
        mSave = findViewById(R.id.stop_loss_btn_save);
        mSave.setOnClickListener(this);

        mSpinner = (Spinner) findViewById(R.id.spinner_exchange);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.exchanges_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        Intent intent = this.getIntent();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stop_loss_btn_save:
                attemptSave();
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
            insertInFireBase(cotacao, quantidade, mSpinner.getSelectedItemPosition());
        }
    }

    private void insertInFireBase(Double cotacao, Double quantidade, int selectedItemPosition) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final StopLoss stopLoss = new StopLoss();
            stopLoss.setUuid(firebaseUser.getUid());
            stopLoss.setQuantidadeBTC(quantidade);
            stopLoss.setExchangeId(selectedItemPosition);
            stopLoss.setCotacaoBTC(cotacao);

            db.collection("stop_loss")
                    .add(stopLoss)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            stopLoss.setId(documentReference.getId());
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference dr = db.collection("stop_loss").document(stopLoss.getId());
                            dr.update("id", stopLoss.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }
    }
}
