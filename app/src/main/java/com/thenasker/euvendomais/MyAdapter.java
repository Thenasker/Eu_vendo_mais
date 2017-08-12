package com.thenasker.euvendomais;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Double.valueOf;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<Venta> ventas;
    private Context ctx;
    static final int REQUEST_PAGO = 2001;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View layVenta;
        public ViewHolder(View v) {
            super(v);
            layVenta = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<Venta> myDataset, Context ctx) {
        this.ctx = ctx;
        ventas = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_venta, parent, false);
        // set the view's size, margins, paddings and layout parameters

        final ViewHolder vh = new ViewHolder(v);

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = vh.getAdapterPosition();
                Intent i = new Intent(ctx,NovoPago.class);
                i.putExtra("IMG",ventas.get(p).getRotaImg());
                i.putExtra("VALOR",ventas.get(p).getValorFin());
                i.putExtra("PAGO",ventas.get(p).getValorPago());
                i.putExtra("NOME",ventas.get(p).getNome());
                i.putExtra("LIM",ventas.get(p).getLimite());
                i.putExtra("TIM",ventas.get(p).getTimestamp());
                i.putExtra("POS",p);
                ((MainActivity) ctx).startActivityForResult(i,REQUEST_PAGO);
            }
        });

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final int p = position;

        CircleImageView img = (CircleImageView) holder.layVenta.findViewById(R.id.imgCliente);
        TextView lblNome = (TextView)holder.layVenta.findViewById(R.id.lblNome);
        TextProgressBar prog = (TextProgressBar)holder.layVenta.findViewById(R.id.progPagamento);

        TextView lblTotal = (TextView)holder.layVenta.findViewById(R.id.lblTotal);

        //TODO-MUDAR ESTO
        if(ventas.get(position)!=null) {


            try {
                if(ventas.get(position).getRotaImg()!= null && !ventas.get(position).getRotaImg().equals("")) {
                    File f = new File(ventas.get(position).getRotaImg());
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    img.setImageBitmap(b);
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            lblNome.setText(ventas.get(position).getNome());
            prog.setProgress((int) ((ventas.get(position).getValorPago() * 100) / ventas.get(position).getValorFin()), true);
            prog.setText(valueOf(ventas.get(position).getValorPago()).toString() + " R$");

            lblTotal.setText(valueOf(ventas.get(position).getValorFin()).toString() + " R$");
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ventas.size();
    }

}