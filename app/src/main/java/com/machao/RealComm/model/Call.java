package com.machao.RealComm.model;

public class Call {
    private Principal calling;
	private Principal callee;

	private RTCSessionDescription callingSessionDescription;
	private RTCSessionDescription calleeSessionDescription;

	public Call() {
		super();
	}

	public Call(Principal calling, Principal callee) {
		super();
		this.calling = calling;
		this.callee = callee;
	}

	public Principal getCalling() {
		return calling;
	}

	public void setCalling(Principal calling) {
		this.calling = calling;
	}

	public Principal getCallee() {
		return callee;
	}

	public void setCallee(Principal callee) {
		this.callee = callee;
	}

	public RTCSessionDescription getCallingSessionDescription() {
		return callingSessionDescription;
	}

	public void setCallingSessionDescription(RTCSessionDescription callingSessionDescription) {
		this.callingSessionDescription = callingSessionDescription;
	}

	public RTCSessionDescription getCalleeSessionDescription() {
		return calleeSessionDescription;
	}

	public void setCalleeSessionDescription(RTCSessionDescription calleeSessionDescription) {
		this.calleeSessionDescription = calleeSessionDescription;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((callee == null) ? 0 : callee.hashCode());
		result = prime * result + ((calling == null) ? 0 : calling.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Call other = (Call) obj;
		if (callee == null) {
			if (other.callee != null)
				return false;
		} else if (!callee.equals(other.callee))
			return false;
		if (calling == null) {
			if (other.calling != null)
				return false;
		} else if (!calling.equals(other.calling))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Call [calling=" + calling + ", callee=" + callee + "]";
	}
}
