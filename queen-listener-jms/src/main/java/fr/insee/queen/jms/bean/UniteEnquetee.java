package fr.insee.queen.jms.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UniteEnquetee implements Serializable {

  private Long externalId;
  private String campagneId;
  private String questionnaireId;
  private String firstName;
  private String lastName;
  private String correlationID;
  private String replyTo;
  private boolean inProgress;
  private boolean done;

  @Override
  public String toString() {
    return "UniteEnquetee{" +
            "externalId=" + externalId +
            ", campagneId='" + campagneId + '\'' +
            ", questionnaireId='" + questionnaireId + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", correlationID='" + correlationID + '\'' +
            ", replyTo='" + replyTo + '\'' +
            ", inProgress=" + inProgress +
            ", done=" + done +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UniteEnquetee that = (UniteEnquetee) o;
    return inProgress == that.inProgress && done == that.done && Objects.equals(externalId, that.externalId) && Objects.equals(campagneId, that.campagneId) && Objects.equals(questionnaireId, that.questionnaireId) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(correlationID, that.correlationID) && Objects.equals(replyTo, that.replyTo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(externalId, campagneId, questionnaireId, firstName, lastName, correlationID, replyTo, inProgress, done);
  }
}

