package hrm.com.custom.listener;

/**
 * Created by Beans on 4/22/2015.
 */
public interface ApproveLeaveListener {

    public void onRejectSelected(int leaveTransactionId);
    public void onApproveSelected(int leaveTransactionId);
}
