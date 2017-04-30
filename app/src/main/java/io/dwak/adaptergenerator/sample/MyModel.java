package io.dwak.adaptergenerator.sample;

public class MyModel {
    public final String id;
    public final String name;

    public MyModel(String id, String name) {
        this.id = id;

        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyModel myModel = (MyModel) o;

        if (!id.equals(myModel.id)) return false;
        return name.equals(myModel.name);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
