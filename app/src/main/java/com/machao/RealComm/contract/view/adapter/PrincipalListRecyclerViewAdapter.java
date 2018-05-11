package com.machao.RealComm.contract.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.machao.RealComm.R;
import com.machao.RealComm.model.Principal;

public class PrincipalListRecyclerViewAdapter extends AbstractRecyclerViewAdapter<Principal, PrincipalListRecyclerViewAdapter.ViewHolder> {
    private final Context context;

    private OnPrincipalListItemClickedListener onPrincipalListItemClickedListener;

    public PrincipalListRecyclerViewAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_user_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.nameTextView = (TextView) view.findViewById(R.id.view_user_list_item_name_text_view);
        viewHolder.statusTextView = (TextView) view.findViewById(R.id.view_user_list_item_status_text_view);
        viewHolder.callButton = (Button) view.findViewById(R.id.view_user_list_item_call_button);
        viewHolder.endCallButton = (Button) view.findViewById(R.id.view_user_list_item_end_call_button);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Principal item = getData(position);

        holder.nameTextView.setText(item.getName() == null ? item.getName() : item.getNickname());
        holder.statusTextView.setText(item.getStatus().toString());
        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onPrincipalListItemClickedListener != null) {
                    PrincipalListRecyclerViewAdapter.this.onPrincipalListItemClickedListener.onCallClicked(item);
                }
            }
        });
        holder.endCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onPrincipalListItemClickedListener != null) {
                    PrincipalListRecyclerViewAdapter.this.onPrincipalListItemClickedListener.onEndCallClicked(item);
                }
            }
        });

        if(Principal.Status.IDLE.equals(item.getStatus())) {
            holder.callButton.setVisibility(View.VISIBLE);
            holder.endCallButton.setVisibility(View.GONE);
        } else if(Principal.Status.CALLING.equals(item.getStatus())) {
            holder.callButton.setVisibility(View.GONE);
            holder.endCallButton.setVisibility(View.VISIBLE);
        }
    }

    public void setOnPrincipalListItemClickedListener(OnPrincipalListItemClickedListener onPrincipalListItemClickedListener) {
        this.onPrincipalListItemClickedListener = onPrincipalListItemClickedListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        public TextView nameTextView;
        public TextView statusTextView;
        public Button callButton;
        public Button endCallButton;
    }

    public interface OnPrincipalListItemClickedListener {
        void onCallClicked(Principal principal);
        void onEndCallClicked(Principal principal);
    }
}
