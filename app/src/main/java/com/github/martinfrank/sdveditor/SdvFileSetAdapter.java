package com.github.martinfrank.sdveditor;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.martinfrank.sdvedit.SdvFileSet;

import java.util.ArrayList;
import java.util.List;

public class SdvFileSetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SdvFileSet> localDataSet;

    private final View.OnClickListener mOnClickListener;
    private final List<View> views = new ArrayList<>();

    public SdvFileSetAdapter(List<SdvFileSet> dataSet, View.OnClickListener listener) {
        localDataSet = dataSet;
        mOnClickListener = listener;
    }


    public void setData(List<SdvFileSet> dataSet) {
        localDataSet = dataSet;
        notifyDataSetChanged();
    }

    public SdvFileSet getFileSet(int index) {
        return localDataSet.get(index);
    }

    public void setSelection(int index) {
        for(View view: views){
            CheckBox checkBox = view.findViewById(R.id.sdv_selected);
            int viewIndex = Integer.parseInt("" + ((TextView)view.findViewById(R.id.index_holder)).getText());
            checkBox.setChecked(viewIndex == index);
        }
    }
    public int getSelection() {
        for(View view: views){
            CheckBox checkBox = view.findViewById(R.id.sdv_selected);
            if(checkBox.isChecked()){
                return Integer.parseInt("" + ((TextView)view.findViewById(R.id.index_holder)).getText());
            }
        }
        return -1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final CheckBox selection;
        private final TextView textView;
        private final TextView indexHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selection = itemView.findViewById(R.id.sdv_selected);
            textView = itemView.findViewById(R.id.sdv_name);
            indexHolder = itemView.findViewById(R.id.index_holder);
        }
        public TextView getTextView() {
            return textView;
        }

        public TextView getIndexHolderView() {
            return indexHolder;
        }

        public CheckBox getSelectionCheckBox(){
            return selection;
        }

        @Override
        public String toString() {
            return "index:"+indexHolder.getText()+" --> "+super.toString();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.sdv_fileset_row_item, viewGroup, false);
        view.setOnClickListener(mOnClickListener);
        views.add(view);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((ViewHolder)viewHolder).getTextView().setText(localDataSet.get(position).toString());
        ((ViewHolder)viewHolder).getIndexHolderView().setText(Integer.toString(position));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}
