package fr.insee.queen.api.dto.questionnairemodel;

public class QuestionnaireIdDto {
	
	private String questionnaireId;
	
	public QuestionnaireIdDto() {
		super();
	}
	
	public QuestionnaireIdDto(String id) {
		this.setQuestionnaireId(id);
	}

	public String getQuestionnaireId() {
		return questionnaireId;
	}

	public void setQuestionnaireId(String questionnaireId) {
		this.questionnaireId = questionnaireId;
	}


	
}
