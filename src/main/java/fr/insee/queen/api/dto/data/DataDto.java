package fr.insee.queen.api.dto.data;


import com.fasterxml.jackson.annotation.JsonRawValue;

public record DataDto (@JsonRawValue String value) {}
