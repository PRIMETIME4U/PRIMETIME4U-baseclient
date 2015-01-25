package it.scripto.primetime4u.cards;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class MaterialTasteCardListAdapter extends ArrayAdapter<TasteCard> {
    private ArrayList<Class> mClassList = new ArrayList<>();
    private ArrayList<Class> mDeletedList = new ArrayList<>();

    public MaterialTasteCardListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        TasteCard card = getItem(position);

        if (convertView == null || !convertView.getTag().getClass().isInstance(card)) {
            for (Class<?> classType : mClassList) {
                if (classType.isInstance(card)) {
                    convertView = View.inflate(getContext(), card.getLayout(), null);
                    convertView.setTag(card);
                    break;
                }
            }
        }

        if (convertView != null) {
            ((TasteCardItemView) convertView).build(card);
        }

        return convertView;
    }

    @Override
    public void add(TasteCard card) {
        super.add(card);
        Class cl = card.getClass();
        if (!mClassList.contains(cl)) {
            mClassList.add(cl);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void addAll(Collection<? extends TasteCard> collection) {
        super.addAll(collection);
        for (TasteCard card : collection) {
            Class cl = card.getClass();
            if (!mClassList.contains(cl)) {
                mClassList.add(cl);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void addAll(TasteCard... items) {
        super.addAll(items);
        List<TasteCard> cards = Arrays.asList(items);
        for (TasteCard card : cards) {
            Class cl = card.getClass();
            if (!mClassList.contains(cl)) {
                mClassList.add(cl);
            }
        }
    }

    @Override
    public void insert(TasteCard card, int index) {
        super.insert(card, index);
        Class cl = card.getClass();
        if (!mClassList.contains(cl)) {
            mClassList.add(cl);
        }
    }

    @Override
    public void remove(TasteCard card) {
        super.remove(card);
        if (!mDeletedList.contains(card.getClass())) {
            mDeletedList.add(card.getClass());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position > -1 && position < getCount()) {
            for (int i = 0; i < mClassList.size(); i++) {
                Class cl = mClassList.get(i);
                if (cl.isInstance(getItem(position))) return i;
            }
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        // BugFix: Can't have a viewTypCount < 1 (Exception)
        return mClassList.isEmpty() ? 1 : mClassList.size();
    }

    @Override
    public boolean isEmpty() {
        Log.i("MaterialTasteCardListAdapter", "Chiamo isEmpty()");
        return mClassList.isEmpty();
    }
    
    // TODO: they don't work...
    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }
    
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}