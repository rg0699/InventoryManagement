package com.example.inventorymanagement.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanagement.R;
import com.example.inventorymanagement.activities.DetailsActivity;
import com.example.inventorymanagement.database.InventoryDbHelper;
import com.example.inventorymanagement.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductsViewHolder> {

    private Context mContext;
    private List<Product> products;


    public ProductAdapter(Context mContext, List<Product> products) {
        setHasStableIds(true);
        this.mContext = mContext;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
        return new ProductsViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductsViewHolder holder, int position) {

        holder.product_name.setText(products.get(position).getProduct_name());
        holder.product_qauntity.setText(String.valueOf(products.get(position).getProduct_qauntity()));
        holder.product_price.setText(String.valueOf(products.get(position).getProduct_price()));

        if(products.get(position).getProduct_image() != null){
            holder.product_image.setImageBitmap(products.get(position).getProduct_image());
        }

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductsViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout product_layout;
        ImageView product_image;
        TextView product_qauntity, product_price, product_name;
        private int position;

        InventoryDbHelper productDbhelper = new InventoryDbHelper(mContext);
        private SQLiteDatabase db = productDbhelper.getReadableDatabase();

        ProductsViewHolder(View itemView) {
            super(itemView);
            product_image = itemView.findViewById(R.id.product_image);
            product_qauntity = itemView.findViewById(R.id.product_quantity);
            product_price = itemView.findViewById(R.id.product_price);
            product_name = itemView.findViewById(R.id.product_name);
            product_layout = itemView.findViewById(R.id.product_layout);

            product_layout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent i = new Intent(mContext, DetailsActivity.class);
                    position = ProductsViewHolder.this.getAdapterPosition();
                    i.putExtra("id", products.get(position).getProduct_id());
                    mContext.startActivity(i);

                }

            });

        }

    }

}
