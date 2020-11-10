package com.example.inventorymanagement.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanagement.R;
import com.example.inventorymanagement.database.InventoryDbHelper;
import com.example.inventorymanagement.models.Product;
import com.example.inventorymanagement.ui.adapters.ProductAdapter;
import com.example.inventorymanagement.ui.adapters.TabsAdapter;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_SECTION_TITLE = "section_title";
    private static final String ARG_SECTION_PRODUCTS = "section_products";

    private RecyclerView listView;
    private InventoryDbHelper productDbhelper;
    private SQLiteDatabase db;
    private LinearLayout splash;
    private RelativeLayout emptyView;
    private List<Product> product_list;
    private ProductAdapter productAdapter;
    private ProgressBar progressBar;
    private TextView textView;
    String category;
    private TabsAdapter t;


    public static PlaceholderFragment newInstance(String category, int position) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_TITLE, category);
        args.putInt(ARG_SECTION_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
            category = getArguments().getString(ARG_SECTION_TITLE);
            //product_list = getArguments().getParcelableArrayList(ARG_SECTION_PRODUCTS);
        }

        setHasOptionsMenu(true);
        productDbhelper = new InventoryDbHelper(getContext());
        db = productDbhelper.getReadableDatabase();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);

        progressBar = root.findViewById(R.id.my_progress_bar);
        textView = root.findViewById(R.id.my_textView);
        splash = root.findViewById(R.id.splash);
        listView = root.findViewById(R.id.list_view);
        emptyView = root.findViewById(R.id.empty_view);
        listView.setVisibility(View.INVISIBLE);
        emptyView.setVisibility(View.INVISIBLE);
        splash.setVisibility(View.VISIBLE);

        return root;
    }

    private void showProducts(){

        productDbhelper.onOpen(db);
        product_list = productDbhelper.getAllProducts(category);
        productDbhelper.close();

        splash.setVisibility(View.INVISIBLE);
        listView.setLayoutManager(new LinearLayoutManager(requireContext()));

        if(product_list.size()==0){
            listView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            productAdapter = new ProductAdapter(requireContext(),product_list);
            listView.setAdapter(productAdapter);
        }

    }

    private void showMessage(String s){
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT)
                .show();
    }


    @Override
    public void onResume() {
        super.onResume();
        showProducts();
    }
}