package fr.insee.queen.domain.registre.model;

/**
 * Represents a code list from the registre.
 * Contains the code list identifier and URL.
 *
 * @param id the unique identifier of the code list
 * @param url the URL to access the code list modalities
 */
public record CodeList(
    String id,
    String url
) {}