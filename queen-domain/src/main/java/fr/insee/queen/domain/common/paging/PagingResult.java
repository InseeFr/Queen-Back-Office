package fr.insee.queen.domain.common.paging;

import java.util.List;

public record PagingResult<T>(
        List<T> contents,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages) {
}
