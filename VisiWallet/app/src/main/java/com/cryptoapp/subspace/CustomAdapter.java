package com.cryptoapp.subspace;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DestinationX on 10/9/2018.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<PhotographicWallet> photographicWalletArrayList = new ArrayList<>();
    private Context context;


    public CustomAdapter(Context context, List<PhotographicWallet> photographicWalletArrayList) {
        this.photographicWalletArrayList = photographicWalletArrayList;
        this.context = context;
    }



    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, final int position) {

        final PhotographicWallet photographicWallet = photographicWalletArrayList.get(position);

        final InputStream imageStream;
        //            imageStream = context.getContentResolver().openInputStream(uri);
//            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        Bitmap bmp = BitmapFactory.decodeFile(photographicWallet.getUriImagePath().getPath());
        ImageView photo = holder.img;
        photo.setImageBitmap(bmp);
        photo.setScaleType(ImageView.ScaleType.CENTER_CROP);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(photographicWallet.getWallet().currentReceiveAddress());
                Intent mIntent = new Intent(context, DetailActivity.class);
                mIntent.putExtra("image", photographicWallet.getUriImagePath().getPath());
                mIntent.putExtra("balance", photographicWallet.getWallet().getBalance());
                context.startActivity(mIntent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return photographicWalletArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
        }


    }


}
