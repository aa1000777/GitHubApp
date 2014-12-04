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

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_BROWSABLE;
import static org.eclipse.egit.github.core.event.Event.TYPE_COMMIT_COMMENT;
import static org.eclipse.egit.github.core.event.Event.TYPE_DOWNLOAD;
import static org.eclipse.egit.github.core.event.Event.TYPE_PUSH;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.kevinsawicki.wishlist.ViewFinder;
import com.github.mobile.R.id;
import com.github.mobile.R.layout;
import com.github.mobile.R.string;
import com.github.mobile.core.gist.GistEventMatcher;
import com.github.mobile.core.issue.IssueEventMatcher;
import com.github.mobile.core.repo.RepositoryEventMatcher;
import com.github.mobile.core.user.UserEventMatcher;
import com.github.mobile.core.user.UserEventMatcher.UserPair;
import com.github.mobile.ui.commit.CommitCompareViewActivity;
import com.github.mobile.ui.commit.CommitViewActivity;
import com.github.mobile.ui.gist.GistsViewActivity;
import com.github.mobile.ui.issue.IssuesViewActivity;
import com.github.mobile.ui.repo.RepositoryViewActivity;
import com.github.mobile.ui.user.NewsListAdapter;
import com.github.mobile.util.AvatarLoader;
import com.google.inject.Inject;

import java.util.List;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.Download;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.event.CommitCommentPayload;
import org.eclipse.egit.github.core.event.DownloadPayload;
import org.eclipse.egit.github.core.event.Event;
import org.eclipse.egit.github.core.event.PushPayload;
import org.eclipse.egit.github.core.service.EventService;

/**
 * Base news fragment class with utilities for subclasses to built on
 */
public abstract class NewsFragment extends PagedItemFragment<Event> {

    /**
     * Matcher for finding an {@link Issue} from an {@link Event}
     */
    protected final IssueEventMatcher issueMatcher = new IssueEventMatcher();

    /**
     * Matcher for finding a {@link Gist} from an {@link Event}
     */
    protected final GistEventMatcher gistMatcher = new GistEventMatcher();

    /**
     * Matcher for finding a {@link Repository} from an {@link Event}
     */
    protected final RepositoryEventMatcher repoMatcher = new RepositoryEventMatcher();

    /**
     * Matcher for finding a {@link User} from an {@link Event}
     */
    protected final UserEventMatcher userMatcher = new UserEventMatcher();

    @Inject
    private AvatarLoader avatars;

    /**
     * Event service
     */
    @Inject
    protected EventService service;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(string.no_news);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Event event = (Event) l.getItemAtPosition(position);

        if (TYPE_DOWNLOAD.equals(event.getType())) {
            openDownload(event);
            return;
        }

        if (TYPE_PUSH.equals(event.getType())) {
            openPush(event);
            return;
        }

        if (TYPE_COMMIT_COMMENT.equals(event.getType())) {
            openCommitComment(event);
            return;
        }

        Issue issue = issueMatcher.getIssue(event);
        if (issue != null) {
            Repository repo = RepositoryEventMatcher.getRepository(
                    event.getRepo(), event.getActor(), event.getOrg());
            viewIssue(issue, repo);
            return;
        }

        Gist gist = gistMatcher.getGist(event);
        if (gist != null) {
            startActivity(GistsViewActivity.createIntent(gist));
            return;
        }

        Repository repo = repoMatcher.getRepository(event);
        if (repo != null)
            viewRepository(repo);

