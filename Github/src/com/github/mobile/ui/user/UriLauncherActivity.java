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

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_BROWSABLE;
import static org.eclipse.egit.github.core.client.IGitHubConstants.HOST_GISTS;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.github.mobile.R.string;
import com.github.mobile.core.commit.CommitMatch;
import com.github.mobile.core.commit.CommitUriMatcher;
import com.github.mobile.core.gist.GistUriMatcher;
import com.github.mobile.core.issue.IssueUriMatcher;
import com.github.mobile.core.repo.RepositoryUriMatcher;
import com.github.mobile.core.user.UserUriMatcher;
import com.github.mobile.ui.LightAlertDialog;
import com.github.mobile.ui.commit.CommitViewActivity;
import com.github.mobile.ui.gist.GistsViewActivity;
import com.github.mobile.ui.issue.IssuesViewActivity;
import com.github.mobile.ui.repo.RepositoryViewActivity;

import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryIssue;
import org.eclipse.egit.github.core.User;

/**
 * Activity to launch other activities based on the intent's data {@link URI}
 */
public class UriLauncherActivity extends SherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final Uri data = intent.getData();
        if (HOST_GISTS.equals(data.getHost())) {
            Gist gist = GistUriMatcher.getGist(data);
            if (gist != null) {
                startActivity(GistsViewActivity.createIntent(gist));
                finish();
                return;
            }
        } else {
            RepositoryIssue issue = IssueUriMatcher.getIssue(data);
            if (issue != null) {
                startActivity(IssuesViewActivity.createIntent(issue,
                        issue.getRepository()));
                finish();
                return;
            }

            Repository repository = RepositoryUriMatcher.getRepository(data);
            if (repository != null) {
                startActivity(RepositoryViewActivity.createIntent(repository));
                finish();
                return;
            }

            User user = UserUriMatcher.getUser(data);
            if (user != null) {
                startActivity(UserViewActivity.createIntent(user));
                finish();
                return;
            }

            CommitMatch commit = CommitUriMatcher.getCommit(data);
            if (commit != null) {
                startActivity(CommitViewActivity.createIntent(
                        commit.repository, commit.commit));
                finish();
                return;
            }
        }

        if (!intent.hasCategory(CATEGORY_BROWSABLE)) {
            startActivity(new Intent(ACTION_VIEW, data)
                    .addCategory(CATEGORY_BROWSABLE));
            finish();
        } else
            showParseError(data.toString());
    }

    private void showParseError(String url) {
        AlertDialog dialog = LightAlertDialog.create(this);
        dialog.setTitle(string.title_invalid_github_url);
        dialog.setMessage(MessageFormat.format(
                getString(string.message_invalid_github_url), url));
        dialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        dialog.setButton(BUTTON_POSITIVE, getString(android.R.string.ok),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        dialog.show();
    }
}
