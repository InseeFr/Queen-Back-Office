package fr.insee.queen.api.dto.stateData;

import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.domain.StateStateData;

public class StateDataDto {
	private StateStateData state;
	private Long date;
	private int currentPage;
	
	
	public StateDataDto(StateData stateData) {
		super();
		if(stateData!=null) {
			this.state = stateData.getState();
			this.date = stateData.getDate();
			this.currentPage = stateData.getCurrentPage();
		}
	}
	public StateStateData getState() {
		return state;
	}
	public void setState(StateStateData state) {
		this.state = state;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
}
