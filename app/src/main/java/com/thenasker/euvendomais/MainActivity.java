package com.thenasker.euvendomais;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Double.valueOf;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;
    private ArrayList<Venta> ventas;
    public static String PACKAGE_NAME;
    static final int REQUEST_NOVA = 1001;
    static final int REQUEST_PAGO = 2001;
    private TextView lblTotalNav, lblZeroNav;
    private RelativeLayout layLbl;
    private TextProgressBar progCobros;
    private SQLiteDatabase db;
    private TextProgressBar progPontos;
    private int maxPontos, atualPontos;
    private TextView lblCoisa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logobom);

        ventas = new ArrayList<Venta>();

        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        VentasSQLiteHelper usdbh =
                new VentasSQLiteHelper(this, "DBVentas", null, 1);

        db = usdbh.getWritableDatabase();

        //Si hemos abierto correctamente la base de datos
        if(db != null) {

            Cursor c = db.rawQuery(" SELECT nome, rota, valorfin, limite, timestamp, timestampfin, valorpago FROM Ventas", null);

            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    Venta v = new Venta(c.getString(0),
                            c.getString(1),
                            c.getDouble(2),
                            c.getInt(3),
                            c.getString(4),
                            c.getString(5),
                            c.getDouble(6));

                    ventas.add(v);

                } while (c.moveToNext());
            }
            double totalValor = 0.0;
            double totalPago = 0.0;

            for(Venta v: ventas){
                totalValor += v.getValorFin();
                totalPago += v.getValorPago();
            }

//Creamos el registro a insertar como objeto ContentValues
            if(totalPago < totalValor) {
                ContentValues nuevoRegistro = new ContentValues();
                nuevoRegistro.put("atual", valueOf((totalPago/2)*0.2));
                db.update("Pontos", nuevoRegistro, null, null);
            }else{
                ContentValues nuevoRegistro = new ContentValues();
                nuevoRegistro.put("atual", valueOf(totalValor));
                db.update("Pontos", nuevoRegistro, null, null);

            }

            Cursor c2 = db.rawQuery(" SELECT max, atual FROM Pontos", null);
            if (c2.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    maxPontos = c2.getInt(0);
                    atualPontos = c2.getInt(1);

                } while (c2.moveToNext());

            }
        }

        setSupportActionBar(toolbar);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nova = new Intent(MainActivity.this,NovaVenta.class);
                startActivityForResult(nova,REQUEST_NOVA);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);

        progPontos = (TextProgressBar)headerLayout.findViewById(R.id.progPontos);
        progPontos.setMax(maxPontos);
        progPontos.setProgress(atualPontos);
        progPontos.setText(atualPontos + " pontos");

        progCobros = (TextProgressBar)headerLayout.findViewById(R.id.progCobros);
        lblTotalNav = (TextView)headerLayout.findViewById(R.id.lblTotalNav);
        layLbl = (RelativeLayout)headerLayout.findViewById(R.id.layLbl);
        lblCoisa = (TextView)headerLayout.findViewById(R.id.lblCoisa);



        double totalValor = 0.0;
        double totalPago = 0.0;

        for(Venta v: ventas){
            totalValor += v.getValorFin();
            totalPago += v.getValorPago();
        }
        if(ventas.size()>0) {
            layLbl.setVisibility(View.VISIBLE);
            lblTotalNav.setText(Double.valueOf(totalValor).toString());
        }else{
            layLbl.setVisibility(View.GONE);
        }

        progCobros.setMax((int)totalValor);
        progCobros.setProgress((int)totalPago, true);
        progCobros.setText(valueOf(totalPago).toString() + " R$");

        lblCoisa.setText(valueOf((int)(totalValor/2)-(int)((totalPago/2)*0.2)).toString() + " pontos para ganhar um brinde exclusivo");

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(false);



        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(ventas,this);
        mRecyclerView.setAdapter(mAdapter);



        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                if (direction == ItemTouchHelper.LEFT) {    //if swipe left

                    if(ventas.get(position).getValorFin() == ventas.get(position).getValorPago()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); //alert for confirm to delete
                        builder.setMessage("Seguro que quer apagar?");    //set message

                        builder.setPositiveButton("APAGAR", new DialogInterface.OnClickListener() { //when click on DELETE
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String[] args = new String[]{ventas.get(position).getNome(), ventas.get(position).getTimestamp()};
                                db.delete("Ventas", "nome=? AND timestamp=?", args);

                                mAdapter.notifyItemRemoved(position);    //item removed from recylcerview
                                ventas.remove(position);


                                actualizarResumo();

                                return;
                            }
                        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                                return;
                            }
                        }).show();  //show alert dialog
                    }else{

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); //alert for confirm to delete
                        builder.setMessage("Não pode apagar a venta até ter completado o pago.");    //set message

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { //when click on DELETE
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                                return;
                            }
                        }).show();  //show alert dialog
                    }
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView); //set swipe to recylcerview



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_NOVA) {
            if(resultCode == MainActivity.RESULT_OK){

                SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
                String format = s.format(new Date());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                try{
                    c.setTime(s.parse(format));
                    switch (data.getIntExtra("NOTI",0)){
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

                Venta v = new Venta(data.getStringExtra("NOME"),
                        data.getStringExtra("IMG"),
                        Double.valueOf(data.getStringExtra("VALOR")),
                        data.getIntExtra("NOTI",0),
                        format,
                        dateInString,
                        0);

                ventas.add(v);
                mAdapter.notifyDataSetChanged();

                actualizarResumo();
            }
            if (resultCode == MainActivity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        else if (requestCode == REQUEST_PAGO) {
            if(resultCode == MainActivity.RESULT_OK) {
                ventas.get(data.getIntExtra("POS",0)).setValorPago(data.getDoubleExtra("PAGO",0.0));
                mAdapter.notifyDataSetChanged();

                actualizarResumo();

            //TODO
            }
        }
    }

    public void actualizarResumo(){


        double totalValor = 0.0;
        double totalPago = 0.0;

        for(Venta v: ventas){
            totalValor += v.getValorFin();
            totalPago += v.getValorPago();
        }
        if(ventas.size()>0) {
            layLbl.setVisibility(View.VISIBLE);
            lblTotalNav.setText(Double.valueOf(totalValor).toString());
        }else{
            layLbl.setVisibility(View.GONE);
        }

        progCobros.setMax((int)totalValor);
        progCobros.setProgress((int)totalPago, true);
        progCobros.setText(valueOf(totalPago).toString() + " R$");


    }
}
