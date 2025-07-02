package com.wleowleo.aiadvisor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.wleowleo.aiadvisor.R;
import com.wleowleo.aiadvisor.database.Transaction;
import com.wleowleo.aiadvisor.utils.FormatUtils;
import com.wleowleo.aiadvisor.utils.PreferencesManager;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactions = new ArrayList<>();
    private PreferencesManager preferencesManager;
    
    public TransactionAdapter(PreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;
    }
    
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.bind(transaction, preferencesManager.getCurrencySymbol());
    }
    
    @Override
    public int getItemCount() {
        return transactions.size();
    }
    
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }
    
    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDescription;
        private TextView tvCategory;
        private TextView tvAmount;
        private TextView tvDate;
        private View colorIndicator;
        
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDate = itemView.findViewById(R.id.tv_date);
            colorIndicator = itemView.findViewById(R.id.color_indicator);
        }
        
        public void bind(Transaction transaction, String currencySymbol) {
            tvDescription.setText(transaction.getDescription().isEmpty() ? 
                    transaction.getCategory() : transaction.getDescription());
            tvCategory.setText(transaction.getCategory());
            tvAmount.setText(FormatUtils.formatCurrency(transaction.getAmount(), currencySymbol));
            tvDate.setText(FormatUtils.formatDate(transaction.getTimestamp()));
            
            // Set colors based on transaction type
            if ("income".equals(transaction.getType())) {
                tvAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.income_green));
                colorIndicator.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.income_green));
                tvAmount.setText("+" + FormatUtils.formatCurrency(transaction.getAmount(), currencySymbol));
            } else {
                tvAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.outcome_red));
                colorIndicator.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.outcome_red));
                tvAmount.setText("-" + FormatUtils.formatCurrency(transaction.getAmount(), currencySymbol));
            }
        }
    }
}
