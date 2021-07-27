package de.superchat.auth.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrap simple result in JSON
 * @param <T> type extends Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleResponse<T extends Serializable> {

    /**
     * Wrap simple result in JSON
     */
    private T result;

}

