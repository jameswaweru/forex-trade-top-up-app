package com.forex.forex_topup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.forex.forex_topup.R;
import com.forex.forex_topup.models.Transactions;

import java.util.ArrayList;
import java.util.List;

public class ListTransactionsAdapter extends RecyclerView.Adapter<ListTransactionsAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<Transactions> transactionsList;
    private List<Transactions> transactionsListFiltered;
    private ListTransactionsAdapterListener listener;
//    private PrefManager prefManager;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView transText , transID, transAmount , transMSISDN;
        public ProgressBar progressBar;
        private ImageView transIcon;
        private RelativeLayout myView;

        public MyViewHolder(View view) {
            super(view);
            transText = view.findViewById(R.id.transaction_text);
            transID = view.findViewById(R.id.hold_transaction_id);
            transMSISDN = view.findViewById(R.id.transaction_msisdn);
            transAmount = view.findViewById(R.id.transaction_amount);
            transIcon = view.findViewById(R.id.transaction_icon);
//            progressBar = view.findViewById(R.id.progressBar2);
//            is_selected_Contact = view.findViewById(R.id.show_selected);
//            prefManager = new PrefManager(context);
//            myView = view.findViewById(R.id.my_view_layout);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onTransactionSelected(transactionsListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public void updateData(List<Transactions> viewModels) {
        transactionsList.clear();
        transactionsList.addAll(viewModels);
        notifyDataSetChanged();
    }


    public ListTransactionsAdapter(Context context, List<Transactions> servicesList, ListTransactionsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.transactionsList = servicesList;
        this.transactionsListFiltered = servicesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transactions_row_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Transactions transactions = transactionsList.get(position);

//        if(contact.getColumnContactNumber().equals(prefManager.getSelectedAirtimeRecipientNumber())){
//            holder.is_selected_Contact.setVisibility(View.VISIBLE);
//        }else{
//            holder.is_selected_Contact.setVisibility(View.GONE);
//        }



        holder.transText.setText(transactions.getTransactionText());
        holder.transMSISDN.setText(transactions.getTransactionMsisdn());
        holder.transAmount.setText(transactions.getTransactionAmount());
        holder.transID.setText(transactions.getTransactionID());
//        Glide.with(context)
//                .load(driverServiceOptions.getDriverServiceOptionIcon())
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        holder.progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        holder.progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
//                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return transactionsListFiltered.size();
    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    transactionsListFiltered = transactionsList;
                } else {
                    List<Transactions> filteredList = new ArrayList<>();
                    for (Transactions row : transactionsList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTransactionMsisdn().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    transactionsListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = transactionsListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                transactionsListFiltered = (ArrayList<Transactions>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ListTransactionsAdapterListener {
        void onTransactionSelected(Transactions option);
    }
}
