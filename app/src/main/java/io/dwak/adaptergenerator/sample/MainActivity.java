package io.dwak.adaptergenerator.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import io.dwak.adaptergenerator.R;

public class MainActivity extends AppCompatActivity {

    private int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        Button addButton = (Button) findViewById(R.id.add);

        final ArrayList<MyModel> myModels = new ArrayList<>();
        myModels.add(new MyModel("id" + counter++, "test" + counter));
        myModels.add(new MyModel("id" + counter++, "test" + counter));
        myModels.add(new MyModel("id" + counter++, "test" + counter));
        myModels.add(new MyModel("id" + counter++, "test" + counter));
        myModels.add(new MyModel("id" + counter++, "test" + counter));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MyViewHolderAdapter adapter = new MyViewHolderAdapter(myModels);
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myModels.add(new MyModel("id" + counter++, "test" + counter));
                adapter.setItems(myModels);
            }
        });
    }
}
