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
package com.github.mobile.core.repo;

import android.text.TextUtils;

import org.eclipse.egit.github.core.Repository;

/**
 * Utilities for working with {@link Repository} objects
 */
public class RepositoryUtils {

    /**
     * Does the repository have details denoting it was loaded from an API call?
     * <p>
     * This uses a simple heuristic of either being private, being a fork, or
     * having a non-zero amount of forks or watchers, or has issues enable;
     * meaning it came back from an API call providing those details and more.
     *
     * @param repository
     * @return true if complete, false otherwise
     *
     */
    public static boolean isComplete(final Repository repository) {
        return repository.isPrivate() || repository.isFork()
                || repository.getForks() > 0 || repository.getWatchers() > 0
                || repository.isHasIssues();
    }

    /**
     * Is the given owner name valid?
     *
     * @param name
     * @return true if valid, false otherwise
     */
    public static boolean isValidOwner(final String name) {
        if (TextUtils.isEmpty(name))
            return false;

        if ("about".equals(name) //
                || "account".equals(name) //
                || "admin".equals(name) //
                || "api".equals(name) //
                || "blog".equals(name) //
                || "camo".equals(name) //
                || "contact".equals(name) //
                || "dashboard".equals(name) //
                || "downloads".equals(name) //
                || "edu".equals(name) //
                || "explore".equals(name) //
                || "features".equals(name) //
                || "home".equals(name) //
                || "inbox".equals(name) //
                || "languages".equals(name) //
                || "login".equals(name) //
                || "logout".equals(name) //
                || "new".equals(name) //
                || "notifications".equals(name) //
                || "organizations".equals(name) //
                || "repositories".equals(name) //
                || "search".equals(name) //
                || "security".equals(name) //
                || "settings".equals(name) //
                || "stars".equals(name) //
                || "styleguide".equals(name) //
                || "timeline".equals(name) //
                || "training".equals(name) //
                || "users".equals(name) //
                || "watching".equals(name))
            return false;
        else
            return true;
    }
}
