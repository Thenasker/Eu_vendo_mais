package com.thenasker.euvendomais;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class NovaVenta extends AppCompatActivity {

    private CircleImageView imgClientaDetalhe;
    private Button btnAdd;
    static final int REQUEST_IMAGE_CAPTURE = 1001;
    private String mCurrentPhotoPath;
    private Bitmap bitmap;
    private Spinner spnNotificacion;
    private EditText txtNome, txtValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_venta);


        imgClientaDetalhe = (CircleImageView)findViewById(R.id.imgClientaDetalhe);
        btnAdd = (Button)findViewById(R.id.add_comrpa);
        spnNotificacion = (Spinner)findViewById(R.id.spnNoti);
        txtNome = (EditText)findViewById(R.id.txtNomeDetalhe);
        txtValor = (EditText)findViewById(R.id.txtValorDetalhe);

        imgClientaDetalhe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VentasSQLiteHelper usdbh =
                        new VentasSQLiteHelper(NovaVenta.this, "DBVentas", null, 1);

                SQLiteDatabase db = usdbh.getWritableDatabase();

                SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
                String format = s.format(new Date());

                Calendar c = Calendar.getInstance();
                try{
                    c.setTime(s.parse(format));
                    switch (spnNotificacion.getSelectedItemPosition()){
                        case 0:
                            c.add(Calendar.DATE, 7);
                            break;
                        case 1:
                            c.add(Calendar.DATE, 15);
                            break;
                        case 2:
                            c.add(Calendar.DATE, 30);
                            break;
                        default:
                            break;
                    }
                }catch(ParseException e){

                }
                Date resultdate = new Date(c.getTimeInMillis());
                String dateInString = s.format(resultdate);


                //Creamos el registro a insertar como objeto ContentValues
                ContentValues nuevoRegistro = new ContentValues();
                nuevoRegistro.put("nome", txtNome.getText().toString());
                nuevoRegistro.put("rota",mCurrentPhotoPath);
                nuevoRegistro.put("valorfin", Double.valueOf(txtValor.getText().toString()));
                nuevoRegistro.put("limite",spnNotificacion.getSelectedItemPosition());
                nuevoRegistro.put("timestamp", format);
                nuevoRegistro.put("timestampfin",dateInString);
                nuevoRegistro.put("valorpago",0);
                //Insertamos el registro en la base de datos
                db.insert("Ventas", null, nuevoRegistro);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("IMG",mCurrentPhotoPath);
                returnIntent.putExtra("VALOR",txtValor.getText().toString());
                returnIntent.putExtra("NOTI",spnNotificacion.getSelectedItemPosition());
                returnIntent.putExtra("NOME",txtNome.getText().toString());
                setResult(NovaVenta.RESULT_OK,returnIntent);
                finish();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.thenasker.euvendomais",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        imgClientaDetalhe.setImageBitmap(bitmap);
    }
}
