package io.github.unclelem.androidutils.utils.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

/**
 * T - list items type.
 * K - ViewHolder class (just bunch of Views from resId layout)
 *
 * @author unclelem
 */
public abstract class MutableAdapter<T,K> extends BaseAdapter implements Comparator<T> {
    private final int resId;
    protected final LayoutInflater layoutInflater;
    protected final ArrayList<T> items = new ArrayList<T>();

    public MutableAdapter(Context context, int resId) {
        this(context, resId, null);
    }

    public MutableAdapter(Context context, int resId, Collection<? extends T> items) {
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (items != null) {
            this.items.addAll(items);
        }
        this.resId = resId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        K viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(resId, null);
            viewHolder = getNewViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (K) convertView.getTag();
        }

        T item = getItem(position);
        fillViewHolder(viewHolder, item);
        return convertView;
    }

    /**
     * Get data from item and put it into view holder's views
     * @param viewHolder holder, created in getNewViewHolder
     * @param item object that will be represented by given view holder
     */
    protected abstract void fillViewHolder(K viewHolder, T item);

    /**
     * Implementation must create a new instance of view holder and fill its fields
     * (A ViewHolder object stores each of the component views inside the tag field of the Layout,
     * so you can immediately access them without the need to look them up repeatedly.)
     * @param convertView source view
     * @return new instance of view holder
     */
    protected abstract K getNewViewHolder(View convertView);

    /**
     * Search the outdated object using compare(T lhs, T rhs) method and replace it by argument.
     * Default implementation of compare(T lhs, T rhs) will not match any objects, you have to override it if you want this method to work
     * Only first entry will be replaced
     * @param newItem new item to replace
     * @throws RuntimeException if replace(T newItem) used without compare(T lhs, T rhs) overridden
     */
    public void replace(T newItem) {
        for (T item : items) {
            if (compare(item, newItem) == 0) {
                replace(item, newItem);
                notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * If you don't have access to outdated objects directly or it's not very handy,
     * you can override this method with your own objects comparing code and it will be used in replace(T newItem)
     * (e.g. if you get new version of object from database, new and outdated versions can be matched via their ids)
     * @param lhs the first object to be compared.
     * @param rhs the second object to be compared.
     * @return result of the comparison (0 if first argument equal to the second)
     * @throws RuntimeException if replace(T newItem) used without compare(T lhs, T rhs) overridden
     */
    @Override
    public int compare(T lhs, T rhs) {
        throw new RuntimeException("You have to override compare(T lhs, T rhs) method to use it");
    }

    /**
     * Only first entry will be replaced
     * @param oldItem outdated item to be replaced
     * @param newItem new item to replace
     */
    public void replace(T oldItem, T newItem) {
        int index = items.indexOf(oldItem);
        if (index >= 0) {
            items.remove(index);
            items.add(index, newItem);
        }
        notifyDataSetChanged();
    }

    public void add(T item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void remove(T item) {
        items.remove(item);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        items.remove(index);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
