package fr.insee.queen.api.dto.comment;

import com.fasterxml.jackson.annotation.JsonRawValue;

public record CommentDto(@JsonRawValue String value){}
