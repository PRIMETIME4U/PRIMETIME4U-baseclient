package it.scripto.primetime4u.cards;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class MaterialProposalCardListAdapter extends ArrayAdapter<ProposalCard> {
    private ArrayList<Class> mClassList = new ArrayList<>();
    private ArrayList<Class> mDeletedList = new ArrayList<>();

    public MaterialProposalCardListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ProposalCard card = getItem(position);

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
            ((ProposalCardItemView) convertView).build(card);
        }

        return convertView;
    }

    @Override
    public void add(ProposalCard card) {
        super.add(card);
        Class cl = card.getClass();
        if (!mClassList.contains(cl)) {
            mClassList.add(cl);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void addAll(Collection<? extends ProposalCard> collection) {
        super.addAll(collection);
        for (ProposalCard card : collection) {
            Class cl = card.getClass();
            if (!mClassList.contains(cl)) {
                mClassList.add(cl);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void addAll(ProposalCard... items) {
        super.addAll(items);
        List<ProposalCard> cards = Arrays.asList(items);
        for (ProposalCard card : cards) {
            Class cl = card.getClass();
            if (!mClassList.contains(cl)) {
                mClassList.add(cl);
            }
        }
    }

    @Override
    public void insert(ProposalCard card, int index) {
        super.insert(card, index);
        Class cl = card.getClass();
        if (!mClassList.contains(cl)) {
            mClassList.add(cl);
        }
    }

    @Override
    public void remove(ProposalCard card) {
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