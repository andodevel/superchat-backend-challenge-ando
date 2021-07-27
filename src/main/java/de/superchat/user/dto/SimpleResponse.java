package de.superchat.user.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wrap simple result in JSON
 * @param <T> type extends Serializable
 */
@Data
@AllArgsConstructor
public class SimpleResponse<T extends Serializable> {

    private T result;

}
