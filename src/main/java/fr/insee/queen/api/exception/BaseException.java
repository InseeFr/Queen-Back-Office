package fr.insee.queen.api.exception;

import java.util.Map;

public interface BaseException {
    Map<String, Object> getMapForResponse();
}
