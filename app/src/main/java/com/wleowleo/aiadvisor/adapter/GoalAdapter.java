package com.wleowleo.aiadvisor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.wleowleo.aiadvisor.R;
import com.wleowleo.aiadvisor.database.Goal;
import com.wleowleo.aiadvisor.utils.FormatUtils;
import com.wleowleo.aiadvisor.utils.PreferencesManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {
    private List<Goal> goals = new ArrayList<>();
    private PreferencesManager preferencesManager;
    private OnGoalPaymentListener paymentListener;
    
    public interface OnGoalPaymentListener {
        void onPaymentClicked(Goal goal);
    }
    
    public GoalAdapter(PreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;
    }
    
    public void setOnGoalPaymentListener(OnGoalPaymentListener listener) {
        this.paymentListener = listener;
    }
    
    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.bind(goal, preferencesManager.getCurrencySymbol(), paymentListener);
    }
    
    @Override
    public int getItemCount() {
        return goals.size();
    }
    
    public void setGoals(List<Goal> goals) {
        this.goals = goals;
        notifyDataSetChanged();
    }
    
    static class GoalViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvProgress;
        private TextView tvAmount;
        private TextView tvGoalDate;
        private ProgressBar progressBar;
        private Button btnPayGoal;
        
        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_goal_title);
            tvProgress = itemView.findViewById(R.id.tv_progress);
            tvAmount = itemView.findViewById(R.id.tv_goal_amount);
            tvGoalDate = itemView.findViewById(R.id.tv_goal_date);
            progressBar = itemView.findViewById(R.id.progress_bar);
            btnPayGoal = itemView.findViewById(R.id.btn_pay_goal);
        }
        
        public void bind(Goal goal, String currencySymbol, OnGoalPaymentListener paymentListener) {
            tvTitle.setText(goal.getTitle());
            
            String progressText = FormatUtils.formatCurrency(goal.getCurrentAmount(), currencySymbol) + 
                    " / " + FormatUtils.formatCurrency(goal.getTargetAmount(), currencySymbol);
            tvAmount.setText(progressText);
            
            // Format end date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(new Date(goal.getEndDate()));
            tvGoalDate.setText("Target: " + formattedDate);
            
            double progressPercentage = goal.getProgressPercentage();
            tvProgress.setText(String.format("%.1f%%", progressPercentage));
            progressBar.setProgress((int) progressPercentage);
            
            // Set up payment button
            btnPayGoal.setOnClickListener(v -> {
                if (paymentListener != null) {
                    paymentListener.onPaymentClicked(goal);
                }
            });
            
            // Always show payment button - users can contribute even if goal is exceeded
            btnPayGoal.setVisibility(View.VISIBLE);
        }
    }
}
