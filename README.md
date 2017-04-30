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

Supports Diffs as well
```java
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
```

```java
public class MyViewHolderAdapter extends Adapter<MyViewHolder> {
  private List<MyModel> list;

  public MyViewHolderAdapter() {
    this(new ArrayList<MyModel>());
  }

  public MyViewHolderAdapter(List<MyModel> list) {
    this.list = new ArrayList<MyModel>(list);
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

  public List<MyModel> getItems() {
    return list;
  }

  public void setItems(List<MyModel> list) {
    DiffUtil.calculateDiff(new MyViewHolder.MyDiffCallback(this.list, list)).dispatchUpdatesTo(this);
    this.list = new ArrayList<MyModel>(list);
  }
}
```

Setup
------------
```groovy
buildscript {
    repositories {
        maven {url "https://oss.sonatype.org/content/repositories/snapshots/" }
    }
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
    }
}

apply plugin: 'com.neenbedankt.android-apt'

dependencies {
    apt 'io.dwak:adapter-generator-processor:0.1-SNAPSHOT'
    compile 'io.dwak:adapter-generator-annotations:0.1-SNAPSHOT'
}
```
