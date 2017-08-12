package com.thenasker.euvendomais;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static java.lang.Double.valueOf;

public class NovoPago extends AppCompatActivity {

    private EditText txtCartao1,txtCartao2,txtCartao3,txtCartao4,txtQuantidade;
    private Spinner spnMetodo;
    private ImageView img;
    private TextView lblNome, lblzeroPago,lblTotalPago;
    private TextProgressBar prog;
    private Button btnPago;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_pago);

        txtCartao1 = (EditText)findViewById(R.id.txtCartao1);
        txtCartao2 = (EditText)findViewById(R.id.txtCartao2);
        txtCartao3 = (EditText)findViewById(R.id.txtCartao3);
        txtCartao4 = (EditText)findViewById(R.id.txtCartao4);
        img = (ImageView)findViewById(R.id.imgClientaPago);
        lblNome = (TextView)findViewById(R.id.lblNomePago);
        prog = (TextProgressBar)findViewById(R.id.progPagamentoPago);
        lblzeroPago = (TextView)findViewById(R.id.lblzeroPago);
        lblTotalPago = (TextView)findViewById(R.id.lblTotalPago);
        btnPago = (Button)findViewById(R.id.add_pago);
        txtQuantidade = (EditText)findViewById(R.id.txtQuantidade);

        i = getIntent();

        lblNome.setText(i.getStringExtra("NOME"));

        try {
            if(i.getStringExtra("IMG")!= null && !i.getStringExtra("IMG").equals("")) {
                File f = new File(i.getStringExtra("IMG"));
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                img.setImageBitmap(b);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        lblTotalPago.setText(valueOf(i.getDoubleExtra("VALOR",0.0)).toString() + " R$");

        prog.setProgress((int) ((i.getDoubleExtra("PAGO",0.0) * 100) / i.getDoubleExtra("VALOR",0.0)), true);
        prog.setText(valueOf(i.getDoubleExtra("PAGO",0.0)).toString() + " R$");

        spnMetodo = (Spinner)findViewById(R.id.spnMetodo);

        spnMetodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        txtCartao1.setVisibility(View.GONE);
                        txtCartao2.setVisibility(View.GONE);
                        txtCartao3.setVisibility(View.GONE);
                        txtCartao4.setVisibility(View.GONE);
                        break;
                    case 1:
                        txtCartao1.setVisibility(View.GONE);
                        txtCartao2.setVisibility(View.GONE);
                        txtCartao3.setVisibility(View.GONE);
                        txtCartao4.setVisibility(View.GONE);
                        break;
                    case 2:
                        txtCartao1.setVisibility(View.VISIBLE);
                        txtCartao2.setVisibility(View.VISIBLE);
                        txtCartao3.setVisibility(View.VISIBLE);
                        txtCartao4.setVisibility(View.VISIBLE);
                        break;
                    default:
                        txtCartao1.setVisibility(View.GONE);
                        txtCartao2.setVisibility(View.GONE);
                        txtCartao3.setVisibility(View.GONE);
                        txtCartao4.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                txtCartao1.setVisibility(View.GONE);
                txtCartao2.setVisibility(View.GONE);
                txtCartao3.setVisibility(View.GONE);
                txtCartao4.setVisibility(View.GONE);
            }
        });

        btnPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i.getDoubleExtra("PAGO",0.0) + valueOf(txtQuantidade.getText().toString()) > i.getDoubleExtra("VALOR",0.0)){

                    Toast.makeText(NovoPago.this,"Quantidade maior que o pago faltante",Toast.LENGTH_SHORT).show();

                }else{

                    VentasSQLiteHelper usdbh =
                            new VentasSQLiteHelper(NovoPago.this, "DBVentas", null, 1);

                    SQLiteDatabase db = usdbh.getWritableDatabase();

//Creamos el registro a insertar como objeto ContentValues
                    ContentValues nuevoRegistro = new ContentValues();
                    nuevoRegistro.put("valorpago",valueOf(txtQuantidade.getText().toString()));


                    String[] args = new String[]{i.getStringExtra("NOME"), i.getStringExtra("TIM")};
                    db.update("Ventas", nuevoRegistro, "nome=? AND timestamp=?", args);

                    if(spnMetodo.getSelectedItemPosition() == 1 || spnMetodo.getSelectedItemPosition() == 2){
                        AlertDialog.Builder builder = new AlertDialog.Builder(NovoPago.this); //alert for confirm to delete
                        builder.setMessage("Simulação de pago com cartao ou generação de boleto.");    //set message

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { //when click on DELETE
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("PAGO",i.getDoubleExtra("PAGO",0.0) + valueOf(txtQuantidade.getText().toString()));
                                returnIntent.putExtra("POS",i.getIntExtra("POS",0));
                                setResult(NovaVenta.RESULT_OK,returnIntent);
                                finish();
                            }
                        }).show();
                    }else {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("PAGO", i.getDoubleExtra("PAGO", 0.0) + valueOf(txtQuantidade.getText().toString()));
                        returnIntent.putExtra("POS",i.getIntExtra("POS",0));
                        setResult(NovaVenta.RESULT_OK, returnIntent);
                        finish();
                    }
                }
            }
        });

    }
}
