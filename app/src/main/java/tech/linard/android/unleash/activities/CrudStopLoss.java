package tech.linard.android.unleash.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import tech.linard.android.unleash.R;

public class CrudStopLoss extends AppCompatActivity implements View.OnClickListener {

    Button mSave;
    EditText mCotacao;
    EditText mQuantidade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud_stop_loss);
        mCotacao = findViewById(R.id.edit_cotacao);
        mQuantidade = findViewById(R.id.edit_quantidade);
        mSave = findViewById(R.id.stop_loss_btn_save);
        mSave.setOnClickListener(this);
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

        // Store Values
        Double cotacao = mCotacao.

    }

}