        UserPair users = userMatcher.getUsers(event);
        if (users != null)
            viewUser(users);
    }

    @Override
    public boolean onListItemLongClick(ListView l, View v, int position,
            long itemId) {
        if (!isUsable())
            return false;

        final Event event = (Event) l.getItemAtPosition(position);
        final Repository repo = RepositoryEventMatcher.getRepository(
                event.getRepo(), event.getActor(), event.getOrg());
        final User user = event.getActor();

        if (repo != null && user != null) {
            final AlertDialog dialog = LightAlertDialog.create(getActivity());
            dialog.setTitle(string.navigate_to);
            dialog.setCanceledOnTouchOutside(true);

            View view = getActivity().getLayoutInflater().inflate(
                    layout.nav_dialog, null);
            ViewFinder finder = new ViewFinder(view);
            avatars.bind(finder.imageView(id.iv_user_avatar), user);
            avatars.bind(finder.imageView(id.iv_repo_avatar), repo.getOwner());
            finder.setText(id.tv_login, user.getLogin());
            finder.setText(id.tv_repo_name, repo.generateId());
            finder.onClick(id.ll_user_area, new OnClickListener() {

                public void onClick(View v) {
                    dialog.dismiss();

                    viewUser(user);
                }
            });
            finder.onClick(id.ll_repo_area, new OnClickListener() {

                public void onClick(View v) {
                    dialog.dismiss();

                    viewRepository(repo);
                }
            });
            dialog.setView(view);
            dialog.show();

            return true;
        }

        return false;
    }

    private void openDownload(Event event) {
        Download download = ((DownloadPayload) event.getPayload())
                .getDownload();
        if (download == null)
            return;

        String url = download.getHtmlUrl();
        if (TextUtils.isEmpty(url))
            return;

        Intent intent = new Intent(ACTION_VIEW, Uri.parse(url));
        intent.addCategory(CATEGORY_BROWSABLE);
        startActivity(intent);
    }

    private void openCommitComment(Event event) {
        Repository repo = RepositoryEventMatcher.getRepository(event.getRepo(),
                event.getActor(), event.getOrg());
        if (repo == null)
            return;

        CommitCommentPayload payload = (CommitCommentPayload) event
                .getPayload();
        CommitComment comment = payload.getComment();
        if (comment == null)
            return;

        String sha = comment.getCommitId();
        if (!TextUtils.isEmpty(sha))
            startActivity(CommitViewActivity.createIntent(repo, sha));
    }

    private void openPush(Event event) {
        Repository repo = RepositoryEventMatcher.getRepository(event.getRepo(),
                event.getActor(), event.getOrg());
        if (repo == null)
            return;

        PushPayload payload = (PushPayload) event.getPayload();
        List<Commit> commits = payload.getCommits();
        if (commits == null || commits.isEmpty())
            return;

        if (commits.size() > 1) {
            String base = payload.getBefore();
            String head = payload.getHead();
            if (!TextUtils.isEmpty(base) && !TextUtils.isEmpty(head))
                startActivity(CommitCompareViewActivity.createIntent(repo,
                        base, head));
        } else {
            Commit commit = commits.get(0);
            String sha = commit != null ? commit.getSha() : null;
            if (!TextUtils.isEmpty(sha))
                startActivity(CommitViewActivity.createIntent(repo, sha));
        }
    }

    /**
     * Start an activity to view the given repository
     *
     * @param repository
     */
    protected void viewRepository(Repository repository) {
        startActivity(RepositoryViewActivity.createIntent(repository));
    }

    /**
     * Start an activity to view the given {@link UserPair}
     * <p>
     * This method does nothing by default, subclasses should override
     *
     * @param users
     */
    protected void viewUser(UserPair users) {
    }

    /**
     * Start an activity to view the given {@link User}
     *
     * @param user
     * @return true if new activity started, false otherwise
     */
    protected boolean viewUser(User user) {
        return false;
    }

    /**
     * Start an activity to view the given {@link Issue}
     *
     * @param issue
     * @param repository
     */
    protected void viewIssue(Issue issue, Repository repository) {
        if (repository != null)
            startActivity(IssuesViewActivity.createIntent(issue, repository));
        else
            startActivity(IssuesViewActivity.createIntent(issue));
    }

    @Override
    protected SingleTypeAdapter<Event> createAdapter(List<Event> items) {
        return new NewsListAdapter(getActivity().getLayoutInflater(),
                items.toArray(new Event[items.size()]), avatars);
    }

    @Override
    protected int getLoadingMessage() {
        return string.loading_news;
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return string.error_news_load;
    }
}
