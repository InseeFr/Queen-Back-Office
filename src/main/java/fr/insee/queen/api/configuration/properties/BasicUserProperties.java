package fr.insee.queen.api.configuration.properties;

import java.util.Arrays;
import java.util.Objects;

public record BasicUserProperties(
        String username,
        String password,
        String[] roles) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicUserProperties that = (BasicUserProperties) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password) && Arrays.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(username, password);
        result = 31 * result + Arrays.hashCode(roles);
        return result;
    }

    @Override
    public String toString() {
        return "BasicUserProperties{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + Arrays.toString(roles) +
                '}';
    }
}
