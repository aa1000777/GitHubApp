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
package com.github.mobile.ui.user;

import org.eclipse.egit.github.core.User;

/**
 * Interface to register and unregister a {@link OrganizationSelectionListener}
 */
public interface OrganizationSelectionProvider {

    /**
     * Add selection listener
     *
     * @param listener
     * @return the currently selected organization
     */
    User addListener(OrganizationSelectionListener listener);

    /**
     * Remove selection listener
     *
     * @param listener
     * @return this selection provider
     */
    OrganizationSelectionProvider removeListener(
            OrganizationSelectionListener listener);
}
