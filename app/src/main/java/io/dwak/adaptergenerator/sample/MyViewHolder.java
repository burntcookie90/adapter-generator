package io.dwak.adaptergenerator.sample;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import io.dwak.adaptergenerator.R;
import io.dwak.adaptergenerator.annotation.AdapterGenerator;
import io.dwak.adaptergenerator.annotation.BindViewHolder;
import io.dwak.adaptergenerator.annotation.DiffCallback;

@AdapterGenerator(layoutResId = R.layout.my_view_holder, model = MyModel.class)
public class MyViewHolder extends RecyclerView.ViewHolder {
    private final TextView text;
    public MyViewHolder(View itemView) {
        super(itemView);
        text = (TextView) itemView.findViewById(R.id.text);
    }

    @BindViewHolder
    public void bind(MyModel model) {
        text.setText(model.name);
    }

    @DiffCallback
    public static class MyDiffCallback extends DiffUtil.Callback {
        private final List<MyModel> oldList;
        private final List<MyModel> newList;

        public MyDiffCallback(List<MyModel> oldList, List<MyModel> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).id.equals(newList.get(newItemPosition).id);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}
