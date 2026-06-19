package fr.insee.queen.application.campaign.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
public class CampaignIdsDto {
    @JsonProperty
    private String id;

    public static CampaignIdsDto fromModel(String campaignId) {
        return new CampaignIdsDto(campaignId);
    }
}
