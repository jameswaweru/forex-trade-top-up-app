package com.forex.forex_topup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.forex.forex_topup.R;
import com.forex.forex_topup.models.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class ListUserAccountsAdapter extends RecyclerView.Adapter<ListUserAccountsAdapter.MyViewHolder>
        implements Filterable {

    private Context context;
    private List<UserAccount> userAccountList;
    private List<UserAccount> userAccountListFiltered;
    private UserAccountsAdapterListener listener;




    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView optionName ;

        public MyViewHolder(View view) {
            super(view);
            optionName = view.findViewById(R.id.option_name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onUserAccountSelected(userAccountListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public void updateData(List<UserAccount> viewModels) {
        userAccountList.clear();
        userAccountList.addAll(viewModels);
        notifyDataSetChanged();
    }


    public ListUserAccountsAdapter(Context context, List<UserAccount> servicesList, UserAccountsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.userAccountList = servicesList;
        this.userAccountListFiltered = servicesList;
//        this.prefManager = new PrefManager(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_option_row_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final UserAccount userAccount = userAccountList.get(position);
        holder.optionName.setText(userAccount.getAccountNumber());
    }

    @Override
    public int getItemCount() {
        return userAccountListFiltered.size();
    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    userAccountListFiltered = userAccountList;
                } else {
                    List<UserAccount> filteredList = new ArrayList<>();
                    for (UserAccount row : userAccountList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
//                        if (row.getPaymentOptionName().toLowerCase().contains(charString.toLowerCase())) {
//                            filteredList.add(row);
//                        }
                    }

                    userAccountListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = userAccountListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                userAccountListFiltered = (ArrayList<UserAccount>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface UserAccountsAdapterListener {
        void onUserAccountSelected(UserAccount option);
    }
}
