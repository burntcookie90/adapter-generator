# adapter-generator

```java

@AdapterGenerator(layoutResId = R.layout.my_view_holder, model = MyModel.class)
public class MyViewHolder extends RecyclerView.ViewHolder {
    public MyViewHolder(View itemView) {
        super(itemView);
    }

    @BindViewHolder
    public void bind(MyModel model) {

    }
}
```

generates the following:

```java
public class MyViewHolderAdapter extends Adapter<MyViewHolder> {
  private List<MyModel> list;

  public MyViewHolderAdapter() {
    this(new ArrayList<MyModel>());
  }

  public MyViewHolderAdapter(List<MyModel> list) {
    this.list = list;
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(2130968604, parent, false);
    return new MyViewHolder(view);
  }

  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {
    holder.bind(list.get(position));
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  public void setItems(List<MyModel> list) {
    this.list = list;
    notifyDataSetChanged();
  }
}
```

