
package org.zarroboogs.weibo.othercomponent;

import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.CommentBean;
import org.zarroboogs.weibo.bean.CommentListBean;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.bean.UnreadBean;
import org.zarroboogs.weibo.db.task.NotificationDBTask;
import org.zarroboogs.weibo.service.BigTextNotificationService;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.NotificationUtility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * 注册在AndroidManifest中
 */
public class UnreadMsgReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AccountBean accountBean = intent.getParcelableExtra(BundleArgsConstants.ACCOUNT_EXTRA);
        CommentListBean commentsToMeData = intent
                .getParcelableExtra(BundleArgsConstants.COMMENTS_TO_ME_EXTRA);
        MessageListBean mentionsWeiboData = intent
                .getParcelableExtra(BundleArgsConstants.MENTIONS_WEIBO_EXTRA);
        CommentListBean mentionsCommentData = intent
                .getParcelableExtra(BundleArgsConstants.MENTIONS_COMMENT_EXTRA);
        UnreadBean unreadBean = intent.getParcelableExtra(BundleArgsConstants.UNREAD_EXTRA);

        showNotification(context, accountBean, mentionsWeiboData, commentsToMeData, mentionsCommentData, unreadBean);

    }

    private void showNotification(Context context, AccountBean accountBean, MessageListBean mentionsWeiboData,
                                  CommentListBean commentsToMeData,
                                  CommentListBean mentionsCommentData, UnreadBean unreadBean) {

        Intent clickNotificationToOpenAppPendingIntentInner = MainTimeLineActivity.unReadIntent(accountBean, mentionsWeiboData,
                mentionsCommentData,
                commentsToMeData, unreadBean);
        clickNotificationToOpenAppPendingIntentInner
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        String accountId = accountBean.getUid();

        Set<String> dbUnreadMentionsWeibo = NotificationDBTask.getUnreadMsgIds(accountId,
                NotificationDBTask.UnreadDBType.mentionsWeibo);
        Set<String> dbUnreadMentionsComment = NotificationDBTask.getUnreadMsgIds(accountId,
                NotificationDBTask.UnreadDBType.mentionsComment);
        Set<String> dbUnreadCommentsToMe = NotificationDBTask.getUnreadMsgIds(accountId,
                NotificationDBTask.UnreadDBType.commentsToMe);

        if (mentionsWeiboData != null && mentionsWeiboData.getSize() > 0) {

            List<MessageBean> msgList = mentionsWeiboData.getItemList();
            Iterator<MessageBean> iterator = msgList.iterator();
            while (iterator.hasNext()) {
                MessageBean msg = iterator.next();
                if (dbUnreadMentionsWeibo.contains(msg.getId())) {
                    iterator.remove();
                }
            }

        }

        if (mentionsCommentData != null && mentionsCommentData.getSize() > 0) {
            List<CommentBean> msgList = mentionsCommentData.getItemList();
            Iterator<CommentBean> iterator = msgList.iterator();
            while (iterator.hasNext()) {
                CommentBean msg = iterator.next();
                if (dbUnreadMentionsComment.contains(msg.getId())) {
                    iterator.remove();
                }
            }

        }

        if (commentsToMeData != null && commentsToMeData.getSize() > 0) {
            List<CommentBean> msgList = commentsToMeData.getItemList();
            Iterator<CommentBean> iterator = msgList.iterator();
            while (iterator.hasNext()) {
                CommentBean msg = iterator.next();
                if (dbUnreadCommentsToMe.contains(msg.getId())) {
                    iterator.remove();
                }
            }

        }

        boolean mentionsWeibo = (mentionsWeiboData != null && mentionsWeiboData.getSize() > 0);
        boolean mentionsComment = (mentionsCommentData != null && mentionsCommentData.getSize() > 0);
        boolean commentsToMe = (commentsToMeData != null && commentsToMeData.getSize() > 0);

        if (!mentionsWeibo && !mentionsComment && !commentsToMe) {
            return;
        }

        // boolean commentsToMeDataSizeIsLarge = (commentsToMeData != null) && (
        // commentsToMeData.getSize() >= Integer.valueOf(
        // SettingUtility.getMsgCount()));
        //
        // boolean mentionsWeiboDataSizeIsLarge = (mentionsWeiboData != null) &&
        // (
        // mentionsWeiboData.getSize() >= Integer.valueOf(
        // SettingUtility.getMsgCount()));
        //
        // boolean mentionsCommentDataSizeIsLarge = (mentionsCommentData !=
        // null) && (
        // mentionsCommentData.getSize() >= Integer.valueOf(
        // SettingUtility.getMsgCount()));
        //
        // boolean showSimpleTextNotification = commentsToMeDataSizeIsLarge
        // || mentionsWeiboDataSizeIsLarge || mentionsCommentDataSizeIsLarge;

        // if (showSimpleTextNotification) {
        // String ticker = NotificationUtility
        // .getTicker(unreadBean);
        // Intent intent = new Intent(context,
        // SimpleTextNotificationService.class);
        //
        // intent.putExtra(NotificationServiceHelper.ACCOUNT_ARG, accountBean);
        // intent.putExtra(NotificationServiceHelper.UNREAD_ARG, unreadBean);
        // intent.putExtra(NotificationServiceHelper.PENDING_INTENT_INNER_ARG,
        // clickNotificationToOpenAppPendingIntentInner);
        // intent.putExtra(NotificationServiceHelper.TICKER, ticker);
        // context.startService(intent);
        //
        // } else {

        String ticker = NotificationUtility.getTicker(unreadBean, mentionsWeiboData, mentionsCommentData, commentsToMeData);

        Intent intent = BigTextNotificationService.newIntent(accountBean, mentionsWeiboData, commentsToMeData,
                mentionsCommentData, unreadBean,
                clickNotificationToOpenAppPendingIntentInner, ticker, 0);
        context.startService(intent);

    }

}
