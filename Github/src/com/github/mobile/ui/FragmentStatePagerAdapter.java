/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mobile.ui;

import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Pager that stores current fragment
 */
public abstract class FragmentStatePagerAdapter extends
        android.support.v4.app.FragmentStatePagerAdapter implements
        FragmentProvider {

    private final SherlockFragmentActivity activity;

    private SherlockFragment selected;

    /**
     * @param activity
     */
    public FragmentStatePagerAdapter(final SherlockFragmentActivity activity) {
        super(activity.getSupportFragmentManager());

        this.activity = activity;
    }

    @Override
    public SherlockFragment getSelected() {
        return selected;
    }

    @Override
    public void setPrimaryItem(final ViewGroup container, final int position,
            final Object object) {
        super.setPrimaryItem(container, position, object);

        boolean changed = false;
        if (object instanceof SherlockFragment) {
            changed = object != selected;
            selected = (SherlockFragment) object;
        } else {
            changed = object != null;
            selected = null;
        }

        if (changed)
            activity.invalidateOptionsMenu();
    }
}
