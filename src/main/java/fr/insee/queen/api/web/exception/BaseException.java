package fr.insee.queen.api.web.exception;

import java.util.Map;

public interface BaseException {
    Map<String, Object> getMapForResponse();
}
