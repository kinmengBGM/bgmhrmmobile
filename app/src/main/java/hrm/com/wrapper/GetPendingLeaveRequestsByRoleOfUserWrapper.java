package hrm.com.wrapper;

import hrm.com.model.LeaveTransaction;

public class GetPendingLeaveRequestsByRoleOfUserWrapper {
	LeaveTransaction leaveTransaction;
	Boolean isApproved;
	
	public GetPendingLeaveRequestsByRoleOfUserWrapper(LeaveTransaction leaveTransaction, Boolean isApproved){
		this.leaveTransaction = leaveTransaction;
		this.isApproved = isApproved;
	}
	
	public LeaveTransaction getLeaveTransaction(){
		return this.leaveTransaction;
	}
	
	public void setLeaveTransaction(LeaveTransaction leaveTransaction){
		this.leaveTransaction = leaveTransaction;
	}
	
	public Boolean getIsApproved(){
		return this.isApproved;
	}
	
	public void setIsApproved(Boolean isApproved){
		this.isApproved= isApproved;
	}
}
