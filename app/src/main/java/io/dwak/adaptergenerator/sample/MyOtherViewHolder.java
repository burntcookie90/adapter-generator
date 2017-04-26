package io.dwak.adaptergenerator.sample;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import io.dwak.adaptergenerator.R;
import io.dwak.adaptergenerator.annotation.AdapterGenerator;
import io.dwak.adaptergenerator.annotation.BindViewHolder;

@AdapterGenerator(layoutResId = R.layout.my_view_other_holder, model = MyModel.class)
public class MyOtherViewHolder extends RecyclerView.ViewHolder {
    public MyOtherViewHolder(View itemView) {
        super(itemView);
    }

    @BindViewHolder
    public void bind(MyModel model) {

    }
}
