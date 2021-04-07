package com.example.myapplication;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Build;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



public class AdapterForCards extends RecyclerView.Adapter<AdapterForCards.ViewHolder>  {
    private ArrayList<Card> list;
    private OnCardListener mOnCardListener;


    public AdapterForCards(ArrayList<Card> cardList, OnCardListener onCardListener) {
        list=cardList;
        this.mOnCardListener=onCardListener;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTextView1;
        public TextView mTextView2;
        OnCardListener onCardListener;
        public ViewHolder(@NonNull View itemView, OnCardListener onCardListener) {
            super(itemView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
            this.onCardListener=onCardListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCardListener.onCardClick(getAdapterPosition());

        }
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item,parent,false);


        return new ViewHolder(v,mOnCardListener);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Card currentItem = list.get(position);
        holder.mTextView1.setText(currentItem.getText1());
        holder.mTextView2.setText(currentItem.getText2());



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnCardListener{
        void onCardClick(int position);
    }


}
