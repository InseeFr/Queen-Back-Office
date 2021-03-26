package fr.insee.queen.api.dto.statedata;

import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.domain.StateDataType;

public class StateDataDto {
	private StateDataType state;
	private Long date;
	private String currentPage;
	
	public StateDataDto() {
		super();
	}
	
	public StateDataDto(StateDataType state, Long date, String currentPage) {
		super();
		this.state = state;
		this.date = date;
		this.currentPage = currentPage;
	}

	public StateDataDto(StateData stateData) {
		super();
		if(stateData!=null) {
			this.state = stateData.getState();
			this.date = stateData.getDate();
			this.currentPage = stateData.getCurrentPage();
		}
	}
	public StateDataType getState() {
		return state;
	}
	public void setState(StateDataType state) {
		this.state = state;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public String getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}
}
