package fr.insee.queen.jms.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.*;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class JsonValidator {

    /**
     * Approche JMS pure : on s'appuie d'abord sur getBody/assignableTo,
     * puis on gère explicitement TextMessage/BytesMessage/ObjectMessage/MapMessage.
     */
    public static String extractJson(Message message) throws JMSException {
        // 1) API JMS 2.0 : plus portable quand le provider sait convertir
        if (message.isBodyAssignableTo(String.class)) {
            return message.getBody(String.class);
        }

        // 2) Fallback par type JMS standard
        if (message instanceof TextMessage tm) {
            return tm.getText();
        }

        if (message instanceof BytesMessage bm) {
            bm.reset(); // important avant readBytes
            long len = bm.getBodyLength();
            if (len > Integer.MAX_VALUE) {
                throw new JMSException("BytesMessage trop long: " + len);
            }
            byte[] data = new byte[(int) len];
            int read = bm.readBytes(data);
            if (read < 0) return null;
            return new String(data, 0, read, StandardCharsets.UTF_8);
        }

        if (message instanceof ObjectMessage om) {
            Object obj = om.getObject();
            // si l’émetteur envoie déjà une String JSON :
            if (obj instanceof String s) return s;
            // sinon, on tente un toString() (à utiliser avec prudence)
            return (obj != null) ? obj.toString() : null;
        }

        if (message instanceof MapMessage mm) {
            // On reconstruit un JSON à partir des paires clé/valeur
            var mapper = new ObjectMapper();
            var node = mapper.createObjectNode();
            @SuppressWarnings("unchecked")
            Enumeration<String> names = (Enumeration<String>) mm.getMapNames();
            while (names.hasMoreElements()) {
                String k = names.nextElement();
                Object v = mm.getObject(k); // garde le type (String, Number, Boolean…)
                node.putPOJO(k, v);
            }
            return node.toString();
        }
        // (Optionnel) StreamMessage ou autres types propriétaires -> à adapter si nécessaire
        return null;
    }
}
