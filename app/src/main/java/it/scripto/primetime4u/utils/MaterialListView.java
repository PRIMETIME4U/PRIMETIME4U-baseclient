package it.scripto.primetime4u.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.dexafree.materialList.controller.MaterialListViewAdapter;

public class MaterialListView extends com.dexafree.materialList.view.MaterialListView {

    private View mEmptyView;
    private MaterialListViewAdapter mAdapter;

    public MaterialListView(Context context) {
        super(context);
    }

    public MaterialListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MaterialListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public MaterialListViewAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(MaterialListViewAdapter adapter) {
        mAdapter = adapter;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;

        Log.i("ADAPTER", "setto empty view");

        // If not explicitly specified this view is important for accessibility.
        if (emptyView != null
                && emptyView.getImportantForAccessibility() == IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            emptyView.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
        }

        final MaterialListViewAdapter adapter = getAdapter();
        final boolean empty = (adapter == null) || adapter.isEmpty();
        Log.i("ADAPTER", "mi dicono che sono vuoto " + String.valueOf(empty));
        updateEmptyStatus(empty);
    }

    private void updateEmptyStatus(boolean empty) {
        Log.i("ADAPTER", String.valueOf(empty));
        if (empty) {
            if (mEmptyView != null) {
                Log.i("ADAPTER", "faccio la cosa");
                mEmptyView.setVisibility(View.VISIBLE);
                setVisibility(View.GONE);
            } else {
                // If the caller just removed our empty view, make sure the list view is visible
                setVisibility(View.VISIBLE);
            }

            // We are now GONE, so pending layouts will not be dispatched.
            // Force one here to make sure that the state of the list matches
            // the state of the adapter.
        } else {
            Log.i("ADAPTER", "mostro lista");
            if (mEmptyView != null) mEmptyView.setVisibility(View.GONE);
            setVisibility(View.VISIBLE);
        }
    }
    
}
